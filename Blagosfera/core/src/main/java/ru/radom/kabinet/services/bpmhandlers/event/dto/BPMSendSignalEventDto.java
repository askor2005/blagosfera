package ru.radom.kabinet.services.bpmhandlers.event.dto;

import lombok.Getter;

import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Getter
public class BPMSendSignalEventDto {

    private String signalId;

    private Map<String, Object> parameters;
}
