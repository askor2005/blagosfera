package ru.radom.kabinet.module.rameralisteditor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorItemRepository;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorRepository;
import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorDomainService;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ListEditorDomainServiceImpl implements ListEditorDomainService {

    @Autowired
    private RameraListEditorRepository rameraListEditorRepository;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Override
    public ListEditor getById(Long id) {
        return RameraListEditor.toDomainSafe(rameraListEditorRepository.findOne(id));
    }

    @Override
    public ListEditor getByName(String name) {
        return RameraListEditor.toDomainSafe(rameraListEditorRepository.findByName(name));
    }

    @Override
    public ListEditor save(ListEditor listEditor) {
        RameraListEditor entity;
        if (listEditor.getId() == null) {
            entity = new RameraListEditor();
        } else {
            entity = rameraListEditorRepository.getOne(listEditor.getId());
        }
        entity.setName(listEditor.getName());
        entity.setFormName(listEditor.getFormName());
        entity.setlistEditorType(listEditor.getListEditorType());
        entity = rameraListEditorRepository.save(entity);
        listEditor.setId(entity.getId());
        if (listEditor.getItems() != null) {
            for (ListEditorItem listEditorItem : listEditor.getItems()) {
                listEditorItem.setListEditor(listEditor);
                listEditorItemDomainService.save(listEditorItem);
            }
        }
        return listEditor;
    }

    @Override
    public ListEditor delete(Long id) {
        return null;
    }
}
