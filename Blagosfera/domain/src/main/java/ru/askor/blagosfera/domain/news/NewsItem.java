package ru.askor.blagosfera.domain.news;

import lombok.Data;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.common.Tag;
import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class NewsItem {

    private Long id;
    private String title;
    private String text;
    private String link;
    private int editCount;
    private Date date;
    private Double ratingSum;
    private Double ratingWeight;
    private ListEditorItem category;
    //TODO!!! Выпилить отсюда ссылки на Entity слой
    private RadomAccount author;
    private RadomAccount scope;
    private DiscussionDomain discussion;
    private List<NewsAttachmentDomain> attachments = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
}
