package ru.radom.kabinet.model.communities.schema;

import ru.askor.blagosfera.domain.community.schema.CommunitySchemaConnectionType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/*
insert into community_schema_connection_type (id, name, color ) values(1, 'Прямое подчинение', '#A38B8C', false);
insert into community_schema_connection_type (id, name, color ) values(2, 'Подотчетность', '#0815F9', true);
insert into community_schema_connection_type (id, name, color ) values(3, 'Совместная работа', '#0AFA32', true);
insert into community_schema_connection_type (id, name, color ) values(4, 'Согласование', '#E1FA04', true);
*/

@Entity
@Table(name = "community_schema_connection_type")
public class CommunitySchemaConnectionTypeEntity extends LongIdentifiable {

	@Column(name = "color", nullable = false)
	private String color;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "reversable", nullable = false)
	private Boolean reversable;

	public CommunitySchemaConnectionTypeEntity(){
	}

    public CommunitySchemaConnectionType toDomain() {
        CommunitySchemaConnectionType connectionType = new CommunitySchemaConnectionType();

        connectionType.setId(getId());
        connectionType.setColor(getColor());
        connectionType.setName(getName());
        connectionType.setReversable(isReversable());

        return connectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunitySchemaConnectionTypeEntity)) return false;

        CommunitySchemaConnectionTypeEntity that = (CommunitySchemaConnectionTypeEntity) o;
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isReversable() {
		return reversable;
	}

	public void setReversable(boolean reversable) {
		this.reversable = reversable;
	}
}
