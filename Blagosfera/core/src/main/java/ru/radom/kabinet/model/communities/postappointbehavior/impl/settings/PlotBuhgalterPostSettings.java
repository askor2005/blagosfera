package ru.radom.kabinet.model.communities.postappointbehavior.impl.settings;

import com.google.gson.Gson;

/**
 * Настройки документов назначения на должность бухгалтера - кассира КУч ПО
 * Created by vgusev on 29.09.2015.
 */
public class PlotBuhgalterPostSettings {

    public static final String SETTINGS_KEY = "plot.buhgalter.post.settings";

    private static PlotBuhgalterPostSettings instance;

    private static Gson gson = new Gson();

    public static PlotBuhgalterPostSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (jsonSettings == null || jsonSettings.equals("")) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, PlotBuhgalterPostSettings.class);
            } catch (Exception e) {
                initDefault();
                System.err.println("Необходимо создать настройку " + SETTINGS_KEY + "!");
            }
        }
    }

    private static void initDefault() {
        instance = new PlotBuhgalterPostSettings();
        // Параметры заявления
        instance.statementFromBuhgalterTemplateCode = STATEMENT_FROM_BUHGALTER_TEMPLATE_CODE;
        instance.statementBuhgalterParticipantName = STATEMENT_BUHGALTER_PARTICIPANT_NAME;
        instance.statementPoParticipantName = STATEMENT_PO_PARTICIPANT_NAME;
        instance.statementKuchParticipantName = STATEMENT_KUCH_PARTICIPANT_NAME;
        instance.statementKuchPresidentParticipantName = STATEMENT_KUCH_PRESIDENT_PARTICIPANT_NAME;
        // Параметры приказа о назначении
        instance.orderToAppointBuhgalterTemplateCode = ORDER_TO_APPOINT_BUHGALTER_TEMPLATE_CODE;
        instance.orderToAppointBuhgalterParticipantName = ORDER_TO_APPOINT_BUHGALTER_PARTICIPANT_NAME;
        instance.orderToAppointKuchParticipantName = ORDER_TO_APPOINT_KUCH_PARTICIPANT_NAME;
        instance.orderToAppointPoParticipantName = ORDER_TO_APPOINT_PO_PARTICIPANT_NAME;
        // Параметры договора с бухгалтером кассиром
        instance.contractWithBuhgalterTemplateCode = CONTRACT_WITH_BUHGALTER_TEMPLATE_CODE;
        instance.contractBuhgalterParticipantName = CONTRACT_BUHGALTER_PARTICIPANT_NAME;
        instance.contractKuchParticipantName = CONTRACT_KUCH_PARTICIPANT_NAME;
        instance.contractPoParticipantName = CONTRACT_PO_PARTICIPANT_NAME;
        // Параметры инструкции для бухгалтера
        instance.instructionBuhgalterTemplateCode = INSTRUCTION_BUHGALTER_TEMPLATE_CODE;
        instance.instructionBuhgalterParticipantName = INSTRUCTION_BUHGALTER_PARTICIPANT_NAME;
        instance.instructionKuchParticipantName = INSTRUCTION_KUCH_PARTICIPANT_NAME;
        instance.instructionPoParticipantName = INSTRUCTION_PO_PARTICIPANT_NAME;
    }

    private PlotBuhgalterPostSettings() {
    }

    //------------------------------------------------------------------------------------------------------------------
    // Параметры документа - заявление от бухгалтера на назначение на должность
    //------------------------------------------------------------------------------------------------------------------

    // Значения по умолчанию

    // Код шаблона
    private static final String STATEMENT_FROM_BUHGALTER_TEMPLATE_CODE = "STATEMENT_FROM_BUHGALTER_TEMPLATE_CODE";

    // Участник документа - бухгалтер
    private static final String STATEMENT_BUHGALTER_PARTICIPANT_NAME = "Бухгалтер-Кандидат";

    // Участник документа - ПО
    private static final String STATEMENT_PO_PARTICIPANT_NAME = "Потребительское Общество";

    // Участник документа - КУч ПО
    private static final String STATEMENT_KUCH_PARTICIPANT_NAME = "Кооперативный Участок";

    // Участник - председатель КУч ПО
    private static final String STATEMENT_KUCH_PRESIDENT_PARTICIPANT_NAME = "Председатель КУч";

    // Загруженные значения

    private String statementFromBuhgalterTemplateCode;
    private String statementBuhgalterParticipantName;
    private String statementPoParticipantName;
    private String statementKuchParticipantName;
    private String statementKuchPresidentParticipantName;

    public String getStatementFromBuhgalterTemplateCode() {
        return statementFromBuhgalterTemplateCode;
    }

    public String getStatementBuhgalterParticipantName() {
        return statementBuhgalterParticipantName;
    }

    public String getStatementPoParticipantName() {
        return statementPoParticipantName;
    }

    public String getStatementKuchParticipantName() {
        return statementKuchParticipantName;
    }

    public String getStatementKuchPresidentParticipantName() {
        return statementKuchPresidentParticipantName;
    }

    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Приказ о назначении бухгалетра-кассира КУЧ
    //------------------------------------------------------------------------------------------------------------------

    // Значения по умолчанию

    // Код шаблона
    private static final String ORDER_TO_APPOINT_BUHGALTER_TEMPLATE_CODE = "ORDER_TO_APPOINT_BUHGALTER_TEMPLATE_CODE";

    // Участник документа - бухгалтер
    private static final String ORDER_TO_APPOINT_BUHGALTER_PARTICIPANT_NAME = "Бухгалтер-Кассир";

    // Участник документа - КУч ПО
    private static final String ORDER_TO_APPOINT_KUCH_PARTICIPANT_NAME = "Кооперативный участок";

    // Участник - ПО
    private static final String ORDER_TO_APPOINT_PO_PARTICIPANT_NAME = "Потребительское Общество";


    // Загруженные значения

    private String orderToAppointBuhgalterTemplateCode;
    private String orderToAppointBuhgalterParticipantName;
    private String orderToAppointKuchParticipantName;
    private String orderToAppointPoParticipantName;

    public String getOrderToAppointBuhgalterTemplateCode() {
        return orderToAppointBuhgalterTemplateCode;
    }

    public String getOrderToAppointBuhgalterParticipantName() {
        return orderToAppointBuhgalterParticipantName;
    }

    public String getOrderToAppointKuchParticipantName() {
        return orderToAppointKuchParticipantName;
    }

    public String getOrderToAppointPoParticipantName() {
        return orderToAppointPoParticipantName;
    }

    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Договор с бухгалтером кассиром
    //------------------------------------------------------------------------------------------------------------------

    // Значения по умолчанию

    // Код шаблона
    private static final String CONTRACT_WITH_BUHGALTER_TEMPLATE_CODE = "CONTRACT_WITH_BUHGALTER_TEMPLATE_CODE";

    // Участник документа - бухгалтер
    private static final String CONTRACT_BUHGALTER_PARTICIPANT_NAME = "Бухгалтер-Кандидат";

    // Участник документа - КУч ПО
    private static final String CONTRACT_KUCH_PARTICIPANT_NAME = "Кооперативный Участок";

    // Участник документа - ПО
    private static final String CONTRACT_PO_PARTICIPANT_NAME = "Потребительское Общество";

    // Загруженные значения

    private String contractWithBuhgalterTemplateCode;
    private String contractBuhgalterParticipantName;
    private String contractKuchParticipantName;
    private String contractPoParticipantName;

    public String getContractWithBuhgalterTemplateCode() {
        return contractWithBuhgalterTemplateCode;
    }

    public String getContractBuhgalterParticipantName() {
        return contractBuhgalterParticipantName;
    }

    public String getContractKuchParticipantName() {
        return contractKuchParticipantName;
    }

    public String getContractPoParticipantName() {
        return contractPoParticipantName;
    }

    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Инструкция для бухгалтера - кассира КУч
    //http://ramera.ru/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=162
    //------------------------------------------------------------------------------------------------------------------

    // Значения по умолчанию

    // Код шаблона
    private static final String INSTRUCTION_BUHGALTER_TEMPLATE_CODE = "INSTRUCTION_BUHGALTER";

    // Участник документа - бухгалтер
    private static final String INSTRUCTION_BUHGALTER_PARTICIPANT_NAME = "Бухгалтер-Кандидат";

    // Участник документа - КУч ПО
    private static final String INSTRUCTION_KUCH_PARTICIPANT_NAME = "Кооперативный Участок";

    // Участник документа - ПО
    private static final String INSTRUCTION_PO_PARTICIPANT_NAME = "Потребительское Общество";

    // Загруженные значения

    private String instructionBuhgalterTemplateCode;
    private String instructionBuhgalterParticipantName;
    private String instructionKuchParticipantName;
    private String instructionPoParticipantName;

    public String getInstructionBuhgalterTemplateCode() {
        return instructionBuhgalterTemplateCode;
    }

    public String getInstructionBuhgalterParticipantName() {
        return instructionBuhgalterParticipantName;
    }

    public String getInstructionKuchParticipantName() {
        return instructionKuchParticipantName;
    }

    public String getInstructionPoParticipantName() {
        return instructionPoParticipantName;
    }

    //------------------------------------------------------------------------------------------------------------------

}
