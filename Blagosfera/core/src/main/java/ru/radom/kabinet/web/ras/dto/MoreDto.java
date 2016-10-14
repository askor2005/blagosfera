package ru.radom.kabinet.web.ras.dto;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
public class MoreDto extends ResponseDto {

    public String result = "more";
    public String count;

    public MoreDto(String count) {
        this.count = count;
    }
}
