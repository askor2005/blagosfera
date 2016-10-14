package ru.askor.blagosfera.domain.news;

import lombok.Data;

/**
 * Domain вложения в сущность
 */
@Data
public class NewsAttachmentDomain {

    private String src;
    private NewsAttachmentType type;
    private Integer width;
    private Integer height;
}
