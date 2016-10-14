package ru.radom.kabinet.document.model;

import ru.askor.blagosfera.domain.document.DocumentClass;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель тип шаблона - для документооборота
 * @TODO Надо переименовать в класс документов
 */
@Entity
@Table(name = DocumentClassEntity.TABLE_NAME)
public class DocumentClassEntity extends LongIdentifiable {
    public static final String TABLE_NAME = "documents_types";

    public static class Columns {
        public static final String PARENT_ID = "parent_id";
        public static final String NAME = "name";
        public static final String KEY = "key";
        public static final String POSITION = "position";
    }

    //идентификатор на родителя
    @JoinColumn(name = Columns.PARENT_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentClassEntity parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<DocumentClassEntity> child;

    //наименоваание категории / типа документа
    @Column(name = Columns.NAME, length = 10000)
    private String name;

    //ключ если для типов документов потребуется писать специфичную логику,
    //определение документов в коде будет по ключу
    @Column(name = Columns.KEY)
    private String key;

	//участники документа
	@OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
	private List<DocumentClassDataSourceEntity> participants;

    @Column(name = Columns.POSITION, columnDefinition = "int default 0")
    private Integer position;

    /**
     * Шаблоны, которые принадлежат к этому классу документов
     */
    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    private List<DocumentTemplateEntity> documentTemplates;

    public DocumentClassEntity getParent() {
		return parent;
    }

    public List<DocumentClassEntity> getChild() {
        return child;
    }

    public void setParent(DocumentClassEntity parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

	public List<DocumentClassDataSourceEntity> getParticipants() {
		return participants;
	}

	public void setParticipants(List<DocumentClassDataSourceEntity> participants) {
		this.participants = participants;
	}

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<DocumentTemplateEntity> getDocumentTemplates() {
        return documentTemplates;
    }

    public void setDocumentTemplates(List<DocumentTemplateEntity> documentTemplates) {
        this.documentTemplates = documentTemplates;
    }

    public DocumentClass toDomain() {
        DocumentClass result = new DocumentClass();
        result.setId(getId());
        result.setName(getName());
        result.setDataSources(DocumentClassDataSourceEntity.toDomainList(getParticipants()));
        result.setKey(getKey());
        result.setPosition(getPosition());
        return result;
    }

    public static DocumentClass toDomainSafe(DocumentClassEntity entity) {
        DocumentClass result = null;
        if (entity != null) {
            result = entity.toDomain();
        }
        return result;
    }

    public static List<DocumentClass> toDomainList(List<DocumentClassEntity> entities) {
        List<DocumentClass> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (DocumentClassEntity entity : entities) {
                result.add(toDomainSafe(entity));
            }
        }
        return result;
    }
}