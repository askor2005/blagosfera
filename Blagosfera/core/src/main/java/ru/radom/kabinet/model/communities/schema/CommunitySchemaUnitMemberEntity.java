package ru.radom.kabinet.model.communities.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitMember;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity(name = "community_schema_units_members")
@Table(name = "community_schema_units_members")
public class CommunitySchemaUnitMemberEntity extends LongIdentifiable {

	@JsonIgnore
	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity user;

	@JsonIgnore
	@JoinColumn(name = "unit_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunitySchemaUnitEntity unit;

    // not persistent
	@Transient
	private String ikp;

    @Transient
	private String fullName;

	@Transient
	private String email;

	public CommunitySchemaUnitMemberEntity() {
	}

    public CommunitySchemaUnitMember toDomain() {
        CommunitySchemaUnitMember unitMember = new CommunitySchemaUnitMember();

        unitMember.setId(getId());
        unitMember.setUserId(getUser().getId());
        unitMember.setUnitId(getUnit().getId());
        unitMember.setIkp(getIkp());
        unitMember.setFullName(getFullName());
        unitMember.setEmail(getEmail());

        return unitMember;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunitySchemaUnitMemberEntity)) return false;

        CommunitySchemaUnitMemberEntity that = (CommunitySchemaUnitMemberEntity) o;
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public CommunitySchemaUnitEntity getUnit() {
		return unit;
	}

	public void setUnit(CommunitySchemaUnitEntity unit) {
		this.unit = unit;
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
