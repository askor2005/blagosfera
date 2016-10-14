package ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules;

import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.rules.Connect;
import ru.radom.blagosferabp.activiti.component.stencil.rules.ConnectionRule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Стандартные правила соединения {@link ConnectionRule}
 */
@Component
public class DefaultConnectionRules implements StencilRulesProvider<ConnectionRule> {

    private final ConnectionRule sequenceFlow = ConnectionRule.builder()
            .role("SequenceFlow")
            .connect(Connect.of("sequence_start", Arrays.asList("sequence_end")))
            .build();

    private final ConnectionRule association = ConnectionRule.builder()
            .role("Association")
            .connect(Connect.of("sequence_start", Arrays.asList("TextAnnotation")))
            .connect(Connect.of("sequence_end", Arrays.asList("TextAnnotation")))
            .connect(Connect.of("TextAnnotation", Arrays.asList("sequence_end")))
            .connect(Connect.of("BoundaryCompensationEvent", Arrays.asList("sequence_end")))
            .connect(Connect.of("TextAnnotation", Arrays.asList("sequence_start")))
            .connect(Connect.of("BoundaryCompensationEvent", Arrays.asList("sequence_start")))
            .build();


    private final ConnectionRule dataAssociation = ConnectionRule.builder()
            .role("DataAssociation")
            .connect(Connect.of("sequence_start", Arrays.asList("DataStore")))
            .connect(Connect.of("sequence_end", Arrays.asList("DataStore")))
            .connect(Connect.of("DataStore", Arrays.asList("sequence_end", "sequence_start")))
            .build();

    private final ConnectionRule intermediateEventOnActivityBoundary = ConnectionRule.builder()
            .role("IntermediateEventOnActivityBoundary")
            .connect(Connect.of("Activity", Arrays.asList("IntermediateEventOnActivityBoundary")))
            .build();

    @Override
    public List<ConnectionRule> getAll() {
        return Arrays.asList(
                sequenceFlow,
                association,
                dataAssociation,
                intermediateEventOnActivityBoundary
        );
    }
}
