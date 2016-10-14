package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.model.fields.FieldEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class FieldDto {

    private Long id;
    private String internalName;
    private String name;
    private boolean isRequired;
    private int points;
    private int position;
    private boolean isUseCase;
    private boolean isHiddenByDefault;
    private boolean isHideable;
    private FieldType type;
    private String comment;
    private String example;
    private boolean isUnique;
    private boolean isVerifiedEditable;
    private boolean isAttachedFile;
    private boolean isMetaField;

    public FieldDto(FieldEntity field) {
        setId(field.getId());
        setInternalName(field.getInternalName());
        setName(field.getName());
        setRequired(field.isRequired());
        setPoints(field.getPoints());
        setPosition(field.getPosition());
        setUseCase(BooleanUtils.toBooleanDefaultIfNull(field.isUseCase(), false));
        setHiddenByDefault(field.isHiddenByDefault());
        setHideable(field.isHideable());
        setType(field.getType());
        setComment(field.getComment());
        setExample(field.getExample());
        setUnique(field.isUnique());
        setVerifiedEditable(field.isVerifiedEditable());
        setAttachedFile(BooleanUtils.toBooleanDefaultIfNull(field.getAttachedFile(), false));
        setMetaField(false);
    }

    public static List<FieldDto> toListDto(List<FieldEntity> fields) {
        List<FieldDto> result = new ArrayList<>();
        if (fields != null) {
            for (FieldEntity field : fields) {
                result.add(new FieldDto(field));
            }
        }
        return result;
    }
}
