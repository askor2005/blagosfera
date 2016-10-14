package ru.radom.blagosferabp.activiti.custom.component.signal;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.EventSubscriptionDeclaration;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractFlowNodeBpmnParseHandler;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;

/**
 * Created by Otts Alexey on 30.11.2015.<br/>
 * Парсер поведения для {@link CustomIntermediateCatchSignalEvent}
 */
public class CustomIntermediateCatchSignalEventParseHandler extends AbstractFlowNodeBpmnParseHandler<CustomIntermediateCatchSignalEvent> {

    public Class<? extends BaseElement> getHandledType() {
        return CustomIntermediateCatchSignalEvent.class;
    }

    protected void executeParse(BpmnParse bpmnParse, CustomIntermediateCatchSignalEvent event) {

        ActivityImpl activity;

        ScopeImpl scope = bpmnParse.getCurrentScope();
        String eventBasedGatewayId = getPrecedingEventBasedGateway(bpmnParse, event);
        if (eventBasedGatewayId  != null) {
            ActivityImpl gatewayActivity = scope.findActivity(eventBasedGatewayId);
            activity = createActivityOnScope(bpmnParse, event, BpmnXMLConstants.ELEMENT_EVENT_CATCH, gatewayActivity);
        } else {
            activity = createActivityOnScope(bpmnParse, event, BpmnXMLConstants.ELEMENT_EVENT_CATCH, scope);
        }

        // Catch event behavior is the same for all types
        activity.setActivityBehavior(bpmnParse.getActivityBehaviorFactory().createIntermediateCatchEventActivityBehavior(event));

        SignalEventDefinition eventDefinition = (SignalEventDefinition)event.getEventDefinitions().get(0);

        activity.setProperty("type", "intermediateSignalCatch");

        ExpressionManager expressionManager = bpmnParse.getExpressionManager();
        Expression signalExpression = expressionManager.createExpression(event.getCustomSignal());
        EventSubscriptionDeclaration eventSubscriptionDeclaration = new CustomSignalEventSubscriptionDeclaration(signalExpression);

        if (eventBasedGatewayId != null) {
            eventSubscriptionDeclaration.setActivityId(activity.getId());
            addEventSubscriptionDeclaration(bpmnParse, eventSubscriptionDeclaration, eventDefinition, activity.getParent());
        } else {
            activity.setScope(true);
            addEventSubscriptionDeclaration(bpmnParse, eventSubscriptionDeclaration, eventDefinition, activity);
        }

    }

}
