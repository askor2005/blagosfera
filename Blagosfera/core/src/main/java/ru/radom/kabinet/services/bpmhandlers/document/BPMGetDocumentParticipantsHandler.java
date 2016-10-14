package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.dao.FlowOfDocumentParticipantDao;
import ru.radom.kabinet.document.dto.FlowOfDocumentParticipantDTO;
import ru.radom.kabinet.document.dto.utils.FlowOfDocumentParticipantConverter;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getDocumentParticipantsHandler")
@Transactional
public class BPMGetDocumentParticipantsHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FlowOfDocumentParticipantConverter flowOfDocumentParticipantConverter;

    @Autowired
    private FlowOfDocumentParticipantDao participantDao;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        return documentInfoExecutor(parameters, (id, code) -> {
            ExceptionUtils.check(code == null && id == null, "Не передан ИД или код документа");
            List<DocumentParticipantEntity> documentParticipants = participantDao.findDocumentParticipants(id, code);
            List<FlowOfDocumentParticipantDTO> result = documentParticipants.stream().map(flowOfDocumentParticipantConverter::convert).collect(Collectors.toList());
            return result == null ? null : serializeService.toPrimitiveObject(result);
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