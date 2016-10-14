package ru.radom.kabinet.model.communities.inventory;

import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_inventory_unit_types")
public class CommunityInventoryUnitTypeEntity extends LongIdentifiable {

	@Column(nullable = false)
	private String name;

	@Column(name = "internal_name", nullable = true)
	private String internalName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public CommunityInventoryUnitType toDomain() {
		CommunityInventoryUnitType result = new CommunityInventoryUnitType();
		result.setId(getId());
		result.setName(getName());
		result.setInternalName(getInternalName());
		return result;
	}

	public static CommunityInventoryUnitType toDomainSafe(CommunityInventoryUnitTypeEntity communityInventoryUnitTypeEntity) {
		CommunityInventoryUnitType result = null;
		if (communityInventoryUnitTypeEntity != null) {
			result = communityInventoryUnitTypeEntity.toDomain();
		}
		return result;
	}

	public static List<CommunityInventoryUnitType> toListDomain(List<CommunityInventoryUnitTypeEntity> communityInventoryUnitTypeEntities) {
		List<CommunityInventoryUnitType> result = null;
		if (communityInventoryUnitTypeEntities != null) {
			result = new ArrayList<>();
			for (CommunityInventoryUnitTypeEntity communityInventoryUnitTypeEntity : communityInventoryUnitTypeEntities) {
				result.add(CommunityInventoryUnitTypeEntity.toDomainSafe(communityInventoryUnitTypeEntity));
			}
		}
		return result;
	}

}
