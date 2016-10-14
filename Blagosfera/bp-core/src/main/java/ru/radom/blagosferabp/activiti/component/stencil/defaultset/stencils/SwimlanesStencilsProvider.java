package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.stencil.exchange.Layout;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Список компонентов из группы "Swimlanes"
 */
@Component
@Order(800)
public class SwimlanesStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil pool;
    private Stencil lane;

    @PostConstruct
    private void postConstruct() {
        pool = Stencil.builder()
                .id("Pool")
                .type(Stencil.NODE)
                .title("Pool")
                .description("A pool to stucture the process definition")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"600\"\n   height=\"250\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"0\" oryx:cy=\"124\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"299\" oryx:cy=\"249\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"599\" oryx:cy=\"124\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"299\" oryx:cy=\"0\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"299\" oryx:cy=\"124\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"none\" >\n    <defs>\n\t\t<radialGradient id=\"background\" cx=\"0%\" cy=\"10%\" r=\"100%\" fx=\"20%\" fy=\"10%\">\n\t\t\t<stop offset=\"0%\" stop-color=\"#ffffff\" stop-opacity=\"1\"/>\n\t\t\t<stop id=\"fill_el\" offset=\"100%\" stop-color=\"#ffffff\" stop-opacity=\"1\"/>\n\t\t</radialGradient>\n\t</defs>\n\t  \t\n  \t<rect\n  \t\tid=\"border\"\n  \t\tclass=\"stripable-element-force\"\n  \t\toryx:resize=\"vertical horizontal\"\n  \t\tx=\"0\"\n  \t\ty=\"0\"\n  \t\twidth=\"600\"\n  \t\theight=\"250\"\n  \t\tfill=\"none\"\n  \t\tstroke-width=\"9\"\n  \t\tstroke=\"none\"\n  \t\tvisibility=\"visible\"\n  \t\tpointer-events=\"stroke\"\n  \t/>\n    <rect\n    \tid=\"c\"\n    \toryx:resize=\"vertical horizontal\"\n    \tx=\"0\"\n    \ty=\"0\"\n    \twidth=\"600\" \n    \theight=\"250\" \n    \tstroke=\"black\" \n    \tfill=\"url(#background) white\"\n    \tfill-opacity=\"0.3\" \n    />\n    \n\t<rect \n\t\tid=\"caption\"\n\t\toryx:anchors=\"left top bottom\"\n\t\tx=\"0\"\n\t\ty=\"0\"\n\t\twidth=\"30\"\n\t\theight=\"250\"\n\t\tstroke=\"black\"\n\t\tstroke-width=\"1\"\n\t\tfill=\"url(#background) white\"\n\t\tpointer-events=\"all\"\n\t/>\n\t\n\t<rect \n\t\tid=\"captionDisableAntialiasing\"\n\t\toryx:anchors=\"left top bottom\"\n\t\tx=\"0\"\n\t\ty=\"0\"\n\t\twidth=\"30\"\n\t\theight=\"250\"\n\t\tstroke=\"black\"\n\t\tstroke-width=\"1\"\n\t\tfill=\"url(#background) white\"\n\t\tpointer-events=\"all\"\n\t/>\n\t\n    <text x=\"13\" y=\"125\" font-size=\"12\" id=\"text_name\" oryx:fittoelem=\"caption\" oryx:align=\"middle center\" oryx:anchors=\"left\" oryx:rotate=\"270\" fill=\"black\" stroke=\"black\"></text>\n    \n  </g>\n</svg>")
                .icon("swimlane/pool.png")
                .group("Swimlanes")
                .layout(Layout.of("layout.bpmn2_0.pool"))
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.process_idpackage())
                .roles(Arrays.asList("canContainArtifacts", "all"))
                .build();

        lane = Stencil.builder()
                .id("Lane")
                .type(Stencil.NODE)
                .title("Lane")
                .description("A lane to stucture the process definition")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"600\"\n   height=\"250\"\n   version=\"1.0\">\n  <defs></defs>\n  <g pointer-events=\"none\" >\n  \n     <defs>\n\t\t<radialGradient id=\"background\" cx=\"0%\" cy=\"10%\" r=\"200%\" fx=\"20%\" fy=\"10%\">\n\t\t\t<stop offset=\"0%\" stop-color=\"#ffffff\" stop-opacity=\"1\"/>\n\t\t\t<stop id=\"fill_el\" offset=\"100%\" stop-color=\"#ffffff\" stop-opacity=\"0\"/>\n\t\t</radialGradient>\n\t</defs>\n\t\n  \t<rect id=\"border_invisible\" class=\"stripable-element-force\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"600\" height=\"250\" fill=\"none\" stroke-width=\"10\" stroke=\"white\" visibility=\"hidden\" pointer-events=\"stroke\"/>\t\t\n\t<rect id=\"border\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"600\" height=\"250\" stroke=\"black\" stroke-width=\"1\" fill=\"url(#background) white\" pointer-events=\"none\" />\n\t\n\t\n\t<rect \n\t\tid=\"caption\"\n\t\toryx:anchors=\"left top bottom\"\n\t\tx=\"0\"\n\t\ty=\"1\"\n\t\twidth=\"30\"\n\t\theight=\"248\"\n\t\tstroke=\"black\"\n\t\tstroke-width=\"0\"\n\t\tfill=\"white\"\n\t\tvisibility=\"hidden\"\n\t\tclass=\"stripable-element-force\"\n\t\tpointer-events=\"all\"\n\t/>\n\t\n\t<path\n\t\tstroke=\"black\"\n\t\tstroke-width=\"1\"\n\t\tfill=\"none\"\n\t\td=\"M 0,0 L 0,250\"\n        oryx:anchors=\"left top bottom\"\n        id=\"captionDisableAntialiasing\"\n    />\n\t\n\t<!--rect \n\t\tid=\"captionDisableAntialiasing\"\n\t\toryx:anchors=\"left top bottom\"\n\t\tx=\"0\"\n\t\ty=\"0\"\n\t\twidth=\"30\"\n\t\theight=\"250\"\n\t\tstroke=\"black\"\n\t\tstroke-width=\"1\"\n\t\tfill=\"url(#background) white\"\n\t/-->\n\t\n    <text \n\t\tx=\"13\"\n\t\ty=\"125\"\n\t\toryx:rotate=\"270\" \n\t\tfont-size=\"12\" \n\t\tid=\"text_name\" \n\t\toryx:align=\"middle center\" \n\t\toryx:anchors=\"left\"\n\t\toryx:fittoelem=\"caption\"\n\t\tfill=\"black\" \n\t\tstroke=\"black\">\n\t</text>\n  </g>\n</svg>")
                .icon("swimlane/lane.png")
                .group("Swimlanes")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .roles(Arrays.asList("PoolChild", "canContainArtifacts", "all"))
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                pool,
                lane
        );
    }
}
