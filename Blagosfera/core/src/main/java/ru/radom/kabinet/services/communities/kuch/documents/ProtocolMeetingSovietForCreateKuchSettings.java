package ru.radom.kabinet.services.communities.kuch.documents;

import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;

/**
 * Настройки - протокол собрания совета по созданию КУч
 * Created by vgusev on 20.09.2015.
 */
public class ProtocolMeetingSovietForCreateKuchSettings {

    // Код шаблона - протокол собрания совета по созданию КУч
    private static final String DEFAULT_TEMPLATE_CODE = "create_cooperative_plot_protocol";

    // Группа пайщиков создающих КУч
    private static final String DEFAULT_SHARES_OF_PO_PARTICIPANT_NAME = CommonCreateKuchSettings.SHARES_OF_KUCH_PO_PARTICIPANT_NAME;

    // ПО в рамках которого создаётся КУЧ
    private static final String DEFAULT_PO_COMMUNITY_PARTICIPANT_NAME = CommonCreateKuchSettings.PO_COMMUNITY_PARTICIPANT_NAME;
    //
    private static final String DEFAULT_KUCH_NAME_USER_FIELD_NAME = "НовыйКУЧ";
    //
    private static final String DEFAULT_PRESIDENT_SOVIET_USER_FIELD_NAME = "ЧленСовета1";
    //
    private static final String DEFAULT_COUNT_SHARES_USER_FIELD_NAME = "КоличествоПайщиков";

    private String templateCode;

    // Группа пайщиков создающих КУч
    private String sharesGroupParticipantName;

    // ПО в рамках которого создаётся КУЧ
    private String poCommunityParticipantName;
    // Название КУч
    private String kuchNameUserFieldName;
    // Имя Председателя Совета
    private String presidentSovietFieldName; //"ЧленСовета1"
    // Количество Пайщиков
    private String countSharesFieldName; // "КоличествоПайщиков"


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

    public String getPresidentSovietFieldName() {
        return presidentSovietFieldName == null ||
                presidentSovietFieldName.equals("") ?
                DEFAULT_PRESIDENT_SOVIET_USER_FIELD_NAME : presidentSovietFieldName;
    }

    public String getCountSharesFieldName() {
        return countSharesFieldName == null ||
                countSharesFieldName.equals("") ?
                DEFAULT_COUNT_SHARES_USER_FIELD_NAME : countSharesFieldName;
    }

}
