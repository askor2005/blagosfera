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
 * Список компонентов из группы "Intermediate Catching Events"
 */
@Component
@Order(500)
public class IntermidiateCathchingEventsStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil catchTimerEvent;
    private Stencil catchSignalEvent;
    private Stencil catchMessageEvent;

    @PostConstruct
    private void postConstruct() {
        catchTimerEvent = Stencil.builder()
                .id("CatchTimerEvent")
                .type(Stencil.NODE)
                .title("Intermediate timer catching event")
                .description("An intermediate catching event with a timer trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    \t\n    <circle \n    \tid=\"frame2_non_interrupting\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"12\" \n    \tstroke=\"#585858\" \n    \tfill=\"none\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 4.5, 3\" />\n    \n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <circle id=\"frame2\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    \n    <path id=\"path1\" transform=\"translate(6,6)\"\n    \td=\"M 10 0 C 4.4771525 0 0 4.4771525 0 10 C 0 15.522847 4.4771525 20 10 20 C 15.522847 20 20 15.522847 20 10 C 20 4.4771525 15.522847 1.1842379e-15 10 0 z M 9.09375 1.03125 C 9.2292164 1.0174926 9.362825 1.0389311 9.5 1.03125 L 9.5 3.5 L 10.5 3.5 L 10.5 1.03125 C 15.063526 1.2867831 18.713217 4.9364738 18.96875 9.5 L 16.5 9.5 L 16.5 10.5 L 18.96875 10.5 C 18.713217 15.063526 15.063526 18.713217 10.5 18.96875 L 10.5 16.5 L 9.5 16.5 L 9.5 18.96875 C 4.9364738 18.713217 1.2867831 15.063526 1.03125 10.5 L 3.5 10.5 L 3.5 9.5 L 1.03125 9.5 C 1.279102 5.0736488 4.7225326 1.4751713 9.09375 1.03125 z M 9.5 5 L 9.5 8.0625 C 8.6373007 8.2844627 8 9.0680195 8 10 C 8 11.104569 8.8954305 12 10 12 C 10.931981 12 11.715537 11.362699 11.9375 10.5 L 14 10.5 L 14 9.5 L 11.9375 9.5 C 11.756642 8.7970599 11.20294 8.2433585 10.5 8.0625 L 10.5 5 L 9.5 5 z \"  \n    \tfill=\"#585858\" stroke=\"none\" />\n    \t\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("catching/timer.png")
                .group("Intermediate Catching Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.timercycledefinitionpackage())
                .property(defaultProperties.timerdatedefinitionpackage())
                .property(defaultProperties.timerdurationdefinitionpackage())
                .roles(Arrays.asList("sequence_start", "sequence_end", "CatchEventsMorph", "all"))
                .build();

        catchSignalEvent = Stencil.builder()
                .id("CatchSignalEvent")
                .type(Stencil.NODE)
                .title("Intermediate signal catching event")
                .description("An intermediate catching event with a signal trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    \t\n    <circle \n    \tid=\"frame2_non_interrupting\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"12\" \n    \tstroke=\"#585858\" \n    \tfill=\"none\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 4.5, 3\" />\n    \n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <circle id=\"frame2\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n\t<path\n\t   id=\"signalCatching\"\n\t   stroke=\"#585858\"\n       d=\"M 8.7124971,21.247342 L 23.333334,21.247342 L 16.022915,8.5759512 L 8.7124971,21.247342 z\"\n       style=\"fill:none;stroke-width:1.4;stroke-miterlimit:4;stroke-dasharray:none\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
                .icon("catching/signal.png")
                .group("Intermediate Catching Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.signalrefpackage())
                .roles(Arrays.asList("sequence_start", "sequence_end", "CatchEventsMorph", "all"))
                .build();

        catchMessageEvent = Stencil.builder()
                .id("CatchMessageEvent")
                .type(Stencil.NODE)
                .title("Intermediate message catching event")
                .description("An intermediate catching event with a message trigger")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    \t\n    <circle \n    \tid=\"frame2_non_interrupting\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"12\" \n    \tstroke=\"#585858\" \n    \tfill=\"none\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 4.5, 3\" />\n    \t\n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <circle id=\"frame2\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    \n\t<g id=\"messageCatching\">\n\t\t<path transform=\"translate(7,7)\" id=\"path1\" stroke=\"none\" fill=\"#585858\" stroke-width=\"1\" d=\"M 1 3 L 9 11 L 17 3 L 1 3 z M 1 5 L 1 13 L 5 9 L 1 5 z M 17 5 L 13 9 L 17 13 L 17 5 z M 6 10 L 1 15 L 17 15 L 12 10 L 9 13 L 6 10 z \"/>\n\t</g>\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n\t\n  </g>\n</svg>")
                .icon("catching/message.png")
                .group("Intermediate Catching Events")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.messagerefpackage())
                .roles(Arrays.asList("sequence_start", "sequence_end", "CatchEventsMorph", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                catchTimerEvent,
                catchSignalEvent,
                catchMessageEvent
        );
    }
}
