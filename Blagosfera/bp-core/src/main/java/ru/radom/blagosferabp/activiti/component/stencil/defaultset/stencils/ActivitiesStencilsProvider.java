package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Список компонентов из группы "Activities"
 */
//@Component //TODO убран пока что за ненадобностью
@Order(100)
public class ActivitiesStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil userTask;
    private Stencil serviceTask;
    private Stencil scriptTask;
    private Stencil businessRule;
    private Stencil receiveTask;
    private Stencil manualTask;
    private Stencil mailTask;
    private Stencil camelTask;
    private Stencil muleTask;
    private Stencil sendTask;

    @PostConstruct
    private void postConstruct() {
            userTask = Stencil.builder()
                    .id("UserTask")
                    .type(Stencil.NODE)
                    .title("User task")
                    .description("A manual task assigned to a specific person")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"userTask\" transform=\"translate(3,3)\">\n\t\t<path oryx:anchors=\"top left\"\n       \t\tstyle=\"fill:#d1b575;stroke:none;\"\n       \t\t d=\"m 1,17 16,0 0,-1.7778 -5.333332,-3.5555 0,-1.7778 c 1.244444,0 1.244444,-2.3111 1.244444,-2.3111 l 0,-3.0222 C 12.555557,0.8221 9.0000001,1.0001 9.0000001,1.0001 c 0,0 -3.5555556,-0.178 -3.9111111,3.5555 l 0,3.0222 c 0,0 0,2.3111 1.2444443,2.3111 l 0,1.7778 L 1,15.2222 1,17 17,17\" \n         />\n\t\t\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.user.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.usertaskassignmentpackage())
                    .property(defaultProperties.formkeydefinitionpackage())
                    .property(defaultProperties.duedatedefinitionpackage())
                    .property(defaultProperties.prioritydefinitionpackage())
                    .property(defaultProperties.formpropertiespackage())
                    .property(defaultProperties.tasklistenerspackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            serviceTask = Stencil.builder()
                    .id("ServiceTask")
                    .type(Stencil.NODE)
                    .title("Service task")
                    .description("An automatic task with service logic")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"serviceTask\" transform=\"translate(3,3)\">\n\t<path oryx:anchors=\"top left\"\n\t\tstyle=\"fill:#72a7d0;stroke:none\"\n     d=\"M 8,1 7.5,2.875 c 0,0 -0.02438,0.250763 -0.40625,0.4375 C 7.05724,3.330353 7.04387,3.358818 7,3.375 6.6676654,3.4929791 6.3336971,3.6092802 6.03125,3.78125 6.02349,3.78566 6.007733,3.77681 6,3.78125 5.8811373,3.761018 5.8125,3.71875 5.8125,3.71875 l -1.6875,-1 -1.40625,1.4375 0.96875,1.65625 c 0,0 0.065705,0.068637 0.09375,0.1875 0.002,0.00849 -0.00169,0.022138 0,0.03125 C 3.6092802,6.3336971 3.4929791,6.6676654 3.375,7 3.3629836,7.0338489 3.3239228,7.0596246 3.3125,7.09375 3.125763,7.4756184 2.875,7.5 2.875,7.5 L 1,8 l 0,2 1.875,0.5 c 0,0 0.250763,0.02438 0.4375,0.40625 0.017853,0.03651 0.046318,0.04988 0.0625,0.09375 0.1129372,0.318132 0.2124732,0.646641 0.375,0.9375 -0.00302,0.215512 -0.09375,0.34375 -0.09375,0.34375 L 2.6875,13.9375 4.09375,15.34375 5.78125,14.375 c 0,0 0.1229911,-0.09744 0.34375,-0.09375 0.2720511,0.147787 0.5795915,0.23888 0.875,0.34375 0.033849,0.01202 0.059625,0.05108 0.09375,0.0625 C 7.4756199,14.874237 7.5,15.125 7.5,15.125 L 8,17 l 2,0 0.5,-1.875 c 0,0 0.02438,-0.250763 0.40625,-0.4375 0.03651,-0.01785 0.04988,-0.04632 0.09375,-0.0625 0.332335,-0.117979 0.666303,-0.23428 0.96875,-0.40625 0.177303,0.0173 0.28125,0.09375 0.28125,0.09375 l 1.65625,0.96875 1.40625,-1.40625 -0.96875,-1.65625 c 0,0 -0.07645,-0.103947 -0.09375,-0.28125 0.162527,-0.290859 0.262063,-0.619368 0.375,-0.9375 0.01618,-0.04387 0.04465,-0.05724 0.0625,-0.09375 C 14.874237,10.52438 15.125,10.5 15.125,10.5 L 17,10 17,8 15.125,7.5 c 0,0 -0.250763,-0.024382 -0.4375,-0.40625 C 14.669647,7.0572406 14.641181,7.0438697 14.625,7 14.55912,6.8144282 14.520616,6.6141566 14.4375,6.4375 c -0.224363,-0.4866 0,-0.71875 0,-0.71875 L 15.40625,4.0625 14,2.625 l -1.65625,1 c 0,0 -0.253337,0.1695664 -0.71875,-0.03125 l -0.03125,0 C 11.405359,3.5035185 11.198648,3.4455201 11,3.375 10.95613,3.3588185 10.942759,3.3303534 10.90625,3.3125 10.524382,3.125763 10.5,2.875 10.5,2.875 L 10,1 8,1 z m 1,5 c 1.656854,0 3,1.3431458 3,3 0,1.656854 -1.343146,3 -3,3 C 7.3431458,12 6,10.656854 6,9 6,7.3431458 7.3431458,6 9,6 z\" />\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.service.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.servicetaskclasspackage())
                    .property(defaultProperties.servicetaskexpressionpackage())
                    .property(defaultProperties.servicetaskdelegateexpressionpackage())
                    .property(defaultProperties.servicetaskfieldspackage())
                    .property(defaultProperties.servicetaskresultvariablepackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            scriptTask = Stencil.builder()
                    .id("ScriptTask")
                    .type(Stencil.NODE)
                    .title("Script task")
                    .description("An automatic task with script logic")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"scriptTask\" transform=\"translate(2,2)\">\n\t\t<path oryx:anchors=\"top left\"\n\t\t\td=\"m 5,2 0,0.094 c 0.23706,0.064 0.53189,0.1645 0.8125,0.375 0.5582,0.4186 1.05109,1.228 1.15625,2.5312 l 8.03125,0 1,0 1,0 c 0,-3 -2,-3 -2,-3 l -10,0 z M 4,3 4,13 2,13 c 0,3 2,3 2,3 l 9,0 c 0,0 2,0 2,-3 L 15,6 6,6 6,5.5 C 6,4.1111 5.5595,3.529 5.1875,3.25 4.8155,2.971 4.5,3 4.5,3 L 4,3 z\"\n     \t\tstyle=\"fill:#72a7d0;stroke:none\"\n\t\t/>\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.script.png")
                    .group("Activities")
                    .property(defaultProperties.scriptformatpackage())
                    .property(defaultProperties.scripttextpackage())
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
                    .property(defaultProperties.isforcompensationpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            businessRule = Stencil.builder()
                    .id("BusinessRule")
                    .type(Stencil.NODE)
                    .title("Business rule task")
                    .description("An automatic task with rule logic")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n  \t<defs>\n\t\t<radialGradient id=\"background\" cx=\"10%\" cy=\"10%\" r=\"100%\" fx=\"10%\" fy=\"10%\">\n\t\t\t<stop offset=\"0%\" stop-color=\"#ffffff\" stop-opacity=\"1\"/>\n\t\t\t<stop id=\"fill_el\" offset=\"100%\" stop-color=\"#ffffcc\" stop-opacity=\"1\"/>\n\t\t</radialGradient>\n\t</defs>\n\t\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \n\t<g id=\"businessRuleTask\" transform=\"translate(4,3)\">\n\t\t<path oryx:anchors=\"top left\" \n\t\t\t d=\"m 1,2 0,14 16,0 0,-14 z m 1.45458,5.6000386 2.90906,0 0,2.7999224 -2.90906,0 z m 4.36364,0 8.72718,0 0,2.7999224 -8.72718,0 z m -4.36364,4.1998844 2.90906,0 0,2.800116 -2.90906,0 z m 4.36364,0 8.72718,0 0,2.800116 -8.72718,0 z\"\n     \t\tstyle=\"fill:#72a7d0;stroke:none\"\n\t\t/>\n\t</g>\n\t\n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.business.rule.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.ruletask_rulespackage())
                    .property(defaultProperties.ruletask_variables_inputpackage())
                    .property(defaultProperties.ruletask_excludepackage())
                    .property(defaultProperties.ruletask_resultpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            receiveTask = Stencil.builder()
                    .id("ReceiveTask")
                    .type(Stencil.NODE)
                    .title("Receive task")
                    .description("An task that waits for a signal")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \n\t<g id=\"receiveTask\" transform=\"translate(4,3)\">\n\t\t<path oryx:anchors=\"left top\" \n\t\t\t style=\"fill:#16964d;stroke:none;\"\n     \t\t d=\"m 0.5,2.5 0,13 17,0 0,-13 z M 2,4 6.5,8.5 2,13 z M 4,4 14,4 9,9 z m 12,0 0,9 -4.5,-4.5 z M 7.5,9.5 9,11 10.5,9.5 15,14 3,14 z\"\n\t\t />\n\t</g>\n\t\n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.receive.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            manualTask = Stencil.builder()
                    .id("ManualTask")
                    .type(Stencil.NODE)
                    .title("Manual task")
                    .description("An automatic task with no logic")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    <g id=\"manualTask\" transform=\"translate(3,1)\">\n    \t<path oryx:anchors=\"top left\"\n    \t\tstyle=\"fill:#d1b575;stroke=none\"\n     \t\td=\"m 17,9.3290326 c -0.0069,0.5512461 -0.455166,1.0455894 -0.940778,1.0376604 l -5.792746,0 c 0.0053,0.119381 0.0026,0.237107 0.0061,0.355965 l 5.154918,0 c 0.482032,-0.0096 0.925529,0.49051 0.919525,1.037574 -0.0078,0.537128 -0.446283,1.017531 -0.919521,1.007683 l -5.245273,0 c -0.01507,0.104484 -0.03389,0.204081 -0.05316,0.301591 l 2.630175,0 c 0.454137,-0.0096 0.872112,0.461754 0.866386,0.977186 C 13.619526,14.554106 13.206293,15.009498 12.75924,15 L 3.7753054,15 C 3.6045812,15 3.433552,14.94423 3.2916363,14.837136 c -0.00174,0 -0.00436,0 -0.00609,0 C 1.7212035,14.367801 0.99998255,11.458641 1,11.458641 L 1,7.4588393 c 0,0 0.6623144,-1.316333 1.8390583,-2.0872584 1.1767614,-0.7711868 6.8053358,-2.40497 7.2587847,-2.8052901 0.453484,-0.40032 1.660213,1.4859942 0.04775,2.4010487 C 8.5332315,5.882394 8.507351,5.7996113 8.4370292,5.7936859 l 6.3569748,-0.00871 c 0.497046,-0.00958 0.952273,0.5097676 0.94612,1.0738232 -0.0053,0.556126 -0.456176,1.0566566 -0.94612,1.0496854 l -4.72435,0 c 0.01307,0.1149374 0.0244,0.2281319 0.03721,0.3498661 l 5.952195,0 c 0.494517,-0.00871 0.947906,0.5066305 0.940795,1.0679848 z\"\n    \t/>\n\t</g>\n\t\n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.manual.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            mailTask = Stencil.builder()
                    .id("MailTask")
                    .type(Stencil.NODE)
                    .title("Mail task")
                    .description("An mail task")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \n\t<g id=\"sendTask\" transform=\"translate(4,3)\">\n\t\n\t<!-- path here -->\n\t\t<path oryx:anchors=\"top left\"\n\t\t\tstyle=\"fill:#16964d;stroke:none;\"\n     \t\td=\"M 1 3 L 9 11 L 17 3 L 1 3 z M 1 5 L 1 13 L 5 9 L 1 5 z M 17 5 L 13 9 L 17 13 L 17 5 z M 6 10 L 1 15 L 17 15 L 12 10 L 9 13 L 6 10 z \"\n     \t/>\n\t</g>\n\t\n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.send.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.mailtasktopackage())
                    .property(defaultProperties.mailtaskfrompackage())
                    .property(defaultProperties.mailtasksubjectpackage())
                    .property(defaultProperties.mailtaskccpackage())
                    .property(defaultProperties.mailtaskbccpackage())
                    .property(defaultProperties.mailtasktextpackage())
                    .property(defaultProperties.mailtaskhtmlpackage())
                    .property(defaultProperties.mailtaskcharsetpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            camelTask = Stencil.builder()
                    .id("CamelTask")
                    .type(Stencil.NODE)
                    .title("Camel task")
                    .description("An task that sends a message to Camel")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"camelTask\" transform=\"translate(4,4)\">\n\t\t<path\n     style=\"fill:#bd4848;fill-opacity:1\"\n     d=\"m 8.1878027,15.383782 c -0.824818,-0.3427 0.375093,-1.1925 0.404055,-1.7743 0.230509,-0.8159 -0.217173,-1.5329 -0.550642,-2.2283 -0.106244,-0.5273 -0.03299,-1.8886005 -0.747194,-1.7818005 -0.712355,0.3776 -0.9225,1.2309005 -1.253911,1.9055005 -0.175574,1.0874 -0.630353,2.114 -0.775834,3.2123 -0.244009,0.4224 -1.741203,0.3888 -1.554386,-0.1397 0.651324,-0.3302 1.13227,-0.9222 1.180246,-1.6705 0.0082,-0.7042 -0.133578,-1.3681 0.302178,-2.0083 0.08617,-0.3202 0.356348,-1.0224005 -0.218996,-0.8051 -0.694517,0.2372 -1.651062,0.6128 -2.057645,-0.2959005 -0.696769,0.3057005 -1.102947,-0.611 -1.393127,-1.0565 -0.231079,-0.6218 -0.437041,-1.3041 -0.202103,-1.9476 -0.185217,-0.7514 -0.39751099,-1.5209 -0.35214999,-2.301 -0.243425,-0.7796 0.86000899,-1.2456 0.08581,-1.8855 -0.76078999,0.1964 -1.41630099,-0.7569 -0.79351899,-1.2877 0.58743,-0.52829998 1.49031699,-0.242 2.09856399,-0.77049998 0.816875,-0.3212 1.256619,0.65019998 1.923119,0.71939998 0.01194,0.7333 -0.0031,1.5042 -0.18417,2.2232 -0.194069,0.564 -0.811196,1.6968 0.06669,1.9398 0.738382,-0.173 1.095723,-0.9364 1.659041,-1.3729 0.727298,-0.3962 1.093982,-1.117 1.344137,-1.8675 0.400558,-0.8287 1.697676,-0.6854 1.955367,0.1758 0.103564,0.5511 0.9073983,1.7538 1.2472763,0.6846 0.121868,-0.6687 0.785541,-1.4454 1.518183,-1.0431 0.813587,0.4875 0.658233,1.6033 1.285504,2.2454 0.768715,0.8117 1.745394,1.4801 2.196633,2.5469 0.313781,0.8074 0.568552,1.707 0.496624,2.5733 -0.35485,0.8576005 -1.224508,-0.216 -0.64725,-0.7284 0.01868,-0.3794 -0.01834,-1.3264 -0.370249,-1.3272 -0.123187,0.7586 -0.152778,1.547 -0.10869,2.3154 0.270285,0.6662005 1.310741,0.7653005 1.060553,1.6763005 -0.03493,0.9801 0.294343,1.9505 0.148048,2.9272 -0.320479,0.2406 -0.79575,0.097 -1.185062,0.1512 -0.165725,0.3657 -0.40138,0.921 -1.020848,0.6744 -0.564671,0.1141 -1.246404,-0.266 -0.578559,-0.7715 0.679736,-0.5602 0.898618,-1.5362 0.687058,-2.3673 -0.529674,-1.108 -1.275984,-2.0954005 -1.839206,-3.1831005 -0.634619,-0.1004 -1.251945,0.6779 -1.956789,0.7408 -0.6065893,-0.038 -1.0354363,-0.06 -0.8495673,0.6969005 0.01681,0.711 0.152396,1.3997 0.157345,2.1104 0.07947,0.7464 0.171287,1.4944 0.238271,2.2351 0.237411,1.0076 -0.687542,1.1488 -1.414811,0.8598 z m 6.8675483,-1.8379 c 0.114364,-0.3658 0.206751,-1.2704 -0.114466,-1.3553 -0.152626,0.5835 -0.225018,1.1888 -0.227537,1.7919 0.147087,-0.1166 0.265559,-0.2643 0.342003,-0.4366 z\"\n     />\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.camel.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.cameltaskcamelcontextpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            muleTask = Stencil.builder()
                    .id("MuleTask")
                    .type(Stencil.NODE)
                    .title("Mule task")
                    .description("An task that sends a message to Mule")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"muleTask\" transform=\"translate(4,4)\">\n\t\t<path\n     style=\"fill:#bd4848;fill-opacity:1\"\n     d=\"M 8,0 C 3.581722,0 0,3.5817 0,8 c 0,4.4183 3.581722,8 8,8 4.418278,0 8,-3.5817 8,-8 L 16,7.6562 C 15.813571,3.3775 12.282847,0 8,0 z M 5.1875,2.7812 8,7.3437 10.8125,2.7812 c 1.323522,0.4299 2.329453,1.5645 2.8125,2.8438 1.136151,2.8609 -0.380702,6.4569 -3.25,7.5937 -0.217837,-0.6102 -0.438416,-1.2022 -0.65625,-1.8125 0.701032,-0.2274 1.313373,-0.6949 1.71875,-1.3125 0.73624,-1.2317 0.939877,-2.6305 -0.03125,-4.3125 l -2.75,4.0625 -0.65625,0 -0.65625,0 -2.75,-4 C 3.5268433,7.6916 3.82626,8.862 4.5625,10.0937 4.967877,10.7113 5.580218,11.1788 6.28125,11.4062 6.063416,12.0165 5.842837,12.6085 5.625,13.2187 2.755702,12.0819 1.238849,8.4858 2.375,5.625 2.858047,4.3457 3.863978,3.2112 5.1875,2.7812 z\"\n     />\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.mule.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .property(defaultProperties.muletaskendpointurlpackage())
                    .property(defaultProperties.muletasklanguagepackage())
                    .property(defaultProperties.muletaskpayloadexpressionpackage())
                    .property(defaultProperties.muletaskresultvariablepackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();

            sendTask = Stencil.builder()
                    .id("SendTask")
                    .type(Stencil.NODE)
                    .title("Send task")
                    .description("An task that sends a message")
                    .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n    \n\t<g id=\"sendTask\" transform=\"translate(4,3)\">\n\t\n\t<!-- path here -->\n\t\t<path oryx:anchors=\"top left\"\n\t\t\tstyle=\"fill:#16964d;stroke:none;\"\n     \t\td=\"M 1 3 L 9 11 L 17 3 L 1 3 z M 1 5 L 1 13 L 5 9 L 1 5 z M 17 5 L 13 9 L 17 13 L 17 5 z M 6 10 L 1 15 L 17 15 L 12 10 L 9 13 L 6 10 z \"\n     \t/>\n\t</g>\n\t\n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>")
                    .icon("activity/list/type.send.png")
                    .group("Activities")
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
                    .property(defaultProperties.isforcompensationpackage())
                    .roles(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"))
                    .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                userTask,
                serviceTask,
                scriptTask,
                businessRule,
                receiveTask,
                manualTask,
                mailTask,
                camelTask,
                muleTask,
                sendTask
        );
    }
}
