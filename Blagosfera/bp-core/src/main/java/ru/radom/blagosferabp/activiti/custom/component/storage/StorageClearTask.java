package ru.radom.blagosferabp.activiti.custom.component.storage;

import lombok.Getter;
import lombok.Setter;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;
import ru.radom.blagosferabp.activiti.custom.component.storage.bundles.StorageClearTaskModelBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Задание, которое удаляет значения по ключам
 */
@Getter
@Setter
@CustomServiceTask(bundle = StorageClearTaskModelBundle.class, type = StorageClearTaskModelBundle.STORAGE_CLEAR_TASK)
public class StorageClearTask extends ServiceTask {

    /**
     * Ключи, которые нужно удалить
     */
    @FlowNodeParameter(
        stencilParameter = StorageClearTaskModelBundle.KEYS_TO_CLEAR_PROPERTY,
        xmlParameter = StorageClearTaskModelBundle.KEYS_TO_CLEAR_PROPERTY,
        required = true
    )
    private List<String> keys;

    @Override
    public StorageClearTask clone() {
        StorageClearTask task = new StorageClearTask();
        task.setValues(this);
        return task;
    }

    public void setValues(StorageClearTask task) {
        List<String> keys = task.getKeys();
        this.keys = keys != null ? new ArrayList<>(keys) : null;
        super.setValues(task);
    }

    @Override
    public String getImplementationType() {
        return ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION;
    }
}
