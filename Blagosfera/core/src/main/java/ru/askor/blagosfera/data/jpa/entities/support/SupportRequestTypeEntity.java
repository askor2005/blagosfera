package ru.askor.blagosfera.data.jpa.entities.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.askor.blagosfera.domain.support.SupportRequestType;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.web.PageEdition;

import javax.persistence.*;
import java.util.List;

/**
 * категории обращения в техподдержку
 */
@Entity
@Table(name = "support_requests_types")
public class SupportRequestTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "support_requests_types_id_generator")
    @SequenceGenerator(name = "support_requests_types_id_generator", sequenceName = "support_requests_types_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name",columnDefinition = "TEXT",nullable = false, unique = true)
    private String name;
    @Column(name = "admin_emails_list",columnDefinition = "TEXT",nullable = false)
    private String adminEmailsList;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supportRequestType")
    private List<SupportRequestEntity> requests;

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

    public String getAdminEmailsList() {
        return adminEmailsList;
    }

    public void setAdminEmailsList(String adminEmailsList) {
        this.adminEmailsList = adminEmailsList;
    }

    public List<SupportRequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(List<SupportRequestEntity> requests) {
        this.requests = requests;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupportRequestTypeEntity)) return false;

        SupportRequestTypeEntity that = (SupportRequestTypeEntity) o;

        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    public SupportRequestType toDomain() {
        return new SupportRequestType(getId(),getName(),getAdminEmailsList());
    }
}
