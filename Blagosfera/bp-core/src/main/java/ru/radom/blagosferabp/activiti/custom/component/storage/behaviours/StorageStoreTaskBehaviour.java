package ru.radom.blagosferabp.activiti.custom.component.storage.behaviours;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.service.StoredKeyValueService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Поведение для {@link ru.radom.blagosferabp.activiti.custom.component.storage.StorageStoreTask}
 */
@Component
@Scope("prototype")
public class StorageStoreTaskBehaviour extends TaskActivityBehavior {

    @Autowired
    private StoredKeyValueService storedKeyValueService;

    /**
     * Данные, которые будут сохранены в хранилище
     */
    Map<Expression, Expression> dataToStore;

    public StorageStoreTaskBehaviour(Map<Expression, Expression> dataToStore) {
        this.dataToStore = dataToStore;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        Map<String, Object> data = convertData(execution);
        storedKeyValueService.updateValues(data);
        leave(execution);
    }

    private Map<String, Object> convertData(ActivityExecution execution) {
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<Expression, Expression> entry : dataToStore.entrySet()) {
            try {
                String key = (String) entry.getKey().getValue(execution);
                if (key != null) {
                    Object value;
                    try {
                        value = entry.getValue().getValue(execution);
                    } catch (ActivitiException e) {
                        value = null;
                    }
                    data.put(key, value);
                }
            } catch (ActivitiException e) {
                //ignore
            }
        }
        return data;
    }

}
