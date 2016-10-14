package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.RegisteredVoterStatus;

/**
 *
 * Created by vgusev on 24.05.2016.
 */
public class BatchVoterUserDto {

    public Long id;

    public String ikp;

    public String fullName;

    public String avatar;

    public String link;

    public boolean sex;

    public RegisteredVoterStatus status;

    public BatchVoterUserDto(User user, RegisteredVoterStatus registeredVoterStatus) {
        id = user.getId();
        ikp = user.getIkp();
        fullName = user.getFullName();
        avatar = user.getAvatar();
        link = user.getLink();
        sex = user.isSex();
        status = registeredVoterStatus;
    }

}
