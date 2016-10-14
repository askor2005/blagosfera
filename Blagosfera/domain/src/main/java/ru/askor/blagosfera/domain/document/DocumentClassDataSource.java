package ru.askor.blagosfera.domain.document;

import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Источник данных класса документа.
 * Created by vgusev on 16.06.2015.
 */
@Data
public class DocumentClassDataSource {

    private Long id;

    /**
     * Наименование (код) участника в шаблоне.
     */
    private String name;

    /**
     * Наименование типа участника.
     */
    private ParticipantsTypes type;

    /**
     * Форма объединения
     * Ссылка на объект итем компонента универсальных списков
     */
    private ListEditorItem associationForm;

    /**
     * Способ применения фильтра по форме объединения
     */
    private AssociationFormSearchType associationFormSearchType;
}
