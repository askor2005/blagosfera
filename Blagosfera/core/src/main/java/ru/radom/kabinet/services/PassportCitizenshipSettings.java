package ru.radom.kabinet.services;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebelyaev on 25.09.2015.
 */
public class PassportCitizenshipSettings {
    private static PassportCitizenshipSettings instance;

    private static Gson gson = new Gson();

    public static PassportCitizenshipSettings getInstance() {
        return instance;
    }

    public static void init(String jsonSettings) {
        if (StringUtils.isBlank(jsonSettings)) {
            initDefault();
        } else {
            try {
                instance = gson.fromJson(jsonSettings, PassportCitizenshipSettings.class);
            } catch (Exception e) {
                initDefault();
                e.printStackTrace();
            }
        }
    }

    private static void initDefault() {
        instance = new PassportCitizenshipSettings();
        instance.defaultSelectedCitizenship = DEFAULT_SELECTED_CITIZENSHIP;
        instance.defaultSetting = new PassportCitizenshipFieldsSettings();
        instance.settings = new ArrayList<PassportCitizenshipFieldsSettings>();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static final String SETTINGS_KEY = "passport.citizenship.settings";
    private static final String DEFAULT_SELECTED_CITIZENSHIP = "ru";

    // Выбранное по умолчанию гражданство(в случае если в профиле оно ещё не задано)
    private String defaultSelectedCitizenship;

    // Настройка по умолчанию(в случае если для выбранного гражданства нет заданной для него нстройки)
    private PassportCitizenshipFieldsSettings defaultSetting;

    // Настройки для конкретных гражданств
    private List<PassportCitizenshipFieldsSettings> settings;

    //------------------------------------------------------------------------------------------------------------------

    public String getDefaultSelectedCitizenship() {
        return defaultSelectedCitizenship;
    }

    public void setDefaultSelectedCitizenship(String defaultSelectedCitizenship) {
        this.defaultSelectedCitizenship = defaultSelectedCitizenship;
    }

    public PassportCitizenshipFieldsSettings getDefaultSetting() {
        return defaultSetting;
    }

    public void setDefaultSetting(PassportCitizenshipFieldsSettings defaultSetting) {
        this.defaultSetting = defaultSetting;
    }

    public List<PassportCitizenshipFieldsSettings> getSettings() {
        return settings;
    }

    public void setSettings(List<PassportCitizenshipFieldsSettings> settings) {
        this.settings = settings;
    }
}
