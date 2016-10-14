package ru.radom.kabinet.web.admin.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.section.SectionDomain;

/**
 *
 * Created by vgusev on 29.03.2016.
 */
@Data
public class SectionTreeNodeDto {

    private Long id;
    private String name;
    private int level;

    public SectionTreeNodeDto(SectionDomain section, int level) {
        this.id = section.getId();
        this.name = section.getTitle();
        this.level = level;
    }
}
