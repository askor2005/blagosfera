package ru.radom.kabinet.enums.system;

/**
 * Перечисление всевозможных системных настроек
 */
public enum SystemSetting {

    NEWS_TAGS_MAX_COUNT("news.tags.max-count"),//Максимальное число тегов, которое можно использовать в новости
    TAGS_MIN_USAGES_TO_AUTOCOMPLETE("tags.min-usages-to-autocomplete"),//Минимальное число использований тега, после которого он становится доступным для автодополнения
    TAGS_MAX_COUNT_FOR_AUTOCOMPLETE("tags.max-count-for-autocomplete")//Максимальное число тегов, предлагающихся для выбора в списке автодополнения
    ;

    //Ключ, по которому настройка получается из Базы
    private String key;

    SystemSetting(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
