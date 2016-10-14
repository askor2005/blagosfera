package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class CommunityDocumentDto {

    public Long id;
    public String code;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date createdDate;
    public String name;
    private String shortName;
    public String link;

    public CommunityDocumentDto() {
    }

    public CommunityDocumentDto(Document document) {
        setId(document.getId());
        setCode(document.getCode());
        setCreatedDate(document.getCreateDate());
        setName(document.getName());
        setShortName(document.getShortName());
        setLink(document.getLink());
    }

    public static List<CommunityDocumentDto> toListDto(List<Document> documents) {
        List<CommunityDocumentDto> result = null;
        if (documents != null) {
            result = new ArrayList<>();
            for (Document document : documents) {
                result.add(new CommunityDocumentDto(document));
            }
        }
        return result;
    }
}
