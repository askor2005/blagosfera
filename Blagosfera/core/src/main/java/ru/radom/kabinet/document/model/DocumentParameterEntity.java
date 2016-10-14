package ru.radom.kabinet.document.model;

import ru.askor.blagosfera.domain.document.DocumentParameter;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 11.08.2015.
 * Класс - сушность сохраняемые параметры Документа
 */
@Entity
@Table(name = "flow_of_document_parameter")
public class DocumentParameterEntity extends LongIdentifiable {

    @Column(name = "parameter_name", length = 1000)
    private String name;

    @Column(name = "parameter_value", length = 100000)
    private String value;

    @JoinColumn(name = "document_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentEntity document;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public DocumentParameter toDomain() {
        DocumentParameter result = new DocumentParameter();
        result.setId(getId());
        result.setName(getName());
        result.setValue(getValue());
        return result;
    }

    public static DocumentParameter toDomainSafe(DocumentParameterEntity entity) {
        DocumentParameter result = null;
        if (entity != null) {
            result = entity.toDomain();
        }
        return result;
    }

    public static List<DocumentParameter> toDomainList(List<DocumentParameterEntity> entities) {
        List<DocumentParameter> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (DocumentParameterEntity entity : entities) {
                result.add(toDomainSafe(entity));
            }
        }
        return result;
    }
}
