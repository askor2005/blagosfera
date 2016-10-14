package ru.askor.blagosfera.domain.registration.request;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

import java.util.Date;

/**
 * Created by vtarasenko on 12.08.2016.
 */
@Data
public class RegistrationRequestDomain {
    private Long id;
    private Date created;
    private User registrator;
    private String comment;
}
