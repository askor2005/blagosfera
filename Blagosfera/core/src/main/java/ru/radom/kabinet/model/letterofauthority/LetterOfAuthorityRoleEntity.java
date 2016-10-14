package ru.radom.kabinet.model.letterofauthority;

import ru.askor.blagosfera.domain.letterofauthority.LetterOfAuthorityRole;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import javax.persistence.*;

/**
 * Сущность - права доверенности
 * Created by vgusev on 21.09.2015.
 */
@Entity
@Table(name = "letter_of_authority_role")
public class LetterOfAuthorityRoleEntity extends LongIdentifiable {

    // Ключ
    @Column(name = "key", length = 500, unique = true)
    private String key;

    // Наименование
    @Column(name = "name", length = 500)
    private String name;

    // Скрипт создания документа
    @Column(name = "document_script", length = 100000)
    private String createDocumentScript;

    // Тип объекта в рамках которого действует доверенность
    @Column(name = "scope_type", length = 100)
    private String scopeType;

    // Ссылка на объект итем компонента универсальных списков (для подтипизации объединений)
    @JoinColumn(name = "list_editor_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RameraListEditorItem rameraListEditorItem;

    // Наименование типа роли в объекте в рамках которого создаётся доверенность
    // Для объединения например: Должность, Роль
    @Column(name = "scope_role_type", length = 500)
    private String scopeRoleType;

    // Наименование роли в объекте в рамках которого создаётся доверенность
    // Для объединения например: Руквоводитель, Редактор пользовательских полей
    @Column(name = "scope_role_name", length = 500)
    private String scopeRoleName;

    public LetterOfAuthorityRoleEntity() {
    }

    public LetterOfAuthorityRole toDomain() {
        LetterOfAuthorityRole result = new LetterOfAuthorityRole();
        result.setId(getId());
        result.setKey(getKey());
        result.setName(getName());
        result.setCreateDocumentScript(getCreateDocumentScript());
        result.setScopeType(getScopeType());
        result.setScopeRoleType(getScopeRoleType());
        result.setScopeRoleName(getScopeRoleName());
        return result;
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

    public RameraListEditorItem getRameraListEditorItem() {
        return rameraListEditorItem;
    }

    public void setRameraListEditorItem(RameraListEditorItem rameraListEditorItem) {
        this.rameraListEditorItem = rameraListEditorItem;
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
