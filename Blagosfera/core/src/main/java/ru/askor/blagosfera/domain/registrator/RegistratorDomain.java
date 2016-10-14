package ru.askor.blagosfera.domain.registrator;

import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dto.Timetable;
import ru.radom.kabinet.model.registration.RegistratorLevel;

/**
 * модель регистратора
 */
public class RegistratorDomain {

    private final User user;

    private final RegistratorLevel level;

    private final Double distance;

    private Timetable timetable;
    private String registratorOfficePhone;
    private String skype;
    private String registratorMobilePhone;
    private boolean requested;
    private boolean requestedToMe;

    public RegistratorDomain(final User user, final RegistratorLevel level, final Double distance, Timetable timetable, String registratorOfficePhone, String registratorMobilePhone, String skype,boolean requested,boolean requestedToMe) {
        this.user = user;
        this.level = level;
        this.distance = distance;
        this.timetable = timetable;
        this.registratorOfficePhone = registratorOfficePhone;
        this.skype = skype;
        this.registratorMobilePhone = registratorMobilePhone;
        this.requested = requested;
        this.requestedToMe = requestedToMe;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public String getRegistratorOfficePhone() {
        return registratorOfficePhone;
    }

    public String getSkype() {
        return skype;
    }

    public String getRegistratorMobilePhone() {
        return registratorMobilePhone;
    }

    public User getUser() {
        return user;
    }

    public RegistratorLevel getLevel() {
        return level;
    }

    public Double getDistance() {
        return distance;
    }

    public boolean isRequested() {
        return requested;
    }

    public boolean isRequestedToMe() {
        return requestedToMe;
    }
}
