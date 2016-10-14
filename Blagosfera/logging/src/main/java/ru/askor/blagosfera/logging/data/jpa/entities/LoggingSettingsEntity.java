package ru.askor.blagosfera.logging.data.jpa.entities;

import ru.askor.blagosfera.logging.domain.AuditLevel;
import ru.askor.blagosfera.logging.domain.LoggingSettings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
@Entity
@Table(name = "logging_settings")
public class LoggingSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logging_settings_id_generator")
    @SequenceGenerator(name = "logging_settings_id_generator", sequenceName = "logging_settings_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "audit_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditLevel auditLevel = AuditLevel.OFF;

    @ElementCollection
    @CollectionTable(name = "execution_target_whitelist", joinColumns = @JoinColumn(name = "logging_settings_id"))
    private List<String> whitelist = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "execution_target_blacklist", joinColumns = @JoinColumn(name = "logging_settings_id"))
    private List<String> blacklist = new ArrayList<>();

    public LoggingSettingsEntity() {
    }

    public LoggingSettings toDomain() {
        LoggingSettings loggingSettings = new LoggingSettings();
        loggingSettings.setId(getId());
        loggingSettings.setAuditLevel(getAuditLevel());
        loggingSettings.getWhitelist().addAll(getWhitelist());
        loggingSettings.getBlacklist().addAll(getBlacklist());
        return loggingSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggingSettingsEntity)) return false;

        LoggingSettingsEntity that = (LoggingSettingsEntity) o;

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

    public AuditLevel getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(AuditLevel auditLevel) {
        this.auditLevel = auditLevel;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }
}
