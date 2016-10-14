package ru.askor.blagosfera.domain.field;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Created by vgusev on 08.03.2016.
 */
public class Field implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String value;
    private String internalName;
    private String name;
    private boolean hidden;
    private boolean hideable;
    private String comment;
    private FieldType type;
    private String example;
    private int points;
    private boolean isRequired;
    private int position;
    private boolean isAttachedFile;
    private List<FieldFile> fieldFiles;
    private String fieldsGroup;
    private String mask;
    private String placeholder;

    public Field() {
    }

    public Field(Long id, String value, String internalName, String name,
                 boolean hidden, boolean hideable, String comment, FieldType type,
                 String example, int points, boolean isRequired, int position,
                 boolean isAttachedFile, List<FieldFile> fieldFiles, String fieldsGroup,
                 String mask, String placeholder) {
        this.id = id;
        this.value = value;
        this.internalName = internalName;
        this.name = name;
        this.hidden = hidden;
        this.hideable = hideable;
        this.comment = comment;
        this.type = type;
        this.example = example;
        this.points = points;
        this.isRequired = isRequired;
        this.position = position;
        this.isAttachedFile = isAttachedFile;
        this.fieldFiles = fieldFiles;
        this.fieldsGroup = fieldsGroup;
        this.mask = mask;
        this.placeholder = placeholder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHideable() {
        return hideable;
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAttachedFile() {
        return isAttachedFile;
    }

    public void setAttachedFile(boolean attachedFile) {
        isAttachedFile = attachedFile;
    }

    public List<FieldFile> getFieldFiles() {
        return fieldFiles;
    }

    public void setFieldFiles(List<FieldFile> fieldFiles) {
        this.fieldFiles = fieldFiles;
    }

    public String getFieldsGroup() {
        return fieldsGroup;
    }

    public void setFieldsGroup(String fieldsGroup) {
        this.fieldsGroup = fieldsGroup;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
