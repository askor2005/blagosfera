package ru.radom.blagosferabp.activiti.component.converters.xml.parsers;

import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.radom.blagosfera.scripting.commons.ScriptData;
import ru.radom.blagosferabp.activiti.rabbit.RabbitTask;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 12.11.2015.<br/>
 * TODO comment me pls
 */
public class ScriptDataChildParser extends BaseChildElementParser implements ParameterToChildElementParser<ScriptData> {

    private final String SCRIPT_CONTEXT = "scriptContext";
    private final String SCRIPT_BODY = "script";
    private final String SCRIPT_OUTPUT = "scriptOutput";

    @Override
    public String getElementName() {
        return "scriptData";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
        String param = xtr.getAttributeValue(null, PARAM_NAME);
        if(param != null) {
            RabbitTask task = (RabbitTask) parentElement;
            task.getCustomParameters().put(param, parseBack(xtr, null));
        }
    }

    @Override
    public ScriptData parseBack(XMLStreamReader xtr, String elementNameOverride) throws XMLStreamException {
        ScriptData data = new ScriptData();
        while (true) {
            xtr.next();
            if(xtr.isStartElement()) {
                String name = xtr.getLocalName();
                if(SCRIPT_BODY.equals(name)) {
                    break;
                }
                switch (name) {
                    case SCRIPT_CONTEXT:
                        data.setContext(parseMap(name, xtr));
                        break;
                    case SCRIPT_OUTPUT:
                        data.setOutputMapping(parseMap(name, xtr));
                        break;
                    default:
                }
            }
        }
        while(true) {
            xtr.next();
            int eventType = xtr.getEventType();
            if(eventType == XMLEvent.CDATA || eventType == XMLEvent.CHARACTERS) {
                data.setScript(xtr.getText());
                break;
            }
            if(xtr.isEndElement() && SCRIPT_BODY.equals(xtr.getLocalName())) {
                data.setScript("");
                break;
            }
        }
        return data;
    }

    private Map parseMap(String name, XMLStreamReader xtr) throws XMLStreamException {
        Map<String, String> map = null;
        while (true) {
            xtr.next();
            if(xtr.isEndElement() && xtr.getLocalName().equals(name)) {
                break;
            }
            if(xtr.isStartElement()) {
                if ("entry".equals(xtr.getLocalName())) {
                    String key = xtr.getAttributeValue(null, "key");
                    if (StringUtils.hasText(key)) {
                        String value = xtr.getAttributeValue(null, "value");
                        if (StringUtils.hasText(value)) {
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            map.put(key, value);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public void createChild(String name, ScriptData param, XMLStreamWriter xtw, String elementNameOverride) throws XMLStreamException {
        String elementName = elementNameOverride != null ? elementNameOverride : getElementName();
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, elementName, ACTIVITI_EXTENSIONS_NAMESPACE);

        xtw.writeAttribute(PARAM_NAME, name);
        Map<String, Object> ctx = param.getContext();
        if(!CollectionUtils.isEmpty(ctx)) {
            xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, SCRIPT_CONTEXT, ACTIVITI_EXTENSIONS_NAMESPACE);
            writeMap(xtw, ctx);
            xtw.writeEndElement();
        }

        Map<String, String> out = param.getOutputMapping();
        if(!CollectionUtils.isEmpty(out)) {
            xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, SCRIPT_OUTPUT, ACTIVITI_EXTENSIONS_NAMESPACE);
            writeMap(xtw, out);
            xtw.writeEndElement();
        }

        String script = param.getScript();
        if (!StringUtils.hasText(script)) {
            script = "";
        }
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, SCRIPT_BODY, ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeCData(script);
        xtw.writeEndElement();

        xtw.writeEndElement();
    }

    private void writeMap(XMLStreamWriter xtw, Map<String, ?> map) throws XMLStreamException {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String value = (String) entry.getValue();
            if(StringUtils.hasText(value)) {
                xtw.writeEmptyElement("entry");
                xtw.writeAttribute("key", entry.getKey());
                xtw.writeAttribute("value", value);
            }
        }
    }
}
