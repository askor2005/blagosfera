package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки - положение КУч
 * Created by vgusev on 20.09.2015.
 */
public class StateKuchSettings {

    // Код шаблона - положение КУч
    private static final String DEFAULT_TEMPLATE_CODE = "STATE_KUCH_PO";

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;

    // Созданный КУч
    private static final String DEFAULT_KUCH_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.KUCH_COMMUNITY_PARTICIPANT_NAME;
    // Пользовательское поле - Дата подписания протокола
    private static final String DEFAULT_DATE_SIGN_PROTOCOL_USER_FIELD_NAME = "Дата подписания протокола";


    private String templateCode;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;

    // Участник документа - созданный КУч
    private String kuchCommunityParticipantName;
    //
    private String dateSignProtocolUserFieldName;

    public String getTemplateCode() {
        return templateCode == null ||
                templateCode.equals("") ?
                DEFAULT_TEMPLATE_CODE : templateCode;
    }

    public String getPoCommunityParticipantName() {
        return poCommunityParticipantName == null ||
                poCommunityParticipantName.equals("") ?
                DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME : poCommunityParticipantName;
    }

    public String getKuchCommunityParticipantName() {
        return kuchCommunityParticipantName == null ||
                kuchCommunityParticipantName.equals("") ?
                DEFAULT_KUCH_COMMUNITY_PARTICIPANT_NAME : kuchCommunityParticipantName;
    }

    public String getDateSignProtocolUserFieldName() {
        return dateSignProtocolUserFieldName == null ||
                dateSignProtocolUserFieldName.equals("") ?
                DEFAULT_DATE_SIGN_PROTOCOL_USER_FIELD_NAME : dateSignProtocolUserFieldName;
    }

}
