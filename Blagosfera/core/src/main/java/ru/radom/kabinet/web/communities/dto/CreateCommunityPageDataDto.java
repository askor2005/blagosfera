package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.field.FieldsGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by dream_000 on 10.05.2016.
 */
@Data
public class CreateCommunityPageDataDto {

    private List<CommunityFieldGroupDto> fieldGroups = new ArrayList<>();

    public CreateCommunityPageDataDto(List<FieldsGroup> fieldsGroups) {
        if (fieldsGroups != null) {
            for (FieldsGroup fieldsGroup : fieldsGroups) {
                fieldGroups.add(new CommunityFieldGroupDto(fieldsGroup));
            }
        }
    }
}
