package ru.radom.kabinet.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 25.03.2016.
 */
@Getter
public class SuccessResponseDto implements CommonResponseDto {

    private String result = "success";

    protected SuccessResponseDto() {}

    private static SuccessResponseDto instance = new SuccessResponseDto();

    public static SuccessResponseDto get() {
        return instance;
    }
}
