package ru.radom.kabinet.web.communities.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.radom.kabinet.dto.CommonResponseDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 10.05.2016.
 */
@Getter
public class CreateCommunityResponseDto implements CommonResponseDto {

    private String result;

    private String message;

    private Map<String, String> errors;

    private String link;

    private Long id;

    public CreateCommunityResponseDto(String message) {
        this.result = "error";
        this.message = message;
    }

    public CreateCommunityResponseDto(String message, Map<String, String> errors) {
        this.result = "error";
        this.message = message;
        this.errors = errors;
    }

    public CreateCommunityResponseDto(Community community) {
        this.result = "success";
        this.link = community.getLink();
        this.id = community.getId();
    }

    @Override
    public String getResult() {
        return result;
    }
}
