package ru.radom.kabinet.web.invite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Created by vtarasenko on 19.04.2016.
 */
@Data
public class AcceptRegistrationRequestDto {
    private String hash;

    private String password;

    private String base64AvatarSrc;

    private String base64Avatar;

    private boolean needSendPassword;
}
