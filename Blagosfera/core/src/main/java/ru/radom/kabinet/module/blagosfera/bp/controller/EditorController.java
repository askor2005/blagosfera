package ru.radom.kabinet.module.blagosfera.bp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilRabbitConstants;
import ru.radom.kabinet.module.blagosfera.bp.dao.BPModelDAO;

/**
 * Created by Otts Alexey on 15.10.2015.<br/>
 * Контроллер для работы с редактором бизнесс процессов
 */
@Controller
@RequestMapping("/admin/bpeditor")
public class EditorController {

    @Autowired
    private BPModelDAO bpModelDAO;

    @Autowired
    private BPMBlagosferaUtils bpmBlagosferaUtils;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Основная View для редактора
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEditorView(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("model", bpModelDAO.getById(id).getData());
        return "bpeditor";
    }

    /**
     * Основная View для редактора дерева процессов
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public String getTreeView() {
        return "adminBpTree";
    }

    /**
     * View для редактировния компоненетов редактора
     */
    @RequestMapping(value = "/stencils")
    public String getStencilsEditor(Model model) throws JsonProcessingException {
        //Object stencils = rabbitTemplate.convertSendAndReceive(StencilRabbitConstants.STENCILS_EXCHANGE, StencilRabbitConstants.GET_CUSTOM_STENCILS, "");
        Object stencils = bpmBlagosferaUtils.getCustomStencils();
        model.addAttribute("stencils", objectMapper.writeValueAsString(stencils));
        return "adminBpComponentsEditor";
    }
}
