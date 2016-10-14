package ru.radom.kabinet.web.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;

/**
 * Created by vtarasenko on 05.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpSectionDto {

    private Long id;

    private String name;

    private boolean published;

    private Long pageId;

    private Long parentId;

    private String title;
    public static HelpSectionDto toDto(HelpSectionDomain helpSectionDomain) {
       return new HelpSectionDto(helpSectionDomain.getId(),helpSectionDomain.getName(),helpSectionDomain.isPublished(),helpSectionDomain.getPageId(),helpSectionDomain.getParentId(),helpSectionDomain.getTitle());
    }
}
