package ru.radom.kabinet.services.bpmhandlers.user.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Data
public class BPMUsersWebSocketDto {

    private List<Long> userIds;

    private String eventType;

    private Map<String, Object> parameters;
}
