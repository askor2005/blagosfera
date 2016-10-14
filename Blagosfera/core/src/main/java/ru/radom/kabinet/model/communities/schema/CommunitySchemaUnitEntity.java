package ru.radom.kabinet.model.communities.schema;

import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_schema_units")
public class CommunitySchemaUnitEntity extends SchemaBasic {

	@Column(nullable = false)
	private CommunitySchemaUnitType type;

	@Column(length = 1000, nullable = false)
	private String name;

	@JoinColumn(name = "manager_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity manager;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunitySchemaConnectionEntity> connections = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunitySchemaConnectionEntity> incomingConnections = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id")
	private List<CommunitySchemaUnitMemberEntity> members = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "schema_id")
	private CommunitySchemaEntity schema;
	
	@Column
	private Integer x;
	
	@Column
	private Integer y;

	@Column
	private Integer width;
	
	@Column
	private Integer height;
	
	@Column
	private String bgColor;

    // not persistent
	@Transient
	private String managerIkp;

	@Transient
	private String managerFullName;

    @Column
	private String draw2dId;

	public CommunitySchemaUnitEntity() {
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunitySchemaUnitEntity)) return false;

        CommunitySchemaUnitEntity that = (CommunitySchemaUnitEntity) o;
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommunitySchemaUnitType getType() {
		return type;
	}

	public void setType(CommunitySchemaUnitType type) {
		this.type = type;
	}

	public UserEntity getManager() {
		return manager;
	}

	public void setManager(UserEntity manager) {
		this.manager = manager;
	}

	public List<CommunitySchemaConnectionEntity> getConnections() {
		return connections;
	}

    public List<CommunitySchemaConnectionEntity> getIncomingConnections() {
		return incomingConnections;
	}
	
	public List<CommunitySchemaUnitMemberEntity> getMembers() {
		return members;
	}

	public CommunitySchemaEntity getSchema() {
		return schema;
	}

	public void setSchema(CommunitySchemaEntity schema) {
		this.schema = schema;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getManagerIkp() {
		return managerIkp;
	}

	public void setManagerIkp(String managerIkp) {
		this.managerIkp = managerIkp;
	}

	public String getDraw2dId() {
		return draw2dId;
	}

	public void setDraw2dId(String draw2dId) {
		this.draw2dId = draw2dId;
	}

	public String getManagerFullName() {
		return managerFullName;
	}

	public void setManagerFullName(String managerFullName) {
		this.managerFullName = managerFullName;
	}

	public CommunitySchemaUnit toDomain() {
		CommunitySchemaUnit unit = new CommunitySchemaUnit();

		unit.setId(getId());
		unit.setName(getName());
		unit.setBgColor(getBgColor());
		unit.setHeight(getHeight() != null ? getHeight() : -1);
		unit.setWidth(getWidth() != null ? getWidth() : -1);
		unit.setX(getX() != null ? getX() : -1);
		unit.setY(getY() != null ? getY() : -1);
		unit.setType(getType());
        unit.setSchemaId(getSchema().getId());
        unit.setManagerIkp(getManagerIkp());
        unit.setManagerFullName(getManagerFullName());
        unit.setDraw2dId(getDraw2dId());

		if (getManager() != null) {
			unit.setManager(getManager().toDomain());
		}

        for (CommunitySchemaConnectionEntity connection : getConnections()) {
            unit.getConnections().add(connection.toDomain());
        }

        for (CommunitySchemaConnectionEntity connection : getIncomingConnections()) {
            unit.getIncomingConnections().add(connection.toDomain());
        }

        for (CommunitySchemaUnitMemberEntity member : getMembers()) {
            unit.getMembers().add(member.toDomain());
        }

		return unit;
	}

	public static CommunitySchemaUnit toDomainSafe(CommunitySchemaUnitEntity communitySchemaUnitEntity) {
		CommunitySchemaUnit result = null;
		if (communitySchemaUnitEntity != null) {
			result = communitySchemaUnitEntity.toDomain();
		}
		return result;
	}

	public static List<CommunitySchemaUnit> toListDomain(List<CommunitySchemaUnitEntity> communitySchemaUnitEntities) {
		List<CommunitySchemaUnit> result = null;
		if (communitySchemaUnitEntities != null) {
			result = new ArrayList<>();
			for (CommunitySchemaUnitEntity communitySchemaUnitEntity : communitySchemaUnitEntities) {
				result.add(toDomainSafe(communitySchemaUnitEntity));
			}
		}
		return result;
	}
}
