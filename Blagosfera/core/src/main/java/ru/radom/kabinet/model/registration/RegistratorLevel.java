package ru.radom.kabinet.model.registration;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by vzuev on 17.03.2015.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RegistratorLevel {

    LEVEL_0("registrator.level0", "Высшего ранга"),
    LEVEL_1("registrator.level1", "1-го ранга"),
    LEVEL_2("registrator.level2", "2-го ранга"),
    LEVEL_3("registrator.level3", "3-го ранга");

    public final static String PREFIX = "registrator";

    private final String mnemo;

    private final String name;

    RegistratorLevel(String mnemo, String name) {
        this.mnemo = mnemo;
        this.name = name;
    }

    public String getMnemo() {
        return mnemo;
    }

    public String getName() {
        return name;
    }

    public static RegistratorLevel getByMnemo(final String mnemo) {
        for (final RegistratorLevel level : RegistratorLevel.values()) {
            if (level.getMnemo().equals(mnemo)) return level;
        }
        return null;
    }

    public static RegistratorLevel geByName(final String name) {
        for (final RegistratorLevel level : RegistratorLevel.values()) {
            if (level.name().equals(name)) return level;
        }
        return null;
    }
}
