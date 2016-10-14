package ru.radom.kabinet.document.dto;

import lombok.Data;
import org.jsoup.Jsoup;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

import java.util.List;

/**
 * Обёртка с данными документа
 * Created by vgusev on 15.09.2015.
 */
@Data
public class FlowOfDocumentDTO {

    // Наименование документа
    private String name;

    // Сокращённое название документа
    private String shortName;

    // Контент документа
    private String content;

    // Участники документа
    private List<DocumentParticipantSourceDto> participants;

    public FlowOfDocumentDTO(String name, String shortName, String content, List<DocumentParticipantSourceDto> participants) {
        this.name = name;
        this.shortName = shortName;
        this.content = content;
        this.participants = participants;
    }

    public String getHtmlDecodedName() {
        return Jsoup.parse(name).text();
    }

    public String getHtmlDecodedContent() {
        return Jsoup.parse(content).text();
    }
}
