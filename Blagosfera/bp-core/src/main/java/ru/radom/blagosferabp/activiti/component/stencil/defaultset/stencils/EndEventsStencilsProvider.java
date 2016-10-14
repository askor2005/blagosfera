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
 * Список компонентов из группы "End Events"
 */
@Component
@Order(700)
public class EndEventsStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil endNoneEvent;
    private Stencil endErrorEvent;
    private Stencil endCancelEvent;
    private Stencil endTerminateEvent;

    @PostConstruct
    private void postConstruct() {
        endNoneEvent = Stencil.builder()
                .id("EndNoneEvent")
                .type(Stencil.NODE)
                .title("End event")
                .description("An end event without a specific trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"14\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"3\"/>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"32\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("endevent/none.png")
                .group("End Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .roles(Arrays.asList("EndEventsMorph", "sequence_end", "all"))
                .build();

        endErrorEvent = Stencil.builder()
                .id("EndErrorEvent")
                .type(Stencil.NODE)
                .title("End error event")
                .description("An end event that throws an error event")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"14\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"3\"/>\n    \n    <path\n         fill=\"#585858\"\n         stroke=\"#585858\"\n         style=\"stroke-width:1.5;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:10\"\n         d=\"M 22.820839,11.171502 L 19.36734,24.58992 L 13.54138,14.281819 L 9.3386512,20.071607 L 13.048949,6.8323057 L 18.996148,16.132659 L 22.820839,11.171502 z\"\n         id=\"errorPolygon\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"32\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("endevent/error.png")
                .group("End Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.errorrefpackage())
                .roles(Arrays.asList("EndEventsMorph", "sequence_end", "all"))
                .build();

        endCancelEvent = Stencil.builder()
                .id("EndCancelEvent")
                .type(Stencil.NODE)
                .title("End cancel event")
                .description("A cancel end event")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"14\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"3\"/>\n    \n    <path id=\"path1\" d=\"M 9 9 L 23 23 M 9 23 L 23 9\" fill=\"none\" stroke=\"#585858\" stroke-width=\"5\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"32\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("endevent/cancel.png")
                .group("End Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .roles(Arrays.asList("EndEventsMorph", "sequence_end", "all"))
                .build();

        endTerminateEvent = Stencil.builder()
                .id("EndTerminateEvent")
                .type(Stencil.NODE)
                .title("End terminate event")
                .description("A terminate end event")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"14\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"3\"/>\n    \n    <circle id=\"circle1\" cx=\"16\" cy=\"16\" r=\"9\" stroke=\"#585858\" fill=\"#585858\" stroke-width=\"1\"/>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"32\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("endevent/terminate.png")
                .group("End Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .roles(Arrays.asList("EndEventsMorph", "sequence_end", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                endNoneEvent,
                endErrorEvent,
                endCancelEvent,
                endTerminateEvent
        );
    }
}
