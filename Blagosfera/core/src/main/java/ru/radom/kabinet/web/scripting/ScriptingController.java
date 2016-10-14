package ru.radom.kabinet.web.scripting;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.blagosfera.scripting.commons.ScriptData;

import static ru.radom.blagosfera.scripting.commons.ScriptingRabbitConstants.EXECUTE_SCRIPT_QUEUE;
import static ru.radom.blagosfera.scripting.commons.ScriptingRabbitConstants.SCRIPTING_EXCHANGE;

/**
 * Created by Otts Alexey on 13.11.2015.<br/>
 */
@Controller
@RequestMapping("/api/scripting")
public class ScriptingController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ResponseBody
    @RequestMapping(value = "/call", method = RequestMethod.POST)
    public void callScript(
        @RequestBody ScriptData scriptData
    ) {
        rabbitTemplate.convertAndSend(SCRIPTING_EXCHANGE, EXECUTE_SCRIPT_QUEUE, scriptData);
    }

}
