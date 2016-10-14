package ru.radom.blagosferabp.activiti.component.converters.xml;

import lombok.Data;
import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.CollectionChildParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.MapChildParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.parsers.ParameterToChildElementParser;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.XMLReflectionUtils;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.AttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Otts Alexey on 29.11.2015.<br/>
 * Общая реализация для коверторов не стандартных {@link FlowNode}
 */
public abstract class CustomFlowNodeCommonXMLConverter<T extends FlowNode> extends BaseBpmnXMLConverter {

    protected Map<Class<? extends T>, ClassConvertInformation> classConvertInformation = new HashMap<>();
    protected Map<String, Class<? extends T>> taskTypeToClass = new HashMap<>();
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected MapChildParser mapChildParser;

    @Autowired
    protected CollectionChildParser collectionChildParser;

    protected abstract BaseBpmnXMLConverter getOriginalConverter();

    /**
     * Добавить конвертер для не стандартного {@link FlowNode}'a
     */
    public void addCustomTask(Class<? extends T> taskClass, String taskType, ModelBundle modelBundle) {
        Class<? extends T> previousClass = taskTypeToClass.put(taskType, taskClass);
        if (previousClass != null) {
            taskTypeToClass.put(taskType, previousClass);
            throw new IllegalArgumentException(String.format(
                "Type '%s' of service task is already reserved by '%s'. But '%s' is trying to reserve it.",
                taskType,
                previousClass,
                taskClass
            ));
        }

        final ClassConvertInformation information = new ClassConvertInformation();
        information.setType(taskType);
        BaseBpmnXMLConverter xmlConverter = modelBundle.getXMLConverter();
        if (xmlConverter != null) {
            information.setAlternativeConverter(xmlConverter);
        } else {
            final Map<Field, String> fieldToXmlAttribute = new HashMap<>();
            final Map<Field, String> fieldToExtensionName = new HashMap<>();
            final Map<Field, AttributeValueExtractor> fieldToAttributeExtractor = new HashMap<>();
            final Map<Field, ParameterToChildElementParser> fieldToParameterToChildElementParser = new HashMap<>();
            final List<BaseChildElementParser> baseChildElementParsers = new ArrayList<>();
            ReflectionUtils.doWithFields(taskClass, field -> {
                FlowNodeParameter parameter = field.getDeclaredAnnotation(FlowNodeParameter.class);
                if (parameter != null) {
                    String xmlParameter = "".equals(parameter.xmlParameter()) ? field.getName() : parameter.xmlParameter();
                    Class<?> type = field.getType();
                    boolean isMap = Map.class.isAssignableFrom(type);
                    if (isMap || Collection.class.isAssignableFrom(type)) {
                        fieldToExtensionName.put(field, xmlParameter);
                        ParameterToChildElementParser parser = isMap ? CustomFlowNodeCommonXMLConverter.this.mapChildParser : CustomFlowNodeCommonXMLConverter.this.collectionChildParser;
                        fieldToParameterToChildElementParser.put(field, parser);
                        BaseChildElementParser p = new BaseChildElementParser() {
                            @Override
                            public String getElementName() {
                                return xmlParameter;
                            }

                            @Override
                            public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
                                PropertyUtils.setProperty(parentElement, field.getName(), parser.parseBack(xtr, xmlParameter));
                            }
                        };
                        baseChildElementParsers.add(p);
                    } else {
                        fieldToXmlAttribute.put(field, xmlParameter);
                        fieldToAttributeExtractor.put(field, XMLReflectionUtils.getExtractorForField(field));
                    }
                }
            });

            information.setFieldToXmlAttribute(fieldToXmlAttribute);
            information.setFieldToAttributeExtractor(fieldToAttributeExtractor);
            information.setFieldToParameterToChildElementParser(fieldToParameterToChildElementParser);
            information.setBaseChildElementParsers(baseChildElementParsers);
            information.setFieldToExtensionName(fieldToExtensionName);
        }

        classConvertInformation.put(taskClass, information);

