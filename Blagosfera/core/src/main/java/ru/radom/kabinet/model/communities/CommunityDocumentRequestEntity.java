package ru.radom.kabinet.model.communities;

import ru.askor.blagosfera.domain.community.CommunityDocumentRequest;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с данными по запросу пользователя на вступление в объединение в котором нужно подипсать документы
 * Created by vgusev on 15.07.2016.
 */
@Entity
@Table(name = "community_document_request")
public class CommunityDocumentRequestEntity extends LongIdentifiable {

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @JoinColumn(name = "community_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityEntity community;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "community_document_request_document",
            joinColumns = {
                    @JoinColumn(name = "request_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "document_id", nullable = false, updatable = false)})
    private List<DocumentEntity> documents = new ArrayList<>();

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public List<DocumentEntity> getDocuments() {
        return documents;
    }

    public CommunityDocumentRequest toDomain() {
        CommunityDocumentRequest result = new CommunityDocumentRequest();
        result.setId(getId());
        result.setUser(user.toDomain());
        result.setCommunity(community.toDomain());
        if (documents != null) {
            result.getDocuments().addAll(DocumentEntity.toDomainList(documents, true, null));
        }
        return result;
    }

    public static CommunityDocumentRequest toSafeDomain(CommunityDocumentRequestEntity entity) {
        return entity == null ? null : entity.toDomain();
    }

    public static List<CommunityDocumentRequest> toDomainList(List<CommunityDocumentRequestEntity> entities) {
        List<CommunityDocumentRequest> result = null;
        if (entities != null && !entities.isEmpty()) {
            result = new ArrayList<>();
            for (CommunityDocumentRequestEntity entity : entities) {
                result.add(toSafeDomain(entity));
            }
        }
        return result;
    }
}
