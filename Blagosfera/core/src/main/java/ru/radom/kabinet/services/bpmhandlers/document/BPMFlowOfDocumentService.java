package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTemplateDao;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.dao.FlowOfDocumentParticipantDao;
import ru.radom.kabinet.document.dto.utils.FlowOfDocumentParticipantConverter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.pdf.PdfService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Сервис обработки задач activiti для подсистемы документооборота
 * Created by vgusev on 24.02.2016.
 */
@Service
@Transactional
public class BPMFlowOfDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(BPMFlowOfDocumentService.class);

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentTemplateDao documentTemplateDao;

    @Autowired
    @Qualifier("calibreHtmlToPdfServiceImpl")
    private PdfService pdfService;

    @Autowired
    protected FlowOfDocumentParticipantDao participantDao;

    @Autowired
    private FlowOfDocumentParticipantConverter flowOfDocumentParticipantConverter;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    // -TODO Переделать на BPMHandler
    /*@RabbitListener(queues = "documents.create.pdf.zip")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createPdfZip(Message message,
                             @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    logger.info("Задача " + taskId + ". Запуск создания zip архива pdf документов.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    FileOutputStream fileOutputStream = null;
                    String filePath = null;
                    try {
                        BPMDocumentsZipDto bpmDocumentsZipDto = serializeService.toObject(data, BPMDocumentsZipDto.class);
                        List<Long> docIds = bpmDocumentsZipDto.getDocumentMap() == null ?
                                Collections.<Long>emptyList() : new ArrayList<>(bpmDocumentsZipDto.getDocumentMap().values());
                        List<DocumentEntity> documents = documentDao.getByIds(docIds);
                        File tempFile = File.createTempFile("documents", ".zip");
                        fileOutputStream = new FileOutputStream(tempFile);
                        writeZipToStream(documents, fileOutputStream);
                        tempFile.deleteOnExit();
                        filePath = tempFile.getAbsolutePath();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        IOUtils.closeQuietly(fileOutputStream); // NPE safe
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, filePath));
                        if (filePath == null) {
                            logger.info("Задача " + taskId + ". При создании zip архива pdf документов произошла ошибка.");
                        } else {
                            logger.info("Задача " + taskId + ". Zip архив создан успешно. Путь до файла: " + filePath);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/



    /*private void convertAdditionalDocParaters(Map<String, Object> additionalAttributes) {
        if (additionalAttributes != null && !additionalAttributes.isEmpty()) {
            for (String key : additionalAttributes.keySet()) {
                Object val = additionalAttributes.get(key);
                if (val instanceof String) {
                    String strVal = (String)val;
                    Object newVal = strVal;
                    if (strVal.contains("|||")) {
                        String[] pair = strVal.split("\\|\\|\\|");
                        if (pair.length == 2) {
                            String param = pair[0];
                            String func = pair[1];
                            switch (func) {
                                case "getBatchVoting":
                                    newVal = getBatchVoting(param);
                                    break;
                            }
                        }
                    }
                    additionalAttributes.put(key, newVal);
                }
            }
        }
    }*/

    private BatchVoting getBatchVoting(String batchVotingIdStr) {
        BatchVoting result = null;
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, null);
        if (batchVotingId != null) {
            try {
                result = batchVotingService.getBatchVoting(batchVotingId, true, true);
            } catch (VotingSystemException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Создать документ по шаблону
     */
    // -TODO Переделать на BPMHandler
    /*@RabbitListener(queues = "core.flow.document.create")
    // Для того, чтобы исключение не бросалось при откате транзакции делаем без поддержки транзакции
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createDocumentWorker(
            Message message,
            @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId
    ) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    // TODO Переписать всё с использованием DTO переменной
                    DocumentEntity document = null;
                    String code = null;
                    try {
                        logger.info("Задача " + taskId + ". Запуск создания документа.");
                        Map<String, Object> commonData = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                        Map<String, Object> data = (Map<String, Object>)commonData.get("document");
                        Long owner = extractSingleId(data.get("owner"));
                        ExceptionUtils.check(owner == null, "При выполнении таска создания документа не найден создатель.");

                        Map<String, Object> templateData = (Map<String, Object>) data.get("template");
                        Long id = MapUtils.getLong(templateData, "id");
                        DocumentTemplateEntity template = documentTemplateDao.getById(id);
                        code = template.getCode();
                        logger.info("Задача " + taskId + ". Код документа: " + code);
                        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
                        Map<String, Map<String, Object>> participantsData = (Map<String, Map<String, Object>>) data.get("participants");
                        Map<String, Map<String, Object>> fieldsData = (Map<String, Map<String, Object>>) data.get("fields");
                        Map<Long, List<Map<String, Object>>> fieldsByParticipant =
                                fieldsData.values().stream().collect(Collectors.groupingBy(d -> MapUtils.getLong(d, "participantId")));

                        if (participantsData != null) {
                            documentParameters = participantsData.values().stream().map(participant -> {
                                ParticipantCreateDocumentParameter participantParameter;
                                String type = MapUtils.getString(participant, "type");
                                ParticipantsTypes ptype = ParticipantsTypes.valueOf(type);
                                List<UserFieldValue> fields = extractUserFields(fieldsByParticipant, MapUtils.getLong(participant, "id"));
                                switch (ptype) {
                                    default:
                                    case INDIVIDUAL:
                                    case REGISTRATOR:
                                    case COMMUNITY_WITH_ORGANIZATION:
                                    case COMMUNITY_WITHOUT_ORGANIZATION:
                                    case COMMUNITY_IP: {
                                        Long participantId;
                                        Object value = participant.get("value");
                                        participantId = extractSingleId(value);
                                        participantParameter = new ParticipantCreateDocumentParameter(
                                                type,
                                                participantId,
                                                MapUtils.getString(participant, "name")
                                        );
                                        break;
                                    }
                                    case COMMUNITY_WITH_ORGANIZATION_LIST:
                                    case INDIVIDUAL_LIST: {
                                        List<Long> participants;
                                        Object value = participant.get("value");
                                        if (value instanceof List) {
                                            participants = new ArrayList<>();
                                            ((List<Object>) value).forEach(val -> participants.add(extractSingleId(val)));
                                            //participants = ((List<Object>) value).stream().map(extractSingleId).collect(Collectors.toList());
                                        } else {
                                            participants = extactIds(value);
                                        }
                                        participantParameter = new ParticipantCreateDocumentParameter(
                                                type,
                                                participants,
                                                MapUtils.getString(participant, "name")
                                        );
                                        break;
                                    }
                                }
                                return new CreateDocumentParameter(participantParameter, fields);
                            }).collect(Collectors.toList());
                        }
                        // Дополнительные параметры шаблона документа для создания документа с использованием EL переменных
                        Map<String, Object> additionalAttributes;
                        try {
                            additionalAttributes = (Map<String, Object>)commonData.get("additionalAttributes");
                        } catch (Exception e) {
                            additionalAttributes = new HashMap<>();
                        }
                        convertAdditionalDocParaters(additionalAttributes);
                        document = documentService.createDocument(code, documentParameters, owner, null, null, true, additionalAttributes);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        Object payload = document != null ? convertDocumentToSend(document) : null;
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, payload));
                        if (payload == null) {
                            logger.info("Задача " + taskId + " выполнена. При создании документа с кодом шаблона " + code + " произошла ошибка.");
                        } else {
                            logger.info("Задача " + taskId + " выполнена. Документ с кодом шаблона " + code + " создан.");
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/

    /**
     * Вытащить пользовательские поля участника
     */
    /*private List<UserFieldValue> extractUserFields(Map<Long, List<Map<String, Object>>> fieldsByParticipant, Long id) {
        List<Map<String, Object>> fields = fieldsByParticipant.get(id);
        if (fields == null) {
            return Collections.emptyList();
        }
        return fields.stream().map(d -> {
            String type = MapUtils.getString(d, "type");
            return UserFieldValueBuilder.createByType(type, MapUtils.getString(d, "name"), d.get("value"));
        }).collect(Collectors.toList());
    }

    *//**
     * Конвертируем документ, чтобы послать его через Rabbit
     *//*
    private Map<String, Object> convertDocumentToSend(DocumentEntity document) {
        return serializeService.toPrimitiveObject(document);
    }

    *//**
     * Пытается достать id из сырых данных
     *//*
    private Long extractSingleId(Object value) {
        if (value == null) {
            return null;
        }
        Long id;
        if (value instanceof Map) {
            id = MapUtils.getLong((Map) value, "id");
        } else if (value instanceof Number) {
            id = ((Number) value).longValue();
        } else {
            id = Long.parseLong(value.toString());
        }
        return id;
    }

    private List<Long> extactIds(Object value) {
        List<Long> ids = null;
        if (value instanceof Number) {
            Long id = ((Number) value).longValue();
            ids = Collections.singletonList(id);
        } else if (value instanceof String) {
            ids = new ArrayList<>();
            String val = (String)value;
            if (val != null && val.contains(",")) {
                String[] idsStr = val.split(",");
                for (String idStr : idsStr) {
                    Long id = VarUtils.getLong(idStr, null);
                    if (id != null) {
                        ids.add(id);
                    }
                }
            } else {
                Long id = VarUtils.getLong(val, null);
                if (id != null) {
                    ids = Collections.singletonList(id);
                }
            }
        }
        return ids;
    }*/

    /**
     * Получить документы для участника
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(queues = "core.flow.document.by.participant")
    public void getDocumentsByParticipantWorker(
            Message message
    ) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Long participant = extractSingleId(data.get("participant"));
            if (participant == null) {
                //TODO выбросить ошибку когда сделаю
                return "";
            }
            Boolean signed = MapUtils.getBoolean(data, "signed");
            Boolean signedByParticipant = MapUtils.getBoolean(data, "signedByParticipant");
            Date beforeDate = resolveDate(data.get("before"));
            Date afterDate = resolveDate(data.get("after"));
            return documentDao.findDocumentsOfParticipant(participant, signedByParticipant, signed, afterDate, beforeDate);
        });
    }*/

    // -TODO Переделать на BPMHandler
    /*@RabbitListener(queues = "core.flow.document.remove.images")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void removeImagesFromDocument(
            Message message,
            @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId
    ) {
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    Document document = null;
                    boolean result = false;
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    try {
                        BPMRemoveImagesFromDocumentDto bpmRemoveImagesFromDocumentDto = serializeService.toObject(data, BPMRemoveImagesFromDocumentDto.class);
                        document = documentDomainService.getById(bpmRemoveImagesFromDocumentDto.documentId);
                        logger.info("Задача " + taskId + ". Запуск удаления картинок из документа. ИД документа: " + document.getId());

                        org.jsoup.nodes.Document doc = Jsoup.parse(document.getContent());
                        doc.select("img").remove();
                        document.setContent(doc.body().html());

                        documentDomainService.save(document);
                        logger.info("Задача " + taskId + ". Удаление картинок из документа выполнено. ИД документа: " + document.getId());
                        result = true;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        Object payload = document != null ? serializeService.toPrimitiveObject(document) : null;
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, payload));
                        logger.info("Задача " + taskId + " выполнена " + (result ? "успешно" : "не успешно") + ".");
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/

    /**
     * Пытаемся преобразовать объект к дате
     */
    /*private Date resolveDate(Object value) {
        if (value instanceof String) {
            Date date = DateUtils.parseDate((String) value, null, "dd.MM.yyyy");
            if (date == null) {
                try {
                    Number time = NumberFormat.getInstance().parse((String) value);
                    if (time != null) {
                        return new Date(time.longValue());
                    }
                } catch (ParseException e) {
                    return null;
                }
            }
            return date;
        } else if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        return null;
    }*/

    /**
     * Получить документ по id или коду
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(queues = "core.flow.document.by.id.or.code")
    public void getDocumentsByIdOrCodeWorker(
            Message message
    ) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            return documentInfoExecutor(data, (id, code) -> {
                if (code == null && id == null) {
                    //TODO выбросить ошибку когда сделаю
                    return "";
                }
                DocumentEntity document = documentDao.findFirst(id == null ? Restrictions.eq("code", code) : Restrictions.eq("id", id));
                return document == null ? "" : convertDocumentToSend(document);
            });
        });
    }*/

    /**
     * Получить участников документ по id или коду
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(queues = "core.flow.document.participants.by.id.or.code")
    public void getDocumentParticipantsByIdOrCodeWorker(
            Message message
    ) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            return documentInfoExecutor(data, (id, code) -> {

                if (code == null && id == null) {
                    //TODO выбросить ошибку когда сделаю
                    return "";
                }
                List<DocumentParticipantEntity> documentParticipants = participantDao.findDocumentParticipants(id, code);
                return documentParticipants.stream().map(flowOfDocumentParticipantConverter::convert).collect(Collectors.toList());
            });
        });
    }*/

    /**
     * Обобщенный метод, который знает как достать из данных id документа и code.
     */
    /*private <T> T documentInfoExecutor(Map<String, Object> data, BiFunction<Long, String, T> fun) {
        String code;
        Long id;
        Object document = data.get("document");
        if (document instanceof Map) {
            code = MapUtils.getString((Map) document, "code");
            id = MapUtils.getLong((Map) document, "id");
        } else if (document instanceof String) {
            code = (String) document;
            try {
                Number number = NumberFormat.getInstance().parse((String) document);
                if (number != null) {
                    id = number.longValue();
                } else {
                    id = null;
                }
            } catch (ParseException e) {
                id = null;
            }
        } else if (document instanceof Number) {
            code = null;
            id = ((Number) document).longValue();
        } else {
            code = null;
            id = null;
        }
        return fun.apply(id, code);
    }*/

}
