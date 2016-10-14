package ru.radom.kabinet.web.admin.dto;

import lombok.Data;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Data
public class CreateSupportRequestDto {
    private String email;
    private String theme;
    private String description;
    private Long supportRequestTypeId;
    private String captcha;
}
