package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.stencil.exchange.Layout;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Список компонентов из группы "Connecting Objects"
 */
@Component
public class ConnectingStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil sequenceFlow;
    private Stencil messageFlow;
    private Stencil association;
    private Stencil dataAssociation;

    @PostConstruct
    private void postConstruct() {
        sequenceFlow = Stencil.builder()
                .id("SequenceFlow")
                .type(Stencil.EDGE)
                .title("Sequence flow")
                .description("Sequence flow defines the execution order of activities.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<svg\r\n\txmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:oryx=\"http://www.b3mn.org/oryx\"\r\n\tversion=\"1.0\"\r\n\toryx:edge=\"edge\" >\r\n\t<defs>\r\n\t  \t<marker id=\"start\" refX=\"1\" refY=\"5\" markerUnits=\"userSpaceOnUse\" markerWidth=\"17\" markerHeight=\"11\" orient=\"auto\">\r\n\t  \t\t<!-- <path id=\"conditional\"   d=\"M 0 6 L 8 1 L 15 5 L 8 9 L 1 5\" fill=\"white\" stroke=\"black\" stroke-width=\"1\" /> -->\r\n\t\t\t<path id=\"default\" d=\"M 5 0 L 11 10\" fill=\"white\" stroke=\"#585858\" stroke-width=\"1\" />\r\n\t  \t</marker>\r\n\t  \t<marker id=\"end\" refX=\"15\" refY=\"6\" markerUnits=\"userSpaceOnUse\" markerWidth=\"15\" markerHeight=\"12\" orient=\"auto\">\r\n\t  \t\t<path id=\"arrowhead\" d=\"M 0 1 L 15 6 L 0 11z\" fill=\"#585858\" stroke=\"#585858\" stroke-linejoin=\"round\" stroke-width=\"2\" />\r\n\t  \t</marker>\r\n\t</defs>\r\n\t<g id=\"edge\">\r\n\t\t<path id=\"bg_frame\" d=\"M10 50 L210 50\" stroke=\"#585858\" fill=\"none\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" marker-start=\"url(#start)\" marker-end=\"url(#end)\" />\r\n\t\t<text id=\"text_name\" x=\"0\" y=\"0\" oryx:edgePosition=\"startTop\"/>\r\n\t</g>\r\n</svg>")
                .icon("connector/sequenceflow.png")
                .group("Connecting Objects")
                .layout(Layout.of("layout.bpmn2_0.sequenceflow"))
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.conditionsequenceflowpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.defaultflowpackage())
                .roles(Arrays.asList("ConnectingObjectsMorph", "all"))
                .build();

        messageFlow = Stencil.builder()
                .id("MessageFlow")
                .type(Stencil.EDGE)
                .title("Message flow")
                .description("Message flow to connect elements in different pools.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<svg\r\n\txmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:oryx=\"http://www.b3mn.org/oryx\"\r\n\tversion=\"1.0\"\r\n\toryx:edge=\"edge\" >\r\n\t<defs>\r\n\t\t<marker id=\"start\" oryx:optional=\"yes\" oryx:enabled=\"yes\" refX=\"5\" refY=\"5\" markerUnits=\"userSpaceOnUse\" markerWidth=\"10\" markerHeight=\"10\" orient=\"auto\">\r\n\t  \t\t<!-- <path d=\"M 10 10 L 0 5 L 10 0\" fill=\"none\" stroke=\"#585858\" /> -->\r\n\t  \t\t<circle id=\"arrowhead\" cx=\"5\" cy=\"5\" r=\"5\" fill=\"white\" stroke=\"black\" />\r\n\t  \t</marker>\r\n\r\n\t  \t<marker id=\"end\" refX=\"10\" refY=\"5\" markerUnits=\"userSpaceOnUse\" markerWidth=\"10\" markerHeight=\"10\" orient=\"auto\">\r\n\t  \t\t<path id=\"arrowhead2\" d=\"M 0 0 L 10 5 L 0 10 L 0 0\" fill=\"white\" stroke=\"#585858\" />\r\n\t  \t</marker>\r\n\t</defs>\r\n\t<g id=\"edge\">\r\n\t    <path id=\"bg_frame\" d=\"M10 50 L210 50\" stroke=\"#585858\" fill=\"none\" stroke-width=\"2\" stroke-dasharray=\"3, 4\" marker-start=\"url(#start)\" marker-end=\"url(#end)\" />\r\n\t\t<text id=\"text_name\" x=\"0\" y=\"0\" oryx:edgePosition=\"midTop\"/>\r\n\t</g>\r\n</svg>")
                .icon("connector/messageflow.png")
                .group("Connecting Objects")
                .layout(Layout.of("layout.bpmn2_0.sequenceflow"))
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .roles(Arrays.asList("ConnectingObjectsMorph", "all"))
                .build();

        association = Stencil.builder()
                .id("Association")
                .type(Stencil.EDGE)
                .title("Association")
                .description("Associates a text annotation with an element.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<svg\r\n\txmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:oryx=\"http://www.b3mn.org/oryx\"\r\n\tversion=\"1.0\"\r\n\toryx:edge=\"edge\" >\r\n\t<g id=\"edge\">\r\n\t    <path id=\"bg_frame\" d=\"M10 50 L210 50\" stroke=\"#585858\" fill=\"none\" stroke-width=\"2\" stroke-dasharray=\"3, 4\" />\r\n\t\t<text id=\"name\" x=\"0\" y=\"0\" oryx:edgePosition=\"midTop\" oryx:offsetTop=\"6\" style=\"font-size:9px;\"/>\r\n\t</g>\r\n</svg>")
                .icon("connector/association.undirected.png")
                .group("Connecting Objects")
                .layout(Layout.of("layout.bpmn2_0.sequenceflow"))
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .roles(Arrays.asList("ConnectingObjectsMorph", "all"))
                .build();

        dataAssociation = Stencil.builder()
                .id("DataAssociation")
                .type(Stencil.EDGE)
                .title("DataAssociation")
                .description("Associates a data element with an activity.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<svg\r\n\txmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:oryx=\"http://www.b3mn.org/oryx\"\r\n\tversion=\"1.0\"\r\n\toryx:edge=\"edge\" >\r\n\t<defs>\r\n\t  \t<marker id=\"end\" refX=\"10\" refY=\"5\" markerUnits=\"userSpaceOnUse\" markerWidth=\"10\" markerHeight=\"10\" orient=\"auto\">\r\n\t  \t\t<path id=\"arrowhead\" d=\"M 0 0 L 10 5 L 0 10\" fill=\"none\" stroke=\"#585858\" />\r\n\t  \t</marker>\r\n\t</defs>\r\n\t<g id=\"edge\">\r\n\t    <path id=\"bg_frame\" d=\"M10 50 L210 50\" stroke=\"#585858\" fill=\"none\" stroke-width=\"2\" stroke-dasharray=\"3, 4\" marker-end=\"url(#end)\" />\r\n\t\t<text id=\"name\" x=\"0\" y=\"0\" oryx:edgePosition=\"midTop\" oryx:offsetTop=\"6\" style=\"font-size:9px;\"/>\r\n\t</g>\r\n</svg>")
                .icon("connector/association.unidirectional.png")
                .group("Connecting Objects")
                .layout(Layout.of("layout.bpmn2_0.sequenceflow"))
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .roles(Arrays.asList("ConnectingObjectsMorph", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                sequenceFlow,
                messageFlow,
                association,
                dataAssociation
        );
    }
}
