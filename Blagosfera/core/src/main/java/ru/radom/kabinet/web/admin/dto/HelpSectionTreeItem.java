package ru.radom.kabinet.web.admin.dto;

import lombok.Data;

/**
 * Created by vtarasenko on 05.04.2016.
 */
@Data
public class HelpSectionTreeItem {
    private Long id;
    private String text;
    private boolean leaf;
    private boolean checked;
    private boolean expanded;
}
