package ru.radom.kabinet.web.user.dto;

import ru.askor.blagosfera.domain.user.UserRole;
import ru.radom.kabinet.model.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 17.08.2016.
 */
public class UserRoleDto {

    public String name;

    public UserRoleDto(UserRole userRole) {
        this.name = userRole.getName();
    }

    public static List<UserRoleDto> toDtoList(List<UserRole> userRoles) {
        List<UserRoleDto> result;
        if (userRoles != null && !userRoles.isEmpty()) {
            result = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                result.add(new UserRoleDto(userRole));
            }
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
}
