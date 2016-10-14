package ru.radom.blagosferabp.activiti.component;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.engine.parse.BpmnParseHandler;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.List;

/**
 * Created by alex on 02.10.2015.<br/>
 * Пакет описывающий всё необходимое для работы компонента
 */
public interface ModelBundle {

    String getStencilId();

    /**
     * Компонент для отображения в редакторе
     */
    Stencil getStencil();

    /**
     * JSON конвертер компонента
     */
    ExtendedBaseBpmnJsonConverter getJsonConverter();

    /**
     * XML конвертер компонента
     */
    BaseBpmnXMLConverter getXMLConverter();

    /**
     * Парсер поведения компонента
     */
    BpmnParseHandler getBpmnParseHandler();

    /**
     * Необходимые для модели правила
     */
    List<StencilRule> getRequiredRules();
}
