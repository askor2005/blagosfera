package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки - протокол собрания по выбору председателя и ревизора куч
 * Created by vgusev on 20.09.2015.
 */
public class SecondBatchVotingProtocolSettings {

    // Код шаблона - протокол собрания по выбору председателя и ревизора куч
    private static final String DEFAULT_TEMPLATE_CODE = "voting_protocol_president_of_cooperative_plot";

    // Председатель собрания
    private static final String DEFAULT_PRESIDENT_OF_MEETING_PARTICIPANT_NAME = CommonCreateKuchSettings.PRESIDENT_OF_MEETING_PARTICIPANT_NAME;

    // Секретарь собрания
    private static final String DEFAULT_SECRETARY_OF_MEETING_PARTICIPANT_NAME = CommonCreateKuchSettings.SECRETARY_OF_MEETING_PARTICIPANT_NAME;

    // Группа пайщиков создающих КУч
    private static final String DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME = CommonCreateKuchSettings.SHARES_OF_KUCH_PO_PARTICIPANT_NAME;

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;

    // Созданный КУч
    private static final String DEFAULT_KUCH_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.KUCH_COMMUNITY_PARTICIPANT_NAME;
    // Наименование пользовательского поля - "Результаты выборов Председателя КУч"
    private static final String DEFAULT_PROTOCOL_VOTING_PRESIDENT_OF_KUCH_USER_FIELD_NAME = "Результаты выборов Председателя КУч";


    // Председатель КУч
    private static final String DEFAULT_PRESIDENT_OF_KUCH_PARTICIPANT_NAME = CommonCreateKuchSettings.PRESIDENT_OF_KUCH_PARTICIPANT_NAME;

    // Ревизор КУч
    private static final String DEFAULT_REVISOR_OF_KUCH_PARTICIPANT_NAME = CommonCreateKuchSettings.REVISOR_OF_KUCH_PARTICIPANT_NAME;



    private String templateCode;

    // Председатель собрания
    private String presidentOfMeetingParticipantName;

    // Секретарь собрания
    private String secretaryOfMeetingParticipantName;

    // Группа пайщиков создающих КУч
    private String sharesGroupParticipantName;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;

    //------------------------------------------------------------------------------------------------------------------
    // Участник документа - созданный КУч
    //------------------------------------------------------------------------------------------------------------------
    private String kuchCommunityParticipantName;
    //
    private String protocolVotingPresidentOfKuchUserFieldName;

    //------------------------------------------------------------------------------------------------------------------

    // Председатель КУч
    private String presidentOfKuchParticipantName;

    // Ревизор КУч
    private String revisorOfKuchParticipantName;

    public String getTemplateCode() {
        return templateCode == null ||
                templateCode.equals("") ?
                DEFAULT_TEMPLATE_CODE : templateCode;
    }

    public String getPresidentOfMeetingParticipantName() {
        return presidentOfMeetingParticipantName == null ||
                presidentOfMeetingParticipantName.equals("") ?
                DEFAULT_PRESIDENT_OF_MEETING_PARTICIPANT_NAME : presidentOfMeetingParticipantName;
    }

    public String getSecretaryOfMeetingParticipantName() {
        return secretaryOfMeetingParticipantName == null ||
                secretaryOfMeetingParticipantName.equals("") ?
                DEFAULT_SECRETARY_OF_MEETING_PARTICIPANT_NAME : secretaryOfMeetingParticipantName;
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

    public String getProtocolVotingPresidentOfKuchUserFieldName() {
        return protocolVotingPresidentOfKuchUserFieldName == null ||
                protocolVotingPresidentOfKuchUserFieldName.equals("") ?
                DEFAULT_PROTOCOL_VOTING_PRESIDENT_OF_KUCH_USER_FIELD_NAME :
                protocolVotingPresidentOfKuchUserFieldName;
    }

    public String getPresidentOfKuchParticipantName() {
        return presidentOfKuchParticipantName == null ||
                presidentOfKuchParticipantName.equals("") ?
                DEFAULT_PRESIDENT_OF_KUCH_PARTICIPANT_NAME :
                presidentOfKuchParticipantName;
    }

    public String getRevisorOfKuchParticipantName() {
        return revisorOfKuchParticipantName == null ||
                revisorOfKuchParticipantName.equals("") ?
                DEFAULT_REVISOR_OF_KUCH_PARTICIPANT_NAME :
                revisorOfKuchParticipantName;
    }

}
