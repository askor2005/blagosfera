package ru.askor.blagosfera.web.controllers.ng.user.dto;

import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;

/**
 * Created by mnikitin on 21.04.2016.
 */
public class FieldDto {

    public Long id;
    public String value;
    public String internalName;
    public String name;
    public boolean hidden;
    public boolean hideable;
    public String comment;
    public FieldType type;
    public String example;
    public int points;
    public boolean isRequired;
    public int position;
    public boolean isAttachedFile;
    //public List<FieldFile> fieldFiles;
    public String fieldsGroup;

    public FieldDto() {
    }

    public FieldDto(Field field) {
        id = field.getId();
        value = field.getValue();
        internalName = field.getInternalName();
        name = field.getName();
        hidden = field.isHidden();
        hideable = field.isHideable();
        comment = field.getComment();
        type = field.getType();
        example = field.getExample();
        points = field.getPoints();
        isRequired = field.isRequired();
        position = field.getPosition();
        isAttachedFile = field.isAttachedFile();
        fieldsGroup = field.getFieldsGroup();
    }
}
