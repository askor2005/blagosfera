package ru.radom.kabinet.web.lettersofauthority.dto;

import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.utils.DateUtils;

public class LetterOfAuthorityDto {

    private Long id;
    private RadomAccountDto owner;
    private RadomAccountDto delegate;
    private RadomAccountDto scope;
    private LetterOfAuthorityRoleDto authorityRole;
    private String documentLink;
    private String documentCode;
    private String documentName;
    private String expiredDate;
    private boolean active;

    public LetterOfAuthorityDto() {
    }

    public LetterOfAuthorityDto(Long id, RadomAccountDto owner, RadomAccountDto delegate, RadomAccountDto scope,
                                LetterOfAuthorityRoleDto authorityRole, DocumentEntity document) {
        this.id = id;
        this.owner = owner;
        this.delegate = delegate;
        this.scope = scope;
        this.authorityRole = authorityRole;
        this.documentLink = document.getLink();
        this.documentCode = document.getCode();
        this.documentName = document.getName();
        if (document.getExpiredDate() != null) {
            this.expiredDate = DateUtils.formatDate(document.getExpiredDate(), DateUtils.Format.DATE);
        }
        this.active = document.getActive();
    }

    public Long getId() {
        return id;
    }

    public RadomAccountDto getOwner() {
        return owner;
    }

    public RadomAccountDto getDelegate() {
        return delegate;
    }

    public RadomAccountDto getScope() {
        return scope;
    }

    public LetterOfAuthorityRoleDto getAuthorityRole() {
        return authorityRole;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public boolean isActive() {
        return active;
    }
}
