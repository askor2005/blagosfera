package ru.radom.kabinet.document.model;

import org.hibernate.annotations.Type;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maxim Nikitin on 28.03.2016.
 */
@Entity
@Table(name = "document_folder")
public class DocumentFolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_folder_id_generator")
    @SequenceGenerator(name = "document_folder_id_generator", sequenceName = "document_folder_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    @Type(type="text")
    private String name;

    @Column(name = "description")
    @Type(type="text")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder")
    private Set<DocumentEntity> documents = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_folder_id")
    public DocumentFolderEntity parentFolder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentFolder")
    public Set<DocumentFolderEntity> subFolders = new HashSet<>();

    public DocumentFolderEntity() {
    }

    public DocumentFolder toDomain(boolean withDocuments, boolean withParticipants) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setId(getId());
        documentFolder.setName(getName());
        documentFolder.setDescription(getDescription());

        if (withDocuments && (getDocuments().size() > 0)) {
            Set<Document> documentsList = new HashSet<>();

            for (DocumentEntity document : getDocuments()) {
                documentsList.add(document.toDomain(withParticipants, null));
            }

            documentFolder.setDocuments(documentsList);
        }

        return documentFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentFolderEntity)) return false;

        DocumentFolderEntity that = (DocumentFolderEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DocumentEntity> getDocuments() {
        return documents;
    }

    public DocumentFolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(DocumentFolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }

    public Set<DocumentFolderEntity> getSubFolders() {
        return subFolders;
    }
}
