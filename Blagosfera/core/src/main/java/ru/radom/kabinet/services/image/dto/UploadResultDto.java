package ru.radom.kabinet.services.image.dto;

import lombok.Getter;
import ru.radom.kabinet.dto.CommonResponseDto;

/**
 *
 * Created by dream_000 on 10.05.2016.
 */
@Getter
public class UploadResultDto implements CommonResponseDto {

    private String result;

    private String image;

    public UploadResultDto(String url) {
        this.result = "success";
        this.image = url;
    }
}
