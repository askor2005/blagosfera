package ru.askor.blagosfera.logging.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
public class LoggingSettings {

    private Long id;
    private AuditLevel auditLevel = AuditLevel.OFF;
    private List<String> whitelist = new ArrayList<>();
    private List<String> blacklist = new ArrayList<>();

    public LoggingSettings() {
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
