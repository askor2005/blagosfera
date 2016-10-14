package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;
import ru.askor.blagosfera.domain.community.CommunityMember;

/**
 *
 * Created by vgusev on 01.04.2016.
 */
@Data
public class CommunityInventoryUnitSaveDto {

    private Long id;

    private Long communityId;

    private String number;

    private String photo;

    private String guid;

    private Long responsibleId;

    private String description;

    private Long typeId;

    private Long leasedCommunityId;

    public CommunityInventoryUnit toDomain() {
        Community community = new Community();
        community.setId(getCommunityId());
        CommunityMember responsible = new CommunityMember();
        responsible.setId(getResponsibleId());
        CommunityInventoryUnitType type = new CommunityInventoryUnitType();
        type.setId(getTypeId());
        Community leasedCommunity = new Community();
        leasedCommunity.setId(getLeasedCommunityId());


        CommunityInventoryUnit result = new CommunityInventoryUnit();
        result.setId(getId());
        result.setCommunity(community);
        result.setGuid(getGuid());
        result.setNumber(getNumber());
        result.setPhoto(getPhoto());
        result.setResponsible(responsible);
        result.setDescription(getDescription());
        result.setType(type);
        result.setLeasedTo(leasedCommunity);
        return result;
    }
}
