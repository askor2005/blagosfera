package ru.radom.kabinet.voting;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Created by vgusev on 30.06.2016.
 */
public interface BatchVotingConstants {

    /**
     * Доп. параметр собрания - ключ настройки списка протоколов
     */
    String BATCH_VOTING_PROTOCOL_SETTINGS_ATTR_NAME = "batchVotingProtocolSettingsKey";

    String BATCH_VOTING_STANDARD_PROTOCOL_SETTINGS_KEY = "constructor.batch.voting.settings";

    String IS_PROTOCOL_OF_BATCH_VOTING = "IS_PROTOCOL_OF_CONSTRUCTOR_BATCH_VOTING";

    String BATCH_VOTING_ID_ATTR_NAME = "batchVotingIdFromConstructor";

    int VOTING_FOR_PRESIDENT_OF_MEETING_INDEX = 0;

    int VOTING_FOR_SECRETARY_OF_MEETING_INDEX = 1;

    String STANDARD_BEHAVIOR_NAME = "defaultBatchVotingConstructorBehavior";

    /**
     * ИД Протокола собрания в расширенных параметрах
     */
    String BATCH_VOTING_PROTOCOL_ID = "batchVotingProtocolId";

    // Тип источника вариантов для голосования
    String SELECT_FROM_TYPE = "selectFromType";
    //брать параметры для голосования из другого голосования
    String SOURCE_VOTING_INDEX = "sourceVotingIndex";
    // Описание голосования
    String VOTING_DESCRIPTION = "description";
    // Наименование параметр - пайщик ПО, который предложил повеску дня.
    String COOPERATIVE_AGENTA_CREATOR = "cooperative_agenta_creator";
    // Наименование параметра - имя создаваемого КУЧ
    String COOPERATIVE_PLOT_NAME_ATTR_NAME = "plotName";
    // Наименование параметра - цели собрания (могут быть использованы для дальнейщего сохранения в виде целей и задач КУч)
    String BATCH_VOTING_TARGETS_ATTR_NAME = "meetingTargets";
    // Наименование параметра - доп. цели собрания (используется как доп. текст на странице голосования в подробностях)
    String ADDITIONAL_MEETING_TARGETS_ATTR_NAME = "additionalMeetingTargets";
    // Наименование параметра - описание на странице регистрации в собрании
    String MEETING_REGISTRATION_DESCRIPTION = "meetingRegistrationDescription";
    // Шаблон в тексте - имя председателя собрания
    String PRESIDENT_OF_MEETING_TEMPLATE = "presidentOfMeeting";
    // ИД Объединения
    String COMMUNITY_ID_ATTR_NAME = "communityId";
    // ИД созданного КУч на первом этапе собрания
    String KUCH_COMMUNITY_ID_ATTR_NAME = "kuchCommunityId";
    // Кодированные поля с адресом для создаваемого КУч
    String ADDRESS_FIELDS_ATTR_NAME = "addressFields";
    // Доп. параметр собрания - тип голосований
    String VOTING_TYPE_ATTR_NAME = "votingType";
    // Доп. параметр собрания - нужно ли создавать голосования за председателя, секретаря и повестку дня
    String NEED_ADD_ADDITIONAL_VOTINGS = "isNeedAddAdditionalVotings";
    // Кворум в процентах
    String QUORUM_PERCENT_ATTR_NAME = "quorumPercent";
    /**
     * Атрибут голосования - json вида:
     * [
     *  {
     *     buttonText : "Надпись на кнопке",
     *     content : "Текст в модальном окне при клике на кнопку"
     *  }
     * ]
     */
    String VOTING_BUTTONS_WITH_MODAL_CONTENT = "votingButtonsWithModalContents";
    /**
     * Текст который отображается в протоколе голосования при победе какого то варианта
     */
    String VOTING_WINNER_TEXT = "votingWinnerText";
    /**
     * Атрибут голосования - процент для победы в голосованиях с множественным выбором
     */
    String PERCENT_FOR_WIN = "percentForWin";

    /**
     * Аттрибут собрания - ИДы участников через запятую которые подписывают документ
     */
    String PARTICIPANTS_WHO_SING_PROTOCOL_IDS_BY_COMMA = "PARTICIPANTS_WHO_SING_PROTOCOL_IDS_BY_COMMA";
    /**
     * Атрибут голосования - остановить собрание прикривом голосовании
     */
    String STOP_BATCH_VOTING_ON_FAIL_RESULT = "stopBatchVotingOnFailResult";


    /*
        Виды голосований:
        Тайное голосование (Отображается только кол-во проголосовавших за тот или иной вариант, без поименных протоколов на страницах)
        Обычное голосование (Все как сейчас)
        Открытое голосование с поименным учетом
        */
    String CLOSED_VOTING_TYPE_KEY = "CLOSED_VOTING";
    String OPENED_VOTING_TYPE_KEY = "OPENED_VOTING";
    String OPENED_EXTENDED_VOTING_TYPE_KEY = "OPENED_EXTENDED_VOTING";
    // Текстовки типов голосований
    Map<String, String> VOTING_TYPES = new HashMap<String, String>(){{
        put(CLOSED_VOTING_TYPE_KEY, "Тайное голосование");
        put(OPENED_VOTING_TYPE_KEY, "Обычное голосование");
        put(OPENED_EXTENDED_VOTING_TYPE_KEY, "Открытое голосование с поименным учетом");
    }};
    // Описание подробностей собрания (название кнопки в регистрации и голосовании "Подробности")
    String BATCH_VOTING_DESCRIPTION_ATTR_NAME = "batchVotingDescription";
    // ИД диалога чата в собрании
    String BATCH_VOTING_DIALOG_ID_ATTR_NAME = "batchVotingDialogId";
    /**
     * Строка с повесткой дня собрания
     */
    String VOTING_AGENDA_ATTR_NAME = "votingAgendaString";

    /**
     * Постановление в случае успешного голосования
     */
    String VOTING_SUCCESS_DECREE_ATTR_NAME = "successDecree";

    /**
     * Постановоение в случае неуспешного голосования
     */
    String VOTING_FAIL_DECREE_ATTR_NAME = "failDecree";

    /**
     * Предложение по голосованию
     */
    String VOTING_SENTENCE_ATTR_NAME = "sentence";

    String VOTING_MIN_WINNERS_COUNT_ATTR_NAME = "minWinnersCount";

    String VOTING_MAX_WINNERS_COUNT_ATTR_NAME = "maxWinnersCount";

    /**
     * JSON с массивм кнопок для страницы результатов собрания
     */
    String BATCH_VOTING_RESULT_PAGE_BUTTONS_ATTR_NAME = "resultPageButtons";
}
