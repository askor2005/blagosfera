package ru.askor.blagosfera.domain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * модель запроса о пользователей
 */
public class SupportRequest {
    private Long id;
    private String email;
    private String theme;
    private String description;
    private SupportRequestType type;
    private SupportRequestStatus status;

    public SupportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }

    public SupportRequestType getType() {
        return type;
    }

    public void setType(SupportRequestType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SupportRequest(String email, String theme, String description, SupportRequestStatus status,SupportRequestType supportRequestType) {
        this.email = email;
        this.theme = theme;
        this.description = description;
        this.status= status;
        this.type = supportRequestType;
    }

    public SupportRequest() {
    }
}
