package ru.radom.kabinet.voting.settings;

import com.google.gson.Gson;

/**
 * Общие настройки регистрации в собрании.
 * Created by vgusev on 30.09.2015.
 */
public class RegistrationInBatchVotingSettings {

    private static RegistrationInBatchVotingSettings instance;

    private static Gson gson = new Gson();

    public static RegistrationInBatchVotingSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (jsonSettings == null || jsonSettings.equals("")) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, RegistrationInBatchVotingSettings.class);
            } catch (Exception e) {
                initDefault();
                e.printStackTrace();
            }
        }
    }

    private static void initDefault() {
        instance = new RegistrationInBatchVotingSettings();
        instance.registrationCommonDescription = REGISTRATION_COMMON_DESCRIPTION_DEFAULT;
        instance.registrationSharerText = REGISTRATION_SHARER_TEXT_DEFAULT;
    }

    public static final String SETTINGS_KEY = "registration.in.batchvoting.settings";

    private static final String REGISTRATION_COMMON_DESCRIPTION_DEFAULT =
            "Перед началом собрания все заявленные участники собрания должны пройти обязательную процедуру регистрации. " +
            "Как только все участники собрания будут зарегистрированы, " +
            "или закончится отведённое организатором собрания время на регистрацию, " +
            "будет произведён подсчёт зарегистрировавшихся участников. " +
            "Собрание начнётся (и Вы получите уведомление об этом), " +
            "как только все приглашённые участники зарегистрируются, " +
            "либо если по достижении назначенного времени будет достигнут кворум в 51%. " +
            "В случае, если по достижении назначенного времени кворум в 51% не будет достигнут, " +
            "собрание будет считаться несостоявшимся.";

    private static final String REGISTRATION_SHARER_TEXT_DEFAULT =
            "Вы должны зарегистрироваться для участия в собрании. " +
                    "Чтобы зарегистрироваться, необходимо нажать кнопку «Зарегистрироваться». " +
                    "Как только процедура регистрации завершится и начнётся собрание, Вам придёт уведомление.";

    // Общее описание на странице собрания
    private String registrationCommonDescription;
    // Текст для участника по дальнейшим действиям
    private String registrationSharerText;

    public String getRegistrationCommonDescription() {
        return registrationCommonDescription;
    }

    public String getRegistrationSharerText() {
        return registrationSharerText;
    }
}
