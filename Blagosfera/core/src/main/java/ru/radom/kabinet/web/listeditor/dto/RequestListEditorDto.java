package ru.radom.kabinet.web.listeditor.dto;

import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 27.06.2016.
 */
public class RequestListEditorDto {

    public Long id;

    public String name;

    public String formName;

    public List<RequestListEditorItemDto> items = new ArrayList<>();

    public RameraListEditorType listEditorType;

    public RequestListEditorDto() {}

    public ListEditor toDomain() {
        ListEditor result = new ListEditor();
        result.setId(id);
        result.setName(name);
        result.setFormName(formName);
        result.setItems(RequestListEditorItemDto.toDomainList(items, result));
        result.setListEditorType(listEditorType);
        return result;
    }
}
