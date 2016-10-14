package ru.radom.kabinet.web.user.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.05.2016.
 */
@Data
public class SearchUserDto {

    private Long id;

    private String fullName;

    public SearchUserDto(UserEntity userEntity) {
        setId(userEntity.getId());
        setFullName(userEntity.getFullName());
    }

    public static List<SearchUserDto> toDtoList(List<UserEntity> userEntities) {
        List<SearchUserDto> result = null;
        if (userEntities != null && !userEntities.isEmpty()) {
            result = new ArrayList<>();
            for (UserEntity userEntity : userEntities) {
                result.add(new SearchUserDto(userEntity));
            }
        }
        return result;
    }

    public SearchUserDto(User user) {
        setId(user.getId());
        setFullName(user.getFullName());
    }

    public static List<SearchUserDto> toDtoListFromDomainUsers(List<User> users) {
        List<SearchUserDto> result = null;
        if (users != null && !users.isEmpty()) {
            result = new ArrayList<>();
            for (User user : users) {
                result.add(new SearchUserDto(user));
            }
        }
        return result;
    }
}
