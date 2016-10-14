package ru.radom.kabinet.model.invite;

import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.List;

/**
 * Модель отношения (список значений кем может приходится приглашаемый)
 */
@Entity
@Table(name = InviteRelationshipType.TABLE_NAME)
public class InviteRelationshipType extends LongIdentifiable {
    public static final String TABLE_NAME = "invite_relationship_types";

    public static class Columns {
        public static final String NAME = "name";
        public static final String INDEX = "index";
    }

    @Column(name = Columns.NAME)
    private String name;

    @Column(name = Columns.INDEX)
    private Integer index;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "invites_relationships", joinColumns = {@JoinColumn(name = "invite_relationship_type_id", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "invite_id", nullable = false, updatable = false)})
    private List<InvitationEntity> invites;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<InvitationEntity> getInvites() {
        return invites;
    }

    public void setInvites(List<InvitationEntity> invites) {
        this.invites = invites;
    }
    public InviteRelationshipTypeDomain toDomain() {
        return new InviteRelationshipTypeDomain(getId(),getName(),getIndex());
    }
}