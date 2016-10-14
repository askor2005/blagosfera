package ru.askor.blagosfera.domain.document;

import lombok.Data;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentTemplateParticipant {

    private Long id;

    private String participantName;

    private String parentParticipantName;
}
