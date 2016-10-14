package ru.radom.kabinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.askor.blagosfera.domain.community.OkvedDomain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "okveds")
public class OkvedEntity extends LongIdentifiable {

	@JsonIgnore
	@JoinColumn(name = "parent_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private OkvedEntity parent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("code")
	private List<OkvedEntity> children;

	@Column(length = 255)
	private String code;

	@Column(name = "short_name", length = 255)
	private String shortName;

	@Column(name = "long_name", length = 512)
	private String longName;

	public OkvedEntity getParent() {
		return parent;
	}

	public void setParent(OkvedEntity parent) {
		this.parent = parent;
	}

	public List<OkvedEntity> getChildren() {
		return children;
	}

	public void setChildren(List<OkvedEntity> children) {
		this.children = children;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public OkvedDomain toDomain() {
		OkvedDomain result = new OkvedDomain();
		result.setId(getId());
		result.setCode(getCode());
		result.setLongName(getLongName());
		result.setShortName(getShortName());
		return result;
	}

	public static OkvedDomain toDomainSafe(OkvedEntity entity) {
		OkvedDomain result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<OkvedDomain> toDomainList(List<OkvedEntity> entities) {
		List<OkvedDomain> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (OkvedEntity entity : entities) {
				result.add(toDomainSafe(entity));
			}
		}
		return result;
	}

}
