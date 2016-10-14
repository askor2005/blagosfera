package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Набор стандартных компонентов
 */
@Component
public class DefaultStencils implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil BPMNDiagram;

    @PostConstruct
    private void postConstruct() {
        BPMNDiagram = Stencil.builder()
                .id("BPMNDiagram")
                .type(Stencil.NODE)
                .title("BPMN-Diagram")
                .description("A BPMN 2.0 diagram.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"800\"\n   height=\"600\"\n   version=\"1.0\">\n  <defs></defs>\n  <g pointer-events=\"fill\" >\n    <polygon stroke=\"black\" fill=\"black\" stroke-width=\"1\" points=\"0,0 0,590 9,599 799,599 799,9 790,0\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-miterlimit=\"10\" />\n    <rect id=\"diagramcanvas\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"790\" height=\"590\" stroke=\"black\" stroke-width=\"2\" fill=\"white\" />\n    \t<text font-size=\"22\" id=\"diagramtext\" x=\"400\" y=\"25\" oryx:align=\"top center\" stroke=\"#373e48\"></text>\n  </g>\n</svg>")
                .icon("diagram.png")
                .group("Diagram")
                .mayBeRoot(true)
                .hide(true)
                .property(defaultProperties.process_idpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.process_authorpackage())
                .property(defaultProperties.process_versionpackage())
                .property(defaultProperties.process_namespacepackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.eventlistenerspackage())
                .property(defaultProperties.signaldefinitionspackage())
                .property(defaultProperties.messagedefinitionspackage())
                .build();

    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(BPMNDiagram);
    }
}
