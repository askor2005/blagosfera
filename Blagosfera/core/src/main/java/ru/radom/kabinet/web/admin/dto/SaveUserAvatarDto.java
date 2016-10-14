package ru.radom.kabinet.web.admin.dto;

import lombok.Data;

/**
 * Created by vtarasenko on 08.08.2016.
 */
@Data
public class SaveUserAvatarDto {
    private String url;
    private String croppedUrl;
}
