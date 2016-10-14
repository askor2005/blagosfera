package ru.radom.kabinet.web.registrators.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.Address;
import ru.radom.kabinet.model.registration.RegistrationRequest;

/**
 * Created by vtarasenko on 14.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistratorsPageDto {
    private int profileFillingPercent = 0;
    private Long registratorsCount;
    private Address actualAddress;
    private Address registrationAddress;
    private Address sharerAddress;
    private RegistrationRequest currentRequest;
    private boolean registrator;
}
