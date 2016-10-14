package ru.radom.kabinet.services.batchVoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate.*;
import ru.radom.kabinet.model.votingtemplate.*;
import ru.radom.kabinet.services.batchVoting.dto.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;

import java.util.*;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Service
@Transactional
public class BatchVotingTemplateService {

    @Autowired
    private BatchVotingTemplateRepository batchVotingTemplateRepository;

    @Autowired
    private BatchVotingAttributeTemplateRepository batchVotingAttributeTemplateRepository;

    @Autowired
    private VotingAttributeTemplateRepository votingAttributeTemplateRepository;

    @Autowired
    private VotingItemTemplateRepository votingItemTemplateRepository;

    @Autowired
    private VotingTemplateRepository votingTemplateRepository;

    @Autowired
    private VoterAllowedTemplateRepository voterAllowedTemplateRepository;


    public void checkDto(BatchVotingTemplateDto batchVotingDto){
        if (batchVotingDto.subject == null || batchVotingDto.subject.equals("")) {
            throw new RuntimeException("Не установлена тема собрания.");
        }
        if (batchVotingDto.description == null || batchVotingDto.description.equals("")) {
            throw new RuntimeException("Не установлено описание собрания.");
        }
        if (batchVotingDto.behavior == null) {
            throw new RuntimeException("Не установлено поведение собрания.");
        }
        if (!BatchVotingConstructorService.BEHAVIORS.containsKey(batchVotingDto.behavior)) {
            throw new RuntimeException("Выбранное поведение не существует.");
        }
        if (batchVotingDto.quorum < 1) {
            throw new RuntimeException("Не установлен кворум собрания.");
        }
        if (batchVotingDto.quorum > 100) {
            throw new RuntimeException("Кворум не может быть больше 100 процентов.");
        }
        Date startDate = batchVotingDto.startDate;
        Date endDate = batchVotingDto.endDate;
        Date votersRegistrationEndDate = batchVotingDto.registrationEndDate;

        ExceptionUtils.check(startDate == null, "Не установлена дата начала собрания.");
        ExceptionUtils.check(endDate == null, "Не установлена дата окончания собрания.");
        ExceptionUtils.check(votersRegistrationEndDate == null, "Не установлена дата окончания голосования.");


        ExceptionUtils.check(startDate.getTime() > votersRegistrationEndDate.getTime(),
                "Дата начала собрания не может быть больше даты окончания голосования");
        ExceptionUtils.check(votersRegistrationEndDate.getTime() > endDate.getTime(),
                "Дата окончания голосования не может быть больше даты окончания собрания");
        ExceptionUtils.check(batchVotingDto.votersAllowed == null || batchVotingDto.votersAllowed.size() == 0,
                "Не установлены участники собрания.");
        /*ExceptionUtils.check(batchVotingDto.mode == null,
                "Не установлен режим проведения собрания (последовательные выборы либо параллельные).");*/

        ExceptionUtils.check(votersRegistrationEndDate == null, "Не установлена дата окончания регистрации в собрании.");

        ExceptionUtils.check(!(votersRegistrationEndDate.getTime() >= startDate.getTime() + 60*60*1000),
                "От начала собрания до окончания регистрации должно быть не менее часа");
        ExceptionUtils.check(!(endDate.getTime() >=  votersRegistrationEndDate.getTime() + 60*60*1000),
                "От окончания регистрации до окончания собрания должно быть не менее часа");
        ExceptionUtils.check(batchVotingDto.votingRestartCount < 0,
                "Количество рестартов голосований должно быть от 0 и до " + Integer.MAX_VALUE + ".");
        ExceptionUtils.check(batchVotingDto.votings == null || batchVotingDto.votings.size() == 0,
                "Не добавлено ни одного голосования в собрание.");
    }

