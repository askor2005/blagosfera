package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.UserEntity;

public class CashboxUserDto {

    public long id;
    public String name;
    public String ikp;

    public CashboxUserDto() {
    }

    public CashboxUserDto(UserEntity userEntity) {
        id = userEntity.getId();
        name = userEntity.getFullName();
        ikp = userEntity.getIkp();
    }
}
