package ru.radom.blagosferabp.activiti.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * DTO для {@link ru.radom.blagosferabp.activiti.model.StoredKeyValue}
 */
@Data
public class StoredKeyValueDTO {

    /**
     * Ключ
     */
    private String key;

    /**
     * Значение
     */
    private Object value;

    /**
     * Дата последней модификации значения
     */
    private Date lastModified;
}
