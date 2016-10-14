package ru.radom.kabinet.module.blagosfera.bp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.radom.kabinet.module.blagosfera.bp.dto.BPTreeForm;
import ru.radom.kabinet.module.blagosfera.bp.dto.BPTreeItem;
import ru.radom.kabinet.module.blagosfera.bp.dto.converter.BPTreeItemConverter;
import ru.radom.kabinet.module.blagosfera.bp.model.ProcessTreeItem;
import ru.radom.kabinet.module.blagosfera.bp.service.ProcessTreeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Контроллер для работы с деревом процессов
 */
@Controller
@RequestMapping("/admin/bpeditor/tree/items")
public class EditorTreeItemsController {

    private final String FOLDER_TYPE = "folder";

    @Autowired
    private ProcessTreeService processTreeService;

    @Autowired
    private BPTreeItemConverter bpTreeItemConverter;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<BPTreeItem> getTreeItems(
            @RequestParam(required = false) Long id
    ) {
        List<ProcessTreeItem> children = processTreeService.getChildren(id);
        return children.stream().map(bpTreeItemConverter::convert).collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BPTreeItem createTreeItem(
            @RequestBody BPTreeForm form
    ) {
        ProcessTreeService.InsertPosition insertPosition =
                "last".equals(form.getPosition()) ? ProcessTreeService.InsertPosition.Last :ProcessTreeService.InsertPosition.First;
        ProcessTreeItem item;
        if(FOLDER_TYPE.equals(form.getType())) {
            item = processTreeService.createFolder(form.getParentId(), form.getName(), insertPosition);
        } else {
            item = processTreeService.createModel(form.getParentId(), form.getName(), insertPosition);
        }
        return bpTreeItemConverter.convert(item);
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void delete(
        @RequestBody BPTreeForm form
    ) {
        if(FOLDER_TYPE.equals(form.getType())) {
            processTreeService.deleteFolder(form.getId(), Boolean.TRUE.equals(form.getMoveChildrenOnDelete()));
        } else {
            processTreeService.deleteModel(form.getId());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/move", method = RequestMethod.PUT)
    public BPTreeItem moveTreeItem(
        @RequestBody BPTreeForm form
    ) {
        ProcessTreeItem item = processTreeService.moveItem(form.getId(), form.getParentId(), form.getPositionToMove());
        return bpTreeItemConverter.convert(item);
    }
}
