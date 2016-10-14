package ru.radom.kabinet.web.lettersofauthority.dto;

import ru.askor.blagosfera.domain.RadomAccount;

/**
 *
 * Created by vgusev on 25.09.2015.
 */
public class RadomAccountDto implements RadomAccount {

    private Long id;
    private String objectType;
    private String name;
    private String avatar;
    private String link;
    private String ikp;

    public RadomAccountDto(){
    }

    public RadomAccountDto(Long id, String objectType, String name, String avatar, String link, String ikp) {
        this.id = id;
        this.objectType = objectType;
        this.name = name;
        this.avatar = avatar;
        this.link = link;
        this.ikp = ikp;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public String getIkp() {
        return ikp;
    }
}
