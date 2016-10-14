package ru.radom.blagosferabp.activiti.custom.component.signal;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.parser.EventSubscriptionDeclaration;
import org.activiti.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.SignalEventSubscriptionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * Created by Otts Alexey on 30.11.2015.<br/>
 * Описание, которое пораждает сигналы на основе выражения
 */
public class CustomSignalEventSubscriptionDeclaration extends EventSubscriptionDeclaration {

    /**
     * Выражение которое вычисляется в имя сигнала
     */
    private final Expression signalExpression;

    /** */
    public CustomSignalEventSubscriptionDeclaration(Expression signalExpression) {
        super(signalExpression.getExpressionText(), "signal");
        this.signalExpression = signalExpression;
    }

    @Override
    public EventSubscriptionEntity prepareEventSubscriptionEntity(ExecutionEntity execution) {
        Object value = signalExpression.getValue(execution);
        if(!(value instanceof String)) {
            throw new ActivitiIllegalArgumentException("SignalExpression должен вычисляться в строку, но найден: " + (value == null ? "null" : value.getClass()));
        }
        EventSubscriptionEntity eventSubscriptionEntity = new SignalEventSubscriptionEntity(execution);

        eventSubscriptionEntity.setEventName((String)value);
        if(activityId != null) {
            ActivityImpl activity = execution.getProcessDefinition().findActivity(activityId);
            eventSubscriptionEntity.setActivity(activity);
        }

        if (configuration != null) {
            eventSubscriptionEntity.setConfiguration(configuration);
        }

        return eventSubscriptionEntity;
    }
}
