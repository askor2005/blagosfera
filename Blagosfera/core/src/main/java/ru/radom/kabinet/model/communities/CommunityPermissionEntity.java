package ru.radom.kabinet.model.communities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "community_permissions")
public class CommunityPermissionEntity extends LongIdentifiable {

	@Column(length = 100, nullable = false)
	private String title;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(nullable = false)
	private int position;

	@Column(length = 1000, nullable = true)
	private String description;

	/**
	 * Таблица с формами объединений в которых данная роль будет доступна
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinTable(name = "community_permission_association_forms", uniqueConstraints =
				@UniqueConstraint(name = "UK_community_permission_id_association_form_id",
					columnNames = {"community_permission_id", "association_form_id"}),
			joinColumns = {
					@JoinColumn(name = "community_permission_id", nullable = false, updatable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "association_form_id", nullable = false, updatable = false)})
	private Set<RameraListEditorItem> communityAssociationForms;

	/**
	 * Коллекция объединений, которым вне зависимости от формы объединения выдана роль (сделано для security ролей)
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinTable(name = "community_security_permissions_communities", uniqueConstraints =
	@UniqueConstraint(name = "UK_community_permission_id_community_id",
			columnNames = {"community_permission_id", "community_id"}),
			joinColumns = {
					@JoinColumn(name = "community_permission_id", nullable = false, updatable = true)},
			inverseJoinColumns = {
					@JoinColumn(name = "community_id", nullable = false, updatable = true)})
	private Set<CommunityEntity> communities;

	@Column(name = "security_role", nullable = false)
	private boolean securityRole;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<RameraListEditorItem> getCommunityAssociationForms() {
		return communityAssociationForms;
	}

	public void setCommunityAssociationForms(Set<RameraListEditorItem> communityAssociationForms) {
		this.communityAssociationForms = communityAssociationForms;
	}

	public Set<CommunityEntity> getCommunities() {
		return communities;
	}

	public void setCommunities(Set<CommunityEntity> communities) {
		this.communities = communities;
	}

	public boolean isSecurityRole() {
		return securityRole;
	}

	public void setSecurityRole(boolean securityRole) {
		this.securityRole = securityRole;
	}

	public CommunityPermission toDomain(boolean withCommunities, boolean withAssociationForms) {
		CommunityPermission result = new CommunityPermission();
		result.setId(getId());
		result.setName(getName());
		result.setTitle(getTitle());
		result.setSecurityRole(isSecurityRole());
		result.setPosition(getPosition());
		result.setDescription(getDescription());
		if (withCommunities && getCommunities() != null) {
			Set<CommunityEntity> communitiesEntities = getCommunities();
			List<Community> communities = new ArrayList<>();
			for (CommunityEntity community : communitiesEntities) {
				communities.add(community.toDomain());
			}
			result.setCommunities(communities);
		}
		if (withAssociationForms && getCommunityAssociationForms() != null) {
			Set<RameraListEditorItem> listEditorItemEntities = getCommunityAssociationForms();
			List<ListEditorItem> listEditorItems = new ArrayList<>();
			for (RameraListEditorItem listEditorItemEntity : listEditorItemEntities) {
				listEditorItems.add(listEditorItemEntity.toDomain());
			}
			result.setAssociationForms(listEditorItems);
		}
		return result;
	}

	public static CommunityPermission toDomainSafe(CommunityPermissionEntity communityPermissionEntity,
												   boolean withCommunities, boolean withAssociationForms) {
		CommunityPermission result = null;
		if (communityPermissionEntity != null) {
			result = communityPermissionEntity.toDomain(withCommunities, withAssociationForms);
		}
		return result;
	}

	public static List<CommunityPermission> toListDomain(List<CommunityPermissionEntity> communityPermissionEntities,
												   boolean withCommunities, boolean withAssociationForms) {
		List<CommunityPermission> result = null;
		if (communityPermissionEntities != null) {
			result = new ArrayList<>();
			for (CommunityPermissionEntity communityPermissionEntity : communityPermissionEntities) {
				result.add(toDomainSafe(communityPermissionEntity, withCommunities, withAssociationForms));
			}
		}
		return result;
	}

}
