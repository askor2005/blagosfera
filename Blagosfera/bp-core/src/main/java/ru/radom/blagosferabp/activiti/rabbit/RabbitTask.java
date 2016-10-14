package ru.radom.blagosferabp.activiti.rabbit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.rabbit.bundle.RabbitTaskBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 05.11.2015.<br/>
 * Таск, который отправляет и ожидает сообщение по RabbitMQ
 */
@Data
@EqualsAndHashCode(callSuper = true)
@CustomServiceTask(bundle = RabbitTaskBundle.class, type = RabbitTaskBundle.TASK_TYPE)
public class RabbitTask extends ServiceTask {

    /**
     * ID элемента в базе
     */
    private String stencilEntityId;

    /**
     * Очередь, в которую отправляются данные, когда задача начинает выполняться
     */
    private String queueToSend;

    /**
     * Тип ответа:
     * <li>
     *     <ul>{@link StencilEntity#FIRE_AND_FORGET}</ul>
     *     <ul>{@link StencilEntity#IMMEDIATE_WAIT}</ul>
     *     <ul>{@link StencilEntity#WAIT_ANSWER_MESSAGE}</ul>
     * </li>
     */
    private String answerType;

    /**
     * Не стандартные параметры (название -> значение)
     */
    private Map<String, Object> customParameters;

    @Override
    public String getImplementationType() {
        return ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION;
    }

    public void setValues(RabbitTask that) {
        super.setValues(that);
        this.setQueueToSend(that.getQueueToSend());
        this.setCustomParameters(new HashMap<>(that.getCustomParameters()));
    }

    @Override
    public RabbitTask clone() {
        RabbitTask clone = new RabbitTask();
        clone.setValues(this);
        return clone;
    }
}
