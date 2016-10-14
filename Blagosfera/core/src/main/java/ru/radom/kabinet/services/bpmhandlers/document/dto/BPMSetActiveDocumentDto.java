package ru.radom.kabinet.services.bpmhandlers.document.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Getter
public class BPMSetActiveDocumentDto {

    private Long documentId;

    private Boolean active;
}
