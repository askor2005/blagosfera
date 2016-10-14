package ru.radom.kabinet.model.rameralisteditor;

import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 02.06.2015.
 * Класс - сущность списков для компонтов select, checkbox, radio.
 */
@Entity
@Table(name = "list_editor")
public class RameraListEditor extends LongIdentifiable {
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "form_name")
    private String formName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listEditor", cascade = CascadeType.ALL)
    private List<RameraListEditorItem> items = new ArrayList<>();

    @Column(nullable = false)
    private RameraListEditorType listEditorType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public List<RameraListEditorItem> getItems() {
        return items;
    }

    public void setItems(List<RameraListEditorItem> items) {
        this.items = items;
    }

    public RameraListEditorType getlistEditorType() {
        return listEditorType;
    }

    public void setlistEditorType(RameraListEditorType listEditorType) {
        this.listEditorType = listEditorType;
    }

    public void addItem(RameraListEditorItem listEditorItem) {
        this.items.add(listEditorItem);
        listEditorItem.setListEditor(this);
    }

    public void addItems(List<RameraListEditorItem> listEditorItems) {
        this.items.addAll(listEditorItems);
        for (RameraListEditorItem listEditorItem : listEditorItems) {
            listEditorItem.setListEditor(this);
        }
    }

    public void attachItems() {
        for (RameraListEditorItem listEditorItem : this.items) {
            listEditorItem.setListEditor(this);
        }
    }

    public ListEditor toDomain() {
        ListEditor result = new ListEditor();
        result.setId(getId());
        result.setName(getName());
        result.setFormName(getFormName());
        result.setListEditorType(getlistEditorType());
        result.setItems(RameraListEditorItem.toDomainList(getItems(), false, true));
        return result;
    }

    public static ListEditor toDomainSafe(RameraListEditor rameraListEditor) {
        ListEditor result = null;
        if (rameraListEditor != null) {
            result = rameraListEditor.toDomain();
        }
        return result;
    }
}