package ru.radom.blagosferabp.activiti.component.converters.xml.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 17.11.2015.<br/>
 * Парсер для значение типа {@link Map}
 */
@Component
public class MapChildParser extends ComplexChildParser<Map<String, Object>> {

    @Autowired
    private CollectionChildParser collectionChildParser;

    @Override
    public String getElementName() {
        return "map";
    }

    @Override
    public Map<String, Object> parseBack(XMLStreamReader xtr, String elementNameOverride) throws XMLStreamException {
        Map<String, Object> map = new HashMap<>();
        String elementName = elementNameOverride != null ? elementNameOverride : getElementName();
        while (true) {
            xtr.next();
            if(xtr.isEndElement() && xtr.getLocalName().equals(elementName)) {
                break;
            }
            if(xtr.isStartElement()) {
                if ("entry".equals(xtr.getLocalName())) {
                    String key = xtr.getAttributeValue(null, "key");
                    if (StringUtils.hasText(key)) {
                        String type = xtr.getAttributeValue(null, "type");
                        if(isSimpleType(type)) {
                            String value = xtr.getAttributeValue(null, "value");
                            map.put(key, parseSimpleValue(value, type));
                        } else {
                            ParameterToChildElementParser parser = resolveParser(type);
                            map.put(key, parser.parseBack(xtr, null));
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public void createChild(String name, Map<String, Object> param, XMLStreamWriter xtw, String elementNameOverride) throws XMLStreamException {
        String elementName = elementNameOverride != null ? elementNameOverride : getElementName();
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, elementName, ACTIVITI_EXTENSIONS_NAMESPACE);
        if(name != null) {
            xtw.writeAttribute(PARAM_NAME, name);
        }
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            Object value = entry.getValue();
            String type = resolveType(value);
            if(type == null) {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass());
            } else if(NULL_TYPE.equals(type)) {
                continue;
            }

            boolean simpleType = isSimpleType(type);
            if(simpleType) {
                xtw.writeEmptyElement(ACTIVITI_EXTENSIONS_PREFIX, "entry", ACTIVITI_EXTENSIONS_NAMESPACE);
                xtw.writeAttribute("value", value.toString());
            } else {
                xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "entry", ACTIVITI_EXTENSIONS_NAMESPACE);
            }
            xtw.writeAttribute("key", entry.getKey());
            xtw.writeAttribute("type", type);
            if(!simpleType) {
                ParameterToChildElementParser parser = resolveParser(type);
                parser.createChild(null, value, xtw, null);
                xtw.writeEndElement();
            }
        }

        xtw.writeEndElement();
    }

    ParameterToChildElementParser resolveParser(String type) {
        if(COLLECTION_TYPE.equals(type)) {
            return collectionChildParser;
        } else if(MAP_TYPE.equals(type)) {
            return this;
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
