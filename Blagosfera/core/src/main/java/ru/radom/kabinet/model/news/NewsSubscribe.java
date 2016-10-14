package ru.radom.kabinet.model.news;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;

@Entity
@Table(name = "news_subscribes")
public class NewsSubscribe extends LongIdentifiable {

	@Any(metaColumn = @Column(name = "scope_type", length = 50), fetch = FetchType.EAGER)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY), @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER) })
	@JoinColumn(name = "scope_id")
	private LongIdentifiable scope;

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private UserEntity user;

	@Column(name = "scope_id", insertable = false, updatable = false)
	private Long scopeId;

	@Column(name = "scope_type", insertable = false, updatable = false)
	private String scopeType;

	public NewsSubscribe(LongIdentifiable scope, UserEntity user) {
		super();
		this.scope = scope;
		this.user = user;
	}

	public NewsSubscribe() {

	}

	public LongIdentifiable getScope() {
		return scope;
	}

	public void setScope(LongIdentifiable scope) {
		this.scope = scope;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Long getScopeId() {
		return scopeId;
	}

	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}
}
