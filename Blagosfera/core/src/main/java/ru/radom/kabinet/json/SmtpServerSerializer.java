package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.SmtpServer;

@Component("smtpServerSerializer")
public class SmtpServerSerializer extends AbstractSerializer<SmtpServer> {
    @Override
    public JSONObject serializeInternal(SmtpServer smtpServer) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", smtpServer.getId());
        jsonObject.put("host", smtpServer.getHost());
        jsonObject.put("port", smtpServer.getPort());
        jsonObject.put("username", smtpServer.getUsername());
        jsonObject.put("password", smtpServer.getPassword());
        jsonObject.put("protocol", smtpServer.getProtocol());
        jsonObject.put("using", smtpServer.isUsing());
        jsonObject.put("debug", smtpServer.isDebug());
        return jsonObject;
    }
}