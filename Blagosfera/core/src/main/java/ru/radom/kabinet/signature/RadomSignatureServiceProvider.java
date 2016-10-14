package ru.radom.kabinet.signature;

import lombok.AllArgsConstructor;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.ramera.signer.common.service.impl.RestClientCommonSignerParametersProvider;

/**
 * Провайдер параметров доступа к сервису подписей.
 * Created by vgusev on 24.12.2015.
 */
@AllArgsConstructor
public class RadomSignatureServiceProvider implements RestClientCommonSignerParametersProvider {

    private SettingsManager settingsManager;

    /**
     * Настройка - ключ параметра - api key сервиса подписей
     */
    private static final String SIGNATURE_KEY_SYS_SETTINGS = "signature.service.api.key";

    /**
     * Ключ по умолчанию
     */
    private static final String DEFAULT_SIGNATURE_API_KEY = "rgpergpwefer'ghlmehmewefweg";

    /**
     * Настройка - базовый урл сервиса подписей
     */
    private static final String SIGNATURE_SERVICE_BASE_URL_SYS_SETTINGS = "signature.service.base.url";

    /**
     * Базовый урл по умолчанию
     */
    private static final String DEFAULT_SIGNATURE_SERVICE_BASE_URL = "http://localhost:8081/";

    @Override
    public String getKey() {
        return settingsManager.getSystemSetting(SIGNATURE_KEY_SYS_SETTINGS, DEFAULT_SIGNATURE_API_KEY);
    }

    @Override
    public String getBaseServiceUrl() {
        return settingsManager.getSystemSetting(SIGNATURE_SERVICE_BASE_URL_SYS_SETTINGS, DEFAULT_SIGNATURE_SERVICE_BASE_URL);
    }
}
