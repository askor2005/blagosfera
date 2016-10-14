package ru.radom.kabinet.model.applications;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.web.Section;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application extends LongIdentifiable {

	public static final String DEFAULT_LOGO_URL = "https://images.blagosfera.su/images/VGHF3HUFH5J/FDFXGRMCCV.png";

	@Column(length = 100, nullable = false)
	private String name;

	@Column(length = 10000000)
	private String description;

	@Column(name = "iframe_url", length = 1000, nullable = false)
	private String iframeUrl;

	@Column(name = "logo_url", length = 1000, nullable = false)
	private String logoUrl;

	@Column(nullable = false, columnDefinition = "numeric(19,2) default 0.00")
	private BigDecimal cost;

	@JoinColumn(name = "features_library_section_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Section featuresLibrarySection;

	@Column(name = "client_id", nullable = true, length = 48)
	private String clientId;

	@Column(name = "client_secret", nullable = true, length = 48)
	private String clientSecret;

	@Column(name = "redirect_url", nullable = true, length = 1000)
	private String redirectUri;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "application")
	private List<SharerApplication> sharerApplications;

	@Column(name = "for_communities", nullable = false)
	private boolean forCommunities;
/*
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "applications_community_association_forms", joinColumns = { @JoinColumn(name = "application_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "community_association_form_id", nullable = false, updatable = false) })
	private List<CommunityAssociationForm> communityAssociationForms;*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIframeUrl() {
		return iframeUrl;
	}

	public void setIframeUrl(String iframeUrl) {
		this.iframeUrl = iframeUrl;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Section getFeaturesLibrarySection() {
		return featuresLibrarySection;
	}

	public void setFeaturesLibrarySection(Section featuresLibrarySection) {
		this.featuresLibrarySection = featuresLibrarySection;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public boolean isFree() {
		return cost.compareTo(BigDecimal.ZERO) == 0;
	}

	public String getStartLink() {
		return "/application/start/" + getId();
	}

	public String getInfoLink() {
		return "/application/info/" + getId();
	}

	public String getEditLink() {
		return "/admin/apps/edit/" + getId();
	}

	public List<SharerApplication> getSharerApplications() {
		return sharerApplications;
	}

	public void setSharerApplications(List<SharerApplication> sharerApplications) {
		this.sharerApplications = sharerApplications;
	}

	public boolean isForCommunities() {
		return forCommunities;
	}

	public void setForCommunities(boolean forCommunities) {
		this.forCommunities = forCommunities;
	}

	/*public List<CommunityAssociationForm> getCommunityAssociationForms() {
		return communityAssociationForms;
	}

	public void setCommunityAssociationForms(List<CommunityAssociationForm> communityAssociationForms) {
		this.communityAssociationForms = communityAssociationForms;
	}*/

}
