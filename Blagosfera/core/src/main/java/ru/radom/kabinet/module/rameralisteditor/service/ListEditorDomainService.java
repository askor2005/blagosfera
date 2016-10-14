package ru.radom.kabinet.module.rameralisteditor.service;

import ru.askor.blagosfera.domain.listEditor.ListEditor;

/**
 *
 * Created by vgusev on 12.04.2016.
 */
public interface ListEditorDomainService {

    ListEditor getById(Long id);

    ListEditor getByName(String name);

    ListEditor save(ListEditor listEditor);

    ListEditor delete(Long id);
}