    public List<VotingTemplateEntity> getVotingTemplates(BatchVotingTemplateDto batchVotingDto, BatchVotingTemplateEntity batchVotingTemplate) {
        List<VotingTemplateEntity> result = new ArrayList<>();

        for (VotingTemplateDto votingDto : batchVotingDto.votings) {
            VotingTemplateEntity votingTemplate = new VotingTemplateEntity();
            votingTemplate.setId(votingDto.id);
            votingTemplate.setSubject(votingDto.subject);
            votingTemplate.setDescription(votingDto.description);
            votingTemplate.setBatchVoting(batchVotingTemplate);
            votingTemplate.setIndex(votingDto.index);
            votingTemplate.setStopBatchVotingOnFailResult(votingDto.isStopBatchVotingOnFailResult());
            votingTemplate.setIsVisible(votingDto.isVisible);
            votingTemplate.setIsVoteCancellable(votingDto.isVoteCancellable);
            votingTemplate.setAddAbstain(votingDto.addAbstain);
            votingTemplate.setIsVoteCommentsAllowed(votingDto.isVoteCommentsAllowed);
            votingTemplate.setMinSelectionCount(votingDto.minSelectionCount);
            votingTemplate.setMaxSelectionCount(votingDto.maxSelectionCount);
            votingTemplate.setMinWinnersCount(votingDto.minWinnersCount);
            votingTemplate.setMaxWinnersCount(votingDto.maxWinnersCount);
            votingTemplate.setVotingState(votingDto.votingState);
            votingTemplate.setVotingType(votingDto.votingType);
            votingTemplate.setMultipleWinners(votingDto.multipleWinners);
            votingTemplate.setUseBiometricIdentification(votingDto.useBiometricIdentification);
            votingTemplate.setSkipResults(votingDto.skipResults);
            votingTemplate.setSuccessDecree(votingDto.getSuccessDecree());
            votingTemplate.setFailDecree(votingDto.getFailDecree());
            votingTemplate.setSentence(votingDto.getSentence());
            votingTemplate.setPercentForWin(votingDto.getPercentForWin());
            votingTemplate.setNotRestartable(votingDto.notRestartable);

            for (VotingAttributeTemplateDto votingAttributeTemplateDto  : votingDto.attributes) {
                VotingAttributeTemplate votingAttributeTemplate = new VotingAttributeTemplate();
                votingAttributeTemplate.setId(votingAttributeTemplateDto.id);
                votingAttributeTemplate.setName(votingAttributeTemplateDto.name);
                votingAttributeTemplate.setValue(votingAttributeTemplateDto.value);
                votingAttributeTemplate.setVoting(votingTemplate);
                votingTemplate.getAttributes().add(votingAttributeTemplate);
            }

            for (VotingItemTemplateDto votingItemDto : votingDto.votingItems) {
                VotingItemTemplate votingItemTemplate = new VotingItemTemplate();
                votingItemTemplate.setId(votingItemDto.id);
                votingItemTemplate.setValue(votingItemDto.value);
                votingItemTemplate.setVoting(votingTemplate);
                votingTemplate.getVotingItems().add(votingItemTemplate);
            }

            result.add(votingTemplate);
        }

        return result;
    }

