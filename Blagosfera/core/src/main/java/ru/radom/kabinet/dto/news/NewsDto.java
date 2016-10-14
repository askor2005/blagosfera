package ru.radom.kabinet.dto.news;

import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.common.Tag;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.news.NewsAttachmentDomain;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.radom.kabinet.dto.discussion.DiscussionDto;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.web.lettersofauthority.dto.RadomAccountDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewsDto {

    public Long id;
    public String title;
    public String text;
    public NewsListItemCategoryDto category;
    public String link;
    public int editCount;
    public String date;
    public Long ratingSum;
    public Long ratingWeight;
    public String authorType;
    public String scopeType;
    public ru.radom.kabinet.web.lettersofauthority.dto.RadomAccountDto author;
    public ru.radom.kabinet.web.lettersofauthority.dto.RadomAccountDto scope;
    public DiscussionDto discussion;
    public List<NewsAttachmentDto> attachments = new ArrayList<>();
    public List<String> tags = new ArrayList<>();

    public NewsDto() {}

    public NewsDto(NewsItem news) {
        id = news.getId();
        title = news.getTitle();
        text = news.getText();
        category = NewsListItemCategoryDto.toDto(news.getCategory());
        link = news.getLink();
        editCount = news.getEditCount();
        ratingSum = (news.getRatingSum() == null) ? 0 : news.getRatingSum().longValue();
        ratingWeight = (news.getRatingWeight() == null) ? 0 : news.getRatingWeight().longValue();
        authorType = news.getAuthor() instanceof UserEntity ? Discriminators.SHARER : Discriminators.SYSTEM_ACCOUNT;
        scopeType = news.getScope() instanceof UserEntity ? Discriminators.SHARER : Discriminators.COMMUNITY;

        if (news.getDate() != null) {
            date = DateUtils.formatDate(news.getDate(), "dd.MM.yyyy HH:mm:ss");
        }

        author = new RadomAccountDto(news.getAuthor().getId(), news.getAuthor().getObjectType(), news.getAuthor().getName(),
                news.getAuthor().getAvatar(), news.getAuthor().getLink(), news.getAuthor().getIkp());

        scope = new RadomAccountDto(news.getScope().getId(), news.getScope().getObjectType(), news.getScope().getName(),
                news.getScope().getAvatar(), news.getScope().getLink(), news.getScope().getIkp());

        discussion = news.getDiscussion() != null ? new DiscussionDto(news.getDiscussion()) : null;

        for (NewsAttachmentDomain attachment: news.getAttachments()) {
            attachments.add(new NewsAttachmentDto(attachment));
        }

        tags.addAll(news.getTags().stream()
                .map(Tag::getText)
                .collect(Collectors.toList()));
    }

    public NewsItem toDomain() {
        NewsItem result = new NewsItem();
        result.setId(id);
        result.setTitle(title);
        result.setText(text);

        for (NewsAttachmentDto attachmentDto : attachments) {
            result.getAttachments().add(attachmentDto.toDomain());
        }

        result.setTags(tags.stream()
                .map(Tag::new)
                .collect(Collectors.toList()));

        result.setCategory(new ListEditorItem());
        NewsListItemCategoryDto newsListItemCategoryDto = category;
        result.getCategory().setText(newsListItemCategoryDto.text);
        result.getCategory().setId(newsListItemCategoryDto.id);
        return result;
    }

    public NewsItem toDomain(RadomAccount author, RadomAccount scope) {
        NewsItem result = this.toDomain();
        result.setAuthor(author);
        result.setScope(scope);
        return result;
    }
}
