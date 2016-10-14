package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.field.FieldType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by vgusev on 22.03.2016.
 */
@Data
public class CommunityFieldDto {

    private Long id;

    private String internalName;

    private String name;

    private String value;

    private boolean hidden;

    private boolean hideable;

    private FieldType type;

    private String comment;

    private String example;

    private boolean isRequired;

    private boolean isAttachedFile;

    private int position;

    private int points;

    private String mask;

    private String placeholder;

    private List<CommunityFieldFileDto> files;

    public CommunityFieldDto() {}

    public CommunityFieldDto(Field field) {
        setId(field.getId());
        setInternalName(field.getInternalName());
        setName(field.getName());
        setValue(field.getValue());
        setHidden(field.isHidden());
        setHideable(field.isHideable());
        setType(field.getType());
        setComment(field.getComment());
        setExample(field.getExample());
        setRequired(field.isRequired());
        setAttachedFile(field.isAttachedFile());
        setPosition(field.getPosition());
        setPoints(field.getPoints());
        setMask(field.getMask());
        setPlaceholder(field.getPlaceholder());
        /*if (field.getFieldFiles() != null) {
            List<CommunityFieldFileDto> files = new ArrayList<>();
            for (FieldFile fieldFile : field.getFieldFiles()) {
                CommunityFieldFileDto fieldFileDto = new CommunityFieldFileDto();
                fieldFileDto.setId(fieldFile.getId());
                fieldFileDto.setName(fieldFile.getName());
                fieldFileDto.setUrl(fieldFile.getUrl());
                files.add(fieldFileDto);
            }
            setFiles(files);
        }*/
    }

    private List<FieldFile> toDomainFieldFiles() {
        List<FieldFile> result = null;
        if (files != null) {
            result = new ArrayList<>();
            for (CommunityFieldFileDto communityFieldFile : files) {
                FieldFile fieldFile = new FieldFile();
                fieldFile.setUrl(communityFieldFile.getUrl());
                fieldFile.setName(communityFieldFile.getName());
                result.add(fieldFile);
            }
        }
        return result;
    }

    public static List<CommunityFieldDto> toListDto(List<Field> fields) {
        List<CommunityFieldDto> result = null;
        if (fields != null) {
            result = new ArrayList<>();
            for (Field field : fields) {
                result.add(new CommunityFieldDto(field));
            }
        }
        return result;
    }

    public Field toDomain() {
        Field result = new Field();
        result.setId(getId());
        result.setInternalName(getInternalName());
        result.setName(getName());
        result.setValue(getValue());
        result.setHidden(isHidden());
        result.setHideable(isHideable());
        result.setType(getType());
        result.setComment(getComment());
        result.setExample(getExample());
        result.setRequired(isRequired());
        result.setAttachedFile(isAttachedFile());
        result.setPosition(getPosition());
        result.setPoints(getPoints());
        result.setFieldFiles(toDomainFieldFiles());
        return result;
    }

    public static List<Field> toListDomain(Collection<CommunityFieldDto> communityFields) {
        List<Field> result = null;
        if (communityFields != null) {
            result = new ArrayList<>();
            for (CommunityFieldDto communityField : communityFields) {
                result.add(communityField.toDomain());
            }
        }
        return result;
    }
}
