package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.field.FieldFile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 23.03.2016.
 */
@Data
public class CommunityFieldFileDto {

    private Long id;

    private String name;

    private String url;

    public CommunityFieldFileDto() {}

    public CommunityFieldFileDto(FieldFile fieldFile) {
        setId(fieldFile.getId());
        setName(fieldFile.getName());
        setUrl(fieldFile.getUrl());
    }

    public static List<CommunityFieldFileDto> toDtoList(List<FieldFile> fieldFiles) {
        List<CommunityFieldFileDto> result = null;
        if (fieldFiles != null) {
            result = new ArrayList<>();
            for (FieldFile fieldFile : fieldFiles) {
                result.add(new CommunityFieldFileDto(fieldFile));
            }
        }
        return result;
    }

    public static List<FieldFile> toDomainList(List<CommunityFieldFileDto> communityFieldFiles) {
        List<FieldFile> result = null;
        if (communityFieldFiles != null) {
            result = new ArrayList<>();
            for (CommunityFieldFileDto communityFieldFile : communityFieldFiles) {
                FieldFile fieldFile = new FieldFile();
                fieldFile.setId(communityFieldFile.getId());
                fieldFile.setName(communityFieldFile.getName());
                fieldFile.setUrl(communityFieldFile.getUrl());
                result.add(fieldFile);
            }
        }
        return result;
    }

}
