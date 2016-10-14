package ru.radom.kabinet.document.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.json.ShortDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Обёртка документа для страницы подписания списка документов
 * Created by vgusev on 14.04.2016.
 */
@Data
public class DocumentSignPageDto {

    private Long id;

    private String name;

    @JsonSerialize(using = ShortDateSerializer.class)
    private Date createDate;

    public DocumentSignPageDto(Document document) {
        setId(document.getId());
        setName(prepareDocumentName(document.getName()));
        setCreateDate(document.getCreateDate());
    }

    public static List<DocumentSignPageDto> toDtoList(List<Document> documents) {
        List<DocumentSignPageDto> result = null;
        if (documents != null) {
            result = new ArrayList<>();
            for (Document document : documents) {
                result.add(new DocumentSignPageDto(document));
            }
        }
        return result;
    }

    private String prepareDocumentName(String name) {
        name = Jsoup.parse(name).text();
        name = name.replaceAll("&nbsp;", " ");
        name = StringUtils.join(name.split("[\\s]"), " ");
        return name;
    }
}
