package ru.radom.blagosferabp.activiti.component.converters.xml.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Otts Alexey on 17.11.2015.<br/>
 * Парсер для значение типа {@link Collection}
 */
@Component
public class CollectionChildParser extends ComplexChildParser<Collection> {

    @Autowired
    private MapChildParser mapChildParser;

    @Override
    public String getElementName() {
        return "list";
    }

    @Override
    public Collection parseBack(XMLStreamReader xtr, String elementNameOverride) throws XMLStreamException {
        Collection coll = new ArrayList<>();
        String elementName = elementNameOverride != null ? elementNameOverride : getElementName();
        while (true) {
            xtr.next();
            if(xtr.isEndElement() && xtr.getLocalName().equals(elementName)) {
                break;
            }
            if(xtr.isStartElement()) {
                if ("item".equals(xtr.getLocalName())) {
                    String type = xtr.getAttributeValue(null, "type");
                    if(isSimpleType(type)) {
                        String value = xtr.getAttributeValue(null, "value");
                        coll.add(parseSimpleValue(value, type));
                    } else {
                        ParameterToChildElementParser parser = resolveParser(type);
                        coll.add(parser.parseBack(xtr, null));
                    }
                }
            }
        }
        return coll;
    }

    @Override
    public void createChild(String name, Collection param, XMLStreamWriter xtw, String elementNameOverride) throws XMLStreamException {
        String elementName = elementNameOverride != null ? elementNameOverride : getElementName();
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, elementName, ACTIVITI_EXTENSIONS_NAMESPACE);
        if(name != null) {
            xtw.writeAttribute(PARAM_NAME, name);
        }
        for (Object value : param) {
            String type = resolveType(value);
            if(type == null) {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass());
            } else if(NULL_TYPE.equals(type)) {
                continue;
            }

            boolean simpleType = isSimpleType(type);
            if(simpleType) {
                xtw.writeEmptyElement(ACTIVITI_EXTENSIONS_PREFIX, "item", ACTIVITI_EXTENSIONS_NAMESPACE);
                xtw.writeAttribute("value", value.toString());
            } else {
                xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "item", ACTIVITI_EXTENSIONS_NAMESPACE);
            }
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
            return this;
        } else if(MAP_TYPE.equals(type)) {
            return mapChildParser;
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
