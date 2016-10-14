package ru.askor.blagosfera.core.services.userlist.dto;

import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 23.05.2016.
 */

public class UserForListDto {

    public Long id;

    public String ikp;

    public String shortName;

    public String fullName;

    public String link;

    public String avatar;

    public String additionalData;

    public boolean online;

    public String geoData;

    public UserForListDto(User user, String additionalData) {
        if (user.getActualAddress() != null) {
            geoData = user.getActualAddress().getFullAddress();
        }
        id = user.getId();
        ikp = user.getIkp();
        shortName = user.getShortName();
        fullName = user.getFullName();
        link = user.getLink();
        avatar = user.getAvatar();
        this.additionalData = additionalData;
    }

    public static List<UserForListDto> toDtoList(List<User> users, Map<Long, String> additionalDataMap) {
        List<UserForListDto> result = null;
        if (users != null) {
            result = new ArrayList<>();
            for (User user : users) {
                String additionalData = null;
                if (additionalDataMap != null && additionalDataMap.containsKey(user.getId())) {
                    additionalData = additionalDataMap.get(user.getId());
                }
                result.add(new UserForListDto(user, additionalData));
            }
        }
        return result;
    }
}
