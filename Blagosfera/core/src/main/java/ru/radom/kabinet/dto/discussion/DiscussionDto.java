package ru.radom.kabinet.dto.discussion;

import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.radom.kabinet.utils.DateUtils;

/**
 * DTO обсуждения
 */
public class DiscussionDto {

    private int commentsCount;
    private String lastCommentDate;
    private String lastCommentAuthor;
    private String lastCommentAuthorLink;


    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getLastCommentDate() {
        return lastCommentDate;
    }

    public void setLastCommentDate(String lastCommentDate) {
        this.lastCommentDate = lastCommentDate;
    }

    public String getLastCommentAuthor() {
        return lastCommentAuthor;
    }

    public void setLastCommentAuthor(String lastCommentAuthor) {
        this.lastCommentAuthor = lastCommentAuthor;
    }

    public String getLastCommentAuthorLink() {
        return lastCommentAuthorLink;
    }

    public void setLastCommentAuthorLink(String lastCommentAuthorLink) {
        this.lastCommentAuthorLink = lastCommentAuthorLink;
    }
    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */

    public DiscussionDto(DiscussionDomain discussion) {


        setCommentsCount(discussion.getCommentsCount());

        if (discussion.getLastCommentDate() != null) {
            setLastCommentDate(DateUtils.formatDate(discussion.getLastCommentDate(), DateUtils.Format.DATE_TIME_SHORT));
        }

        setLastCommentAuthor(discussion.getLastCommentAuthor());
        setLastCommentAuthorLink(discussion.getLastCommentAuthorLink());
    }
}
