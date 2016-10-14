package ru.askor.blagosfera.data.jpa.entities.support;


import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;

import javax.persistence.*;

/**
 * табличка запросов в поддержку
 */
@Entity
@Table(name = "support_requests")
public class SupportRequestEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "support_requests_id_generator")
    @SequenceGenerator(name = "support_requests_id_generator", sequenceName = "support_requests_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "email",columnDefinition = "TEXT",nullable = false)
    private String email;
    @Column(name = "theme",columnDefinition = "TEXT",nullable = false)
    private String theme;
    @Column(name = "description",columnDefinition = "TEXT",nullable = false)
    private String description;
    @Column(name="status",nullable = false,columnDefinition = "TEXT")
    @Enumerated(value = EnumType.STRING)
    private SupportRequestStatus status;
    @JoinColumn(name = "support_request_type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SupportRequestTypeEntity supportRequestType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }
    public SupportRequestTypeEntity getSupportRequestType() {
        return supportRequestType;
    }
    public void setSupportRequestType(SupportRequestTypeEntity supportRequestType) {
        this.supportRequestType = supportRequestType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupportRequestEntity)) return false;

        SupportRequestEntity that = (SupportRequestEntity) o;

        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    public SupportRequest toDomain() {
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setEmail(getEmail());
        supportRequest.setTheme(getTheme());
        supportRequest.setDescription(getDescription());
        supportRequest.setId(getId());
        supportRequest.setStatus(getStatus());
        supportRequest.setType(getSupportRequestType() != null ? getSupportRequestType().toDomain() : null);
        return supportRequest;
    }
}
