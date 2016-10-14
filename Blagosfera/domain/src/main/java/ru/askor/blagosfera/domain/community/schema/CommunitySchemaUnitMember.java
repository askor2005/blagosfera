package ru.askor.blagosfera.domain.community.schema;

/**
 * Created by mnikitin on 20.06.2016.
 */
public class CommunitySchemaUnitMember {

    private Long id;
    private Long userId;
    private Long unitId;
    private String ikp;
    private String fullName;
    private String email;

    public CommunitySchemaUnitMember() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getIkp() {
        return ikp;
    }

    public void setIkp(String ikp) {
        this.ikp = ikp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
