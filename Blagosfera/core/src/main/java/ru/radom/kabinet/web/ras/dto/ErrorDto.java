package ru.radom.kabinet.web.ras.dto;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
public class ErrorDto extends ResponseDto {

    public String result;
    public String message;

    public ErrorDto(String message) {
        this.message = message == null ? "Произошла ошибка!" : message;
        result = "error";
    }
}
