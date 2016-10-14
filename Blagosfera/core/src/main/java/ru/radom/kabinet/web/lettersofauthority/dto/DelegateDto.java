package ru.radom.kabinet.web.lettersofauthority.dto;

import ru.radom.kabinet.model.UserEntity;

/**
 * Created by mnikitin on 03.08.2016.
 */
public class DelegateDto {

    public Long id;
    public String ikp;
    public String fullName;
    public String shortName;
    public String mediumName;
    public String groupName;
    public String link;
    public String avatar;

    public DelegateDto() {
    }

    public DelegateDto(UserEntity user) {
        this.id = user.getId();
        this.ikp = user.getIkp();
        this.fullName = user.getFullName();
		this.shortName = user.getShortName();
		this.mediumName = user.getMediumName();
		this.groupName = user.getGroup() != null ? user.getGroup().getName() : "";
		this.link = user.getLink();
		this.avatar = user.getAvatar();
    }
}
