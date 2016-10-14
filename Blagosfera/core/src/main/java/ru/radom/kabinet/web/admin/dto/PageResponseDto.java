package ru.radom.kabinet.web.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by vtarasenko on 01.04.2016.
 * класс для возврата данных для редактирования и просмотра страницы
 */
@Data
public class PageResponseDto {
    private PageDto page;
    private boolean hasEditPermission;
    private String currentEditor;
    private String pageEditLink;
    //слово "раз" в падеже, например релдактировалась 4 раза
    private String timesWord;
    private List<PageEditionDto> editions;
    private boolean published;
    private String accessType;
    private String sectionLink;
}
