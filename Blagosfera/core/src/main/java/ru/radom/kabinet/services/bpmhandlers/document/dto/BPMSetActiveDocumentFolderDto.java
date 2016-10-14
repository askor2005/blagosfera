package ru.radom.kabinet.services.bpmhandlers.document.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Getter
public class BPMSetActiveDocumentFolderDto {

    private Long documentFolderId;

    private Boolean active;
}
