package ru.radom.kabinet.services.communities.kuch;

import com.google.gson.Gson;
import padeg.lib.Padeg;
import ru.askor.blagosfera.domain.community.Community;
import ru.radom.kabinet.services.communities.kuch.documents.*;
import ru.radom.kabinet.utils.PadegConstants;

/**
 *
 * Created by vgusev on 20.09.2015.
 */
public class CommonCreateKuchSettings {

    private static CommonCreateKuchSettings instance;

    private static Gson gson = new Gson();

    public static CommonCreateKuchSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (jsonSettings == null || jsonSettings.equals("")) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, CommonCreateKuchSettings.class);
            } catch (Exception e) {
                initDefault();
                //System.err.println("Необходимо создать настройку " + SETTINGS_KEY + "!");
                e.printStackTrace();
            }
        }
    }

    private static void initDefault() {
        instance = new CommonCreateKuchSettings();
        instance.shortKuchName = DEFAULT_SHORT_KUCH_NAME;
        instance.fullKuchName = DEFAULT_FULL_KUCH_NAME;
        instance.shortKuchNamePrefix = DEFAULT_SHORT_KUCH_NAME_PREFIX;
        instance.fullKuchNamePrefix = DEFAULT_FULL_KUCH_NAME_PREFIX;

        instance.presidentOfKuchPostName = DEFAULT_PRESIDENT_OF_KUCH_POST_NAME;
        instance.revisorOfKuchPostName = DEFAULT_REVISOR_OF_KUCH_POST_NAME;
        instance.buhgalterOfKuchPostName = DEFAULT_BUHGALTER_OF_KUCH_POST_NAME;

        instance.firstBatchVotingProtocolSettings = new FirstBatchVotingProtocolSettings();
        instance.statementToSovietForCreateKuchSettings = new StatementToSovietForCreateKuchSettings();
        instance.secondBatchVotingProtocolSettings = new SecondBatchVotingProtocolSettings();
        instance.statementToSovietForApprovePresidentAndRevisorSettings = new StatementToSovietForApprovePresidentAndRevisorSettings();
        instance.protocolMeetingSovietForCreateKuchSettings = new ProtocolMeetingSovietForCreateKuchSettings();
        instance.stateKuchSettings = new StateKuchSettings();
        instance.proxyPresidentKuchSettings = new ProxyPresidentKuchSettings();
    }

    public static final String SETTINGS_KEY = "common.create.kuch.settings";

    private static final String DEFAULT_SHORT_KUCH_NAME = "\"{sourceKuchName}\" - {communityShortName}\"";

    private static final String DEFAULT_FULL_KUCH_NAME = "\"{sourceKuchName}\" - {communityFullName}\"";

    private static final String DEFAULT_SHORT_KUCH_NAME_PREFIX = "КУч ";

    private static final String DEFAULT_FULL_KUCH_NAME_PREFIX = "Кооперативный участок ";

    private static final String DEFAULT_PRESIDENT_OF_KUCH_POST_NAME = "Председатель Кооперативного участка Потребительского Общества";

    private static final String DEFAULT_REVISOR_OF_KUCH_POST_NAME = "Ревизор Кооперативного участка Потребительского Общества";

    private static final String DEFAULT_BUHGALTER_OF_KUCH_POST_NAME = "Бухгалтер-кассир Кооперативного участка Потребительского Общества";


    //------------------------------------------------------------------------------------------------------------------
    // Наименования источников данных из классов документов для документов для создания КУч. Значения по умолчанию
    //------------------------------------------------------------------------------------------------------------------

    public static final String PO_COMMUNITY_PARTICIPANT_NAME = "Потребительское Общество";
    //
    public static final String KUCH_COMMUNITY_PARTICIPANT_NAME = "Кооперативный участок";
    //
    public static final String PRESIDENT_OF_MEETING_PARTICIPANT_NAME = "Председатель общего собрания Пайщиков Потребительского Общества";
    //
    public static final String SECRETARY_OF_MEETING_PARTICIPANT_NAME = "Секретарь общего собрания Пайщиков Потребительского Общества";
    //
    public static final String PRESIDENT_OF_KUCH_PARTICIPANT_NAME = "Председатель Кооперативного участка Потребительского Общества";
    //
    public static final String REVISOR_OF_KUCH_PARTICIPANT_NAME = "Ревизор Кооперативного участка Потребительского Общества";
    //
    public static final String SHARES_OF_KUCH_PO_PARTICIPANT_NAME = "группа Пайщиков (физических лиц) Кооперативного участка Потребительского Общества";
    //
    public static final String SHARES_OF_PO_PARTICIPANT_NAME = "группа Пайщиков (физических лиц) Потребительского Общества";

    //------------------------------------------------------------------------------------------------------------------


    // Короткое имя КУч
    private String shortKuchName;

    // Длинное имя КУч
    private String fullKuchName;

    // Короткое имя КУч для сохранения в объединении
    private String shortKuchNamePrefix;

    // Длинное имя КУч для созранения в объединении
    private String fullKuchNamePrefix;

    // Наименование поста - председателя КУч
    private String presidentOfKuchPostName;

    // Наименование поста - ревизора КУч
    private String revisorOfKuchPostName;

    // Наименование поста - бухгалтера КУч
    private String buhgalterOfKuchPostName;

    //------------------------------------------------------------------------------------------------------------------
    // Настройки шаблонов документов для создания КУч
    //------------------------------------------------------------------------------------------------------------------

    // Настройки шаблона - протокол собрания по созданию КУч
    private FirstBatchVotingProtocolSettings firstBatchVotingProtocolSettings;

    // Настройки шаблона - заявление в совет ПО на создание КУч
    private StatementToSovietForCreateKuchSettings statementToSovietForCreateKuchSettings;

    // Настройки шаблона - протокол собрания по выбору председателя и ревизора куч
    private SecondBatchVotingProtocolSettings secondBatchVotingProtocolSettings;

    // Настройки шаблона - заявление в совет ПО об утверждении председателя и ревизора КУч
    private StatementToSovietForApprovePresidentAndRevisorSettings statementToSovietForApprovePresidentAndRevisorSettings;

    // Настройки шаблона - протокол собрания совета по созданию КУч
    private ProtocolMeetingSovietForCreateKuchSettings protocolMeetingSovietForCreateKuchSettings;

    // Настройки шаблона - положение КУч
    private StateKuchSettings stateKuchSettings;

    // Настройки шаблона - доверенность председателя КУч ПО
    private ProxyPresidentKuchSettings proxyPresidentKuchSettings;
    //------------------------------------------------------------------------------------------------------------------

    // Короткое имя КУч, которое сохранится в создаваемом объединении
    public String getShortKuchNameCommunityField() {
        return shortKuchNamePrefix + shortKuchName;
    }

    // Полное имя КУч, которое сохранится в создаваемом объединении
    public String getFullKuchNameCommunityField() {
        return fullKuchNamePrefix + fullKuchName;
    }

    public String getShortKuchName() {
        return shortKuchName;
    }

    public String getFullKuchName() {
        return fullKuchName;
    }

    public String getShortKuchNamePrefix() {
        return shortKuchNamePrefix;
    }

    public String getFullKuchNamePrefix() {
        return fullKuchNamePrefix;
    }

    public String getPresidentOfKuchPostName() {
        return presidentOfKuchPostName;
    }

    public String getRevisorOfKuchPostName() {
        return revisorOfKuchPostName;
    }

    public String getBuhgalterOfKuchPostName() {
        return buhgalterOfKuchPostName;
    }

    public FirstBatchVotingProtocolSettings getFirstBatchVotingProtocolSettings() {
        return firstBatchVotingProtocolSettings;
    }

    public StatementToSovietForCreateKuchSettings getStatementToSovietForCreateKuchSettings() {
        return statementToSovietForCreateKuchSettings;
    }

    public SecondBatchVotingProtocolSettings getSecondBatchVotingProtocolSettings() {
        return secondBatchVotingProtocolSettings;
    }

    public StatementToSovietForApprovePresidentAndRevisorSettings getStatementToSovietForApprovePresidentAndRevisorSettings() {
        return statementToSovietForApprovePresidentAndRevisorSettings;
    }

    public ProtocolMeetingSovietForCreateKuchSettings getProtocolMeetingSovietForCreateKuchSettings() {
        return protocolMeetingSovietForCreateKuchSettings;
    }

    public StateKuchSettings getStateKuchSettings() {
        return stateKuchSettings;
    }

    public ProxyPresidentKuchSettings getProxyPresidentKuchSettings() {
        return proxyPresidentKuchSettings;
    }

    // Получить строку настройки на основе параметров
    public static String getStringFromSettings(String source, Community community, String sourceKuchName){
        //CommonCreateKuchSettings
        //{communityShortName} - короткое имя ПО
        //{communityFullName} - полное имя ПО в род. падеже
        //{sourceKuchName} - имя КУч, которое было задано на странице генерации собрания по созданию КУч
        return source.replaceAll("\\{communityFullName\\}", Padeg.getOfficePadeg(community.getFullRuName(), PadegConstants.PADEG_R))
                .replaceAll("\\{communityShortName\\}", community.getShortRuName())
                .replaceAll("\\{sourceKuchName\\}", sourceKuchName);
    }
}
