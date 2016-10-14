package ru.radom.blagosferabp.activiti.custom.component.storage.behaviours;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.dto.StoredKeyValueDTO;
import ru.radom.blagosferabp.activiti.service.StoredKeyValueService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Поведение для {@link ru.radom.blagosferabp.activiti.custom.component.storage.StorageGetTask}
 */
@Component
@Scope("prototype")
public class StorageGetTaskBehaviour extends TaskActivityBehavior {

    private final String pattern = "dd.MM.yyyy";
    @Autowired
    private StoredKeyValueService storedKeyValueService;

    /**
     * Данные, которые будут взяты из хранилища и переложены в контекст
     */
    Map<Expression, String> dataMapping;

    /**
     * Будут взяты только данные не младше этой даты
     */
    Expression lastModifiedDate;

    /**
     * Переменная в контексте, в которую будут сохранены результаты
     */
    String variableName;

    public StorageGetTaskBehaviour(Map<Expression, String> dataMapping, Expression lastModifiedDate, String variableName) {
        this.dataMapping = dataMapping;
        this.lastModifiedDate = lastModifiedDate;
        this.variableName = variableName;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        Map<String, String> data = convertData(execution);
        List<StoredKeyValueDTO> keys;
        Date lastModifiedDate = extractDate(execution);
        if(data == null) {
            if(lastModifiedDate == null) {
                throw new BpmnError("BehaviourFulledIncorrectly");
            }
            keys = storedKeyValueService.getValues(lastModifiedDate);
        } else {
            if(lastModifiedDate == null) {
                keys = storedKeyValueService.getValues(data.keySet());
            } else {
                keys = storedKeyValueService.getValues(data.keySet(), lastModifiedDate);
            }
        }
        Map<String, Object> res = new HashMap<>();
        for (StoredKeyValueDTO key : keys) {
            String keyInCtx;
            if(data == null) {
                keyInCtx = key.getKey();
            } else {
                keyInCtx = data.remove(key.getKey());
            }
            if(keyInCtx != null) {
                res.put(keyInCtx, key.getValue());
            }
        }
        if(data != null) {
            for (String s : data.values()) {
                res.put(s, null);
            }
        }
        if(variableName != null) {
            execution.setVariable(variableName, res);
        } else {
            execution.setVariables(res);
        }
        leave(execution);
    }

    private Date extractDate(ActivityExecution execution) {
        if(lastModifiedDate == null) {
            return null;
        }
        try {
            Object value = lastModifiedDate.getValue(execution);
            if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            } else if (value instanceof Date) {
                return (Date) value;
            } else if (value instanceof String) {
                try {
                    return new SimpleDateFormat(pattern).parse((String) value);
                } catch (ParseException e) {
                    return null;
                }
            }
        } catch (ActivitiException e) {
            return null;
        }
        return null;
    }

    private Map<String, String> convertData(ActivityExecution execution) {
        if(dataMapping == null) {
            return null;
        }
        Map<String, String> data = new HashMap<>();
        for (Map.Entry<Expression, String> entry : dataMapping.entrySet()) {
            try {
                String key = (String) entry.getKey().getValue(execution);
                if (key != null) {
                    data.put(key, entry.getValue());
                }
            } catch (ActivitiException e) {
                //ignore
            }
        }
        return data;
    }

}
