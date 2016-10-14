package ru.radom.kabinet.module.rameralisteditor;

import lombok.Data;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.web.listeditor.dto.RequestListEditorDto;

/**
 *
 * Created by vgusev on 02.06.2015.
 */
@Data
public class RameraListEditorAction {
    private RameraListEditorActionType rameraListEditorActionType;

    //private RameraListEditor rameraListEditor;

    private RequestListEditorDto rameraListEditor;

    private RameraListEditorItem rameraListEditorItem;

    private Long id;

    private String listEditorName;
}
