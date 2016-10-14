package ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute;

/**
 * Created by alex on 06.10.2015.<br/>
 * Преобразователь строки в нужный формат
 */
public interface AttributeValueExtractor<T> {

    /**
     * Преобразовать значение аттрибута их строки в нужный формат
     */
    T extract(String attributeValue);
}
