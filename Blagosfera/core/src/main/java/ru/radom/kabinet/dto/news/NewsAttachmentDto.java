package ru.radom.kabinet.dto.news;

import ru.askor.blagosfera.domain.news.NewsAttachmentDomain;
import ru.askor.blagosfera.domain.news.NewsAttachmentType;

public class NewsAttachmentDto {

    public String src;
    public NewsAttachmentType type;
    public Integer width;
    public Integer height;

    public NewsAttachmentDto() {
    }

    public NewsAttachmentDto(NewsAttachmentDomain newsAttachment) {
        src = newsAttachment.getSrc();
        type = newsAttachment.getType();
        width = newsAttachment.getWidth();
        height = newsAttachment.getHeight();
    }

    public NewsAttachmentDomain toDomain() {
        NewsAttachmentDomain result = new NewsAttachmentDomain();
        result.setSrc(src);
        result.setType(type);
        result.setWidth(width);
        result.setHeight(height);
        return result;
    }
}
