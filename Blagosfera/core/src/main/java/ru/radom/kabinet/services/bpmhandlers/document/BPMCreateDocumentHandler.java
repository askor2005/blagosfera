package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTemplateDao;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("createDocumentHandler")
@Transactional
public class BPMCreateDocumentHandler implements BPMHandler {

    private static final Logger logger = LoggerFactory.getLogger(BPMCreateDocumentPdfZipHandler.class);

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private DocumentTemplateDao documentTemplateDao;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private DocumentService documentService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        DocumentEntity document = null;
        String code = null;
        logger.info("Запуск создания документа.");
        Map<String, Object> data = (Map<String, Object>)parameters.get("document");
        Long owner = extractSingleId(data.get("owner"));
        ExceptionUtils.check(owner == null, "При выполнении таска создания документа не найден создатель.");

        Map<String, Object> templateData = (Map<String, Object>)data.get("template");
        Long id = MapUtils.getLong(templateData, "id");
        DocumentTemplateEntity template = documentTemplateDao.getById(id);
        code = template.getCode();
        logger.info("Код документа: " + code);
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
            additionalAttributes = (Map<String, Object>)parameters.get("additionalAttributes");
        } catch (Exception e) {
            additionalAttributes = new HashMap<>();
        }
        convertAdditionalDocParameters(additionalAttributes);
        document = documentService.createDocument(code, documentParameters, owner, null, null, true, additionalAttributes);

        return document == null ? null : serializeService.toPrimitiveObject(document);
    }

    /**
     * Пытается достать id из сырых данных
     */
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

    /**
     * Вытащить пользовательские поля участника
     */
    private List<UserFieldValue> extractUserFields(Map<Long, List<Map<String, Object>>> fieldsByParticipant, Long id) {
        List<Map<String, Object>> fields = fieldsByParticipant.get(id);
        if (fields == null) {
            return Collections.emptyList();
        }
        return fields.stream().map(d -> {
            String type = MapUtils.getString(d, "type");
            return UserFieldValueBuilder.createByType(type, MapUtils.getString(d, "name"), d.get("value"));
        }).collect(Collectors.toList());
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
    }

    private void convertAdditionalDocParameters(Map<String, Object> additionalAttributes) {
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
    }

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

}