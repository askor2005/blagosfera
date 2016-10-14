package ru.radom.kabinet.services.bpmhandlers.voting;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmFinishTaskEvent;
import ru.askor.blagosfera.domain.events.voting.BatchVotingStateChangeCallbackEvent;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.business.event.VotingEventType;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.*;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.batchVoting.BatchVotingConstructorService;
import ru.radom.kabinet.services.bpmhandlers.voting.dto.*;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.BatchVotingStateChangeCallback;
import ru.radom.kabinet.voting.CommonVotingService;

import java.util.*;


/**
 * Обработчик тасков по собраниям
 * Created by vgusev on 09.12.2015.
 */
@Service
@Transactional
public class BPMBatchVotingService implements BatchVotingStateChangeCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchVotingStateChangeCallback.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private VotingService votingService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommonVotingService commonVotingService;

    @Autowired
    private BatchVotingConstructorService batchVotingConstructorService;

    @Autowired
    private StompService stompService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final long MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

    /**
     * Наименование атрибута собрания - ИД выполняемой задачи BPM
     */
    private static final String TASK_ID_BATCH_VOTING_ATTR_NAME = "batchVotingBPMTaskId";

    /**
     * Наименование атрибута собрания - очередь куда слать сообщение о созданном собрании
     */
    private static final String CREATE_BATCH_VOTING_WEB_SOCKET_QUEUE_ATTR_NAME = "createBatchVotingQueue";

    /**
     * ИД собрания, которое завершилось фейлом
     */
    private static final Long FAIL_RESULT_BATCH_VOTING_ID = -1l;

    public BPMBatchVotingExtended getBpmBatchVotingExtended(BatchVoting batchVoting) {
        return new BPMBatchVotingExtended(batchVoting);
    }

    @Data
    static class CreateVotingsResult {

        private List<Voting> votings;

        private Map<String, String> additionlaData;
    }

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @EventListener
    public void onVotingEvent(VotingEvent event) {
        if (event.getEventType() == VotingEventType.BATCH_VOTING_STATE_CHANGE) {
            BatchVoting eventBatchVoting = event.getBatchVoting();

            if (!eventBatchVoting.getParameters().getBehavior().equals(BPMBatchVotingBehavior.NAME)) {
                return;
            }

            blagosferaEventPublisher.publishEvent(new BatchVotingStateChangeCallbackEvent(this, eventBatchVoting.getId(), this));
        }
    }

    @Override
    public void batchVotingStateChangeCallback(long batchVotingId) {
        try {
            final BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);
            Voting firstFailedVoting = batchVotingService.getFirstFailedVoting(batchVoting);

            boolean isNeedFinishTask = false;
            boolean isFailBatchVoting = false;
            if (batchVoting.getState() == BatchVotingState.FINISHED && firstFailedVoting == null) {
                // Собрание завершено удачно
                isNeedFinishTask = true;
            } else if (batchVoting.getState() == BatchVotingState.FINISHED && firstFailedVoting != null) {
                // Собрание зафейлилось
                isNeedFinishTask = true;
                isFailBatchVoting = true;
            } else if (batchVoting.getState() == BatchVotingState.VOTERS_REGISTRATION &&
                    batchVoting.getAdditionalData().containsKey(CREATE_BATCH_VOTING_WEB_SOCKET_QUEUE_ATTR_NAME)) {
                // Создано новое собрание
                BPMBatchVotingResponseDto bpmBatchVotingResponseDto =
                        new BPMBatchVotingResponseDto(
                                batchVoting.getId(),
                                ru.askor.blagosfera.core.util.DateUtils.toDate(batchVoting.getParameters().getStartDate()),
                                ru.askor.blagosfera.core.util.DateUtils.toDate(batchVoting.getParameters().getEndDate()),
                                ru.askor.blagosfera.core.util.DateUtils.toDate(batchVoting.getParameters().getVotersRegistrationEndDate()),
                                new Date(),
                                batchVoting.getSubject(),
                                batchVoting.getAdditionalData()
                        );
                stompService.send(
                        userDataService.getByIdMinData(batchVoting.getOwnerId()).getEmail(),
                        batchVoting.getAdditionalData().get(CREATE_BATCH_VOTING_WEB_SOCKET_QUEUE_ATTR_NAME),
                        bpmBatchVotingResponseDto);
            }
            if (isNeedFinishTask) {
                if (isFailBatchVoting) {
                    batchVoting.setId(FAIL_RESULT_BATCH_VOTING_ID);
                }
                String taskId = batchVoting.getAdditionalData().get(TASK_ID_BATCH_VOTING_ATTR_NAME);
                Map<String, Object> batchVotingConverted = serializeService.toPrimitiveObject(getBpmBatchVotingExtended(batchVoting));

                //BPMBlagosferaUtils.finishTask(rabbitTemplate, taskId, batchVotingConverted);

                BpmFinishTaskEvent bpmFinishTaskEvent = new BpmFinishTaskEvent(this, taskId, batchVotingConverted);
                blagosferaEventPublisher.publishEvent(bpmFinishTaskEvent);

                LOGGER.info("Задача " + taskId + ". Собрание " + batchVoting.getSubject() + "(ИД: " + batchVoting.getId() + ") завершено успешно.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработчик задачи - собрание
     * Данная задача является задачей с ожиданием ответа, поэтому сигнал о выполнении задачи посылается
     * отдельно при окончании собрания
     *
     * @param message сообщение из jackrabbit
     * @param taskId  ИД задачи activiti
     */
    // -TODO Переделать на BPMHandler
    /*@SuppressWarnings("unchecked")
    @RabbitListener(queues = "core.batch.voting")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createBatchVoting(Message message,
                                  @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    LOGGER.info("Задача " + taskId + ". Запуск создания собрания.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    try {
                        BPMBatchVotingDto bpmBatchVotingDto = serializeService.toObject(data, BPMBatchVotingDto.class);
                        createBatchVoting(bpmBatchVotingDto, taskId);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);

                        BatchVoting batchVoting = new BatchVoting();
                        batchVoting.setId(FAIL_RESULT_BATCH_VOTING_ID);
                        Map<String, Object> batchVotingConverted = serializeService.toPrimitiveObject(getBpmBatchVotingExtended(batchVoting));

                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, batchVotingConverted));
                        LOGGER.info("Задача " + taskId + ". При создании собрания произошла ошибка.");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("Задача " + taskId + ". При создании собрания произошла ошибка. Текст ошибки: " + e.getMessage());
        }
    }*/

    // -TODO Переделать на BPMHandler
    /*@SuppressWarnings("unchecked")
    @RabbitListener(queues = "batch.voting.add.parameters")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addToBatchVotingParameters(Message message,
                                  @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    LOGGER.info("Задача " + taskId + ". Добавление к собранию дополнительных параметров.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    try {
                        BPMBatchVotingAddParametersDto bpmBatchVotingDto = serializeService.toObject(data, BPMBatchVotingAddParametersDto.class);
                        BatchVoting batchVoting = batchVotingService.getBatchVoting(bpmBatchVotingDto.getBatchVotingId(), true, true);
                        batchVoting.getAdditionalData().putAll(bpmBatchVotingDto.getAdditionalParameters());

                        batchVotingService.saveAdditionalData(batchVoting);

                        Map<String, Object> batchVotingConverted = serializeService.toPrimitiveObject(getBpmBatchVotingExtended(batchVoting));
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, batchVotingConverted));
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);

                        BatchVoting batchVoting = new BatchVoting();
                        batchVoting.setId(FAIL_RESULT_BATCH_VOTING_ID);
                        Map<String, Object> batchVotingConverted = serializeService.toPrimitiveObject(getBpmBatchVotingExtended(batchVoting));

                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, batchVotingConverted));
                        LOGGER.info("Задача " + taskId + ". При добавлении параметров к собранию произошли ошибки.");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("Задача " + taskId + ". При создании собрания произошла ошибка. Текст ошибки: " + e.getMessage());
        }
    }*/

    // -TODO Переделать на BPMHandler
    /*@SuppressWarnings("unchecked")
    @RabbitListener(queues = "batch.voting.get.candidates.winners")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void getCandidatesWinnersIds(Message message,
                                           @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    LOGGER.info("Задача " + taskId + ". Получение ИД победиелей в выборах.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    try {
                        BPMGetCandidatesWinnersIdsDto bpmGetCandidatesWinnersIdsDto = serializeService.toObject(data, BPMGetCandidatesWinnersIdsDto.class);
                        List<VotingItem> votingItems = commonVotingService.getWinners(bpmGetCandidatesWinnersIdsDto.votingId);
                        List<String> result = votingItems.stream().map(VotingItem::getValue).collect(Collectors.toList());

                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId,
                                StringUtils.join(result, bpmGetCandidatesWinnersIdsDto.joinString)
                        ));
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);

                        BatchVoting batchVoting = new BatchVoting();
                        batchVoting.setId(FAIL_RESULT_BATCH_VOTING_ID);
                        Map<String, Object> batchVotingConverted = serializeService.toPrimitiveObject(getBpmBatchVotingExtended(batchVoting));

                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, batchVotingConverted));
                        LOGGER.info("Задача " + taskId + ". При получении ИД победиелей в выборах произошли ошибки.");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("Задача " + taskId + ". При создании собрания произошла ошибка. Текст ошибки: " + e.getMessage());
        }
    }*/


    /**
     * Создание собрания на основе данных из таска BPM
     *
     * @param bpmBatchVotingDto обёртка собрания
     * @param taskId            ИД задачи в activiti
     * @return созданное собрание
     */
    public BatchVoting createBatchVoting(BPMBatchVotingDto bpmBatchVotingDto, String taskId) {
        Date startDate = bpmBatchVotingDto.getStartDate();
        Date endDate = bpmBatchVotingDto.getEndDate();
        Date votersRegistrationEndDate = bpmBatchVotingDto.getRegistrationEndDate();

        Map<String, String> additionalData = new HashMap<>();
        // ИД выполняемой задачи
        additionalData.put(TASK_ID_BATCH_VOTING_ATTR_NAME, taskId);
        // Очередь о созданном собрании
        if (!StringUtils.isBlank(bpmBatchVotingDto.getCreateBatchVotingQueue())) {
            additionalData.put(CREATE_BATCH_VOTING_WEB_SOCKET_QUEUE_ATTR_NAME, bpmBatchVotingDto.getCreateBatchVotingQueue());
        }
        if (bpmBatchVotingDto.getAdditionalParameters() != null) {
            additionalData.putAll(bpmBatchVotingDto.getAdditionalParameters());
        }

        CreateVotingsResult createVotingsResult = createVotings(bpmBatchVotingDto, bpmBatchVotingDto.getVotersAllowed());

        additionalData.putAll(createVotingsResult.getAdditionlaData());

        return commonVotingService.createBatchVoting(
                bpmBatchVotingDto.getOwnerId(), bpmBatchVotingDto.getSubject(), bpmBatchVotingDto.getDescription(),
                null, null, BPMBatchVotingBehavior.NAME, bpmBatchVotingDto.getQuorum(),
                bpmBatchVotingDto.getVotersAllowed(),
                startDate, endDate, votersRegistrationEndDate,
                bpmBatchVotingDto.isCanFinishBeforeEndDate(), bpmBatchVotingDto.getMode(),
                bpmBatchVotingDto.getVotingRestartCount(), bpmBatchVotingDto.isSecretVoting(), bpmBatchVotingDto.isNeedAddAdditionalVotings(),
                bpmBatchVotingDto.isNeedCreateChat(), null, additionalData, createVotingsResult.getVotings()
        );
    }

    private Set<Long> getCandidates(Set<Object> votingItems) {
        Set<Long> result = new HashSet<>();
        for (Object item : votingItems) {
            if (item instanceof Integer) {
                result.add(((Integer) item).longValue());
            } else if (item instanceof String) {
                Long value = VarUtils.getLong((String) item, null);
                if (value != null) {
                    result.add(value);
                }
            } else if (item instanceof Long) {
                Long value = (Long) item;
                if (value != null) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    /**
     * Создать голосования в собрании
     *
     * @param bpmBatchVotingDto обёртка собрания
     * @param votersIds         ИД голосующих
     * @return коллекция голосований
     * @throws Exception
     */
    private CreateVotingsResult createVotings(BPMBatchVotingDto bpmBatchVotingDto, Set<Long> votersIds) {
        CreateVotingsResult createVotingsResult = new CreateVotingsResult();
        List<Voting> result = new ArrayList<>();
        Map<String, String> additionalData = new HashMap<>();
        try {
            boolean isAddVotingItemsAllowed = false;

            int deltaIndex = 0;
            // Создавать дополнительные голосования нужно если выставлен флаг или поведение не по умолчанию
            if (bpmBatchVotingDto.isNeedAddAdditionalVotings()) {
                deltaIndex = 3;
            }

            List<BPMVotingDto> votingsForCreate = new ArrayList<>();
            for (BPMVotingDto votingDto : bpmBatchVotingDto.getVotings().getData()) {
                // Если условие создания голосования - false, то не создаём голосование
                if (!"false".equals(votingDto.getVotingCreateCondition())) {
                    votingsForCreate.add(votingDto);
                }
            }
            Collections.sort(votingsForCreate, (o1, o2) -> {
                int result1;
                if (o1.getIndex() < o2.getIndex()) {
                    result1 = -1;
                } else {
                    result1 = 1;
                }
                return result1;
            });

            List<Voting> templateVotings = new ArrayList<>();
            int index = 0;
            for (BPMVotingDto votingDto : votingsForCreate) {
                Voting voting = null;
                int votingIndex = index + deltaIndex;
                String sentence = votingDto.getSentence();
                String successDecree = votingDto.getSuccessDecree();
                String failDecree = votingDto.getFailDecree();
                Map<String, String> additionalParameters = new HashMap<>();
                additionalParameters.put(BatchVotingConstants.SELECT_FROM_TYPE, votingDto.getSelectFromType());
                if (votingDto.getSourceVotingIndex() != null) {
                    additionalParameters.put(BatchVotingConstants.SOURCE_VOTING_INDEX, String.valueOf(votingDto.getSourceVotingIndex() + deltaIndex));
                }
                switch (votingDto.getVotingType()) {
                    case CANDIDATE: { // Кандидат
                        voting = commonVotingService.createVotingCandidate(
                                getCandidates(votingDto.getVotingItems()), votingDto.getSubject(), votingDto.getDescription(),
                                votingDto.isVoteCancellable(), votingDto.isVoteCommentsAllowed(),
                                votingIndex, isAddVotingItemsAllowed,
                                votingDto.isVisible(), true, votingDto.isAddAbstain(), votingDto.getVotingButtons(),
                                votingDto.getVotingWinnerText(), votingDto.getMinSelectionCount(), votingDto.getMaxSelectionCount(),
                                votingDto.getMinWinnersCount(), votingDto.getMaxWinnersCount(), votingDto.isMultipleWinners(), additionalParameters,
                                successDecree, failDecree, sentence,
                                votingDto.getPercentForWin());
                        break;
                    }
                    case PRO_CONTRA: { // За\Против
                        // Количечество победителей в За\Против всегда 1
                        voting = commonVotingService.createVotingProContraAbstain(
                                votingDto.getSubject(), votingDto.getDescription(),
                                votingDto.isVoteCancellable(), votingDto.isVoteCommentsAllowed(),
                                votingIndex, votingDto.isVisible(),
                                votingDto.isFailOnContraResult(), votingDto.isAddAbstain(), votingDto.getVotingButtons(),
                                votingDto.getVotingWinnerText(), false, additionalParameters, successDecree, failDecree, sentence,
                                votingDto.getPercentForWin());
                        break;
                    }
                    case INTERVIEW: {
                        voting = commonVotingService.createInterviewVoting(
                                votingDto.getSubject(), votingDto.getDescription(),
                                votingDto.isVoteCancellable(), votingDto.isVoteCommentsAllowed(),
                                votingIndex, votingDto.isVisible(), votingDto.isAddAbstain(), votingDto.getVotingButtons(),
                                votingDto.getVotingWinnerText(), true, additionalParameters, successDecree, failDecree, sentence);
                        break;
                    }
                    case MULTIPLE_SELECTION: {
                        List<String> variants = new ArrayList<>();
                        variants.addAll((Set) votingDto.getVotingItems());
                        voting = commonVotingService.createMultipleSelectionVoting(
                                votingDto.getSubject(), votingDto.getDescription(),
                                votingDto.isVoteCancellable(), votingDto.isVoteCommentsAllowed(),
                                votingIndex, votingDto.isVisible(), true,
                                variants, votingDto.getMinSelectionCount(), votingDto.getMaxSelectionCount(),
                                votingDto.getMinWinnersCount(), votingDto.getMaxWinnersCount(),
                                votingDto.isAddAbstain(), votingDto.getVotingButtons(),
                                votingDto.getVotingWinnerText(), votingDto.isMultipleWinners(), additionalParameters, successDecree, failDecree, sentence,
                                votingDto.getPercentForWin());
                        break;
                    }
                    case SINGLE_SELECTION: {
                        List<String> variants = new ArrayList<>();
                        variants.addAll((Set) votingDto.getVotingItems());
                        voting = commonVotingService.createSingleSelectionVoting(
                                votingDto.getSubject(), votingDto.getDescription(),
                                votingDto.isVoteCancellable(), votingDto.isVoteCommentsAllowed(),
                                votingIndex, votingDto.isVisible(), true,
                                variants, votingDto.isAddAbstain(), votingDto.getVotingButtons(),
                                votingDto.getVotingWinnerText(), votingDto.isMultipleWinners(), additionalParameters, successDecree, failDecree, sentence,
                                votingDto.getPercentForWin());
                        break;
                    }
                }
                templateVotings.add(voting);
                index++;
            }


            // Создавать дополнительные голосования нужно если выставлен флаг или поведение не по умолчанию
            if (bpmBatchVotingDto.isNeedAddAdditionalVotings()) {
                // Первое голосование - за председателя собрания
                result.add(batchVotingConstructorService.createVotingForChoosePresidentOfBatchVoting(votersIds, false));
                // Второе голосование - за секретаря собрания
                result.add(batchVotingConstructorService.createVotingForChooseSecretaryOfBatchVoting(votersIds, false));
                // Третье голосование - за повестку дня на основе созданных голосований
                result.add(batchVotingConstructorService.createVotingForMeetingAgenda(templateVotings, false));

                // Устанавливаем в доп параметры собрания повестку дня
                additionalData.put(BatchVotingConstants.VOTING_AGENDA_ATTR_NAME, batchVotingConstructorService.createBatchVotingAgenda(templateVotings));
            }
            result.addAll(templateVotings);

            createVotingsResult.setVotings(result);
            createVotingsResult.setAdditionlaData(additionalData);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return createVotingsResult;
    }

}
