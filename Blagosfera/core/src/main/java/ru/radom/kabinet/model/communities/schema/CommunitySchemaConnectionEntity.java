package ru.radom.kabinet.model.communities.schema;

import ru.askor.blagosfera.domain.community.schema.CommunitySchemaConnection;

import javax.persistence.*;

@Entity
@Table(name = "community_schema_connections")
public class CommunitySchemaConnectionEntity extends SchemaBasic {

	@JoinColumn(name = "type_id", nullable = false)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private CommunitySchemaConnectionTypeEntity type;

	@JoinColumn(name = "source_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunitySchemaUnitEntity source;

	@JoinColumn(name = "target_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunitySchemaUnitEntity target;

    @Column
	private String draw2dId;

    @Transient
	private String sourceDraw2dId;

	@Transient
	private String targetDraw2dId;

	public CommunitySchemaConnectionEntity() {
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunitySchemaConnectionEntity)) return false;

        CommunitySchemaConnectionEntity that = (CommunitySchemaConnectionEntity) o;
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public CommunitySchemaConnection toDomain() {
        CommunitySchemaConnection connection = new CommunitySchemaConnection();

        connection.setId(getId());
        connection.setType(getType().toDomain());
        connection.setSourceUnitId(getSource().getId());
        connection.setTargetUnitId(getTarget().getId());
        connection.setDraw2dId(getDraw2dId());
        connection.setSourceDraw2dId(getSourceDraw2dId());
        connection.setTargetDraw2dId(getTargetDraw2dId());

        return connection;
    }

	public CommunitySchemaUnitEntity getSource() {
		return source;
	}

	public void setSource(CommunitySchemaUnitEntity source) {
		this.source = source;
	}

	public CommunitySchemaUnitEntity getTarget() {
		return target;
	}

	public void setTarget(CommunitySchemaUnitEntity target) {
		this.target = target;
	}

	public CommunitySchemaConnectionTypeEntity getType() {
		return type;
	}

	public void setType(CommunitySchemaConnectionTypeEntity type) {
		this.type = type;
	}

	public String getDraw2dId() {
		return draw2dId;
	}

	public void setDraw2dId(String draw2dId) {
		this.draw2dId = draw2dId;
	}

	public String getSourceDraw2dId() {
		return sourceDraw2dId;
	}

	public void setSourceDraw2dId(String sourceDraw2dId) {
		this.sourceDraw2dId = sourceDraw2dId;
	}

	public String getTargetDraw2dId() {
		return targetDraw2dId;
	}

	public void setTargetDraw2dId(String targetDraw2dId) {
		this.targetDraw2dId = targetDraw2dId;
	}
}
