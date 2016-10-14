package ru.radom.kabinet.services;

import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.SharerService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service("stompService")
public class StompServiceImpl implements StompService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SharerService sharerService;

    @Async
    @RabbitListener(queues = "core.social.show.popup")
    @Override
    public void showPopupWorker(Map<String, Object> data) {
        Object receiverObj = data.get("receiver");
        User receiver = sharerService.tryGetUser(receiverObj);
        if (receiver != null) {
            Object senderObj = data.get("sender");
            User sender = sharerService.tryGetUser(senderObj);
            Map<String, Object> map = new HashMap<>();
            map.put("sharer", sender);
            map.put("text", MapUtils.getString(data, "text"));
            map.put("clientWindowId", MapUtils.getString(data, "clientWindowId"));
            map.put("type", MapUtils.getString(data, "popupType"));
            map.put("force", MapUtils.getBoolean(data, "force"));
            map.put("closeWith", MapUtils.getString(data, "closeWith", "").trim());
            map.put("timeout", MapUtils.getLong(data, "timeout"));
            map.put("clickScript", data.get("clickScript"));
            send(receiver.getEmail(), "show_popup", map);
        }
    }

    @Async
    @RabbitListener(queues = "core.client.scripting.execute")
    @Override
    public void executeClientScriptWorker(Map<String, Object> data) {
        Object sharerObj = data.get("sharer");
        User user = sharerService.tryGetUser(sharerObj);
        if (user != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("script", MapUtils.getString(data, "script"));
            map.put("clientWindowId", MapUtils.getString(data, "clientWindowId"));
            map.put("context", data.get("context"));
            send(user.getEmail(), "execute_client_script", map);
        }
    }

    @Async
    @Override
    public void send(Collection<User> users, String destination, Object payload) {
        for (User user : users) {
            send(user.getEmail(), destination, payload);
        }
    }

    @Async
    @Override
    public void send(String username, String destination, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/" + destination, payload);
    }

    @Async
    @Override
    public void send(String destination, Object payload) {
        simpMessagingTemplate.convertAndSend(destination, payload);
    }
}
