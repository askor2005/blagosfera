package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityPostPageSchemaUnitDto {

    private Long id;

    private CommunitySchemaUnitType type;

    private String name;

    public CommunityPostPageSchemaUnitDto(CommunitySchemaUnit communitySchemaUnit) {
        setId(communitySchemaUnit.getId());
        setType(communitySchemaUnit.getType());
        setName(communitySchemaUnit.getName());
    }

    public static List<CommunityPostPageSchemaUnitDto> toDtoList(List<CommunitySchemaUnit> communitySchemaUnits) {
        List<CommunityPostPageSchemaUnitDto> result = null;
        if (communitySchemaUnits != null) {
            result = new ArrayList<>();
            for (CommunitySchemaUnit communitySchemaUnit : communitySchemaUnits) {
                result.add(new CommunityPostPageSchemaUnitDto(communitySchemaUnit));
            }
        }
        return result;
    }
}
