package ru.radom.blagosferabp.activiti.component.converters.xml.parsers;

import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.rabbit.RabbitTask;

import javax.xml.stream.XMLStreamReader;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Otts Alexey on 17.11.2015.<br/>
 * Общий класс для сложных атрибутов
 */
@Component
public abstract class ComplexChildParser<T> extends BaseChildElementParser implements ParameterToChildElementParser<T> {

    protected final String STRING_TYPE = "string";
    protected final String BOOLEAN_TYPE = "boolean";
    protected final String NULL_TYPE = "null";
    protected final String INT_TYPE = "int";
    protected final String LONG_TYPE = "long";
    protected final String FLOAT_TYPE = "float";
    protected final String DOUBLE_TYPE = "double";
    protected final String COLLECTION_TYPE = "list";
    protected final String MAP_TYPE = "map";


    @Override
    public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
        String param = xtr.getAttributeValue(null, PARAM_NAME);
        if(param != null) {
            RabbitTask task = (RabbitTask) parentElement;
            task.getCustomParameters().put(param, parseBack(xtr, null));
        }
    }

    protected String resolveType(Object val) {
        if(val == null) {
            return NULL_TYPE;
        } else if(val instanceof Boolean) {
            return BOOLEAN_TYPE;
        } else if(val instanceof Integer) {
            return INT_TYPE;
        } else if(val instanceof Long) {
            return LONG_TYPE;
        } else if(val instanceof Float) {
            return FLOAT_TYPE;
        } else if(val instanceof Double) {
            return DOUBLE_TYPE;
        } else if(val instanceof String) {
            return STRING_TYPE;
        } else if(val instanceof Collection) {
            return COLLECTION_TYPE;
        } else if(val instanceof Map) {
            return MAP_TYPE;
        }
        return null;
    }

    protected Object parseSimpleValue(String val, String type) {
        switch(type) {
            case BOOLEAN_TYPE:
                return Boolean.parseBoolean(val);
            case INT_TYPE:
                return Integer.parseInt(val);
            case LONG_TYPE:
                return Long.parseLong(val);
            case FLOAT_TYPE:
                return Float.parseFloat(val);
            case DOUBLE_TYPE:
                return Double.parseDouble(val);
            case STRING_TYPE:
            default:
                return val;
        }
    }

    protected boolean isSimpleType(String type) {
        switch(type) {
            case BOOLEAN_TYPE:
            case INT_TYPE:
            case LONG_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
                return true;
            default:
                return false;
        }
    }
}