        BpmnXMLConverter.addConverter(this, taskClass);
    }

    @Override
    protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
        String type = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TYPE);
        if (type != null) {
            Class<? extends T> taskClass = taskTypeToClass.get(type.trim());
            if (taskClass != null) {
                ClassConvertInformation convertInformation = this.classConvertInformation.get(taskClass);
                BaseBpmnXMLConverter alternativeConverter = convertInformation.getAlternativeConverter();
                if(alternativeConverter != null) {
                    return invokeConvertXMLToElement(xtr, model, alternativeConverter);
                }

                T flowNode = instantiateFlowNode(taskClass);
                BpmnXMLUtil.addXMLLocation(flowNode, xtr);

                for (Map.Entry<Field, String> entry : convertInformation.getFieldToXmlAttribute().entrySet()) {
                    Field field = entry.getKey();
                    String rawValue = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, entry.getValue());
                    if(rawValue != null) {
                        AttributeValueExtractor extractor = convertInformation.getFieldToAttributeExtractor().get(field);
                        Object value = extractor.extract(rawValue);
                        if (value != null) {
                            PropertyUtils.setProperty(flowNode, field.getName(), value);
                        }
                    }
                }

                afterExtractAttributes(xtr, model, flowNode);

                Map<String, BaseChildElementParser> extensions = new HashMap<>();
                for (BaseChildElementParser baseChildElementParser : convertInformation.getBaseChildElementParsers()) {
                    extensions.put(baseChildElementParser.getElementName(), baseChildElementParser);
                }
                parseChildElements(getXMLElementName(), flowNode, extensions, model, xtr);
                return flowNode;
            }
        }

        return invokeConvertXMLToElement(xtr, model, getOriginalConverter());
    }

    /**
     * Вызывается после стандартного чтения аттрибутов на случай,
     * если нужно сделать что то дополнительное в наследниках
     * @throws Exception
     */
    protected void afterExtractAttributes(XMLStreamReader xtr, BpmnModel model, T flowNode) throws Exception {

    }

    /**
     * Создает экземпляр при конвертации из xml
     */
    protected T instantiateFlowNode(Class<? extends T> taskClass) {
        return applicationContext.getBean(taskClass, new Object[]{});
    }

    /**
     * Вызвать метод {@link BaseBpmnXMLConverter#convertXMLToElement} через Reflection API
     */
    private BaseElement invokeConvertXMLToElement(XMLStreamReader xtr, BpmnModel model, BaseBpmnXMLConverter converter) {
        Method method = ReflectionUtils.findMethod(
            converter.getClass(),
            "convertXMLToElement",
            XMLStreamReader.class,
            BpmnModel.class
        );
        ReflectionUtils.makeAccessible(method);
        return (BaseElement) ReflectionUtils.invokeMethod(method, converter, xtr, model);
    }

    @Override
    protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends T> taskClass = (Class<? extends T>) element.getClass();
        ClassConvertInformation convertInformation = this.classConvertInformation.get(taskClass);
        if(convertInformation != null) {
            BaseBpmnXMLConverter alternativeConverter = convertInformation.getAlternativeConverter();
            if (alternativeConverter != null) {
                invokeWriteAdditionalAttributes(element, model, xtw, alternativeConverter);
                return;
            }

            for (Map.Entry<Field, String> entry : convertInformation.getFieldToXmlAttribute().entrySet()) {
                Field field = entry.getKey();
                Object value = PropertyUtils.getProperty(element, field.getName());
                if(value != null) {
                    //TODO придумать более универсальное преобразование к строке
                    writeQualifiedAttribute(entry.getValue(), String.valueOf(value), xtw);
                }
            }
            writeQualifiedAttribute(ATTRIBUTE_TYPE, convertInformation.getType(), xtw);
            afterWriteAdditionalAttributes((T)element, model, xtw);
        } else {
            invokeWriteAdditionalAttributes(element, model, xtw, getOriginalConverter());
        }
    }

    /**
     * Вызывается после стандартного записывания аттрибутов на случай,
     * если нужно сделать что то дополнительное в наследниках
     * @throws Exception
     */
    protected void afterWriteAdditionalAttributes(T flowNode, BpmnModel model, XMLStreamWriter xtw) throws Exception {

    }

    /**
     * Вызвать метод {@link BaseBpmnXMLConverter#writeAdditionalAttributes} через Reflection API
     */
    private void invokeWriteAdditionalAttributes(
        BaseElement element,
        BpmnModel model,
        XMLStreamWriter xtw,
        BaseBpmnXMLConverter converter
    ) {
        Method method = ReflectionUtils.findMethod(
            converter.getClass(),
            "writeAdditionalAttributes",
            BaseElement.class,
            BpmnModel.class,
            XMLStreamWriter.class
        );
        ReflectionUtils.makeAccessible(method);
        ReflectionUtils.invokeMethod(method, converter, element, model, xtw);
    }

    @Override
    protected boolean writeExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
        Class<? extends T> taskClass = (Class<? extends T>) element.getClass();
        ClassConvertInformation convertInformation = this.classConvertInformation.get(taskClass);
        if(convertInformation != null) {
            BaseBpmnXMLConverter alternativeConverter = convertInformation.getAlternativeConverter();
            if (alternativeConverter != null) {
                return invokeWriteExtensionChildElements(element, didWriteExtensionStartElement, xtw, alternativeConverter);
            }
            Map<Field, ParameterToChildElementParser> parsers = convertInformation.getFieldToParameterToChildElementParser();
            Map<Field, String> fieldToExtensionName = convertInformation.getFieldToExtensionName();
            for (Map.Entry<Field, ParameterToChildElementParser> entry : parsers.entrySet()) {
                Field field = entry.getKey();
                Object value = PropertyUtils.getProperty(element, field.getName());
                if(value != null) {
                    ParameterToChildElementParser parser = entry.getValue();
                    if(!didWriteExtensionStartElement) {
                        xtw.writeStartElement(ELEMENT_EXTENSIONS);
                        didWriteExtensionStartElement = true;
                    }
                    //noinspection unchecked
                    parser.createChild(null, value, xtw, fieldToExtensionName.get(field));
                }
            }
            return didWriteExtensionStartElement;
        }
        if (element.getClass().equals(getBpmnElementType())) {
            return invokeWriteExtensionChildElements(element, didWriteExtensionStartElement, xtw, getOriginalConverter());
        }
        return didWriteExtensionStartElement;
    }

    /**
     * Вызвать метод {@link BaseBpmnXMLConverter#writeExtensionChildElements} через Reflection API
     */
    private boolean invokeWriteExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw, BaseBpmnXMLConverter converter) {
        Method method = ReflectionUtils.findMethod(
            converter.getClass(),
            "writeExtensionChildElements",
            BaseElement.class,
            boolean.class,
            XMLStreamWriter.class
        );
        ReflectionUtils.makeAccessible(method);
        return (boolean) ReflectionUtils.invokeMethod(method, converter, element, didWriteExtensionStartElement, xtw);
    }

    @Override
    protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        Class<? extends T> taskClass = (Class<? extends T>) element.getClass();
        ClassConvertInformation convertInformation = this.classConvertInformation.get(taskClass);
        if(convertInformation != null) {
            BaseBpmnXMLConverter alternativeConverter = convertInformation.getAlternativeConverter();
            if (alternativeConverter != null) {
                invokeWriteAdditionalChildElements(element, model, xtw, alternativeConverter);
                return;
            }
        }
        if (element.getClass().equals(getBpmnElementType())) {
            invokeWriteAdditionalChildElements(element, model, xtw, getOriginalConverter());
        } else {
            afterWriteAdditionalChildElement((T) element, model, xtw);
        }
    }

    /**
     * Вызывается после стандартного записывания дочерних элементов на случай,
     * если нужно сделать что то дополнительное в наследниках
     * @throws Exception
     */
    protected void afterWriteAdditionalChildElement(T flowNode, BpmnModel model, XMLStreamWriter xtw) throws Exception {

    }

    /**
     * Вызвать метод {@link BaseBpmnXMLConverter#writeAdditionalChildElements} через Reflection API
     */
    private void invokeWriteAdditionalChildElements(BaseElement element, BpmnModel model, XMLStreamWriter xtw, BaseBpmnXMLConverter converter) {
        Method method = ReflectionUtils.findMethod(
            converter.getClass(),
            "writeAdditionalChildElements",
            BaseElement.class,
            BpmnModel.class,
            XMLStreamWriter.class
        );
        ReflectionUtils.makeAccessible(method);
        ReflectionUtils.invokeMethod(method, converter, element, model, xtw);
    }

    @Data
    protected static class ClassConvertInformation {

        /**
         * Тип, которым отмечается таск в xml
         */
        private String type;

        /**
         * Сопоставление поля класса с названием xml аттрибута
         */
        private Map<Field, String> fieldToXmlAttribute;

        /**
         * Сопоставление поля класса с распаковщиков значение аттрибута
         */
        private Map<Field, AttributeValueExtractor> fieldToAttributeExtractor;

        /**
         * Сопоставление поля класса с запаковщиком в элемент расширения
         */
        private Map<Field, ParameterToChildElementParser> fieldToParameterToChildElementParser;

        /**
         * Распоковщиком из элементов расширения
         */
        private List<BaseChildElementParser> baseChildElementParsers;

        /**
         * Сопоставление поля класса с названием xml элемента в расширениях
         */
        private Map<Field, String> fieldToExtensionName;

        /**
         * Не стандартный конвертер для типа
         */
        private BaseBpmnXMLConverter alternativeConverter;
    }
}
