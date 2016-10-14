package ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules;

import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.rules.MorphingRule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Правила преобразования компонентов
 */
@Component
public class DefaultMorhpingRules implements StencilRulesProvider<MorphingRule> {


    private final MorphingRule activitiesMorph = MorphingRule.builder()
            .role("ActivitiesMorph")
            .base("UserTask")
            .preserveBounds(true)
            .build();

    private final MorphingRule gatewaysMorph = MorphingRule.builder()
            .role("GatewaysMorph")
            .base("ExclusiveGateway")
            .build();

    private final MorphingRule startEventsMorph = MorphingRule.builder()
            .role("StartEventsMorph")
            .base("StartNoneEvent")
            .build();

    private final MorphingRule endEventsMorph = MorphingRule.builder()
            .role("EndEventsMorph")
            .base("StartNoneEvent")
            .build();

    private final MorphingRule catchEventsMorph = MorphingRule.builder()
            .role("CatchEventsMorph")
            .base("CatchTimerEvent")
            .build();

    private final MorphingRule throwEventsMorph = MorphingRule.builder()
            .role("ThrowEventsMorph")
            .base("ThrowNoneEvent")
            .build();

    private final MorphingRule boundaryEventsMorph = MorphingRule.builder()
            .role("BoundaryEventsMorph")
            .base("ThrowNoneEvent")
            .build();

    private final MorphingRule boundaryCompensationEvent = MorphingRule.builder()
            .role("BoundaryCompensationEvent")
            .base("BoundaryCompensationEvent")
            .build();

    private final MorphingRule textAnnotation = MorphingRule.builder()
            .role("TextAnnotation")
            .base("TextAnnotation")
            .build();

    private final MorphingRule dataStore = MorphingRule.builder()
            .role("DataStore")
            .base("DataStore")
            .build();


    @Override
    public List<MorphingRule> getAll() {
        return Arrays.asList(
                activitiesMorph,
                gatewaysMorph,
                startEventsMorph,
                endEventsMorph,
                catchEventsMorph,
                throwEventsMorph,
                boundaryEventsMorph,
                boundaryCompensationEvent,
                textAnnotation,
                dataStore
        );
    }
}
