package ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules;

import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.rules.CardinalityRule;
import ru.radom.blagosferabp.activiti.component.stencil.rules.EdgeOccurrence;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Провайдер стандартных {@link CardinalityRule}
 */
@Component
public class DefaultCardinalityRules implements StencilRulesProvider<CardinalityRule> {

    private final CardinalityRule startEventsAllCardinality = CardinalityRule.builder()
            .role("Startevents_all")
            .incomingEdge(EdgeOccurrence.forRole("SequenceFlow").setMaximum(0))
            .build();

    private final CardinalityRule endEventsAllCardinality = CardinalityRule.builder()
            .role("Endevents_all")
            .outgoingEdge(EdgeOccurrence.forRole("SequenceFlow").setMaximum(0))
            .build();

    @Override
    public List<CardinalityRule> getAll() {
        return Arrays.asList(startEventsAllCardinality, endEventsAllCardinality);
    }
}
