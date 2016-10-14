package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getParticipantDocumentsHandler")
@Transactional
public class BPMGetParticipantDocumentsHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        Long participant = extractSingleId(parameters.get("participant"));
        ExceptionUtils.check(participant == null, "Не передан участник");
        Boolean signed = MapUtils.getBoolean(parameters, "signed");
        Boolean signedByParticipant = MapUtils.getBoolean(parameters, "signedByParticipant");
        Date beforeDate = resolveDate(parameters.get("before"));
        Date afterDate = resolveDate(parameters.get("after"));
        List<DocumentEntity> documents = documentDao.findDocumentsOfParticipant(participant, signedByParticipant, signed, afterDate, beforeDate);
        return serializeService.toPrimitiveObject(documents);
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
     * Пытаемся преобразовать объект к дате
     */
    private Date resolveDate(Object value) {
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
    }
}