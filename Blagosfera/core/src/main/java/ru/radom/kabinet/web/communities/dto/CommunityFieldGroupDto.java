package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.field.FieldsGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by vgusev on 23.03.2016.
 */
@Data
public class CommunityFieldGroupDto {

    private Long id;

    private String name;

    private String internalName;

    private int position;

    private List<ListEditorItemDto> associationForms;

    private List<CommunityFieldDto> fields;

    public CommunityFieldGroupDto() {}

    public CommunityFieldGroupDto(FieldsGroup fieldGroup) {
        setId(fieldGroup.getId());
        setName(fieldGroup.getName());
        setInternalName(fieldGroup.getInternalName());
        setPosition(fieldGroup.getPosition());
        setFields(CommunityFieldDto.toListDto(fieldGroup.getFields()));
        setAssociationForms(ListEditorItemDto.toListDto(fieldGroup.getAssociationForms()));
    }

    public FieldsGroup toDomain() {
        FieldsGroup result = new FieldsGroup();
        result.setId(getId());
        result.setName(getName());
        result.setInternalName(getInternalName());
        result.setPosition(getPosition());
        result.getAssociationForms().addAll(ListEditorItemDto.toListDomain(getAssociationForms()));
        result.getFields().addAll(CommunityFieldDto.toListDomain(getFields()));
        return result;
    }

    public static List<FieldsGroup> toListDomain(Collection<CommunityFieldGroupDto> communityFieldGroups) {
        List<FieldsGroup> result = null;
        if (communityFieldGroups != null) {
            result = new ArrayList<>();
            for (CommunityFieldGroupDto communityFieldGroup : communityFieldGroups) {
                result.add(communityFieldGroup.toDomain());
            }
        }
        return result;
    }
}
