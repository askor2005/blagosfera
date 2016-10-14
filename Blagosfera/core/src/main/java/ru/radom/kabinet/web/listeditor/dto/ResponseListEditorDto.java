package ru.radom.kabinet.web.listeditor.dto;

import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 27.06.2016.
 */
public class ResponseListEditorDto {

    public Long id;

    public String name;

    public String formName;

    public List<ResponseListEditorItemDto> items = new ArrayList<>();

    public RameraListEditorType listEditorType;

    public ResponseListEditorDto() {}

    public static ResponseListEditorDto toDtoSafe(ListEditor listEditor) {
        ResponseListEditorDto result = null;
        if (listEditor != null) {
            result = new ResponseListEditorDto();
            result.id = listEditor.getId();
            result.name = listEditor.getName();
            result.formName = listEditor.getFormName();
            result.items = ResponseListEditorItemDto.toDtoList(listEditor.getItems(), result);
            result.listEditorType = listEditor.getListEditorType();
        }
        return result;
    }

    public static List<ResponseListEditorDto> toDtoList(List<ListEditor> listEditors) {
        List<ResponseListEditorDto> result = null;
        if (listEditors != null) {
            result = new ArrayList<>();
            for (ListEditor listEditor : listEditors) {
                result.add(toDtoSafe(listEditor));
            }
        }
        return result;
    }
}