    /**
     * Конвертирование дто с данными из UI в шаблон
     * @param batchVotingDto
     * @return
     */
    public BatchVotingTemplateEntity getBatchVotingTemplate(BatchVotingTemplateDto batchVotingDto) {
        BatchVotingTemplateEntity batchVotingTemplate;
        try {
            // Поведение всегда будет по умолчанию
            batchVotingDto.behavior = BatchVotingConstants.STANDARD_BEHAVIOR_NAME;
            checkDto(batchVotingDto);

            batchVotingTemplate = new BatchVotingTemplateEntity();

            if (batchVotingDto.id != null) {
                batchVotingTemplate.setId(batchVotingDto.id);
            }

            batchVotingTemplate.setSubject(batchVotingDto.subject);
            batchVotingTemplate.setDescription(batchVotingDto.description);
            batchVotingTemplate.setIsNeedCreateChat(batchVotingDto.isNeedCreateChat);
            batchVotingTemplate.setBehavior(batchVotingDto.behavior);
            batchVotingTemplate.setQuorum(batchVotingDto.quorum);

            batchVotingTemplate.setStartDate(batchVotingDto.startDate);

            if (batchVotingDto.registrationEndDate != null && batchVotingDto.startDate != null) {
                int countHours = (int)((batchVotingDto.registrationEndDate.getTime() - batchVotingDto.startDate.getTime()) / 1000 / 60 / 60);
                batchVotingTemplate.setRegistrationHoursCount(countHours);
            }
            if (batchVotingDto.endDate != null && batchVotingDto.startDate != null) {
                int countHours = (int)((batchVotingDto.endDate.getTime() - batchVotingDto.startDate.getTime()) / 1000 / 60 / 60);
                batchVotingTemplate.setBatchVotingHoursCount(countHours);
            }
            /*
            batchVotingTemplate.setRegistrationHoursCount((int) ((DateUtils.parseDate(
                    batchVotingDto.registrationEndDate, null, DateUtils.Format.DATE_TIME_SHORT).getTime() -
                    DateUtils.parseDate(batchVotingDto.startDate, null, DateUtils.Format.DATE_TIME_SHORT).getTime()) / 1000 / 60 / 60));
            batchVotingTemplate.setBatchVotingHoursCount((int) ((DateUtils.parseDate(
                    batchVotingDto.endDate, null, DateUtils.Format.DATE_TIME_SHORT).getTime() -
                    DateUtils.parseDate(batchVotingDto.startDate, null, DateUtils.Format.DATE_TIME_SHORT).getTime()) / 1000 / 60 / 60));*/
            batchVotingTemplate.setIsCanFinishBeforeEndDate(batchVotingDto.isCanFinishBeforeEndDate);

            batchVotingTemplate.getVotersAllowed().addAll(VoterAllowedTemplateDto.toListDomain(batchVotingDto.votersAllowed));
            batchVotingTemplate.setMode(batchVotingDto.mode);

            batchVotingTemplate.setVotingRestartCount(batchVotingDto.votingRestartCount);
            batchVotingTemplate.setSecretVoting(batchVotingDto.secretVoting);
            batchVotingTemplate.setIsNeedAddAdditionalVotings(batchVotingDto.isNeedAddAdditionalVotings);

            List<BatchVotingAttributeTemplate> attributes = new ArrayList<>();
            for (BatchVotingAttributeTemplateDto batchVotingAttributeTemplateDto : batchVotingDto.attributes) {
                BatchVotingAttributeTemplate attributeTemplate = new BatchVotingAttributeTemplate();
                attributeTemplate.setName(batchVotingAttributeTemplateDto.name);
                attributeTemplate.setValue(batchVotingAttributeTemplateDto.value);
                attributes.add(attributeTemplate);
            }

            // Сохраняем атрибуты
            batchVotingTemplate.getAttributes().addAll(attributes);

            // Создаём голосования
            batchVotingTemplate.getVotings().addAll(getVotingTemplates(batchVotingDto, batchVotingTemplate));

            batchVotingTemplate.setUseBiometricIdentificationInAdditionalVotings(batchVotingDto.useBiometricIdentificationInAdditionalVotings);
            batchVotingTemplate.setUseBiometricIdentificationInRegistration(batchVotingDto.useBiometricIdentificationInRegistration);
            batchVotingTemplate.setAddChatToProtocol(batchVotingDto.addChatToProtocol);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return batchVotingTemplate;
    }

    public void deleteById(Long id) {
        BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateRepository.getOne(id);
        for (VotingTemplateEntity votingTemplate : batchVotingTemplate.getVotings()) {
            for (VotingAttributeTemplate votingAttributeTemplate : votingTemplate.getAttributes()) {
                votingAttributeTemplateRepository.delete(votingAttributeTemplate);
            }
            for (VotingItemTemplate votingItemTemplate : votingTemplate.getVotingItems()) {
                votingItemTemplateRepository.delete(votingItemTemplate);
            }
            votingTemplateRepository.delete(votingTemplate);
        }
        for (BatchVotingAttributeTemplate batchVotingAttributeTemplate : batchVotingTemplate.getAttributes()) {
            batchVotingAttributeTemplateRepository.delete(batchVotingAttributeTemplate);
        }
        batchVotingTemplateRepository.delete(batchVotingTemplate);
    }

    /**
     * Удалить варианты голосований, которые были удалены из UI
     * @param oldVotingItemTemplates
     * @param newVotingItemTemplates
     */
    private void removeDeletedVotingItemTemplates(Set<VotingItemTemplate> oldVotingItemTemplates, Set<VotingItemTemplate> newVotingItemTemplates) {
        for (VotingItemTemplate oldVotingItemTemplate : oldVotingItemTemplates) {
            boolean found = false;
            for (VotingItemTemplate newVotingItemTemplate : newVotingItemTemplates) {
                if (oldVotingItemTemplate.getId().equals(newVotingItemTemplate.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                votingItemTemplateRepository.delete(oldVotingItemTemplate);
            }
        }
    }

    /**
     * Удалить голосования, которые были удалены из UI
     * @param oldVotingTemplates
     * @param newVotingTemplates
     */
    private void removeDeletedVotingTemplates(List<VotingTemplateEntity> oldVotingTemplates, List<VotingTemplateEntity> newVotingTemplates) {
        for (VotingTemplateEntity oldVotingTemplate : oldVotingTemplates) {
            boolean found = false;
            for (VotingTemplateEntity newVotingTemplate : newVotingTemplates) {
                if (oldVotingTemplate.getId().equals(newVotingTemplate.getId())) {
                    // Удалить варианты голосований
                    removeDeletedVotingItemTemplates(oldVotingTemplate.getVotingItems(), newVotingTemplate.getVotingItems());
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (oldVotingTemplate.getVotingItems() != null) {
                    for (VotingItemTemplate votingItemTemplate : oldVotingTemplate.getVotingItems()) {
                        votingItemTemplateRepository.delete(votingItemTemplate);
                    }
                }
                votingTemplateRepository.delete(oldVotingTemplate);
            }
        }
    }

    private void removeDeletedBatchVotingAttributeTemplates(List<BatchVotingAttributeTemplate> oldBatchVotingAttributeTemplates,
                                                            List<BatchVotingAttributeTemplate> newBatchVotingAttributeTemplates) {
        for (BatchVotingAttributeTemplate oldBatchVotingAttributeTemplate : oldBatchVotingAttributeTemplates) {
            boolean found = false;
            for (BatchVotingAttributeTemplate newBatchVotingAttributeTemplate : newBatchVotingAttributeTemplates) {
                if (oldBatchVotingAttributeTemplate.getId().equals(newBatchVotingAttributeTemplate.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                batchVotingAttributeTemplateRepository.delete(oldBatchVotingAttributeTemplate);
            }
        }
    }

    private void removeVotersAllowed(BatchVotingTemplateEntity batchVotingTemplate) {
        if (batchVotingTemplate != null && batchVotingTemplate.getVotersAllowed() != null) {
            for (VoterAllowedTemplate voterAllowedTemplate : batchVotingTemplate.getVotersAllowed()) {
                voterAllowedTemplateRepository.delete(voterAllowedTemplate);
            }
        }
    }

    public void save(BatchVotingTemplateEntity batchVotingTemplate) {
        // Обновление данных
        if (batchVotingTemplate.getId() != null) {
            BatchVotingTemplateEntity oldBatchVotingTemplate = batchVotingTemplateRepository.findOne(batchVotingTemplate.getId());
            if (oldBatchVotingTemplate != null) {
                List<VotingTemplateEntity> oldVotingTemplates = oldBatchVotingTemplate.getVotings();
                // Удалить удалённые варианты голосований
                removeDeletedVotingTemplates(oldVotingTemplates, batchVotingTemplate.getVotings());
                List<BatchVotingAttributeTemplate> oldBatchVotingAttributeTemplates = oldBatchVotingTemplate.getAttributes();
                // Удалить удалённые атрибуты
                removeDeletedBatchVotingAttributeTemplates(oldBatchVotingAttributeTemplates, batchVotingTemplate.getAttributes());
                // Удалить возможных участников
                removeVotersAllowed(oldBatchVotingTemplate);
            }
            if (batchVotingTemplate.getLastBatchVotingDate() == null) {
                batchVotingTemplate.setLastBatchVotingDate(oldBatchVotingTemplate.getLastBatchVotingDate());
            }
            batchVotingTemplate.getBatchVotings().addAll(oldBatchVotingTemplate.getBatchVotings());
            saveEntity(batchVotingTemplate);
        } else { // Сохранение новых данных
            saveEntity(batchVotingTemplate);
        }
    }

    private void saveEntity(BatchVotingTemplateEntity batchVotingTemplate) {
        for (VotingTemplateEntity votingTemplate : batchVotingTemplate.getVotings()) {
            if (votingTemplate.getId() != null) {
                VotingTemplateEntity existingVotingTemplate = votingTemplateRepository.findOne(votingTemplate.getId());

                for (Iterator<VotingAttributeTemplate> it = existingVotingTemplate.getAttributes().iterator(); it.hasNext();) {
                    VotingAttributeTemplate attribute = it.next();
                    boolean attributeExists = false;
                    String newValue = null;

                    for (Iterator<VotingAttributeTemplate> it2 = votingTemplate.getAttributes().iterator(); it2.hasNext();) {
                        VotingAttributeTemplate attribute2 = it2.next();

                        if (attribute.getName().equals(attribute2.getName())) {
                            attributeExists = true;
                            newValue = attribute2.getValue();
                            break;
                        }
                    }

                    if (attributeExists) {
                        attribute.setValue(newValue);
                        attribute = votingAttributeTemplateRepository.save(attribute);
                    } else {
                        it.remove();
                        votingAttributeTemplateRepository.delete(attribute);
                    }
                }

                for (Iterator<VotingAttributeTemplate> it = votingTemplate.getAttributes().iterator(); it.hasNext();) {
                    VotingAttributeTemplate attribute = it.next();
                    boolean attributeExists = false;

                    for (Iterator<VotingAttributeTemplate> it2 = existingVotingTemplate.getAttributes().iterator(); it2.hasNext();) {
                        VotingAttributeTemplate attribute2 = it2.next();

                        if (attribute.getName().equals(attribute2.getName())) {
                            attributeExists = true;
                            break;
                        }
                    }

                    if (!attributeExists) {
                        attribute.setVoting(existingVotingTemplate);
                        existingVotingTemplate.getAttributes().add(attribute);
                        attribute = votingAttributeTemplateRepository.save(attribute);
                    }
                }

                for (Iterator<VotingItemTemplate> it = existingVotingTemplate.getVotingItems().iterator(); it.hasNext();) {
                    VotingItemTemplate item = it.next();
                    boolean itemExists = false;
                    String newValue = null;

                    for (Iterator<VotingItemTemplate> it2 = votingTemplate.getVotingItems().iterator(); it2.hasNext();) {
                        VotingItemTemplate item2 = it2.next();

                        if (item.getId().equals(item2.getId())) {
                            itemExists = true;
                            newValue = item2.getValue();
                            break;
                        }
                    }

                    if (itemExists) {
                        item.setValue(newValue);
                        item = votingItemTemplateRepository.save(item);
                    } else {
                        //votingItemTemplateRepository.delete(item);
                        it.remove();
                    }
                }

                for (Iterator<VotingItemTemplate> it = votingTemplate.getVotingItems().iterator(); it.hasNext();) {
                    VotingItemTemplate item = it.next();
                    boolean itemExists = false;

                    for (Iterator<VotingItemTemplate> it2 = existingVotingTemplate.getVotingItems().iterator(); it2.hasNext();) {
                        VotingItemTemplate item2 = it2.next();

                        if (item.getValue().equals(item2.getValue())) {
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        item.setVoting(existingVotingTemplate);
                        existingVotingTemplate.getVotingItems().add(item);
                        item = votingItemTemplateRepository.save(item);
                    }
                }

                votingTemplate = votingTemplateRepository.save(existingVotingTemplate);
            } else {
                votingTemplate = votingTemplateRepository.save(votingTemplate);

                for (VotingAttributeTemplate votingAttributeTemplate : votingTemplate.getAttributes()) {
                    votingAttributeTemplateRepository.save(votingAttributeTemplate);
                }

                for (VotingItemTemplate votingItemTemplate : votingTemplate.getVotingItems()) {
                    votingItemTemplateRepository.save(votingItemTemplate);
                }
            }
        }

        /*for (BatchVotingAttributeTemplate batchVotingAttributeTemplate : batchVotingTemplate.getAttributes()) {
            batchVotingAttributeTemplateRepository.save(batchVotingAttributeTemplate);
        }*/

        List<VoterAllowedTemplate> votersAllowed = batchVotingTemplate.getVotersAllowed();

        batchVotingTemplate = batchVotingTemplateRepository.save(batchVotingTemplate);

        for (VoterAllowedTemplate voterAllowedTemplate : votersAllowed) {
            voterAllowedTemplate.setBatchVotingTemplate(batchVotingTemplate);
            voterAllowedTemplateRepository.save(voterAllowedTemplate);
        }
    }

    public BatchVotingTemplateEntity getById(Long id) {
        return batchVotingTemplateRepository.findOne(id);
    }
}
