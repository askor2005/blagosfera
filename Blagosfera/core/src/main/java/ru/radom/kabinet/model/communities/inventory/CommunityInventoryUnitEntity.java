package ru.radom.kabinet.model.communities.inventory;

import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_inventory_units")
public class CommunityInventoryUnitEntity extends LongIdentifiable {

	//public static final String DEFAULT_PHOTO = "https://images.blagosfera.su/images/VGHF3HUFH5J/FUEPMLPHDC.png";
	
	@Column(nullable = false)
	private String number;

	@Column(nullable = false)
	private String guid;

	@Column(nullable = true, length = 1024)
	private String description;

	@Column(nullable = false)
	private String photo;
	
	@JoinColumn(name = "type_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityInventoryUnitTypeEntity type;

	@JoinColumn(name = "responsible_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private CommunityMemberEntity responsible;

	@JoinColumn(name = "community_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityEntity community;

    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "leased_to_community_id", nullable = false)
    private CommunityEntity leasedTo;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public CommunityInventoryUnitTypeEntity getType() {
		return type;
	}

	public void setType(CommunityInventoryUnitTypeEntity type) {
		this.type = type;
	}

	public CommunityMemberEntity getResponsible() {
		return responsible;
	}

	public void setResponsible(CommunityMemberEntity responsible) {
		this.responsible = responsible;
	}

	public CommunityEntity getCommunity() {
		return community;
	}

	public void setCommunity(CommunityEntity community) {
		this.community = community;
	}

    public CommunityEntity getLeasedTo() {
        return leasedTo;
    }

    public void setLeasedTo(CommunityEntity leasedTo) {
        this.leasedTo = leasedTo;
    }

	public CommunityInventoryUnit toDomain() {
		CommunityInventoryUnit result = new CommunityInventoryUnit();
		result.setId(getId());
		result.setNumber(getNumber());
		result.setGuid(getGuid());
		result.setDescription(getDescription());
		result.setPhoto(getPhoto());
		result.setType(CommunityInventoryUnitTypeEntity.toDomainSafe(getType()));
		result.setResponsible(CommunityMemberEntity.toDomainSafe(getResponsible(), false, true, true, false));
		result.setCommunity(CommunityEntity.toDomainSafe(getCommunity()));
		result.setLeasedTo(CommunityEntity.toDomainSafe(getLeasedTo()));
		return result;
	}

	public static CommunityInventoryUnit toDomainSafe(CommunityInventoryUnitEntity communityInventoryUnitEntity) {
		CommunityInventoryUnit result = null;
		if (communityInventoryUnitEntity != null) {
			result = communityInventoryUnitEntity.toDomain();
		}
		return result;
	}

	public static List<CommunityInventoryUnit> toListDomain(List<CommunityInventoryUnitEntity> communityInventoryUnitEntities) {
		List<CommunityInventoryUnit> result = null;
		if (communityInventoryUnitEntities != null) {
			result = new ArrayList<>();
			for (CommunityInventoryUnitEntity communityInventoryUnitEntity : communityInventoryUnitEntities) {
				result.add(toDomainSafe(communityInventoryUnitEntity));
			}
		}
		return result;
	}
}
