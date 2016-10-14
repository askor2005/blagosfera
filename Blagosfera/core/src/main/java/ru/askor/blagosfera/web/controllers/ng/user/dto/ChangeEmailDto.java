package ru.askor.blagosfera.web.controllers.ng.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by vtarasenko on 22.04.2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailDto {
    private String code;
    private String email;
}
