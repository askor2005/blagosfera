package ru.radom.blagosferabp.activiti.rabbit;

import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.converters.xml.ExtendedXMLConverter;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.CollectionChildParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.MapChildParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.ParameterToChildElementParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.ScriptDataChildParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.AttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.BooleanAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.DoubleAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.LongAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.StringAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.dao.StencilDAO;
import ru.radom.blagosferabp.activiti.dto.util.StencilConverter;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.rabbit.bundle.RabbitTaskBundle;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * XML конвертер для {@link RabbitTask}
 */
@Component
public class RabbitTaskXMLConverter extends ExtendedXMLConverter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StencilDAO stencilDAO;

    @Autowired
    private StencilConverter stencilConverter;

    @Autowired
    private MapChildParser mapChildParser;

    @Autowired
    private CollectionChildParser collectionChildParser;

    private Map<String, BaseChildElementParser> childAttributesFromXML;
    private Map<String, ParameterToChildElementParser> childAttributesToXML;

    @PostConstruct
    public void postConstruct() {
        childAttributesFromXML = new HashMap<>();
        childAttributesToXML = new HashMap<>();

        ScriptDataChildParser scriptDataChildParser = new ScriptDataChildParser();
        childAttributesFromXML.put(scriptDataChildParser.getElementName(), scriptDataChildParser);
        childAttributesToXML.put(Property.SCRIPT, scriptDataChildParser);

        childAttributesFromXML.put(mapChildParser.getElementName(), mapChildParser);
        childAttributesToXML.put(Property.DOCUMENT_TEMPLATE, mapChildParser);
        childAttributesToXML.put(Property.VOTINGS_TEMPLATE, mapChildParser);
        childAttributesToXML.put(Property.KEY_VALUE, mapChildParser);

        childAttributesFromXML.put(collectionChildParser.getElementName(), collectionChildParser);
    }


    @Override
    protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
        RabbitTask task = applicationContext.getBean(RabbitTask.class, new Object[]{});
        BpmnXMLUtil.addXMLLocation(task, xtr);
        String stencilId = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, RabbitTaskBundle.STENCIL_ENTITY_ID_NODE);
        if(stencilId == null) {
            throw new IllegalArgumentException("StencilEntityId property is not set");
        }
        StencilEntity entity = stencilDAO.getOne(stencilId);
        if(entity == null) {
            throw new IllegalArgumentException("Компонент " + stencilId + " не найден в базе. Возможно он был удален.");
        }
        task.setQueueToSend(entity.getQueueToSend());
        task.setAnswerType(entity.getAnswerType());
        task.setCustomParameters(extractProperties(xtr, entity));
        task.setResultVariableName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SERVICE_RESULTVARIABLE));
        parseChildElements(getXMLElementName(), task, childAttributesFromXML, model, xtr);
        return task;
    }

    @Override
    protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        RabbitTask task = (RabbitTask) element;
        writeQualifiedAttribute(RabbitTaskBundle.STENCIL_ENTITY_ID_NODE, task.getStencilEntityId(), xtw);
        StencilEntity entity = stencilDAO.getOne(task.getStencilEntityId());
        Stencil stencil = stencilConverter.convert(entity);
        Map<String, Object> customParameters = task.getCustomParameters();
        for (Property property : stencil.getProperties()) {
            String type = property.getType();
            String key = property.getId();
            if(!childAttributesToXML.containsKey(type)) {
                Object value = customParameters.get(key);
                if(value != null) {
                    if("type".equals(key)) {
                        key = "_type_";
                    }
                    //TODO придумать более универсальное преобразование к строке
                    writeQualifiedAttribute(key, String.valueOf(value), xtw);
                }
            }
        }
        writeQualifiedAttribute(ATTRIBUTE_TYPE, RabbitTaskBundle.TASK_TYPE, xtw);
        if (StringUtils.isNotEmpty(task.getResultVariableName())) {
            writeQualifiedAttribute(ATTRIBUTE_TASK_SERVICE_RESULTVARIABLE, task.getResultVariableName(), xtw);
        }
    }

    @Override
    protected boolean writeExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
        RabbitTask task = (RabbitTask) element;
        StencilEntity entity = stencilDAO.getOne(task.getStencilEntityId());
        Stencil stencil = stencilConverter.convert(entity);

        Map<String, Object> customParameters = task.getCustomParameters();
        for (Property property : stencil.getProperties()) {
            String type = property.getType();
            String key = property.getId();
            ParameterToChildElementParser parser = childAttributesToXML.get(type);
            if (parser == null && type.contains("complex")) {
                parser = mapChildParser;
            }
            if(parser != null) {
                Object value = customParameters.get(key);
                if(value != null) {
                    if(!didWriteExtensionStartElement) {
                        xtw.writeStartElement(ELEMENT_EXTENSIONS);
                        didWriteExtensionStartElement = true;
                    }
                    //noinspection unchecked
                    parser.createChild(key, value, xtw, null);
                }
            }
        }
        return didWriteExtensionStartElement;
    }

    @Override
    protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {

    }

    private Map<String, Object> extractProperties(XMLStreamReader xtr, StencilEntity entity) {
        Map<String, Object> properties = new HashMap<>();
        Stencil stencil = stencilConverter.convert(entity);
        for (Property property : stencil.getProperties()) {
            String id = property.getId();
            if(RabbitTaskBundle.STENCIL_ENTITY_ID_NODE.equals(id) || stencilConverter.isPropertyDefault(id)) {
                continue;
            }

            String type = property.getType().toLowerCase();
            AttributeValueExtractor extractor = resolveExtractor(type);
            if(extractor == null) {
                continue;
            }
            String idToRead = id;
            if("type".equals(id)) {
                idToRead = "_type_";
            }
            String rawValue = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, idToRead);
            if(rawValue != null) {
                properties.put(id, extractor.extract(rawValue));
            }
        }
        return properties;
    }

    private AttributeValueExtractor resolveExtractor(String type) {
        switch (type) {
            case Property.CHOICE:
            case Property.COLOR:
            case Property.STRING:
            case Property.TEXT:
                return new StringAttributeValueExtractor();
            case Property.BOOLEAN:
                return new BooleanAttributeValueExtractor();
            case Property.INTEGER:
                return new LongAttributeValueExtractor();
            case Property.FLOAT:
                return new DoubleAttributeValueExtractor();
            default:
                return null;
        }
    }
}
