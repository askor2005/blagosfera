package ru.askor.blagosfera.domain.document;

import java.util.Set;

/**
 * Created by Maxim Nikitin on 28.03.2016.
 */
public class DocumentFolder {

    private Long id;
    private String name;
    private String description;
    private Set<Document> documents;

    public DocumentFolder() {
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

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }
}
