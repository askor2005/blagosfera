package ru.radom.kabinet.web.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by vtarasenko on 06.04.2016.
 */
@Data
public class HelpSectionResponseDto {
    private HelpSectionDto currentHelpSection;
    private PageDto page;
    private boolean published;
    private String timesWord;
    private List<PageEditionDto> editions;
    private List<HelpSectionDto> children;
    private boolean admin;
    private int editionsCount;
}
