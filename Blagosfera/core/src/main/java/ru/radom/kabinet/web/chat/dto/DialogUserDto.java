package ru.radom.kabinet.web.chat.dto;

import ru.askor.blagosfera.domain.user.User;

/**
 *
 * Created by vgusev on 24.05.2016.
 */
public class DialogUserDto {

    public Long id;

    public String ikp;

    public String avatar;

    public String firstName;

    public String secondName;

    public String lastName;

    public String shortName;

    public String fullName;

    public String link;

    public boolean online;

    public DialogUserDto(User user, boolean online) {
        id = user.getId();
        ikp = user.getIkp();
        avatar = user.getAvatar();
        firstName = user.getFirstName();
        secondName = user.getSecondName();
        lastName = user.getLastName();
        shortName = user.getShortName();
        fullName = user.getFullName();
        link = user.getLink();
        this.online = online;
    }
}
