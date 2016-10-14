package ru.radom.kabinet.web.listeditor.dto;

import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 27.06.2016.
 */
public class RequestListEditorItemDto {

    public Long id;

    public String text;

    public String mnemoCode;

    public Boolean isActive = false;

    public List<RequestListEditorItemDto> children;

    public RameraListEditorType listEditorItemType;

    public Boolean isSelectedItem;

    public long order;

    public RequestListEditorItemDto() {}

    public ListEditorItem toDomain(ListEditor listEditor) {
        ListEditorItem result = new ListEditorItem();
        result.setId(id);
        result.setText(text);
        result.setCode(mnemoCode);
        result.setActive(BooleanUtils.toBooleanDefaultIfNull(isActive, false));
        result.setChild(toDomainList(children, null));
        result.setListEditorItemType(listEditorItemType);
        result.setSelectedItem(BooleanUtils.toBooleanDefaultIfNull(isSelectedItem, false));
        result.setOrder(order);
        result.setListEditor(listEditor);
        return result;
    }

    public static List<ListEditorItem> toDomainList(List<RequestListEditorItemDto> requestListEditorItems, ListEditor listEditor) {
        List<ListEditorItem> result = null;
        if (requestListEditorItems != null) {
            result = new ArrayList<>();
            for (RequestListEditorItemDto requestListEditorItem : requestListEditorItems) {
                result.add(requestListEditorItem.toDomain(listEditor));
            }
        }
        return result;
    }
}
