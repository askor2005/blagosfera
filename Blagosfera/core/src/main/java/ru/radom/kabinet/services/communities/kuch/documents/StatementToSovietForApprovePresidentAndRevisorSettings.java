package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки шаблона - заявление в совет ПО на об утверждении председателя и ревизора КУч
 * Created by vgusev on 20.09.2015.
 */
public class StatementToSovietForApprovePresidentAndRevisorSettings {

    // Код шаблона - заявление в совет ПО на об утверждении председателя и ревизора КУч
    private static final String DEFAULT_TEMPLATE_CODE = "REQUEST_SELECT_CHAIRMAN_PO";

    // Группа пайщиков создающих КУч
    private static final String DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME = CommonCreateKuchSettings.SHARES_OF_KUCH_PO_PARTICIPANT_NAME;

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;

    // Созданный КУч
    private static final String DEFAULT_KUCH_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.KUCH_COMMUNITY_PARTICIPANT_NAME;

    // Председатель КУч
    private static final String DEFAULT_PRESIDENT_OF_KUCH_PARTICIPANT_NAME = CommonCreateKuchSettings.PRESIDENT_OF_KUCH_PARTICIPANT_NAME;


    private String templateCode;

    // Группа пайщиков создающих КУч
    private String sharesGroupParticipantName;

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
