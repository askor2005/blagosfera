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
 * Список компонентов из группы "Gateways"
 */
@Component
@Order(300)
public class GatewaysStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil exclusiveGateway;
    private Stencil parallelGateway;
    private Stencil inclusiveGateway;
    private Stencil eventGateway;

    @PostConstruct
    private void postConstruct() {
        exclusiveGateway = Stencil.builder()
                .id("ExclusiveGateway")
                .type(Stencil.NODE)
                .title("Exclusive gateway")
                .description("A choice gateway")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns=\"http://www.w3.org/2000/svg\"\n   version=\"1.0\"\n   width=\"40\"\n   height=\"40\">\n  <defs\n     id=\"defs4\" />\n  <oryx:magnets>\n    <oryx:magnet\n       oryx:default=\"yes\"\n       oryx:cy=\"16\"\n       oryx:cx=\"16\" />\n  </oryx:magnets>\t\t\t\t\t\n  <g>\n  \n    <path\n       d=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n       id=\"bg_frame\"\n       fill=\"#ffffff\"\n       stroke=\"#585858\"\n       style=\"stroke-width:1\" />\n    <g\n       id=\"cross\">\n      <path\n      \tid=\"crosspath\"\n      \tstroke=\"#585858\"\n      \tfill=\"#585858\"\n        d=\"M 8.75,7.55 L 12.75,7.55 L 23.15,24.45 L 19.25,24.45 z\"\n        style=\"stroke-width:1\" />\n      <path\n      \tid=\"crosspath2\"\n      \tstroke=\"#585858\"\n      \tfill=\"#585858\"\n        d=\"M 8.75,24.45 L 19.25,7.55 L 23.15,7.55 L 12.75,24.45 z\"\n        style=\"stroke-width:1\" />\n    </g>\n\t\n\t<text id=\"text_name\" x=\"26\" y=\"26\" oryx:align=\"left top\"/>\n\t\n  </g>\n</svg>\n")
                .icon("gateway/exclusive.databased.png")
                .group("Gateways")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.sequencefloworderpackage())
                .roles(Arrays.asList("sequence_start", "GatewaysMorph", "sequence_end", "all"))
                .build();

        parallelGateway = Stencil.builder()
                .id("ParallelGateway")
                .type(Stencil.NODE)
                .title("Parallel gateway")
                .description("A parallel gateway")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns=\"http://www.w3.org/2000/svg\"\n   version=\"1.0\"\n   width=\"40\"\n   height=\"40\">\n   \n  <oryx:magnets>\n    <oryx:magnet\n       oryx:default=\"yes\"\n       oryx:cy=\"16\"\n       oryx:cx=\"16\" />\n  </oryx:magnets>\n  <g>\n    <path\n       d=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n       id=\"bg_frame\"\n       fill=\"#ffffff\"\n       stroke=\"#585858\"\n       style=\"stroke-width:1\" />\n    <path\n       d=\"M 6.75,16 L 25.75,16 M 16,6.75 L 16,25.75\"\n       id=\"path9\"\n       stroke=\"#585858\"\n       style=\"fill:none;stroke-width:3\" />\n    \n\t<text id=\"text_name\" x=\"26\" y=\"26\" oryx:align=\"left top\"/>\n\t\n  </g>\n</svg>\n")
                .icon("gateway/parallel.png")
                .group("Gateways")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.sequencefloworderpackage())
                .roles(Arrays.asList("sequence_start", "GatewaysMorph", "sequence_end", "all"))
                .build();

        inclusiveGateway = Stencil.builder()
                .id("InclusiveGateway")
                .type(Stencil.NODE)
                .title("Inclusive gateway")
                .description("An inclusive gateway")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns=\"http://www.w3.org/2000/svg\"\n   version=\"1.0\"\n   width=\"40\"\n   height=\"40\">\n  <oryx:magnets>\n    <oryx:magnet\n       oryx:default=\"yes\"\n       oryx:cy=\"16\"\n       oryx:cx=\"16\" />\n  </oryx:magnets>\n  <g>\n\n    <path\n       d=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n       id=\"bg_frame\"\n       fill=\"#ffffff\"\n       stroke=\"#585858\"\n       style=\"stroke-width:1\" />\n    <circle\n    \tid=\"circle\"\n    \tstroke=\"#585858\"\n\t\tcx=\"16\"\n\t\tcy=\"16\"\n\t\tr=\"9.75\"\n\t\tstyle=\"fill:none;stroke-width:2.5\" />\n    \n\t<text id=\"text_name\" x=\"26\" y=\"26\" oryx:align=\"left top\"/>\n\t\n  </g>\n</svg>\n")
                .icon("gateway/inclusive.png")
                .group("Gateways")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.sequencefloworderpackage())
                .roles(Arrays.asList("sequence_start", "GatewaysMorph", "sequence_end", "all"))
                .build();

        eventGateway = Stencil.builder()
                .id("EventGateway")
                .type(Stencil.NODE)
                .title("Event gateway")
                .description("An event gateway")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns=\"http://www.w3.org/2000/svg\"\n   version=\"1.0\"\n   width=\"40\"\n   height=\"40\">\n  <oryx:magnets>\n    <oryx:magnet\n       oryx:default=\"yes\"\n       oryx:cy=\"16\"\n       oryx:cx=\"16\" />\n  </oryx:magnets>\n  \n  <g> \n  \t\n\t<path\n\t\td=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n\t\tid=\"bg_frame\"\n\t\tfill=\"#ffffff\"\n\t\tstroke=\"#585858\"\n\t\tstyle=\"stroke-width:1\" />\n\t<circle\n\t\tid=\"circle\"\n\t\tcx=\"16\"\n\t\tcy=\"16\"\n\t\tr=\"10.4\"\n\t\tstroke=\"#585858\"\n\t\tstyle=\"fill:none;stroke-width:0.5\" />\n\t<circle\n\t\tid=\"circle2\"\n\t\tcx=\"16\"\n\t\tcy=\"16\"\n\t\tr=\"11.7\"\n\t\tstroke=\"#585858\"\n\t\tstyle=\"fill:none;stroke-width:0.5\" />\n\t<path\n\t\td=\"M 20.327514,22.344972 L 11.259248,22.344216 L 8.4577203,13.719549 L 15.794545,8.389969 L 23.130481,13.720774 L 20.327514,22.344972 z\"\n\t\tid=\"middlePolygon\"\n\t\tstroke=\"#585858\"\n\t\tstyle=\"fill:none;fill-opacity:1;stroke-width:1.39999998;stroke-linejoin:bevel;stroke-opacity:1\" />\n\t\n\t\n\t<g id=\"instantiate\">\n\t\n\t\t<path\n\t\t\td=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n\t\t\tid=\"bg_frame2\"\n\t\t\tfill=\"#ffffff\"\n\t\t\tstroke=\"#585858\"\n\t\t\tstyle=\"stroke-width:1\" />\n\t\t<circle\n\t\t\tid=\"circle3\"\n\t\t\tcx=\"16\"\n\t\t\tcy=\"16\"\n\t\t\tr=\"11\"\n\t\t\tstroke=\"#585858\"\n\t\t\tstyle=\"fill:none;stroke-width:1\" />\n\t\t<path\n\t\t\td=\"M 20.327514,22.344972 L 11.259248,22.344216 L 8.4577203,13.719549 L 15.794545,8.389969 L 23.130481,13.720774 L 20.327514,22.344972 z\"\n\t\t\tid=\"middlePolygon2\"\n\t\t\tstroke=\"#585858\"\n\t\t\tstyle=\"fill:none;fill-opacity:1;stroke-width:1.39999998;stroke-linejoin:bevel;stroke-opacity:1\" />\n\t\n\t\n\t\t<g id=\"parallel\">\n\t\t\t<path\n\t\t\t\td=\"M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z\"\n\t\t\t\tid=\"bg_frame3\"\n\t\t\t\tfill=\"#ffffff\"\n\t\t\t\tstroke=\"#585858\"\n\t\t\t\tstyle=\"stroke-width:1\" />\n\t\t\t<circle id=\"frame5\" cx=\"16\" cy=\"16\" r=\"12\" stroke=\"#585858\" fill=\"none\" stroke-width=\"1\"/>\n\t\t\t\n\t\t\t<path\n\t\t\t\td=\"M 6.75,14 L6.75,18 L14,18 L14,24.75 L18,24.75 L18,18 L24.75,18 L24.75,14 L18,14 L18,6.75 L14,6.75 L14,14z\"\n\t\t\t\tid=\"path92\"\n\t\t\t\tstroke=\"#585858\"\n\t\t\t\tstyle=\"fill:none;stroke-width:1\" />\n\t\t\n\t\t</g>\n\t\n\t</g>\n\t\n\t<text id=\"text_name\" x=\"26\" y=\"26\" oryx:align=\"left top\"/>\n\t\n  </g>\t\n\t\n</svg>\n")
                .icon("gateway/eventbased.png")
                .group("Gateways")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.asynchronousdefinitionpackage())
                .property(defaultProperties.exclusivedefinitionpackage())
                .property(defaultProperties.sequencefloworderpackage())
                .roles(Arrays.asList("sequence_start", "GatewaysMorph", "sequence_end", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                exclusiveGateway,
                parallelGateway,
                inclusiveGateway,
                eventGateway
        );
    }
}
