package ru.askor.blagosfera.domain.listEditor;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 12.04.2016.
 */
@Data
public class ListEditor {

    private Long id;

    private String name;

    private String formName;

    private List<ListEditorItem> items;

    private RameraListEditorType listEditorType;

}
