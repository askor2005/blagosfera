package ru.radom.kabinet.web.admin.dto;

import lombok.Data;
import ru.radom.kabinet.model.SmtpServer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.05.2016.
 */
@Data
public class SmtpServerResponseDto {

    private Long id;
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private boolean isUsing;
    private boolean isDebug;

    public SmtpServerResponseDto(SmtpServer smtpServer) {
        setId(smtpServer.getId());
        setHost(smtpServer.getHost());
        setPort(smtpServer.getPort());
        setUsername(smtpServer.getUsername());
        setPassword(smtpServer.getPassword());
        setProtocol(smtpServer.getProtocol());
        setUsing(smtpServer.isUsing());
        setDebug(smtpServer.isDebug());
    }

    public static List<SmtpServerResponseDto> toDtoList(List<SmtpServer> smtpServers) {
        List<SmtpServerResponseDto> result = null;
        if (smtpServers != null && !smtpServers.isEmpty()) {
            result = new ArrayList<>();
            for (SmtpServer smtpServer : smtpServers) {
                result.add(new SmtpServerResponseDto(smtpServer));
            }
        }
        return result;
    }
    
}
