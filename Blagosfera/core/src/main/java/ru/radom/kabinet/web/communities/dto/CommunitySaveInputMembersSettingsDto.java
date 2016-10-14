package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;

import java.util.List;

/**
 *
 * Created by vgusev on 26.07.2016.
 */
@Data
public class CommunitySaveInputMembersSettingsDto {

    private List<DocumentTemplateSettingDto> documentTemplateSettings;

    private boolean needCreateDocuments;

}
