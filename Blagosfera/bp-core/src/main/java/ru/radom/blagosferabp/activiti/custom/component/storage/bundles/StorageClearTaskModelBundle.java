package ru.radom.blagosferabp.activiti.custom.component.storage.bundles;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractActivityBpmnParseHandler;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.parse.BpmnParseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.custom.component.storage.StorageClearTask;
import ru.radom.blagosferabp.activiti.custom.component.storage.behaviours.StorageClearTaskBehaviour;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * {@link ModelBundle} для {@link StorageClearTask}
 */
@Component
public class StorageClearTaskModelBundle implements ModelBundle {

    public final static String STORAGE_CLEAR_TASK = "storageClearTask";

    public final static String KEYS_TO_CLEAR_PROPERTY = "values_to_clear";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil storageClearTaskStencil;

    @PostConstruct
    private void postConstruct() {
        storageClearTaskStencil = Stencil.builder()
            .id(getStencilId())
            .type(Stencil.NODE)
            .group("Хранилище")
            .title("Удалить значения")
            .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n   xmlns:cc=\"http://creativecommons.org/ns#\"\n   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"\n   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n    <oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\"/>\n    <oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\"/>\n    <oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\"/>\n    <oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\"/>\n    <oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\"/>\n    <oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\"/>\n    <oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\"/>\n    <oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\"/>\n    <oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\"/>\n    <oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\"/>\n    <oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\"/>\n    <oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\"/>\n    <oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\"/>\n  </oryx:magnets>\n  <g\n     pointer-events=\"fill\"\n     oryx:minimumSize=\"50 40\"\n     id=\"g3565\">\n    <defs\n       id=\"defs3567\">\n      <radialGradient\n         id=\"background\"\n         cx=\"10%\"\n         cy=\"10%\"\n         r=\"100%\"\n         fx=\"10%\"\n         fy=\"10%\">\n        <stop\n           offset=\"0%\"\n           stop-color=\"#ffffff\"\n           stop-opacity=\"1\"\n           id=\"stop3570\" />\n        <stop\n           id=\"fill_el\"\n           offset=\"100%\"\n           stop-color=\"#ffffcc\"\n           stop-opacity=\"1\" />\n      </radialGradient>\n    </defs>\n    <rect\n       id=\"text_frame\"\n       oryx:anchors=\"bottom top right left\"\n       x=\"1\"\n       y=\"1\"\n       width=\"94\"\n       height=\"79\"\n       rx=\"10\"\n       ry=\"10\"\n       stroke-width=\"0\"\n       stroke=\"none\"\n       fill=\"none\" />\n    <rect\n       id=\"bg_frame\"\n       oryx:resize=\"vertical horizontal\"\n       x=\"0\"\n       y=\"0\"\n       width=\"100\"\n       height=\"80\"\n       rx=\"10\"\n       ry=\"10\"\n       stroke-width=\"1\"\n       stroke=\"#bbbbbb\"\n       fill=\"#f9f9f9\" />\n    <text\n       font-size=\"12\"\n       id=\"text_name\"\n       x=\"50\"\n       y=\"40\"\n       oryx:align=\"middle center\"\n       oryx:fittoelem=\"text_frame\"\n       stroke=\"#373e48\" />\n\n    <g\n     transform=\"matrix(0.45438294,0,0,0.45438294,3.1590313,3.023305)\"\n     id=\"data\">\n    <path\n       oryx:anchors=\"top left\"\n       inkscape:connector-curvature=\"0\"\n       style=\"fill:#010002\"\n       d=\"M 16,0 C 9.256,0 2,2.033 2,6.5 l 0,19 c 0,4.465 7.256,6.5 14,6.5 6.743,0 14,-2.035 14,-6.5 l 0,-19 C 30,2.033 22.742,0 16,0 Z M 28,25.5 C 28,27.984 22.627,30 16,30 9.372,30 4,27.984 4,25.5 L 4,21.764 C 6.066,23.893 11.05,25 16,25 c 4.95,0 9.934,-1.107 12,-3.236 l 0,3.736 z m 0,-6 -0.004,0 C 27.996,19.51 28,19.521 28,19.531 28,22 22.627,24 16,24 9.373,24 4,22 4,19.531 4,19.521 4.004,19.51 4.004,19.5 L 4,19.5 4,15.764 C 6.066,17.893 11.05,19 16,19 c 4.95,0 9.934,-1.107 12,-3.236 l 0,3.736 z m 0,-6 -0.004,0 C 27.996,13.51 28,13.521 28,13.531 28,16 22.627,18 16,18 9.373,18 4,16 4,13.531 4,13.521 4.004,13.51 4.004,13.5 L 4,13.5 4,10.064 C 6.621,12.061 11.425,13 16,13 c 4.575,0 9.379,-0.939 12,-2.936 L 28,13.5 Z M 16,11 C 9.372,11 4,8.984 4,6.5 4,4.014 9.372,2 16,2 22.627,2 28,4.014 28,6.5 28,8.984 22.627,11 16,11 Z\"\n       id=\"path6\" />\n    <circle\n       oryx:anchors=\"top left\"\n       style=\"fill:#010002\"\n       cx=\"25\"\n       cy=\"26\"\n       r=\"1\"\n       id=\"circle8\" />\n    <circle\n       oryx:anchors=\"top left\"\n       style=\"fill:#010002\"\n       cx=\"25\"\n       cy=\"20\"\n       r=\"1\"\n       id=\"circle10\" />\n    <circle\n       oryx:anchors=\"top left\"\n       style=\"fill:#010002\"\n       cx=\"25\"\n       cy=\"14\"\n       r=\"1\"\n       id=\"circle12\" />\n  </g>\n  <path\n     oryx:anchors=\"top left\"\n     style=\"fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"\n     d=\"M 3.8771551,2.3857759 17.877156,18.385776 10.908935,10.330692 17.877156,2.3857759 3.8771551,18.385776 10.908936,10.243827 Z\"\n     id=\"path3689\"\n     inkscape:connector-curvature=\"0\"\n     sodipodi:nodetypes=\"ccccccc\" />\n        \n    <!-- <g id=\"parallel\">\n        <path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n    </g>\n     \n    <g id=\"sequential\">\n        <path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n    </g>\n    <g id=\"compensation\">\n        <path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n    </g> -->\n\n\n  </g>\n  \n</svg>\n")
            .icon("storage/clear.png")
            .property(defaultProperties.overrideidpackage())
            .property(defaultProperties.namepackage())
            .property(
                Property.builder()
                    .id(KEYS_TO_CLEAR_PROPERTY)
                    .title("Значения")
                    .description("" +
                        "Значения, которые нужно удалить из хранилища. " +
                        "Если будет передан список, то удалятся все ключи из этого списка." +
                        "Если Map - удаляются значения которые хранятся в ключах.")
                    .type(Property.MULTIPLECOMPLEX)
                    .optional(false)
                    .popular(true)
                    .build()
            )
            .property(defaultProperties.multiinstance_typepackage())
            .property(defaultProperties.multiinstance_cardinalitypackage())
            .property(defaultProperties.multiinstance_collectionpackage())
            .property(defaultProperties.multiinstance_variablepackage())
            .property(defaultProperties.multiinstance_conditionpackage())
            .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
            .build();
    }

    @Override
    public String getStencilId() {
        return STORAGE_CLEAR_TASK;
    }

    @Override
    public Stencil getStencil() {
        return storageClearTaskStencil;
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
        return new AbstractActivityBpmnParseHandler<StorageClearTask>() {
            @Override
            protected Class<? extends BaseElement> getHandledType() {
                return StorageClearTask.class;
            }

            @Override
            protected void executeParse(BpmnParse bpmnParse, StorageClearTask element) {
                ActivityImpl activity = createActivityOnCurrentScope(bpmnParse, element, BpmnXMLConstants.ELEMENT_TASK_SERVICE);
                ExpressionManager expressionManager = bpmnParse.getExpressionManager();
                List<Expression> keys = element.getKeys().stream()
                    .map(expressionManager::createExpression)
                    .collect(Collectors.toList());
                StorageClearTaskBehaviour behaviour = applicationContext.getBean(StorageClearTaskBehaviour.class, keys);
                activity.setActivityBehavior(behaviour);
            }
        };
    }

    @Override
    public List<StencilRule> getRequiredRules() {
        return null;
    }
}
