package ru.radom.blagosferabp.activiti.component.converters.xml.parsers;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by Otts Alexey on 12.11.2015.<br/>
 * Парсер, который преобразует параметр элемента бизнесс процесса в дочерний элемент
 */
public interface ParameterToChildElementParser<T> {

    String PARAM_NAME = "paramName";

    /**
     * Записывает в выходной поток дочерний элемент
     */
    void createChild(String name, T param, XMLStreamWriter xtw, String elementNameOverride) throws XMLStreamException;

    /**
     * Парсит xml в элемент
     */
    T parseBack(XMLStreamReader xtr, String elementNameOverride) throws XMLStreamException;
}
