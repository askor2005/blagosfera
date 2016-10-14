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
 * Список компонентов из группы "Structural"
 */
@Component
@Order(200)
public class StructuralStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil subProcess;
    private Stencil eventSubProcess;
    private Stencil callActivity;

    @PostConstruct
    private void postConstruct() {
        subProcess = Stencil.builder()
                .id("SubProcess")
                .type(Stencil.NODE)
                .title("Sub process")
                .description("A sub process scope")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"200\"\n   height=\"160\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"50\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"80\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"110\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"70\" oryx:cy=\"159\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"159\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"130\" oryx:cy=\"159\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"199\" oryx:cy=\"50\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"199\" oryx:cy=\"80\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"199\" oryx:cy=\"110\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"70\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"130\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"80\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"120 100\" oryx:maximumSize=\"\" >\n    <rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"0\" y=\"0\" width=\"190\" height=\"160\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:anchors=\"bottom top right left\" x=\"0\" y=\"0\" width=\"200\" height=\"160\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#ffffff\" />\n\t<rect id=\"border\" oryx:anchors=\"top bottom left right\" oryx:resize=\"vertical horizontal\" x=\"2.5\" y=\"2.5\" width=\"195\" height=\"155\" rx=\"8\" ry=\"8\" stroke=\"black\" stroke-width=\"1\" fill=\"none\" />\n\t<text \n\t\tfont-size=\"12\" \n\t\tid=\"text_name\" \n\t\tx=\"8\" \n\t\ty=\"10\" \n\t\toryx:align=\"top left\"\n\t\toryx:fittoelem=\"text_frame\"\n\t\toryx:anchors=\"left top\" \n\t\tstroke=\"#373e48\">\n\t</text>\n\t\n\t<g \tid=\"parallel\"\n\t\ttransform=\"translate(1)\">\n\t\t<path \n\t\t\tid=\"parallelpath\"\n\t\t\toryx:anchors=\"bottom\" \n\t\t\tfill=\"none\" stroke=\"#bbbbbb\" d=\"M96 145 v10 M100 145 v10 M104 145 v10\" \n\t\t\tstroke-width=\"2\"\n\t\t/>\n\t</g>\n\t<g \tid=\"sequential\"\n\t\ttransform=\"translate(1)\">\n\t\t<path \n\t\t\tid=\"sequentialpath\"\n\t\t\toryx:anchors=\"bottom\" \n\t\t\tfill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M95,154h10 M95,150h10 M95,146h10\"\n\t\t/>\n\t</g>\n  </g>\n</svg>")
                .icon("activity/expanded.subprocess.png")
                .group("Structural")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.multiinstance_typepackage())
                .property(defaultProperties.multiinstance_cardinalitypackage())
                .property(defaultProperties.multiinstance_collectionpackage())
                .property(defaultProperties.multiinstance_variablepackage())
                .property(defaultProperties.multiinstance_conditionpackage())
                .property(defaultProperties.istransactionpackage())
                .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "all"))
                .build();

        eventSubProcess = Stencil.builder()
                .id("EventSubProcess")
                .type(Stencil.NODE)
                .title("Event sub process")
                .description("A event sub process scope")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"200\"\n   height=\"160\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"0\" oryx:cy=\"80\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"160\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"200\" oryx:cy=\"80\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"0\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"100\" oryx:cy=\"80\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"120 100\" oryx:maximumSize=\"\" >\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"0\" y=\"0\" width=\"190\" height=\"160\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:anchors=\"bottom top right left\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"200\" height=\"160\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" stroke-dasharray=\"2,2,2\" fill=\"#ffffff\" />\n    \t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"8\" \n\t\t\ty=\"10\" \n\t\t\toryx:align=\"top left\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\toryx:anchors=\"left top\" \n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \t\n\t<g id=\"none\"></g>\n\t\n  </g>\n</svg>")
                .icon("activity/event.subprocess.png")
                .group("Structural")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.executionlistenerspackage())
                .roles(Arrays.asList("Activity", "all"))
                .build();

        callActivity = Stencil.builder()
                .id("CallActivity")
                .type(Stencil.NODE)
                .title("Call activity")
                .description("A call activity")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n    <rect oryx:resize=\"vertical horizontal\" oryx:anchors=\"bottom top right left\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"4\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                .icon("activity/task.png")
                .group("Structural")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.executionlistenerspackage())
                .property(defaultProperties.callactivitycalledelementpackage())
                .property(defaultProperties.callactivityinparameterspackage())
                .property(defaultProperties.callactivityoutparameterspackage())
                .property(defaultProperties.multiinstance_typepackage())
                .property(defaultProperties.multiinstance_cardinalitypackage())
                .property(defaultProperties.multiinstance_collectionpackage())
                .property(defaultProperties.multiinstance_variablepackage())
                .property(defaultProperties.multiinstance_conditionpackage())
                .property(defaultProperties.isforcompensationpackage())
                .roles(Arrays.asList("Activity", "ActivitiesMorph", "sequence_start", "sequence_end", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                subProcess,
                eventSubProcess,
                callActivity
        );
    }
}
