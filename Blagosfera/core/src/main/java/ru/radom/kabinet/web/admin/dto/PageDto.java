package ru.radom.kabinet.web.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.cms.Page;

import java.util.Date;

/**
 * Created by vtarasenko on 31.03.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    private Long id;
    private String content;
    private String title;
    private String description;
    private String keywords;
    private int editionsCount;
    private Long currentEditorEditDate;
    private Long currentEditorId;
    public static PageDto createFromPage(Page page) {
        return new PageDto(page.getId(),page.getContent(),page.getTitle(),page.getDescription(),page.getKeywords(),page.getEditionsCount(),page.getCurrentEditorEditDate() != null ? page.getCurrentEditorEditDate().getTime() : null,page.getCurrentEditorId());
    }
}
