package ru.askor.blagosfera.domain.discussion;

import lombok.Data;

import java.util.Date;

/**
 *
 * Domain для сущности Discussion
 */
@Data
public class DiscussionDomain {

    private Long id;
    private String title;
    private int commentsCount;
    private Date lastCommentDate;
    private String lastCommentAuthor;
    private String lastCommentAuthorLink;

}
