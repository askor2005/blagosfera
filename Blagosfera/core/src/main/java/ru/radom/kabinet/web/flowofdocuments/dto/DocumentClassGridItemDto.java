package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 08.04.2016.
 */
@Data
public class DocumentClassGridItemDto {

    private Long id;
    private String name;
    private String key;
    private String pathName;
    private int position;

    private Long parentId;
    private String parentName;
    private Boolean expanded;
    private List<String> children;

}
