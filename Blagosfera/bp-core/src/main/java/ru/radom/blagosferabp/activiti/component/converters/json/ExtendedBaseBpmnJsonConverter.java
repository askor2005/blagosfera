package ru.radom.blagosferabp.activiti.component.converters.json;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 02.10.2015.<br/>
 * Расширение для {@link BaseBpmnJsonConverter}, которое позволит сделать процесс расширения модели более гибким
 */
public abstract class ExtendedBaseBpmnJsonConverter extends BaseBpmnJsonConverter {

    public abstract void fillTypes(
            Map<String, BaseBpmnJsonConverter> convertersToBpmnMap,
            Map<Class<? extends BaseElement>, BaseBpmnJsonConverter> convertersToJsonMap
    );

    /**
     * Заполнить, какой компонент является кругом
     */
    public void fillCirclesTypes(List<String> circles) {

    }

    /**
     * Заполнить, какой компонент является прямоугольником
     */
    public void fillRectanglesTypes(List<String> rectangles) {

    }

    /**
     * Заполнить, какой компонент является воротами (ромб)
     */
    public void fillGatewayTypes(List<String> gateway) {

    }
}
