package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Список компонентов из группы "Intermediate Throwing Events"
 */
@Component
@Order(600)
public class IntermidiateThrowingEventsStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil throwNoneEvent;
    private Stencil throwSignalEvent;

    @PostConstruct
    private void postConstruct() {
        throwNoneEvent = Stencil.builder()
                .id("ThrowNoneEvent")
                .type(Stencil.NODE)
                .title("Intermediate none throwing event")
                .description("An intermediate event without a specific trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n  \n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"1\"/>\n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("throwing/none.png")
                .group("Intermediate Throwing Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .roles(Arrays.asList("ThrowEventsMorph", "sequence_start", "sequence_end", "all"))
                .build();

        throwSignalEvent = Stencil.builder()
                .id("ThrowSignalEvent")
                .type(Stencil.NODE)
                .title("Intermediate signal throwing event")
                .description("An intermediate event with a signal trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"1\"/>\n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <path\n\t   id=\"signalThrowing\"\n       d=\"M 8.7124971,21.247342 L 23.333334,21.247342 L 16.022915,8.5759512 L 8.7124971,21.247342 z\"\n       fill=\"#585858\"\n       stroke=\"#585858\"\n       style=\"stroke-width:1.4;stroke-miterlimit:4;stroke-dasharray:none\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("throwing/signal.png")
                .group("Intermediate Throwing Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.signalrefpackage())
                .roles(Arrays.asList("ThrowEventsMorph", "sequence_start", "sequence_end", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                throwNoneEvent,
                throwSignalEvent
        );
    }
}
