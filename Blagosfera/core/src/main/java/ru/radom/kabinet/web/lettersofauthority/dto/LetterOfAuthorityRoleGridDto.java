package ru.radom.kabinet.web.lettersofauthority.dto;

import lombok.Getter;

import java.util.List;

/**
 *
 * Created by vgusev on 17.08.2016.
 */
@Getter
public class LetterOfAuthorityRoleGridDto {

    private boolean success;

    private int total;

    private List<LetterOfAuthorityRoleDto> items;

    private LetterOfAuthorityRoleGridDto(boolean success, int total, List<LetterOfAuthorityRoleDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public static LetterOfAuthorityRoleGridDto toSuccessDto(int total, List<LetterOfAuthorityRoleDto> items) {
        return new LetterOfAuthorityRoleGridDto(true, total, items);
    }

    public static LetterOfAuthorityRoleGridDto toErrorDto() {
        return new LetterOfAuthorityRoleGridDto(false, 0, null);
    }
}
