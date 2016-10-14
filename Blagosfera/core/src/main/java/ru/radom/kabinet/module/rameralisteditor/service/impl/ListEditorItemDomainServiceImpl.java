package ru.radom.kabinet.module.rameralisteditor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorItemRepository;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorRepository;
import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ListEditorItemDomainServiceImpl implements ListEditorItemDomainService {

    @Autowired
    private RameraListEditorItemRepository rameraListEditorItemRepository;

    @Autowired
    private RameraListEditorRepository rameraListEditorRepository;

    @Override
    public ListEditorItem getById(Long id) {
        return RameraListEditorItem.toDomainSafe(rameraListEditorItemRepository.findOne(id), false, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getDescendantIdsWithParent(Long parentId) {

        List<Long> result = new ArrayList<>();

        if (parentId != null) {

            RameraListEditorItem parent = rameraListEditorItemRepository.findOne(parentId);
            result.add(parentId);
            fillChildrenIds(result, parent);
        }

        return result;
    }

    /**
     * Позволяет рекурсивно добавить в список идентификаторы всех потомков по их предку.
     * @param ids список идентификаторов
     * @param parent потомок
     */
    private void fillChildrenIds(List<Long> ids, RameraListEditorItem parent) {

        for (RameraListEditorItem child : parent.getChildren()) {
            ids.add(child.getId());
            fillChildrenIds(ids, child);
        }

    }

    public List<ListEditorItem> getByIds(List<Long> ids) {
        List<RameraListEditorItem> listEditorItems = rameraListEditorItemRepository.findByIdIn(ids);
        List<ListEditorItem> result = null;
        if (listEditorItems != null) {
            result = new ArrayList<>();
            for (RameraListEditorItem listEditorItem : listEditorItems) {
                result.add(listEditorItem.toDomain());
            }
        }
        return result;
    }

    @Override
    public ListEditorItem getByCode(String code) {
        ListEditorItem result = null;
        RameraListEditorItem rameraListEditorItem = rameraListEditorItemRepository.findByMnemoCode(code);
        if (rameraListEditorItem != null) {
            result = rameraListEditorItem.toDomain();
        }
        return result;
    }

    @Override
    public ListEditorItem save(ListEditorItem listEditorItem) {
        RameraListEditorItem entity;
        if (listEditorItem.getId() == null) {
            entity = new RameraListEditorItem();
        } else {
            entity = rameraListEditorItemRepository.getOne(listEditorItem.getId());
        }
        entity.setText(listEditorItem.getText());
        entity.setMnemoCode(listEditorItem.getCode());
        entity.setOrder(listEditorItem.getOrder());
        entity.setIsSelectedItem(listEditorItem.isSelectedItem());
        entity.setIsActive(listEditorItem.isActive());
        entity.setListEditorItemType(listEditorItem.getListEditorItemType());
        if (listEditorItem.getParent() != null && listEditorItem.getParent().getId() != null) {
            entity.setParent(rameraListEditorItemRepository.getOne(listEditorItem.getParent().getId()));
        }
        if (listEditorItem.getListEditor() != null && listEditorItem.getListEditor().getId() != null) {
            entity.setListEditor(rameraListEditorRepository.getOne(listEditorItem.getListEditor().getId()));
        }
        entity = rameraListEditorItemRepository.save(entity);
        listEditorItem.setId(entity.getId());
        if (listEditorItem.getChild() != null && !listEditorItem.getChild().isEmpty()) {
            for (ListEditorItem child : listEditorItem.getChild()) {
                child.setParent(listEditorItem);
                save(child);
            }
        }
        return listEditorItem;
    }
}
