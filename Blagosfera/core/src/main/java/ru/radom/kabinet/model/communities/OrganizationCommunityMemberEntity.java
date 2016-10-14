package ru.radom.kabinet.model.communities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 19.10.2015.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = OrganizationCommunityMemberEntity.TABLE_NAME, uniqueConstraints = {
        // Объединение и организация должно быть уникальной записью
        @UniqueConstraint( columnNames = {
                OrganizationCommunityMemberEntity.COLUMNS.COMMUNITY_ID,
                OrganizationCommunityMemberEntity.COLUMNS.ORGANIZATION_ID
            }
        )
})
public class OrganizationCommunityMemberEntity extends LongIdentifiable {

    public static final String TABLE_NAME = "organization_community_members";
    public class COLUMNS {
        public static final String COMMUNITY_ID = "community_id";
        public static final String ORGANIZATION_ID = "organization_id";
        public static final String STATUS = "status";
        public static final String DOCUMENT_ID = "document_id";
        //public static final String JOIN_DATE = "join_date";
    }

    // Ссылка на объединение в котором состоит организация в качестве члена
    @JoinColumn(name = COLUMNS.COMMUNITY_ID, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityEntity community;

    // Член объединения - организация
    @JoinColumn(name = COLUMNS.ORGANIZATION_ID, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityEntity organization;

    // Статус члена - организации
    @Column(name = COLUMNS.STATUS, nullable = false)
    private CommunityMemberStatus status;

    // Документ, который связан с участником объединения
    @JoinColumn(name = COLUMNS.DOCUMENT_ID, nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private DocumentEntity document;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organizationCommunityMember")
    private List<OrganizationCommunityMemberParameter> organizationCommunityMemberParameters;

    /*@Column(name = COLUMNS.JOIN_DATE, nullable = false)
    private Date joinDate;*/

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public CommunityEntity getOrganization() {
        return organization;
    }

    public void setOrganization(CommunityEntity organization) {
        this.organization = organization;
    }

    public CommunityMemberStatus getStatus() {
        return status;
    }

    public void setStatus(CommunityMemberStatus status) {
        this.status = status;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public List<OrganizationCommunityMemberParameter> getOrganizationCommunityMemberParameters() {
        return organizationCommunityMemberParameters;
    }

    public void setOrganizationCommunityMemberParameters(List<OrganizationCommunityMemberParameter> organizationCommunityMemberParameters) {
        this.organizationCommunityMemberParameters = organizationCommunityMemberParameters;
    }

    public OrganizationCommunityMember toDomain(Community community, Community organization) {
        OrganizationCommunityMember result = new OrganizationCommunityMember();
        result.setId(getId());
        result.setStatus(getStatus());
        result.setCommunity(community);
        result.setOrganization(organization);
        if (getDocument() != null) {
            result.setDocument(getDocument().toDomain(false, null));
        }
        return result;
    }

    public static OrganizationCommunityMember toDomainSafe(OrganizationCommunityMemberEntity entity, Community community, Community organization) {
        OrganizationCommunityMember result = null;
        if (entity != null) {
            result = entity.toDomain(community, organization);
        }
        return result;
    }
}
