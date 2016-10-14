package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.collections.MapUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getDocumentHandler")
@Transactional
public class BPMGetDocumentHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        return documentInfoExecutor(parameters, (id, code) -> {
            ExceptionUtils.check(code == null && id == null, "Не передан ИД или код документа");
            DocumentEntity document = documentDao.findFirst(id == null ? Restrictions.eq("code", code) : Restrictions.eq("id", id));
            return document == null ? "" : serializeService.toPrimitiveObject(document);
        });
    }

    private <T> T documentInfoExecutor(Map<String, Object> data, BiFunction<Long, String, T> fun) {
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
    }
}