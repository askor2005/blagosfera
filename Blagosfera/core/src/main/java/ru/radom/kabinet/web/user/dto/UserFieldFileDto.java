package ru.radom.kabinet.web.user.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.radom.kabinet.web.communities.dto.CommunityFieldFileDto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.06.2016.
 */
@Data
public class UserFieldFileDto {

    private Long id;

    private String name;

    private String url;

    public UserFieldFileDto(FieldFile fieldFile) {
        setId(fieldFile.getId());
        setName(fieldFile.getName());
        setUrl(fieldFile.getUrl());
    }

    public static List<UserFieldFileDto> toDtoList(List<FieldFile> fieldFiles) {
        List<UserFieldFileDto> result = null;
        if (fieldFiles != null) {
            result = new ArrayList<>();
            for (FieldFile fieldFile : fieldFiles) {
                result.add(new UserFieldFileDto(fieldFile));
            }
        }
        return result;
    }

}
