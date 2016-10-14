package ru.radom.kabinet.module.blagosfera.bp.dto.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.module.blagosfera.bp.dto.BPTreeFolder;
import ru.radom.kabinet.module.blagosfera.bp.dto.BPTreeItem;
import ru.radom.kabinet.module.blagosfera.bp.dto.BPTreeModel;
import ru.radom.kabinet.module.blagosfera.bp.model.ProcessTreeItem;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * Конверетер {@link ProcessTreeItem} -> {@link BPTreeItem}
 */
@Component
public class BPTreeItemConverter implements Converter<ProcessTreeItem, BPTreeItem> {
    @Override
    public BPTreeItem convert(ProcessTreeItem source) {
        BPTreeItem item;
        if(source.getModel() == null) {
            BPTreeFolder folder = new BPTreeFolder();
            folder.setChildren(source.getChildrenCount() != 0);
            item = folder;
        } else {
            BPTreeModel model = new BPTreeModel();
            model.setModelId(source.getModel().getId());
            item = model;
        }
        item.setText(source.getName());
        item.setId(source.getId());
        return item;
    }
}
