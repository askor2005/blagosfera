package ru.radom.blagosferabp.activiti.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * Хранилище ключ значения для
 */
@Getter
@Setter
@Entity
@Table(name = "bp_value_storage")
@NoArgsConstructor
@EqualsAndHashCode(of = "key")
public class StoredKeyValue {

    /**
     * Ключ
     */
    @Id
    @Column(name = "key", unique = true, length = 255, nullable = false)
    private String key;

    /**
     * Сериализованное значение
     */
    @Column(name = "value", nullable = false)
    private byte[] value;

    /**
     * Дата последней модификации значения
     */
    @Column(name = "last_modified", nullable = false)
    private Date lastModified;
}
