package ru.radom.kabinet.dto;

import lombok.Getter;

import java.util.Map;

/**
 *
 * Created by vgusev on 25.03.2016.
 */
@Getter
public class ErrorResponseDto implements CommonResponseDto {

    private String result = "error";

    private String message;

    private Map<String, String> errors;

    public ErrorResponseDto(String message) {
        this.message = message;
    }

    public ErrorResponseDto(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }

}
