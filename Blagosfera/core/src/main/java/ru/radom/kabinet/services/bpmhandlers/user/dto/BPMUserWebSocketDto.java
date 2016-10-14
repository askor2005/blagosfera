package ru.radom.kabinet.services.bpmhandlers.user.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Data
public class BPMUserWebSocketDto {

    private Long userId;

    private String eventType;

    private Map<String, Object> parameters;
}
