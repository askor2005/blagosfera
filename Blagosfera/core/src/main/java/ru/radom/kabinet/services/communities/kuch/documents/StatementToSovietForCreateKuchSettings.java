package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки шаблона - заявление в совет ПО на создание КУч
 * Created by vgusev on 20.09.2015.
 */
public class StatementToSovietForCreateKuchSettings {

    // Код шаблона - заявление в совет ПО на создание КУч
    private static final String DEFAULT_TEMPLATE_CODE = "REQUEST_CREATE_KUCH";

    // Группа пайщиков создающих КУч
    private static final String DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME = CommonCreateKuchSettings.SHARES_OF_PO_PARTICIPANT_NAME;

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;

    // Название нового КУч - пользовательское поле
    private static final String DEFAULT_KUCH_NAME_USER_FIELD_NAME = "Название нового КУЧ";

    private String templateCode;

    // Группа пайщиков создающих КУч
    private String sharesGroupParticipantName;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;

    // Название КУч
    private String kuchNameUserFieldName;

    public String getTemplateCode() {
        return templateCode == null ||
                templateCode.equals("") ?
                DEFAULT_TEMPLATE_CODE : templateCode;
    }

    public String getSharesGroupParticipantName() {
        return sharesGroupParticipantName == null ||
                sharesGroupParticipantName.equals("") ?
                DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME : sharesGroupParticipantName;
    }

    public String getPoCommunityParticipantName() {
        return poCommunityParticipantName == null ||
                poCommunityParticipantName.equals("") ?
                DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME : poCommunityParticipantName;
    }

    public String getKuchNameUserFieldName() {
        return kuchNameUserFieldName == null ||
                kuchNameUserFieldName.equals("") ?
                DEFAULT_KUCH_NAME_USER_FIELD_NAME : kuchNameUserFieldName;
    }

}
