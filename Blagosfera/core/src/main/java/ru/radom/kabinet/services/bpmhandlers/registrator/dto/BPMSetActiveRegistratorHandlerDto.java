package ru.radom.kabinet.services.bpmhandlers.registrator.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 03.08.2016.
 */
@Getter
public class BPMSetActiveRegistratorHandlerDto {

    private Long registratorId;

    private boolean active;
}
