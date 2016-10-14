package ru.askor.blagosfera.domain.document.templatesettings;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
@Data
public class DocumentTemplateSetting {

    private Long id;

    private DocumentTemplate documentTemplate;

    private List<DocumentTemplateParticipantSetting> documentTemplateParticipantSettings = new ArrayList<>();
}
