package ru.radom.kabinet.module.blagosfera.bp.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.radom.kabinet.module.blagosfera.bp.dao.ProcessTreeItemDAO;
import ru.radom.kabinet.module.blagosfera.bp.model.BPModel;
import ru.radom.kabinet.module.blagosfera.bp.model.ProcessTreeItem;
import ru.radom.kabinet.module.blagosfera.bp.service.BPModelService;
import ru.radom.kabinet.module.blagosfera.bp.service.ProcessTreeService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * Реализация для {@link ProcessTreeService}
 */
@Service
public class ProcessTreeServiceImpl implements ProcessTreeService {

    @Autowired
    private ProcessTreeItemDAO processTreeItemDAO;

    @Autowired
    private BPModelService bpModelService;

    @Override
    @Transactional
    public ProcessTreeItem createFolder(Long parentId, String name, InsertPosition insertPosition) {
        Assert.notNull(name);
        Assert.notNull(insertPosition);

        ProcessTreeItem item = createProcessTreeItem(parentId, name, insertPosition);
        processTreeItemDAO.save(item);
        return item;
    }

    @Override
    @Transactional
    public ProcessTreeItem createModel(Long parentId, String name, InsertPosition insertPosition) {
        Assert.notNull(name);
        Assert.notNull(insertPosition);

        BPModel model = bpModelService.create(name);
        ProcessTreeItem item = createProcessTreeItem(parentId, name, insertPosition);
        item.setModel(model);
        processTreeItemDAO.save(item);
        return item;
    }

    @Override
    @Transactional
    public void deleteFolder(Long id, boolean moveChildrenUp) {
        Assert.notNull(id);

        ProcessTreeItem item = processTreeItemDAO.getById(id);
        if(item.getModel() != null) {
            throw new IllegalArgumentException("Item(" + id + ") is not a folder!");
        }
        processTreeItemDAO.delete(item);
    }

    @Override
    @Transactional
    public void deleteModel(Long id) {
        Assert.notNull(id);

        ProcessTreeItem item = processTreeItemDAO.getById(id);
        if(item.getModel() == null) {
            throw new IllegalArgumentException("Item(" + id + ") is not a model!");
        }
        processTreeItemDAO.delete(item);
    }

    @Override
    @Transactional
    public ProcessTreeItem moveItem(Long itemId, Long parentId, int position) {
        Assert.notNull(itemId);

        ProcessTreeItem parent = parentId == null ? null : processTreeItemDAO.loadById(parentId);
        List<ProcessTreeItem> items = getChildrenItems(parent);
        ProcessTreeItem item = processTreeItemDAO.getById(itemId);
        item.setParent(parent);
        item.setName(makeUniqueName(items, item.getName()));

        int size = items.size();
        if(position > size) {
            item.setPosition(size);
        } else {
            item.setPosition(position);
            for (int i = position; i < size; i++) {
                items.get(i).setPosition(i + 1);
            }
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessTreeItem> getChildren(Long parentId) {
        ProcessTreeItem parent = parentId == null ? null : processTreeItemDAO.loadById(parentId);
        return getChildrenItems(parent);
    }

    /**
     * Получить дочерние элементы, если {@code parent} null то возвращаются корневые элементы
     */
    private List<ProcessTreeItem> getChildrenItems(ProcessTreeItem parent) {
        if(parent == null) {
            return processTreeItemDAO.find(Order.asc("position"), Restrictions.isNull("parent"));
        } else {
            return parent.getChildren();
        }
    }

    /**
     * Создает узел в нужной позиции
     */
    private ProcessTreeItem createProcessTreeItem(Long parentId, String name, InsertPosition insertPosition) {
        name = name.trim().replaceAll("\\s+", " ");
        List<ProcessTreeItem> items = processTreeItemDAO.find(
            Restrictions.like("name", name, MatchMode.START),
            parentId == null ? Restrictions.isNull("parent") : Restrictions.eq("parent.id", parentId)
        );
        name = makeUniqueName(items, name);
        ProcessTreeItem parent = parentId == null ? null : processTreeItemDAO.getById(parentId);
        ProcessTreeItem item = new ProcessTreeItem();
        item.setName(name);
        item.setParent(parent);
        item.setPosition(calculatePosition(parent, insertPosition));
        return item;
    }

    private String makeUniqueName(List<ProcessTreeItem> items, String name) {
        if(CollectionUtils.isEmpty(items)) {
            return name;
        }
        Set<String> names = items.stream().map(ProcessTreeItem::getName).collect(Collectors.toSet());
        int i = 1;
        String baseName = name;
        while(names.contains(name)) {
            name = baseName + " (" + i + ")";
            i++;
        }
        return name;
    }

    /**
     * Рассчитывает новую позицию для элемента и сдвигает элементы если это нужно
     */
    private long calculatePosition(ProcessTreeItem parent, InsertPosition insertPosition) {
        switch (insertPosition) {
            case First: {
                processTreeItemDAO.shiftAllChildrenBy(parent, 1);
                return 0;
            }
            case Last: {
                if(parent != null) {
                    return parent.getChildrenCount();
                }
                return processTreeItemDAO.countRootElements();
            }
            default:
                throw new IllegalArgumentException("Unsupported type " + insertPosition);
        }
    }
}
