package ru.radom.kabinet.web.ras.dto;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
public class TokenDto extends ResponseDto {

    public String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
