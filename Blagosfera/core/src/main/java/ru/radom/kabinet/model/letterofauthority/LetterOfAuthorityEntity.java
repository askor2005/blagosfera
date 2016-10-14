package ru.radom.kabinet.model.letterofauthority;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityServiceImpl;
import ru.radom.kabinet.web.lettersofauthority.dto.LetterOfAuthorityDto;
import ru.radom.kabinet.web.lettersofauthority.dto.LetterOfAuthorityRoleDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "letters_of_authorities")
public class LetterOfAuthorityEntity extends LongIdentifiable {

    // Пользователь, который передал права
    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    // Пользователь, который получил права
    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "delegate_id", nullable = false)
    private UserEntity delegate;

    // Ссылка на участника системы в рамках которого выдаётся доверенность
    @Any(metaColumn = @Column(name = "scope_type", length = 50), fetch = FetchType.EAGER)
    @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
            @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY),
            @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER)
    })
    @JoinColumn(name = "scope_id")
    private RadomAccount scope;

    // Ссылка на права в системе у данной доверенности
    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "role_id", nullable = false)
    private LetterOfAuthorityRoleEntity letterOfAuthorityRole;

    // Ссылка на документ - доверенность
    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    // Список атрибутов доверенностей
    @OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "letterOfAuthority")
    private Set<LetterOfAuthorityAttributeEntity> attributes;

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public UserEntity getDelegate() {
        return delegate;
    }

    public void setDelegate(UserEntity delegate) {
        this.delegate = delegate;
    }

    public RadomAccount getScope() {
        return scope;
    }

    public void setScope(RadomAccount scope) {
        this.scope = scope;
    }

    public LetterOfAuthorityRoleEntity getLetterOfAuthorityRole() {
        return letterOfAuthorityRole;
    }

    public void setLetterOfAuthorityRole(LetterOfAuthorityRoleEntity letterOfAuthorityRole) {
        this.letterOfAuthorityRole = letterOfAuthorityRole;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public Set<LetterOfAuthorityAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<LetterOfAuthorityAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public static List<LetterOfAuthorityDto> toDto(List<LetterOfAuthorityEntity> letterOfAuthorityEntities) {
        List<LetterOfAuthorityDto> result = new ArrayList<>();
        for (LetterOfAuthorityEntity letterOfAuthorityEntity : letterOfAuthorityEntities) {
            result.add(letterOfAuthorityEntity.toDto());
        }
        return result;
    }

    public LetterOfAuthorityDto toDto() {
        return new LetterOfAuthorityDto(
                getId(),
                LetterOfAuthorityServiceImpl.toDto(this.getOwner()),
                LetterOfAuthorityServiceImpl.toDto(this.getDelegate()),
                LetterOfAuthorityServiceImpl.toDto(this.getScope()),
                new LetterOfAuthorityRoleDto(getLetterOfAuthorityRole().toDomain()),
                getDocument()
        );
    }
}
