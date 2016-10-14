package ru.radom.kabinet.model.rameralisteditor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vgusev on 02.06.2015.
 * Класс - сущность элементов виджетов.
 */
@Entity
@Table(name = "list_editor_item")
public class RameraListEditorItem extends LongIdentifiable {
    @Column(name = "text")
    private String text;

    @Column(name = "mnemo_code", unique = true)
    private String mnemoCode;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @JsonIgnore
    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RameraListEditorItem parent;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<RameraListEditorItem> children;

    @JsonIgnore
    @JoinColumn(name = "list_editor")
    @ManyToOne(fetch = FetchType.LAZY)
    private RameraListEditor listEditor;

    @Column()
    private RameraListEditorType listEditorItemType;

    @Column(name = "is_selected_item")
    private Boolean isSelectedItem;

    /**
     * Порядок элемента
     */
    @Column(name = "item_order", nullable = true)
    private Long order;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMnemoCode() {
        return mnemoCode;
    }

    public void setMnemoCode(String mnemoCode) {
        this.mnemoCode = mnemoCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public RameraListEditorItem getParent() {
        return parent;
    }

    public void setParent(RameraListEditorItem parent) {
        this.parent = parent;
    }

    public List<RameraListEditorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RameraListEditorItem> children) {
        this.children = children;
    }

    public RameraListEditor getListEditor() {
        return listEditor;
    }

    public void setListEditor(RameraListEditor listEditor) {
        this.listEditor = listEditor;
    }

    public RameraListEditorType getListEditorItemType() {
        return listEditorItemType;
    }

    public void setListEditorItemType(RameraListEditorType listEditorItemType) {
        this.listEditorItemType = listEditorItemType;
    }

    public Boolean getIsSelectedItem() {
        return isSelectedItem;
    }

    public void setIsSelectedItem(Boolean isSelectedItem) {
        this.isSelectedItem = isSelectedItem;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }


    public ListEditorItem toDomain() {
        return toDomain(true, true);
    }

    public ListEditorItem toDomain(boolean withParents, boolean withChild) {
        ListEditorItem result = new ListEditorItem();

        result.setId(getId());
        result.setText(getText());
        result.setCode(getMnemoCode());
        result.setActive(BooleanUtils.toBooleanDefaultIfNull(getIsActive(), false));
        result.setListEditorItemType(getListEditorItemType());
        result.setSelectedItem(BooleanUtils.toBooleanDefaultIfNull(getIsSelectedItem(), false));
        if (getOrder() != null) {
            result.setOrder(getOrder());
        }

        if (withParents && parent != null) {
            result.setParent(parent.toDomain());
        }

        if (withChild && getChildren() != null) {
            result.setChild(toDomainList(getChildren(), false, withChild));
        }

        return result;
    }

    public static ListEditorItem toDomainSafe(RameraListEditorItem rameraListEditorItemEntity) {
        return toDomainSafe(rameraListEditorItemEntity, true, true);
    }

    public static ListEditorItem toDomainSafe(RameraListEditorItem rameraListEditorItemEntity, boolean withParents, boolean withChild) {
        ListEditorItem result = null;
        if (rameraListEditorItemEntity != null) {
            result = rameraListEditorItemEntity.toDomain(withParents, withChild);
        }
        return result;
    }

    public static List<ListEditorItem> toDomainList(Collection<RameraListEditorItem> listEditorItems) {
        return toDomainList(listEditorItems, true, true);
    }

    public static List<ListEditorItem> toDomainList(Collection<RameraListEditorItem> listEditorItems, boolean withParents, boolean withChild) {
        List<ListEditorItem> result = null;
        if (listEditorItems != null) {
            result = new ArrayList<>();
            for (RameraListEditorItem listEditorItem : listEditorItems) {
                result.add(listEditorItem.toDomain(withParents, withChild));
            }
        }
        return result;
    }
}
