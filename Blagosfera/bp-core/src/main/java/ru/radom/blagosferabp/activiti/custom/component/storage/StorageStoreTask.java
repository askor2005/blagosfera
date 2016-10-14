package ru.radom.blagosferabp.activiti.custom.component.storage;

import lombok.Getter;
import lombok.Setter;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;
import ru.radom.blagosferabp.activiti.custom.component.storage.bundles.StorageStoreTaskModelBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Задание, которое сохраняет значения
 */
@Getter
@Setter
@CustomServiceTask(bundle = StorageStoreTaskModelBundle.class, type = StorageStoreTaskModelBundle.STORAGE_STORE_TASK)
public class StorageStoreTask extends ServiceTask {

    @FlowNodeParameter(
        stencilParameter = StorageStoreTaskModelBundle.VALUES_TO_STORE_PROPERTY,
        xmlParameter = StorageStoreTaskModelBundle.VALUES_TO_STORE_PROPERTY,
        required = true
    )
    private Map<String, String> dataToStore;


    @Override
    public StorageStoreTask clone() {
        StorageStoreTask task = new StorageStoreTask();
        task.setValues(this);
        return task;
    }

    public void setValues(StorageStoreTask task) {
        Map<String, String> dataToStore = task.getDataToStore();
        this.dataToStore = dataToStore == null ? null : new HashMap<>(dataToStore);
        super.setValues(task);
    }

    @Override
    public String getImplementationType() {
        return ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION;
    }
}
