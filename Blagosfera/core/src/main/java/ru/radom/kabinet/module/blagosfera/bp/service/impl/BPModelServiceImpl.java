package ru.radom.kabinet.module.blagosfera.bp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.radom.kabinet.module.blagosfera.bp.dao.BPModelDAO;
import ru.radom.kabinet.module.blagosfera.bp.model.BPModel;
import ru.radom.kabinet.module.blagosfera.bp.service.BPModelService;

import static ru.radom.kabinet.module.blagosfera.bp.util.BPConstants.*;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * Реализация для {@link BPModelService}
 */
@Service
public class BPModelServiceImpl implements BPModelService {

    @Autowired
    private BPModelDAO bpModelDAO;

    @Override
    @Transactional
    public BPModel create(String name) {
        Assert.notNull(name);

        BPModel modelData = new BPModel();
        modelData.setData("stub");
        bpModelDAO.save(modelData);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);
        ObjectNode propertiesNode = objectMapper.createObjectNode();
        propertiesNode.put("process_id", "process" + modelData.getId());
        editorNode.set("properties", propertiesNode);

        editorNode.put(MODEL_ID, modelData.getId().toString());
        editorNode.put(MODEL_NAME, name);
        editorNode.put(MODEL_REVISION, 1);
        editorNode.put(MODEL_DESCRIPTION, "");

        modelData.setData(editorNode.toString());
        bpModelDAO.save(modelData);

        return modelData;
    }

    @Override
    @Transactional
    public BPModel copy(BPModel model) {
        BPModel modelData = new BPModel();
        modelData.setData(model.getData());
        bpModelDAO.save(modelData);

        return modelData;
    }

    @Override
    @Transactional
    public void delete(Long modelId) {
        bpModelDAO.delete(modelId);
    }

    @Override
    @Transactional
    public BPModel updateModelData(Long modelId, String data) {
        BPModel model = bpModelDAO.getById(modelId);
        model.setData(data);
        return model;
    }
}
