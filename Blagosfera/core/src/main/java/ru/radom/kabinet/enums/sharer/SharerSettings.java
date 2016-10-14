package ru.radom.kabinet.enums.sharer;

/**
 * Перечисление всевозможных настроек пользователей
 */
public enum SharerSettings {

    ;

    //Ключ, по которому настройка получается из Базы
    private String key;

    SharerSettings(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
