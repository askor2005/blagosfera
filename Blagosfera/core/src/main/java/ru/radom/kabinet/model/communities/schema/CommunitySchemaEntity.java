package ru.radom.kabinet.model.communities.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.askor.blagosfera.domain.community.schema.CommunitySchema;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_schema")
public class CommunitySchemaEntity extends LongIdentifiable {

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunitySchemaUnitEntity> units = new ArrayList<>();
	
	@JsonIgnore
	@JoinColumn(name = "community_id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityEntity community;

	@Column(name = "bgImageUrl", length = 200)
    private String bgImageUrl;
    
	@Column(name = "width")
    private Integer width;
	
	@Column(name = "height")
    private Integer height;
	
	@Column(name = "scrollLeft")
    private Integer scrollLeft;
	
	@Column(name = "scrollTop")
    private Integer scrollTop;
	
	public CommunitySchemaEntity() {
	}

    public CommunitySchema toDomain() {
        CommunitySchema schema = new CommunitySchema();

        schema.setId(getId());
        schema.setCommunityId(getCommunity().getId());
        schema.setBgImageUrl(getBgImageUrl());
        schema.setWidth(getWidth());
        schema.setHeight(getHeight());
        schema.setScrollLeft(getScrollLeft());
        schema.setScrollTop(getScrollTop());

        for (CommunitySchemaUnitEntity unit : getUnits()) {
            schema.getUnits().add(unit.toDomain());
        }

        return schema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunitySchemaEntity)) return false;

        CommunitySchemaEntity that = (CommunitySchemaEntity) o;
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
	
	public List<CommunitySchemaUnitEntity> getUnits() {
		return units;
	}

	public CommunityEntity getCommunity() {
		return community;
	}

	public void setCommunity(CommunityEntity community) {
		this.community = community;
	}

	public String getBgImageUrl() {
		return bgImageUrl;
	}

	public void setBgImageUrl(String bgImageUrl) {
		this.bgImageUrl = bgImageUrl;
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

	public Integer getScrollLeft() {
		return scrollLeft;
	}

	public void setScrollLeft(Integer scrollLeft) {
		this.scrollLeft = scrollLeft;
	}

	public Integer getScrollTop() {
		return scrollTop;
	}

	public void setScrollTop(Integer scrollTop) {
		this.scrollTop = scrollTop;
	}
}
