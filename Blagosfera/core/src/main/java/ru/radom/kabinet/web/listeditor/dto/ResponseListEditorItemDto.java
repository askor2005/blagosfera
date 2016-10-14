package ru.radom.kabinet.web.listeditor.dto;

import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 27.06.2016.
 */
public class ResponseListEditorItemDto {

    public Long id;

    public String text;

    public String mnemoCode;

    public Boolean isActive = false;

    public List<ResponseListEditorItemDto> children;

    //public ResponseListEditorDto listEditor;

    public RameraListEditorType listEditorItemType;

    public Boolean isSelectedItem;

    public long order;

    public ResponseListEditorItemDto() {}

    public static ResponseListEditorItemDto toDtoSafe(ListEditorItem listEditorItem, ResponseListEditorDto listEditor) {
        ResponseListEditorItemDto result = null;
        if (listEditorItem != null) {
            result = new ResponseListEditorItemDto();
            result.id = listEditorItem.getId();
            result.text = listEditorItem.getText();
            result.mnemoCode = listEditorItem.getCode();
            result.isActive = listEditorItem.isActive();
            result.children = toDtoList(listEditorItem.getChild(), null);
            //result.listEditor = listEditor;
            result.listEditorItemType = listEditorItem.getListEditorItemType();
            result.isSelectedItem = listEditorItem.isSelectedItem();
            result.order = listEditorItem.getOrder();
        }
        return result;
    }

    public static List<ResponseListEditorItemDto> toDtoList(List<ListEditorItem> listEditorItems, ResponseListEditorDto listEditor) {
        List<ResponseListEditorItemDto> result = null;
        if (listEditorItems != null) {
            result = new ArrayList<>();
            for (ListEditorItem listEditorItem : listEditorItems) {
                result.add(toDtoSafe(listEditorItem, listEditor));
            }
        }
        return result;
    }
}
