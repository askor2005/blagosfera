package ru.radom.kabinet.module.blagosfera.bp.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.kabinet.module.blagosfera.bp.dao.BPModelDAO;
import ru.radom.kabinet.module.blagosfera.bp.dto.ModelSaveForm;
import ru.radom.kabinet.module.blagosfera.bp.model.BPModel;
import ru.radom.kabinet.module.blagosfera.bp.service.BPModelService;

/**
 * Created by alex on 06.10.2015.<br/>
 * Контроллер для работы с моделью процесса в виде json
 */
@Controller
@RequestMapping("/admin/bpeditor")
public class ModelController {

    @Autowired
    private BPModelService bpModelService;

    @Autowired
    private BPModelDAO bpModelDAO;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BPMBlagosferaUtils bpmBlagosferaUtils;

    @ResponseBody
    @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
    public void saveModel(@PathVariable Long modelId, @RequestBody ModelSaveForm values) {
        bpModelService.updateModelData(modelId, values.getModel());
    }

    @ResponseBody
    @RequestMapping(value="/model/{modelId}/deploy", method = RequestMethod.POST)
    public Object deployModel(@PathVariable Long modelId) {
        try {
            BPModel model = bpModelDAO.getById(modelId);
            bpmBlagosferaUtils.deployModelWorker(model.getData());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        /*Boolean receive = (Boolean) rabbitTemplate.convertSendAndReceive(
            BPMBlagosferaUtils.BPM_EXCHANGE,
            BPMBlagosferaUtils.DEPLOY_MODEL,
            model.getData()
        );*/
        /*if(receive == null) {
            throw new IllegalStateException("BPM не доступен");
        } else {
            if(!receive) {
                throw new IllegalStateException("Возникла ошибка при разворачивании модели");
            }
        }*/
        return true;
    }


}
