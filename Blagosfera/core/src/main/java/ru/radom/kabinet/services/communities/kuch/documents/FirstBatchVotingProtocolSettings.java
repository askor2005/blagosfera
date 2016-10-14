package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки шаблона документа - протокол собрания по созданию КУч
 * Created by vgusev on 20.09.2015.
 */
public class FirstBatchVotingProtocolSettings {

    // Код шаблона - протокол собрания по созданию КУч
    private static final String DEFAULT_TEMPLATE_CODE = "PO_PROTOCOL_SOZDANIE_KUCH";

    // Председатель собрания
    private static final String DEFAULT_PRESIDENT_OF_MEETING_PARTICIPANT_NAME = CommonCreateKuchSettings.PRESIDENT_OF_MEETING_PARTICIPANT_NAME;
    //
    private static final String DEFAULT_KUCH_NAME_USER_FIELD_NAME = "Название КУЧ";
    //
    private static final String DEFAULT_PROTOCOL_VOTING_AGENTA_USER_FIELD_NAME = "Протокол голосования за повестку дня";
    //
    private static final String DEFAULT_PROTOCOL_VOTING_CREATE_KUCH_USER_FIELD_NAME = "Протокол голосования за образование КУЧ";


    // Секретарь собрания
    private static final String DEFAULT_SECRETARY_OF_MEETING_PARTICIPANT_NAME = CommonCreateKuchSettings.SECRETARY_OF_MEETING_PARTICIPANT_NAME;

    // Группа пайщиков создающих КУч
    private static final String DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME = CommonCreateKuchSettings.SHARES_OF_PO_PARTICIPANT_NAME;

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;


    private String templateCode;

    //------------------------------------------------------------------------------------------------------------------
    // Председатель собрания
    //------------------------------------------------------------------------------------------------------------------
    private String presidentOfMeetingParticipantName;

    // Пользовательские поля председателя собрания

    // Название КУч
    private String kuchNameUserFieldName;

    // Протокол голосования за повестку дня
    private String protocolOfVotingOfMeetingAgenta;

    // Протокол голосования за образование КУч
    private String protocolOfVotingOfCreateKuch;

    //------------------------------------------------------------------------------------------------------------------

    // Секретарь собрания
    private String secretaryOfMeetingParticipantName;

    // Группа пайщиков создающих КУч
    private String sharesGroupParticipantName;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;

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

    public String getKuchNameUserFieldName() {
        return kuchNameUserFieldName == null ||
                kuchNameUserFieldName.equals("") ?
                DEFAULT_KUCH_NAME_USER_FIELD_NAME : kuchNameUserFieldName;
    }

    public String getProtocolOfVotingOfMeetingAgenta() {
        return protocolOfVotingOfMeetingAgenta == null ||
                protocolOfVotingOfMeetingAgenta.equals("") ?
                DEFAULT_PROTOCOL_VOTING_AGENTA_USER_FIELD_NAME : protocolOfVotingOfMeetingAgenta;
    }

    public String getProtocolOfVotingOfCreateKuch() {
        return protocolOfVotingOfCreateKuch == null ||
                protocolOfVotingOfCreateKuch.equals("") ?
                DEFAULT_PROTOCOL_VOTING_CREATE_KUCH_USER_FIELD_NAME : protocolOfVotingOfCreateKuch;
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


}
