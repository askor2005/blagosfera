package ru.radom.kabinet.services.communities.organizationmember.behavior.kuch;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

/**
 * Настройки документов для вступления юр лиц в КУч ПО
 * Created by vgusev on 20.10.2015.
 */
public class KuchOrganizationMemberSettings {

    public static final String SETTINGS_KEY = "KuchOrganizationMemberSettings";

    private static KuchOrganizationMemberSettings instance;

    private static Gson gson = new Gson();

    private KuchOrganizationMemberSettings() {

    }

    public static KuchOrganizationMemberSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (StringUtils.isBlank(jsonSettings)) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, KuchOrganizationMemberSettings.class);
            } catch (Exception e) {
                initDefault();
                //System.err.println("Необходимо создать настройку " + SETTINGS_KEY + "!");
                e.printStackTrace();
            }
        }
    }

    private static void initDefault() {
        instance = new KuchOrganizationMemberSettings();

        // Настройки по умолчанию для заявления о вступлении в КУч ПО
        instance.statementJoinTemplateCode = DEFAULT_STATEMENT_JOIN_TEMPLATE_CODE;
        instance.statementJoinCommunityParticipantName = DEFAULT_STATEMENT_JOIN_COMMUNITY_PARTICIPANT_NAME;
        instance.statementJoinOrganizationParticipantName = DEFAULT_STATEMENT_JOIN_ORGANIZATION_PARTICIPANT_NAME;

        // Настройки по умполнчанию для протокола на принятие юр лиц в пайщики КУч ПО
        instance.protocolJoinTemplateCode = DEFAULT_PROTOCOL_JOIN_TEMPLATE_CODE;
        instance.protocolJoinCommunityParticipantName = DEFAULT_PROTOCOL_JOIN_COMMUNITY_PARTICIPANT_NAME;
        instance.protocolJoinOrganizationsParticipantName = DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_PARTICIPANT_NAME;
        instance.protocolJoinOrganizationsDocumentsUserField = DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_DOCUMENTS_USER_FIELD;

        // Настройки по умполнчанию для заявления о выходе из КУч ПО
        instance.statementExcludeTemplateCode = DEFAULT_STATEMENT_EXCLUDE_TEMPLATE_CODE;
        instance.statementExcludeCommunityParticipantName = DEFAULT_STATEMENT_EXCLUDE_COMMUNITY_PARTICIPANT_NAME;
        instance.statementExcludeOrganizationParticipantName = DEFAULT_STATEMENT_EXCLUDE_ORGANIZATION_PARTICIPANT_NAME;

        // Настройки по умполнчанию для протокола на выход юр лиц из КУч ПО
        instance.protocolExcludeTemplateCode = DEFAULT_PROTOCOL_EXCLUDE_TEMPLATE_CODE;
        instance.protocolExcludeCommunityParticipantName = DEFAULT_PROTOCOL_EXCLUDE_COMMUNITY_PARTICIPANT_NAME;
        instance.protocolExcludeOrganizationsParticipantName = DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_PARTICIPANT_NAME;
        instance.protocolExcludeOrganizationsDocumentsUserField = DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_DOCUMENTS_USER_FIELD;
    }

    //---------------------------------------------------
    private static final String DEFAULT_STATEMENT_JOIN_TEMPLATE_CODE = "KUCH_DEFAULT_STATEMENT_JOIN_TEMPLATE_CODE";
    private static final String DEFAULT_STATEMENT_JOIN_COMMUNITY_PARTICIPANT_NAME = "KUCH_DEFAULT_STATEMENT_JOIN_COMMUNITY_PARTICIPANT_NAME";
    private static final String DEFAULT_STATEMENT_JOIN_ORGANIZATION_PARTICIPANT_NAME = "KUCH_DEFAULT_STATEMENT_JOIN_ORGANIZATION_PARTICIPANT_NAME";

    private static final String DEFAULT_PROTOCOL_JOIN_TEMPLATE_CODE = "KUCH_DEFAULT_PROTOCOL_JOIN_TEMPLATE_CODE";
    private static final String DEFAULT_PROTOCOL_JOIN_COMMUNITY_PARTICIPANT_NAME = "KUCH_DEFAULT_PROTOCOL_JOIN_COMMUNITY_PARTICIPANT_NAME";
    private static final String DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_PARTICIPANT_NAME = "KUCH_DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_PARTICIPANT_NAME";
    private static final String DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_DOCUMENTS_USER_FIELD = "KUCH_DEFAULT_PROTOCOL_JOIN_ORGANIZATIONS_DOCUMENTS_USER_FIELD";

    private static final String DEFAULT_STATEMENT_EXCLUDE_TEMPLATE_CODE = "KUCH_DEFAULT_STATEMENT_EXCLUDE_TEMPLATE_CODE";
    private static final String DEFAULT_STATEMENT_EXCLUDE_COMMUNITY_PARTICIPANT_NAME = "KUCH_DEFAULT_STATEMENT_EXCLUDE_COMMUNITY_PARTICIPANT_NAME";
    private static final String DEFAULT_STATEMENT_EXCLUDE_ORGANIZATION_PARTICIPANT_NAME = "KUCH_DEFAULT_STATEMENT_EXCLUDE_ORGANIZATION_PARTICIPANT_NAME";

    private static final String DEFAULT_PROTOCOL_EXCLUDE_TEMPLATE_CODE = "KUCH_DEFAULT_PROTOCOL_EXCLUDE_TEMPLATE_CODE";
    private static final String DEFAULT_PROTOCOL_EXCLUDE_COMMUNITY_PARTICIPANT_NAME = "KUCH_DEFAULT_PROTOCOL_EXCLUDE_COMMUNITY_PARTICIPANT_NAME";
    private static final String DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_PARTICIPANT_NAME = "KUCH_DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_PARTICIPANT_NAME";
    private static final String DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_DOCUMENTS_USER_FIELD = "KUCH_DEFAULT_PROTOCOL_EXCLUDE_ORGANIZATIONS_DOCUMENTS_USER_FIELD";

    // Код шаблона заявления для вступления в КУч ПО
    private String statementJoinTemplateCode;
    // Наименование участника - КУч ПО
    private String statementJoinCommunityParticipantName;
    // Наименование участника - организация, которая вступает в КУч ПО
    private String statementJoinOrganizationParticipantName;

    public String getStatementJoinTemplateCode() {
        return statementJoinTemplateCode;
    }

    public String getStatementJoinCommunityParticipantName() {
        return statementJoinCommunityParticipantName;
    }

    public String getStatementJoinOrganizationParticipantName() {
        return statementJoinOrganizationParticipantName;
    }

    //---------------------------------------------------

    //---------------------------------------------------
    // Код шаблона протокола принятия участника в КУч ПО
    private String protocolJoinTemplateCode;
    // Наименование участника - КУч ПО
    private String protocolJoinCommunityParticipantName;
    // Наименование участника - организации, которые вступают в КУч ПО
    private String protocolJoinOrganizationsParticipantName;
    // Сипсок заявлений от кандидатов в пайшики - пользовательское поле
    private String protocolJoinOrganizationsDocumentsUserField;

    public String getProtocolJoinTemplateCode() {
        return protocolJoinTemplateCode;
    }

    public String getProtocolJoinCommunityParticipantName() {
        return protocolJoinCommunityParticipantName;
    }

    public String getProtocolJoinOrganizationsParticipantName() {
        return protocolJoinOrganizationsParticipantName;
    }

    public String getProtocolJoinOrganizationsDocumentsUserField() {
        return protocolJoinOrganizationsDocumentsUserField;
    }
    //---------------------------------------------------

    //---------------------------------------------------
    // Код шаблона заявления на выход из КУч ПО
    private String statementExcludeTemplateCode;
    // Наименование участника - КУч ПО
    private String statementExcludeCommunityParticipantName;
    // Наименование участника - организация, которая выходит из КУч ПО
    private String statementExcludeOrganizationParticipantName;

    public String getStatementExcludeTemplateCode() {
        return statementExcludeTemplateCode;
    }

    public String getStatementExcludeCommunityParticipantName() {
        return statementExcludeCommunityParticipantName;
    }

    public String getStatementExcludeOrganizationParticipantName() {
        return statementExcludeOrganizationParticipantName;
    }
    //---------------------------------------------------

    //---------------------------------------------------
    // Код шаблона протокола выхода участника из КУч ПО
    private String protocolExcludeTemplateCode;
    // Наименование участника - КУч ПО
    private String protocolExcludeCommunityParticipantName;
    // Наименование участника - организации, которые выходят из КУч ПО
    private String protocolExcludeOrganizationsParticipantName;
    // Сипсок заявлений от кандидатов на выход из КУч ПО - пользовательское поле
    private String protocolExcludeOrganizationsDocumentsUserField;

    public String getProtocolExcludeTemplateCode() {
        return protocolExcludeTemplateCode;
    }

    public String getProtocolExcludeCommunityParticipantName() {
        return protocolExcludeCommunityParticipantName;
    }

    public String getProtocolExcludeOrganizationsParticipantName() {
        return protocolExcludeOrganizationsParticipantName;
    }

    public String getProtocolExcludeOrganizationsDocumentsUserField() {
        return protocolExcludeOrganizationsDocumentsUserField;
    }
    //---------------------------------------------------

}
