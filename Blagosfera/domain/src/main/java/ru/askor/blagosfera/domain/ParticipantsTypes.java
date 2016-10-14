package ru.askor.blagosfera.domain;

public enum ParticipantsTypes {
	INDIVIDUAL("INDIVIDUAL"),     // физ. лицо
    INDIVIDUAL_LIST ("INDIVIDUAL_LIST"), // Список физ. лиц
	REGISTRATOR("REGISTRATOR"),   // регистратор
	COMMUNITY_WITH_ORGANIZATION("COMMUNITY_WITH_ORGANIZATION"),        // объединение в рамках юр. лица
    COMMUNITY_WITH_ORGANIZATION_LIST("COMMUNITY_WITH_ORGANIZATION_LIST"), // список объединений в рамках юр. лица
	COMMUNITY_WITHOUT_ORGANIZATION("COMMUNITY_WITHOUT_ORGANIZATION"),  // объединение вне рамок юр. лица
	COMMUNITY_IP("COMMUNITY_IP"); // объединение ИП

    private final String name;

    ParticipantsTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}