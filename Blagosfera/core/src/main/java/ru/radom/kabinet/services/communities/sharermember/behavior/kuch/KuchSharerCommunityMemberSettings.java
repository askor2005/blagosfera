package ru.radom.kabinet.services.communities.sharermember.behavior.kuch;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

/**
 * Настройки шаблонов документов:
 * - заялвение на вступление в КУч ПО от физ лица
 * - протокол вступления физ лиц в КУч ПО
 * - заялвение на выход из КУч ПО от физ лица
 * - протокол выхода физ лиц из КУч ПО
 * Created by vgusev on 25.10.2015.
 */
public class KuchSharerCommunityMemberSettings {

    public static final String SETTINGS_KEY = "KuchSharerCommunityMemberSettings";

    private static KuchSharerCommunityMemberSettings instance;

    private static Gson gson = new Gson();

    private KuchSharerCommunityMemberSettings() {

    }

    public static KuchSharerCommunityMemberSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (StringUtils.isBlank(jsonSettings)) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, KuchSharerCommunityMemberSettings.class);
            } catch (Exception e) {
                initDefault();
                //System.err.println("Необходимо создать настройку " + SETTINGS_KEY + "!");
                e.printStackTrace();
            }
        }
    }

    private static void initDefault() {
        instance = new KuchSharerCommunityMemberSettings();

        // Настройки для шаблона заявления пайщика о вступлении в КУч ПО
        instance.entranceSharerToCommunityDocumentTemplateCode = ENTRANCE_SHARER_TO_COMMUNITY_DOCUMENT_TEMPLATE_CODE;
        instance.entranceSharerToCommunitySharerParticipantName = ENTRANCE_SHARER_TO_COMMUNITY_SHARER_PARTICIPANT_NAME;
        instance.entranceSharerToCommunityCommunityParticipantName = ENTRANCE_SHARER_TO_COMMUNITY_COMMUNITY_PARTICIPANT_NAME;
        instance.entranceSharerToCommunityParentCommunityParticipantName = ENTRANCE_SHARER_TO_COMMUNITY_PARENT_COMMUNITY_PARTICIPANT_NAME;

        // Настройки протокола собрания о вступлении пайщиков в КУч ПО
        instance.joinSharerToCommunityDocumentTemplateCode = JOIN_SHARER_TO_COMMUNITY_DOCUMENT_TEMPLATE_CODE;
        instance.joinSharerToCommunityLoaDocumentTemplateCode = JOIN_SHARER_TO_COMMUNITY_LOA_DOCUMENT_TEMPLATE_CODE;
        instance.documentProtocolJoinSharersListParticipantName = DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_PARTICIPANT_NAME;
        instance.documentProtocolDelegateParticipantName = DOCUMENT_PROTOCOL_DELEGATE_PARTICIPANT_NAME;
        instance.documentProtocolJoinSharersListCooperativeParticipantName = DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_COOPERATIVE_PARTICIPANT_NAME;
        instance.documentProtocolJoinSharersListParentCooperativeParticipantName = DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_PARENT_COOPERATIVE_PARTICIPANT_NAME;
        instance.sharersStatementDocumentListUserFieldName = SHARERS_STATEMENT_DOCUMENT_LIST_USER_FIELD_NAME;

        // Настройки заявления на выход из КУч ПО
        instance.requestLeaveSharerFromCommunityDocumentTemplateCode = REQUEST_LEAVE_SHARER_FROM_COMMUNITY_DOCUMENT_TEMPLATE_CODE;
        instance.leaveStatementDocumentSharerParticipantName = LEAVE_STATEMENT_DOCUMENT_SHARER_PARTICIPANT_NAME;
        instance.leaveStatementDocumentCommunityParticipantName = LEAVE_STATEMENT_DOCUMENT_COMMUNITY_PARTICIPANT_NAME;
        instance.leaveStatementDocumentParentCommunityParticipantName = LEAVE_STATEMENT_DOCUMENT_PARENT_COMMUNITY_PARTICIPANT_NAME;

        // Настройки протокола совета на выход пайщиков из КУч ПО
        instance.leaveSharersFromCommunityDocumentTemplateCode = LEAVE_SHARERS_FROM_COMMUNITY_DOCUMENT_TEMPLATE_CODE;
        instance.documentProtocolLeaveSharersListParticipantName = DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_PARTICIPANT_NAME;
        instance.documentProtocolLeaveSharersListCooperativeParticipantName = DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_COOPERATIVE_PARTICIPANT_NAME;
        instance.documentProtocolLeaveSharersListParentCooperativeParticipantName = DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_PARENT_COMMUNITY_PARTICIPANT_NAME;
        instance.sharersStatementToLeaveDocumentListUserFieldName = SHARERS_STATEMENT_TO_LEAVE_DOCUMENT_LIST_USER_FIELD_NAME;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Настройки для шаблона заявления пайщика о вступлении в КУч ПО
    //------------------------------------------------------------------------------------------------------------------
    private String entranceSharerToCommunityDocumentTemplateCode;
    private String entranceSharerToCommunitySharerParticipantName;
    private String entranceSharerToCommunityCommunityParticipantName;
    private String entranceSharerToCommunityParentCommunityParticipantName;

    public String getEntranceSharerToCommunityDocumentTemplateCode() {
        return entranceSharerToCommunityDocumentTemplateCode;
    }

    public String getEntranceSharerToCommunitySharerParticipantName() {
        return entranceSharerToCommunitySharerParticipantName;
    }

    public String getEntranceSharerToCommunityCommunityParticipantName() {
        return entranceSharerToCommunityCommunityParticipantName;
    }

    public String getEntranceSharerToCommunityParentCommunityParticipantName() {
        return entranceSharerToCommunityParentCommunityParticipantName;
    }

    // Код шаблона заявления пайщика о вступлении в КУч ПО
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_DOCUMENT_TEMPLATE_CODE = "kuch_statement_to_shareholders_of_the_physical_person";
    // Наименование участника документа - физ лицо
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_SHARER_PARTICIPANT_NAME = "Пайщик физ. лицо";
    // Наименование участника документа - КУч ПО
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_COMMUNITY_PARTICIPANT_NAME = "Кооперативный Участок";
    // Наименование участника документа - ПО
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_PARENT_COMMUNITY_PARTICIPANT_NAME = "Потребительское Общество";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Настройки протокола собрания о вступлении пайщиков в КУч ПО
    //------------------------------------------------------------------------------------------------------------------
    private String joinSharerToCommunityDocumentTemplateCode;
    private String joinSharerToCommunityLoaDocumentTemplateCode;
    private String documentProtocolJoinSharersListParticipantName;
    private String documentProtocolDelegateParticipantName;
    private String documentProtocolJoinSharersListCooperativeParticipantName;
    private String documentProtocolJoinSharersListParentCooperativeParticipantName;
    private String sharersStatementDocumentListUserFieldName;

    public String getJoinSharerToCommunityDocumentTemplateCode() {
        return joinSharerToCommunityDocumentTemplateCode;
    }

    public String getJoinSharerToCommunityLoaDocumentTemplateCode() {
        return joinSharerToCommunityLoaDocumentTemplateCode;
    }

    public String getDocumentProtocolJoinSharersListParticipantName() {
        return documentProtocolJoinSharersListParticipantName;
    }

    public String getDocumentProtocolDelegateParticipantName() {
        return documentProtocolDelegateParticipantName;
    }

    public String getDocumentProtocolJoinSharersListCooperativeParticipantName() {
        return documentProtocolJoinSharersListCooperativeParticipantName;
    }

    public String getDocumentProtocolJoinSharersListParentCooperativeParticipantName() {
        return documentProtocolJoinSharersListParentCooperativeParticipantName;
    }

    public String getSharersStatementDocumentListUserFieldName() {
        return sharersStatementDocumentListUserFieldName;
    }

    // Код шаблона протокола собрания совета КУч ПО
    private static final String JOIN_SHARER_TO_COMMUNITY_DOCUMENT_TEMPLATE_CODE = "kuch_protocol_join_new_members_to_cooperative";
    // Код шаблона протокола собрания совета КУч ПО для подписания по доверенности
    private static final String JOIN_SHARER_TO_COMMUNITY_LOA_DOCUMENT_TEMPLATE_CODE = "kuch_protocol_join_new_members_to_cooperative_loa";


    // Наименование участников документа протокола принятия кандидатов в пайщики - физ лица
    private static final String DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_PARTICIPANT_NAME = "Участники (физ. лица)";
    //
    private static final String DOCUMENT_PROTOCOL_DELEGATE_PARTICIPANT_NAME = "Представитель КУч ПО";
    // Наименование участника документа протокола принятия кандидатов в пайщики - КУч ПО
    private static final String DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_COOPERATIVE_PARTICIPANT_NAME = "Кооперативный Участок";
    // Наименование участника документа протокола принятия кандидатов в пайщики - ПО
    private static final String DOCUMENT_PROTOCOL_JOIN_SHARERS_LIST_PARENT_COOPERATIVE_PARTICIPANT_NAME = "Потребительское Общество";
    // Пользовательское пол - список документов кандидатов в пайшики КУч ПО
    private static final String SHARERS_STATEMENT_DOCUMENT_LIST_USER_FIELD_NAME = "Заявления кандидатов в пайщики";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Настройки заявления на выход из КУч ПО
    //------------------------------------------------------------------------------------------------------------------
    private String requestLeaveSharerFromCommunityDocumentTemplateCode;
    private String leaveStatementDocumentSharerParticipantName;
    private String leaveStatementDocumentCommunityParticipantName;
    private String leaveStatementDocumentParentCommunityParticipantName;

    public String getRequestLeaveSharerFromCommunityDocumentTemplateCode() {
        return requestLeaveSharerFromCommunityDocumentTemplateCode;
    }

    public String getLeaveStatementDocumentSharerParticipantName() {
        return leaveStatementDocumentSharerParticipantName;
    }

    public String getLeaveStatementDocumentCommunityParticipantName() {
        return leaveStatementDocumentCommunityParticipantName;
    }

    public String getLeaveStatementDocumentParentCommunityParticipantName() {
        return leaveStatementDocumentParentCommunityParticipantName;
    }

    // Код шаблона заявления пайщика о выходе из КУч ПО
    private static final String REQUEST_LEAVE_SHARER_FROM_COMMUNITY_DOCUMENT_TEMPLATE_CODE = "kuch_statement_to_leave_physical_persons_from_community";
    // Наименование участника документа - физ лицо
    private static final String LEAVE_STATEMENT_DOCUMENT_SHARER_PARTICIPANT_NAME = "Пайщик физ лицо";
    // Наименование участника документа - КУч ПО
    private static final String LEAVE_STATEMENT_DOCUMENT_COMMUNITY_PARTICIPANT_NAME = "Кооперативный Участок";
    // Наименование участника документа - ПО
    private static final String LEAVE_STATEMENT_DOCUMENT_PARENT_COMMUNITY_PARTICIPANT_NAME = "Потребительское Общество";
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Настройки протокола совета на выход пайщиков из КУч ПО
    //------------------------------------------------------------------------------------------------------------------
    private String leaveSharersFromCommunityDocumentTemplateCode;
    private String documentProtocolLeaveSharersListParticipantName;
    private String documentProtocolLeaveSharersListCooperativeParticipantName;
    private String documentProtocolLeaveSharersListParentCooperativeParticipantName;
    private String sharersStatementToLeaveDocumentListUserFieldName;

    public String getLeaveSharersFromCommunityDocumentTemplateCode() {
        return leaveSharersFromCommunityDocumentTemplateCode;
    }

    public String getDocumentProtocolLeaveSharersListParticipantName() {
        return documentProtocolLeaveSharersListParticipantName;
    }

    public String getDocumentProtocolLeaveSharersListCooperativeParticipantName() {
        return documentProtocolLeaveSharersListCooperativeParticipantName;
    }

    public String getSharersStatementToLeaveDocumentListUserFieldName() {
        return sharersStatementToLeaveDocumentListUserFieldName;
    }

    public String getDocumentProtocolLeaveSharersListParentCooperativeParticipantName() {
        return documentProtocolLeaveSharersListParentCooperativeParticipantName;
    }

    // Код шаблона протокола собрания совета КУч ПО по выходу пайщиков
    private static final String LEAVE_SHARERS_FROM_COMMUNITY_DOCUMENT_TEMPLATE_CODE = "kuch_protocol_leave_members_from_cooperative";
    // Наименование участников документа протокола выхода пайщиков из КУч ПО - физ лица
    private static final String DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_PARTICIPANT_NAME = "Пайщики физ. лица";
    // Наименование участника документа протокола выхода пайщиков из КУч ПО - КУч ПО
    private static final String DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_COOPERATIVE_PARTICIPANT_NAME = "Кооперативный Участок";
    // Наименование участника документа протокола выхода пайщиков из КУч ПО - ПО
    private static final String DOCUMENT_PROTOCOL_LEAVE_SHARERS_LIST_PARENT_COMMUNITY_PARTICIPANT_NAME = "Потребительское Общество";
    // Пользовательское поле - список заявлений выхода пайщиков из КУч ПО
    private static final String SHARERS_STATEMENT_TO_LEAVE_DOCUMENT_LIST_USER_FIELD_NAME = "Заявления пайщиков на выход из КУч ПО";
    //------------------------------------------------------------------------------------------------------------------
}
