package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки - доверенность председателя КУч ПО
 * Created by vgusev on 20.09.2015.
 */
public class ProxyPresidentKuchSettings {

    // Код шаблона - доверенность председателя КУч ПО
    private static final String DEFAULT_TEMPLATE_CODE = "GRANT_PO_TO_KUCH_CHAIRMAN";

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;

    // Созданный КУч
    private static final String DEFAULT_KUCH_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.KUCH_COMMUNITY_PARTICIPANT_NAME;

    // Председатель КУч
    private static final String DEFAULT_PRESIDENT_OF_KUCH_PARTICIPANT_NAME = CommonCreateKuchSettings.PRESIDENT_OF_KUCH_PARTICIPANT_NAME;


    private String templateCode;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;

    // Участник документа - созданный КУч
    private String kuchCommunityParticipantName;

    // Председатель КУч
    private String presidentOfKuchParticipantName;


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

    public String getPresidentOfKuchParticipantName() {
        return presidentOfKuchParticipantName == null ||
                presidentOfKuchParticipantName.equals("") ?
                DEFAULT_PRESIDENT_OF_KUCH_PARTICIPANT_NAME :
                presidentOfKuchParticipantName;
    }

}
