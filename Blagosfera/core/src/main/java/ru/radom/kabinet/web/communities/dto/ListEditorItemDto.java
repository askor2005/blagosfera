package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка данных универсальных списков
 * Created by vgusev on 10.03.2016.
 */
@Data
public class ListEditorItemDto {

    private Long id;

    private String code;

    private String text;

    private ListEditorItemDto child;

    public ListEditorItemDto() {

    }

    public ListEditorItemDto(ListEditorItem listEditorItem) {
        setId(listEditorItem.getId());
        setCode(listEditorItem.getCode());
        setText(listEditorItem.getText());
    }

    public static List<ListEditorItemDto> toListDto(List<ListEditorItem> listEditorItems) {
        List<ListEditorItemDto> result = null;
        if (listEditorItems != null) {
            result = new ArrayList<>();
            for (ListEditorItem listEditorItem : listEditorItems) {
                result.add(new ListEditorItemDto(listEditorItem));
            }
        }
        return result;
    }

    public ListEditorItem toDomain() {
        ListEditorItem result = new ListEditorItem();
        result.setId(getId());
        result.setCode(getCode());
        result.setText(getText());
        return result;
    }

    public static List<ListEditorItem> toListDomain(List<ListEditorItemDto> listEditorItems) {
        List<ListEditorItem> result = null;
        if (listEditorItems != null) {
            result = new ArrayList<>();
            for (ListEditorItemDto listEditorItem : listEditorItems) {
                result.add(listEditorItem.toDomain());
            }
        }
        return result;
    }


}
