package ru.askor.blagosfera.domain.letterofauthority;

public class LetterOfAuthorityRole {

    private Long id;
    private String key;
    private String name;
    private String createDocumentScript;
    private String scopeType;
    private String scopeRoleType;
    private String scopeRoleName;

    public LetterOfAuthorityRole() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDocumentScript() {
        return createDocumentScript;
    }

    public void setCreateDocumentScript(String createDocumentScript) {
        this.createDocumentScript = createDocumentScript;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getScopeRoleType() {
        return scopeRoleType;
    }

    public void setScopeRoleType(String scopeRoleType) {
        this.scopeRoleType = scopeRoleType;
    }

    public String getScopeRoleName() {
        return scopeRoleName;
    }

    public void setScopeRoleName(String scopeRoleName) {
        this.scopeRoleName = scopeRoleName;
    }
}
