package ru.radom.kabinet.model.fields;

import ru.askor.blagosfera.domain.field.FieldFile;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ссылки на файлы поля
 * Created by vgusev on 11.09.2015.
 */
@Entity
@Table(name = "field_files")
public class FieldFileEntity extends LongIdentifiable {

    @Column(name = "name", length = 1000)
    private String name;

    @Column(name = "url", length = 10000)
    private String url;

    @JoinColumn(name = "field_value_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private FieldValueEntity fieldValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FieldValueEntity getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(FieldValueEntity fieldValue) {
        this.fieldValue = fieldValue;
    }

    public FieldFile toDomain() {
        FieldFile result = new FieldFile();
        result.setId(getId());
        result.setName(getName());
        result.setUrl(getUrl());
        return result;
    }

    public static List<FieldFile> toDomainList(List<FieldFileEntity> fieldFiles) {
        List<FieldFile> result = new ArrayList<>();
        if (fieldFiles != null) {
            for (FieldFileEntity fieldFile : fieldFiles) {
                result.add(fieldFile.toDomain());
            }
        }
        return result;
    }
}
