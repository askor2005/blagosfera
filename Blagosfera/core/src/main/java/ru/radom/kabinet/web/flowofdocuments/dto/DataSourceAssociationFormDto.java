package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class DataSourceAssociationFormDto {

    private Long id;

    private String name;

    public DataSourceAssociationFormDto(RameraListEditorItem listEditorItem) {
        if (listEditorItem != null) {
            setId(listEditorItem.getId());
            setName(listEditorItem.getText());
        } else {
            setId(-1l);
            setName("");
        }
    }

    public DataSourceAssociationFormDto(ListEditorItem listEditorItem) {
        if (listEditorItem != null) {
            setId(listEditorItem.getId());
            setName(listEditorItem.getText());
        } else {
            setId(-1l);
            setName("");
        }
    }
}
