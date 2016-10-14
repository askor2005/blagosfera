package ru.radom.blagosferabp.activiti.custom.component.signal;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.engine.parse.BpmnParseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Otts Alexey on 30.11.2015.<br/>
 * {@link ModelBundle} для {@link CustomIntermediateCatchSignalEvent}
 */
@Component
public class CustomIntermediateCatchSignalEventBundle implements ModelBundle {

    public static final String CUSTOM_SIGNAL_EVENT = "customIntermediateCatchSignalEvent";

    public static final String CUSTOM_SIGNAL_ATTR = "custom-signal";

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil customIntermediateCatchSignalEvent;

    @PostConstruct
    private void postConstruct() {
        customIntermediateCatchSignalEvent = Stencil.builder()
            .id(CUSTOM_SIGNAL_EVENT)
            .type(Stencil.NODE)
            .title("Промежуточное ожидание вычислимого сигнала")
            .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   width=\"40\"\n   height=\"40\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"16\" oryx:cy=\"16\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <oryx:docker oryx:cx=\"16\" oryx:cy=\"16\" />\n  <g pointer-events=\"fill\">\n    <circle \n    \tid=\"bg_frame\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"15\" \n    \tstroke=\"#585858\" \n    \tfill=\"#ffffff\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 5.5, 3\" />\n    \t\n    <circle \n    \tid=\"frame2_non_interrupting\" \n    \tcx=\"16\" \n    \tcy=\"16\" \n    \tr=\"12\" \n    \tstroke=\"#585858\" \n    \tfill=\"none\" \n    \tstroke-width=\"1\"\n    \tstyle=\"stroke-dasharray: 4.5, 3\" />\n    \n    <circle id=\"frame\" cx=\"16\" cy=\"16\" r=\"15\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n    <circle id=\"frame2\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#58AA58\" fill=\"none\" stroke-width=\"1\"/>\n\t<path\n\t   id=\"signalCatching\"\n\t   stroke=\"#585858\"\n       d=\"M 8.7124971,21.247342 L 23.333334,21.247342 L 16.022915,8.5759512 L 8.7124971,21.247342 z\"\n       style=\"fill:none;stroke-width:1.4;stroke-miterlimit:4;stroke-dasharray:none\" />\n\t<text font-size=\"11\" \n\t\tid=\"text_name\" \n\t\tx=\"16\" y=\"33\" \n\t\toryx:align=\"top center\" \n\t\tstroke=\"#373e48\"\n\t></text>\n  </g>\n</svg>")
            .icon("catching/signal.png")
            .group("Intermediate Catching Events")
            .property(defaultProperties.overrideidpackage())
            .property(defaultProperties.namepackage())
            .property(defaultProperties.documentationpackage())
            .property(defaultProperties.executionlistenerspackage())
            .property(
                Property.builder()
                    .id("signalref")
                    .type(Property.STRING)
                    .value("dynamic")
                    .popular(false)
                    .build()
            )
            .property(
                Property.builder()
                    .id(CUSTOM_SIGNAL_ATTR)
                    .type(Property.STRING)
                    .title("Сигнал")
                    .value("")
                    .description("Сигнал на который нужно отреагировать. Можно использовать JUEL выражения.")
                    .popular(true)
                    .customEditor("./propertyEditor/ValidateTextEditor")
                    .build()
            )
            .roles(Arrays.asList("sequence_start", "sequence_end", "CatchEventsMorph", "all"))
            .build();
    }


    @Override
    public String getStencilId() {
        return CUSTOM_SIGNAL_EVENT;
    }

    @Override
    public Stencil getStencil() {
        return customIntermediateCatchSignalEvent;
    }

    @Override
    public ExtendedBaseBpmnJsonConverter getJsonConverter() {
        return null;
    }

    @Override
    public BaseBpmnXMLConverter getXMLConverter() {
        return null;
    }

    @Override
    public BpmnParseHandler getBpmnParseHandler() {
        return new CustomIntermediateCatchSignalEventParseHandler();
    }

    @Override
    public List<StencilRule> getRequiredRules() {
        return null;
    }
}
