package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 23.03.2016.
 */
@Data
public class CommunityMenuDataDto {

    private Map<String, String> fields;

    public CommunityMenuDataDto() {}

    public CommunityMenuDataDto(Community community) {
        if (community.getCommunityData() != null && community.getCommunityData().getFields() != null) {
            Map<String, String> fieldValues = new HashMap<>();
            List<Field> communityFields = community.getCommunityData().getFields();
            for (Field communityField : communityFields) {
                fieldValues.put(communityField.getInternalName(), communityField.getValue());
            }
            setFields(fieldValues);
        }
    }
}
