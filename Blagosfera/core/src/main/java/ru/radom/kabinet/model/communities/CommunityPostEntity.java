package ru.radom.kabinet.model.communities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitEntity;
import ru.radom.kabinet.model.document.DocumentTemplateSettingEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "community_posts")
public class CommunityPostEntity extends LongIdentifiable {

	@Column(length = 100, nullable = false)
	private String name;

	@Column(nullable = false)
	private int position;

	@Column(nullable = false)
	private int vacanciesCount;

    @Column(length = 100, nullable = true)
    private String mnemo;

	@JsonIgnore
    @JoinColumn(name = "community_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityEntity community;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "community_members_posts", joinColumns = { @JoinColumn(name = "post_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "member_id", nullable = false, updatable = false) })
	private List<CommunityMemberEntity> members;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "community_posts_permissions", joinColumns = { @JoinColumn(name = "post_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "permission_id", nullable = false, updatable = false) })
	private List<CommunityPermissionEntity> permissions;

	@Column(nullable = false)
	private boolean ceo;

	@JsonIgnore
	@JoinColumn(name = "schema_unit_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private CommunitySchemaUnitEntity schemaUnit;

	/**
	 * ИД бина поведения назначения на должность
	 */
	@JsonIgnore
	@Column(name = "appoint_behavior")
	private String appointBehavior;

	/*@JsonIgnore
	@JoinColumn(name = "template_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private DocumentTemplateEntity documentTemplate;*/

	@ManyToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinTable(name = "community_post_document_template",
			joinColumns = {
					@JoinColumn(name = "community_post_id", nullable = false, updatable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "document_template_id", nullable = false)})
	private List<DocumentTemplateSettingEntity> documentTemplateSettings = new ArrayList<>();

	public CommunityPostEntity() {

	}

	public CommunityPostEntity(CommunityEntity community) {
		super();
		this.community = community;
	}

	public CommunityPostEntity(String name, int position, int vacanciesCount, CommunityEntity community, List<CommunityPermissionEntity> permissions) {
		super();
		this.name = name;
		this.position = position;
		this.vacanciesCount = vacanciesCount;
		this.community = community;
		this.permissions = permissions;
	}

	public CommunityPostEntity(String name, int position, int vacanciesCount, CommunityEntity community, List<CommunityPermissionEntity> permissions, String appointBehavior) {
		super();
		this.name = name;
		this.position = position;
		this.vacanciesCount = vacanciesCount;
		this.community = community;
		this.permissions = permissions;
		this.appointBehavior = appointBehavior;
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

	public int getVacanciesCount() {
		return vacanciesCount;
	}

	public void setVacanciesCount(int vacanciesCount) {
		this.vacanciesCount = vacanciesCount;
	}

    public String getMnemo() {
        return mnemo;
    }

    public void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }

    public CommunityEntity getCommunity() {
		return community;
	}

	public void setCommunity(CommunityEntity community) {
		this.community = community;
	}

	public List<CommunityMemberEntity> getMembers() {
		return members;
	}

	public void setMembers(List<CommunityMemberEntity> members) {
		this.members = members;
	}

	public List<CommunityPermissionEntity> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<CommunityPermissionEntity> permissions) {
		this.permissions = permissions;
	}

	public boolean isCeo() {
		return ceo;
	}

	public void setCeo(boolean ceo) {
		this.ceo = ceo;
	}

	public CommunitySchemaUnitEntity getSchemaUnit() {
		return schemaUnit;
	}

	public void setSchemaUnit(CommunitySchemaUnitEntity schemaUnit) {
		this.schemaUnit = schemaUnit;
	}

	public String getAppointBehavior() {
		return appointBehavior;
	}

	public void setAppointBehavior(String appointBehavior) {
		this.appointBehavior = appointBehavior;
	}

	/*public DocumentTemplateEntity getDocumentTemplate() {
		return documentTemplate;
	}

	public void setDocumentTemplate(DocumentTemplateEntity documentTemplate) {
		this.documentTemplate = documentTemplate;
	}*/

	public List<DocumentTemplateSettingEntity> getDocumentTemplateSettings() {
		return documentTemplateSettings;
	}

	public CommunityPost toDomain(boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema) {
		CommunityPost result = new CommunityPost();
		result.setId(getId());
		result.setName(getName());
		result.setCeo(isCeo());
		result.setMnemo(getMnemo());
		result.setPosition(getPosition());
		result.setVacanciesCount(getVacanciesCount());
		result.setAppointBehavior(getAppointBehavior());
		if (withCommunity && getCommunity() != null) {
			result.setCommunity(getCommunity().toDomain());
		}
		if (withMembers && getMembers() != null) {
			List<CommunityMemberEntity> membersEntities = getMembers();
			result.setMembers(membersEntities.stream().map(CommunityMemberEntity::toDomain).collect(Collectors.toList()));
		}
		if (withPermissions && getPermissions() != null) {
			List<CommunityPermissionEntity> permissionsEntities = getPermissions();
			result.setPermissions(permissionsEntities.stream().map(permission -> permission.toDomain(false, false)).collect(Collectors.toList()));
		}
		if (withSchema && getSchemaUnit() != null) {
			result.setSchemaUnit(getSchemaUnit().toDomain());
		}
		/*if (getDocumentTemplate() != null) {
			result.setDocumentTemplate(DocumentTemplateEntity.toDomainSafe(getDocumentTemplate(), true));
		}*/
		if (documentTemplateSettings != null) {
			List<DocumentTemplateSetting> templateSettings = DocumentTemplateSettingEntity.toDomainList(documentTemplateSettings);
			if (templateSettings != null) {
				result.getDocumentTemplateSettings().addAll(templateSettings);
			}
		}
		return result;
	}

	public static CommunityPost toDomainSafe(
			CommunityPostEntity communityPostEntity,
			boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema) {
		CommunityPost result = null;
		if (communityPostEntity != null) {
			result = communityPostEntity.toDomain(withCommunity, withMembers, withPermissions, withSchema);
		}
		return result;
	}

	public static List<CommunityPost> toListDomain(
			List<CommunityPostEntity> communityPostEntities,
			boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema) {
		List<CommunityPost> result = null;
		if (communityPostEntities != null) {
			result = new ArrayList<>();
			for (CommunityPostEntity communityPostEntity : communityPostEntities) {
				result.add(toDomainSafe(communityPostEntity, withCommunity, withMembers, withPermissions, withSchema));
			}
		}
		return result;
	}
}
