package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.SharerService;

import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getUserHandler")
@Transactional
public class BPMGetUserHandler implements BPMHandler {

    @Autowired
    private SharerService sharerService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        Object sharerObj = parameters.get("sharer");
        User sharer = sharerService.tryGetUser(sharerObj);
        return sharer == null ? null : sharerService.convertUserToSend(sharer);
    }
}