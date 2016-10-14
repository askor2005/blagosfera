package ru.radom.kabinet.module.blagosfera.bp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilForm;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.CommonConstants;

import java.util.Map;

/**
 * Created by Otts Alexey on 04.11.2015.<br/>
 * Контроллер для работы с {@link }
 */
@Controller
@RequestMapping("/admin/bpeditor/stencil")
public class StencilController {
/*
    @Autowired
    private RabbitTemplate rabbitTemplate;*/

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private BPMBlagosferaUtils bpmBlagosferaUtils;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public StencilForm createStencil(@RequestBody StencilForm stencil) {
        return bpmBlagosferaUtils.createStencil(stencil);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public StencilForm updateStencil(@RequestBody StencilForm stencil) {
        return bpmBlagosferaUtils.updateStencil(stencil);
        //return (StencilForm) rabbitTemplate.convertSendAndReceive(STENCILS_EXCHANGE, UPDATE_STENCIL, stencil);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteStencil(@RequestBody Map<String, String> data) {
        bpmBlagosferaUtils.deleteStencil(data.get("id"));
        //rabbitTemplate.convertSendAndReceive(STENCILS_EXCHANGE, REMOVE_STENCIL, data.get("id"));
    }

    @ResponseBody
    @RequestMapping(value = "/set", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    public Map<String, Object> getStencilSet() {
        return serializeService.toPrimitiveObject(bpmBlagosferaUtils.getStencilSet());
        /*String jsonString = (String) rabbitTemplate.convertSendAndReceive(STENCILS_EXCHANGE, GET_STENCIL_SET, "");
        return serializeService.jsonToMap(jsonString);*/
    }

}
