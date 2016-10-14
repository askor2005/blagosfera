package ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules;

import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.rules.ContainmentRule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Стандартные правила {@link ContainmentRule}
 */
@Component
public class DefaultContainmentRules implements StencilRulesProvider<ContainmentRule> {

    private final ContainmentRule containmentRuleForBPMNDiagram = new ContainmentRule("BPMNDiagram", Arrays.asList("all"));

    private final ContainmentRule containmentRuleForSubProcess = new ContainmentRule("SubProcess", Arrays.asList(
            "sequence_start",
            "sequence_end",
            "from_task_event",
            "to_task_event",
            "EventSubProcess",
            "TextAnnotation",
            "DataStore"
    ));

    private final ContainmentRule containmentRuleForEventSubProcess = new ContainmentRule("EventSubProcess", Arrays.asList(
            "sequence_start",
            "sequence_end",
            "from_task_event",
            "to_task_event",
            "TextAnnotation",
            "DataStore"
    ));

    private final ContainmentRule containmentRuleForPool = new ContainmentRule("Pool", Arrays.asList("Lane"));

    private final ContainmentRule containmentRuleForLane = new ContainmentRule("Lane", Arrays.asList(
            "sequence_start",
            "sequence_end",
            "EventSubProcess",
            "TextAnnotation",
            "DataStore"
    ));


    @Override
    public List<ContainmentRule> getAll() {
        return Arrays.asList(
                containmentRuleForBPMNDiagram,
                containmentRuleForSubProcess,
                containmentRuleForEventSubProcess,
                containmentRuleForPool,
                containmentRuleForLane
        );
    }
}
