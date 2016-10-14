package ru.radom.blagosferabp.activiti.custom.component.storage;

import lombok.Getter;
import lombok.Setter;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;
import ru.radom.blagosferabp.activiti.custom.component.storage.bundles.StorageGetTaskModelBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Задание, которое извлекает значения и перекладывает их в контекст
 */
@Getter
@Setter
@CustomServiceTask(bundle = StorageGetTaskModelBundle.class, type = StorageGetTaskModelBundle.STORAGE_GET_TASK)
public class StorageGetTask extends ServiceTask {

    /**
     * Данные, которые будут взяты из хранилища и переложены в контекст
     */
    @FlowNodeParameter(
        stencilParameter = StorageGetTaskModelBundle.VALUES_MAPPING_PROPERTY,
        xmlParameter = StorageGetTaskModelBundle.VALUES_MAPPING_PROPERTY,
        required = false
    )
    private Map<String, String> dataMapping;

    /**
     * Будут взяты только данные не младше этой даты
     */
    @FlowNodeParameter(
        stencilParameter = StorageGetTaskModelBundle.LAST_MODIFIED_DATE_PROPERTY,
        xmlParameter = StorageGetTaskModelBundle.LAST_MODIFIED_DATE_PROPERTY,
        required = false
    )
    private String lastModifiedDate;


    @Override
    public StorageGetTask clone() {
        StorageGetTask task = new StorageGetTask();
        task.setValues(this);
        return task;
    }

    public void setValues(StorageGetTask task) {
        Map<String, String> dataMapping = task.getDataMapping();
        this.dataMapping = dataMapping == null ? null : new HashMap<>(dataMapping);
        this.lastModifiedDate = task.getLastModifiedDate();
        super.setValues(task);
    }

    @Override
    public String getImplementationType() {
        return ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION;
    }
}
