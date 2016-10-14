package ru.radom.kabinet.document.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.json.ShortDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
@Data
public class DocumentFilterDto {

    private Long id;

    private String name;

    @JsonSerialize(using = ShortDateSerializer.class)
    private Date createDate;

    public DocumentFilterDto(Document document) {
        setId(document.getId());
        setName(document.getName());
        setCreateDate(document.getCreateDate());
    }

    public static List<DocumentFilterDto> toDtoList(List<Document> documents) {
        List<DocumentFilterDto> result = null;
        if (documents != null) {
            result = new ArrayList<>();
            for (Document document : documents) {
                result.add(new DocumentFilterDto(document));
            }
        }
        return result;
    }
}
