package ru.askor.blagosfera.domain.document.templatesettings;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
@Data
public class DocumentTemplateParticipantSetting {

    private Long id;

    private DocumentClassDataSource documentClassDataSource;

    private DocumentTemplateParticipantType documentTemplateParticipantType;

    private String sourceName;

    private Long sourceId;

    private String participantSourceName;
}
