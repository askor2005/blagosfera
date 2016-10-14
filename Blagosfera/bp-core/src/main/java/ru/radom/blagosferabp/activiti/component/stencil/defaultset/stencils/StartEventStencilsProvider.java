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
 * Компоненты начала процесса
 */
@Component
@Order(0)
public class StartEventStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil startNoneEvent;
    private Stencil startTimerEvent;
    private Stencil startSignalEvent;
    private Stencil startMessageEvent;
    private Stencil startErrorEvent;

    @PostConstruct
    private void postConstruct() {
        startNoneEvent = Stencil.builder()
                .id("StartNoneEvent")
                .type(Stencil.NODE)
                .title("Start event")
                .description("A start event without a specific trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"1\"/>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("startevent/none.png")
                .group("Start Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.initiatorpackage())
                .property(defaultProperties.formkeydefinitionpackage())
                .property(defaultProperties.formpropertiespackage())
                .roles(Arrays.asList("sequence_start", "Startevents_all", "StartEventsMorph", "all"))
                .build();

        startTimerEvent = Stencil.builder()
                .id("StartTimerEvent")
                .type(Stencil.NODE)
                .title("Start timer event")
                .description("A start event with a timer trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    \n    <path id=\"path1\" transform=\"translate(6,6)\"\n    \td=\"M 10 0 C 4.4771525 0 0 4.4771525 0 10 C 0 15.522847 4.4771525 20 10 20 C 15.522847 20 20 15.522847 20 10 C 20 4.4771525 15.522847 1.1842379e-15 10 0 z M 9.09375 1.03125 C 9.2292164 1.0174926 9.362825 1.0389311 9.5 1.03125 L 9.5 3.5 L 10.5 3.5 L 10.5 1.03125 C 15.063526 1.2867831 18.713217 4.9364738 18.96875 9.5 L 16.5 9.5 L 16.5 10.5 L 18.96875 10.5 C 18.713217 15.063526 15.063526 18.713217 10.5 18.96875 L 10.5 16.5 L 9.5 16.5 L 9.5 18.96875 C 4.9364738 18.713217 1.2867831 15.063526 1.03125 10.5 L 3.5 10.5 L 3.5 9.5 L 1.03125 9.5 C 1.279102 5.0736488 4.7225326 1.4751713 9.09375 1.03125 z M 9.5 5 L 9.5 8.0625 C 8.6373007 8.2844627 8 9.0680195 8 10 C 8 11.104569 8.8954305 12 10 12 C 10.931981 12 11.715537 11.362699 11.9375 10.5 L 14 10.5 L 14 9.5 L 11.9375 9.5 C 11.756642 8.7970599 11.20294 8.2433585 10.5 8.0625 L 10.5 5 L 9.5 5 z \"  \n    \tfill=\"#585858\" stroke=\"none\" />\n   \n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("startevent/timer.png")
                .group("Start Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.timercycledefinitionpackage())
                .property(defaultProperties.timerdatedefinitionpackage())
                .property(defaultProperties.timerdurationdefinitionpackage())
                .roles(Arrays.asList("sequence_start", "Startevents_all", "StartEventsMorph", "all"))
                .build();

        startSignalEvent = Stencil.builder()
                .id("StartSignalEvent")
                .type(Stencil.NODE)
                .title("Start signal event")
                .description("A start event with a signal trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <path\n       d=\"M 8.7124971,21.247342 L 23.333334,21.247342 L 16.022915,8.5759512 L 8.7124971,21.247342 z\"\n       id=\"triangle\"\n       stroke=\"#585858\"\n       style=\"fill:none;stroke-width:1.4;stroke-miterlimit:4;stroke-dasharray:none\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("startevent/signal.png")
                .group("Start Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.signalrefpackage())
                .roles(Arrays.asList("sequence_start", "Startevents_all", "StartEventsMorph", "all"))
                .build();

        startMessageEvent = Stencil.builder()
                .id("StartMessageEvent")
                .type(Stencil.NODE)
                .title("Start message event")
                .description("A start event with a message trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    \n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    \n    <path transform=\"translate(7,7)\" id=\"path1\" stroke=\"none\" fill=\"#585858\" stroke-width=\"1\" d=\"m 0.5,2.5 0,13 17,0 0,-13 z M 2,4 6.5,8.5 2,13 z M 4,4 14,4 9,9 z m 12,0 0,9 -4.5,-4.5 z M 7.5,9.5 9,11 10.5,9.5 15,14 3,14 z\"/>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("startevent/message.png")
                .group("Start Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.messagerefpackage())
                .roles(Arrays.asList("sequence_start", "Startevents_all", "StartEventsMorph", "all"))
                .build();

        startErrorEvent = Stencil.builder()
                .id("StartErrorEvent")
                .type(Stencil.NODE)
                .title("Start error event")
                .description("A start event that catches a thrown BPMN error")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle id=\"bg_frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"#ffffff\" stroke-width=\"1\"/>\n    \n    <path\n         stroke=\"#585858\"\n         style=\"fill:none;stroke-width:1.5;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:10\"\n         d=\"M 22.820839,11.171502 L 19.36734,24.58992 L 13.54138,14.281819 L 9.3386512,20.071607 L 13.048949,6.8323057 L 18.996148,16.132659 L 22.820839,11.171502 z\"\n         id=\"errorPolygon\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("startevent/error.png")
                .group("Start Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.errorrefpackage())
                .roles(Arrays.asList("sequence_start", "Startevents_all", "StartEventsMorph", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                startNoneEvent,
                startTimerEvent,
                startSignalEvent,
                startMessageEvent,
                startErrorEvent
        );
    }
}
