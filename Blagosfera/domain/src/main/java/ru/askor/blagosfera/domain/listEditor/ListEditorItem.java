package ru.askor.blagosfera.domain.listEditor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Domain класс для RameraListEditor
 */
@Data
public class ListEditorItem implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String text;
    private String code;
    private ListEditorItem parent;
    private List<ListEditorItem> child;
    private boolean isActive = false;
    private RameraListEditorType listEditorItemType;
    private boolean isSelectedItem;
    private long order;
    private ListEditor listEditor;

    public ListEditorItem() {}

    /*public ListEditorItem(NewsListItemCategoryDto dto) {
        id = dto.getId();
        text = dto.getText();
    }*/
}
