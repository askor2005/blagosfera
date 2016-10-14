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
 * Список компонентов из группы "Artifacts"
 */
@Component
@Order(900)
public class ArtifactsStencilsProvider implements StencilProvider {

    @Autowired
    private DefaultProperties defaultProperties;

    private Stencil textAnnotation;
    private Stencil dataStore;

    @PostConstruct
    private void postConstruct() {
        textAnnotation = Stencil.builder()
                .id("TextAnnotation")
                .type(Stencil.NODE)
                .title("Text annotation")
                .description("Annotates elements with description text.")
                .view("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n   width=\"102\"\n   height=\"51\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"2\" oryx:cy=\"25\" oryx:anchors=\"left\" oryx:default=\"yes\"/>\n  </oryx:magnets>\n  <g pointer-events=\"all\" oryx:minimumSize=\"10 20\" oryx:maximumSize=\"\" >\n  <rect \n\tid=\"textannotationrect\"\n\toryx:resize=\"vertical horizontal\"\n\tx=\"1\" \n\ty=\"1\"\n\twidth=\"100\"\n\theight=\"50\"\n\tstroke=\"none\"\n\tfill=\"none\" />\n  <path \n  \tid = \"frame\"\n\td=\"M20,1 L1,1 L1,50 L20,50\" \n\toryx:anchors=\"top bottom left\" \n\tstroke=\"#585858\" \n\tfill=\"none\" \n\tstroke-width=\"1\" />\n    \n    <text \n\t\tfont-size=\"12\" \n\t\tid=\"text\" \n\t\tx=\"5\" \n\t\ty=\"25\" \n\t\toryx:align=\"middle left\"\n\t\toryx:fittoelem=\"textannotationrect\"\n\t\toryx:anchors=\"left\"\n\t\tstroke=\"#373e48\">\n\t</text>\n  </g>\n</svg>")
                .icon("artifact/text.annotation.png")
                .group("Artifacts")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .property(defaultProperties.textpackage())
                .role("all")
                .build();

        dataStore = Stencil.builder()
                .id("DataStore")
                .type(Stencil.NODE)
                .title("Data store")
                .description("Reference to a data store.")
                .view("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\" ?>\r\n<svg \r\n\txmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:svg=\"http://www.w3.org/2000/svg\"\r\n   \txmlns:oryx=\"http://www.b3mn.org/oryx\"\r\n   \txmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n\t\r\n\twidth=\"63.001px\" \r\n\theight=\"61.173px\"\r\n\tversion=\"1.0\">\r\n\t<defs></defs>\r\n\t<oryx:magnets>\r\n\t\t<oryx:magnet oryx:cx=\"0\" oryx:cy=\"30.5865\" oryx:anchors=\"left\" />\r\n\t\t<oryx:magnet oryx:cx=\"31.5005\" oryx:cy=\"61.173\" oryx:anchors=\"bottom\" />\r\n\t\t<oryx:magnet oryx:cx=\"63.001\" oryx:cy=\"30.5865\" oryx:anchors=\"right\" />\r\n\t\t<oryx:magnet oryx:cx=\"31.5005\" oryx:cy=\"0\" oryx:anchors=\"top\" />\r\n\t\t<oryx:magnet oryx:cx=\"31.5005\" oryx:cy=\"30.5865\" oryx:default=\"yes\" />\r\n\t</oryx:magnets>\r\n\t\r\n\t<g>\r\n\t\t<defs>\r\n\t\t\t<radialGradient id=\"background\" cx=\"30%\" cy=\"30%\" r=\"50%\" fx=\"0%\" fy=\"0%\">\r\n\t\t\t\t<stop offset=\"0%\" stop-color=\"#ffffff\" stop-opacity=\"1\"></stop>\r\n\t\t\t\t<stop offset=\"100%\" stop-color=\"#ffffff\" stop-opacity=\"1\" id=\"fill_el\"></stop>\r\n\t\t\t</radialGradient>\r\n\t\t</defs>\r\n\t\t\r\n\t\t<path id=\"bg_frame\" fill=\"url(#background) #ffffff\" stroke=\"#000000\" d=\"M31.634,0.662c20.013,0,31.292,3.05,31.292,5.729c0,2.678,0,45.096,0,48.244\r\n\t\t\tc0,3.148-16.42,6.2-31.388,6.2c-14.968,0-30.613-2.955-30.613-6.298c0-3.342,0-45.728,0-48.05\r\n\t\t\tC0.925,4.165,11.622,0.662,31.634,0.662z\"/>\r\n\t\t<path id=\"bg_frame2\" fill=\"none\" stroke=\"#000000\" d=\"\r\n\t\t\tM62.926,15.69c0,1.986-3.62,6.551-31.267,6.551c-27.646,0-30.734-4.686-30.734-6.454 M0.925,11.137\r\n\t\t\tc0,1.769,3.088,6.455,30.734,6.455c27.647,0,31.267-4.565,31.267-6.551 M0.925,6.487c0,2.35,3.088,6.455,30.734,6.455\r\n\t\t\tc27.647,0,31.267-3.912,31.267-6.552 M62.926,6.391v4.844 M0.949,6.391v4.844 M62.926,11.041v4.844 M0.949,11.041v4.844\"/>\r\n\t\t\t \t\r\n\t\t<text font-size=\"12\" id=\"text_name\" x=\"31\" y=\"66\" oryx:align=\"center top\" stroke=\"black\" />\r\n\t\t\t \r\n\t</g>\r\n</svg>\r\n")
                .icon("dataobject/data.store.png")
                .group("Artifacts")
                .property(defaultProperties.overrideidpackage())
                .property(defaultProperties.namepackage())
                .property(defaultProperties.documentationpackage())
                .role("all")
                .build();
    }

    @Override
    public List<Stencil> getAll() {
        return Arrays.asList(
                textAnnotation,
                dataStore
        );
    }
}
