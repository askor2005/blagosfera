package ru.askor.blagosfera.web.controllers.ng.user.dto;

import ru.radom.kabinet.web.user.dto.UserRoleDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
public class UserDto {

    public boolean authorised = false;
    public String username;
    public String ikp;
    public String avatar;
    public String shortName;
    public boolean verified;
    public String firstName;
    public String secondName;
    public String lastName;
    public boolean sex;
    public String identificationMode;
    public String phone;
    public List<UserRoleDto> roles = new ArrayList<>();
}
