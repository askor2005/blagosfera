package ru.radom.kabinet.services.communities.kuch;

import com.google.gson.Gson;
import padeg.lib.Padeg;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.utils.PadegConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Класс для настроек создания КУч второго этапа
 * Created by vgusev on 19.09.2015.
 */
public class CreateKuchSecondMeetingSettings {

    // Ключ настройки
    public static final String SETTINGS_KEY = "create.kuch.order2";

    private static final String DEFAULT_MEETING_NAME = "Собрание группы пайщиков {communityFullName} для выбора Председателя и Ревизора Кооперативного участка {kuchShortName}";

    private static final String DEFAULT_MEETING_REGISTRATION_DESCRIPTION =
            "<a href='{ownerLink}'>{ownerName}</a> {ownerCreate} собрание группы Пайщиков в {communityFullName} " +
            "с целью проведения выборов Председателя и Ревизора Кооперативного участка {kuchShortName}." +
            "Вы приглашены на это собрание. " +
            "Просим Вас пройти процедуру регистрации.";

    private static final List<String> DEFAULT_VOTING_SUBJECTS = Arrays.asList(
            "Выборы Председателя собрания",
            "Выборы Секретаря собрания",
            "Голосование за повестку дня собрания",
            "Выборы Председателя КУч {kuchShortName}",
            "Выборы Ревизора КУч {kuchShortName}"
    );

    private static final List<String> DEFAULT_VOTING_DESCRIPTIONS = Arrays.asList(
            "Выборы Председателя собрания",
            "Выборы Секретаря собрания",
            "Голосование за повестку дня собрания группы Пайщиков {communityShortName} по выбору Председателя и Ревизора КУч " +
                    "{kuchShortName}.<br/>Председатель собрания presidentOfMeeting следующую повестку дня:<br/>" +
                    "1) Выборы Председателя Кооперативного участка {kuchShortName}.<br/> " +
                    "2) Выборы Ревизора Кооперативного участка {kuchShortName}.",
            "Выборы Председателя КУч {kuchShortName}",
            "Выборы Ревизора КУч {kuchShortName}"
    );

    private static CreateKuchSecondMeetingSettings instance;

    private static Gson gson = new Gson();

    public static CreateKuchSecondMeetingSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (jsonSettings == null || jsonSettings.equals("")) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, CreateKuchSecondMeetingSettings.class);
            } catch (Exception e) {
                initDefault();
                System.err.println("Необходимо создать настройку " + SETTINGS_KEY + "!");
            }
        }
    }

    private static void initDefault() {
        instance = new CreateKuchSecondMeetingSettings();
        instance.meetingName = DEFAULT_MEETING_NAME;
        instance.meetingRegistrationDescription = DEFAULT_MEETING_REGISTRATION_DESCRIPTION;
        instance.votingSubjects = DEFAULT_VOTING_SUBJECTS;
        instance.votingDescriptions = DEFAULT_VOTING_DESCRIPTIONS;
    }

    private CreateKuchSecondMeetingSettings() {
    }

    // Наименование собрания
    private String meetingName;

    // Описание собрания на странице регистрации
    private String meetingRegistrationDescription;

    // Наименования голосования
    private List<String> votingSubjects;

    // Описания госований
    private List<String> votingDescriptions;

    public String getMeetingName() {
        return meetingName;
    }

    public String getMeetingRegistrationDescription() {
        return meetingRegistrationDescription;
    }

    public List<String> getVotingSubjects() {
        return votingSubjects;
    }

    public List<String> getVotingDescriptions() {
        return votingDescriptions;
    }

    // Получить строку настройки на основе параметров
    public static String getStringFromSettings(String source, Community community, String newKuchName, User owner){
        //CreateKuchSecondMeetingSettings
        //{communityShortName} - короткое имя ПО
        //{communityFullName} - полное имя ПО в род. падеже
        //{kuchShortName} - коротке имя КУч
        //{ownerLink} - Ссылка на профиль создателя собрания
        //{ownerName} - ФИО создателя собрания
        //{ownerCreate} - слово "организовал"/"организовала" в зависимости от пола
        return source.replaceAll("\\{communityFullName\\}", Padeg.getOfficePadeg(community.getFullRuName(), PadegConstants.PADEG_R))
                .replaceAll("\\{communityShortName\\}", community.getShortRuName())
                .replaceAll("\\{kuchShortName\\}", newKuchName)
                .replaceAll("\\{ownerLink\\}", owner.getLink())
                .replaceAll("\\{ownerName\\}", owner.getName())
                .replaceAll("\\{ownerCreate\\}", owner.isSex()?"организовал":"организовала");
    }
}
