package ru.radom.kabinet.web.communities.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;

import java.util.List;

/**
 *
 * Created by vgusev on 26.07.2016.
 */
@Getter
public class CommunityInputMembersPageDataDto {

    private CommunityAnyPageDto community;

    private List<DocumentTemplateSettingDto> documentTemplateSettings;

    private boolean needCreateDocuments;

    public CommunityInputMembersPageDataDto(
            CommunityAnyPageDto community, List<DocumentTemplateSettingDto> documentTemplateSettings,
            boolean needCreateDocuments) {
        this.community = community;
        this.documentTemplateSettings = documentTemplateSettings;
        this.needCreateDocuments = needCreateDocuments;
    }
}
