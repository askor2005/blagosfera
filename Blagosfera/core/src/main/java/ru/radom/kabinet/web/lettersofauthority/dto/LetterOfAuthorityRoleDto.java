package ru.radom.kabinet.web.lettersofauthority.dto;

import ru.askor.blagosfera.domain.letterofauthority.LetterOfAuthorityRole;

/**
 * Created by vgusev on 23.09.2015.
 */
public class LetterOfAuthorityRoleDto {
    private Long id;
    private String key;
    private String name;
    private String createDocumentScript;

    public LetterOfAuthorityRoleDto(){
    }

    public LetterOfAuthorityRoleDto(LetterOfAuthorityRole role) {
        id = role.getId();
        key = role.getKey();
        name = role.getName();
        createDocumentScript = role.getCreateDocumentScript();
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
}
