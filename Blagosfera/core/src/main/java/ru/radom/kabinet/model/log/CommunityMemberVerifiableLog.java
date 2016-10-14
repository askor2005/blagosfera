package ru.radom.kabinet.model.log;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.events.community.CommunityEvent;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Лог вступления/выхода из объединения
 *
 * Created by ebelyaev on 13.08.2015.
 */
@Entity
@Table(name = "community_member_verifiable_logs")
public class CommunityMemberVerifiableLog extends VerifiableLog {

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "community_name", length = 100)
    private String communityName;


    @Column(name = "sharer_id", nullable = false)
    private Long sharerId;

    @Column(name = "sharer_ikp",length = 20)
    private String sharerIkp;

    @Column(name = "sharer_email",length = 100)
    private String sharerEmail;

    @Column(name = "sharer_name",length = 1000)
    private String sharerName;


    @Column(name = "comment",length = 1000)
    private String comment;

    public CommunityMemberVerifiableLog() {
    }

    private void setCommunity(Community community) {
        this.communityId = community.getId();
        this.communityName = community.getName();
    }

    private void setUser(User user) {
        this.sharerId = user.getId();
        this.sharerIkp = user.getIkp();
        this.sharerEmail = user.getEmail();
        this.sharerName = user.getFullName();
    }

    public CommunityMemberVerifiableLog(CommunityEvent communityEvent) {
        if(communityEvent.getType().equals(CommunityEventType.JOIN) || communityEvent.getType().equals(CommunityEventType.LEAVE)) {
            CommunityMemberEvent e = (CommunityMemberEvent) communityEvent;
            User sharer = e.getMember().getUser();
            setUser(sharer);
            setCommunity(e.getCommunity());
            comment = e.getType().toString();
        }
    }


    @Override
    public String getStringFromFields() {
        return "CommunityMemberVerifiableLog" + communityId + communityName + sharerId + sharerIkp + sharerEmail + sharerName + comment;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getSharerId() {
        return sharerId;
    }

    public void setSharerId(Long sharerId) {
        this.sharerId = sharerId;
    }

    public String getSharerIkp() {
        return sharerIkp;
    }

    public void setSharerIkp(String sharerIkp) {
        this.sharerIkp = sharerIkp;
    }

    public String getSharerEmail() {
        return sharerEmail;
    }

    public void setSharerEmail(String sharerEmail) {
        this.sharerEmail = sharerEmail;
    }

    public String getSharerName() {
        return sharerName;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
