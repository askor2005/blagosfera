package ru.askor.blagosfera.domain.field;

import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 23.03.2016.
 */
public class FieldsGroup implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String internalName;
    private String name;
    private int position;
    private List<ListEditorItem> associationForms = new ArrayList<>();
    private List<Field> fields = new ArrayList<>();

    public FieldsGroup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<ListEditorItem> getAssociationForms() {
        return associationForms;
    }

    public List<Field> getFields() {
        return fields;
    }
}
