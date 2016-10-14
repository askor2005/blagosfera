package ru.askor.blagosfera.web.controllers.ng.user.dto;

import lombok.Data;

import java.util.Map;

/**
 * Created by vtarasenko on 03.08.2016.
 */
@Data
public class SaveProfileDto {
    private Map<String,String> basicInformation;
    private Map<String,String> factAddress;
    private Map<String,String> regAddress;
    private Map<String,String> registratorOfficeAddress;
    private Map<String,String> registratorData;
}
