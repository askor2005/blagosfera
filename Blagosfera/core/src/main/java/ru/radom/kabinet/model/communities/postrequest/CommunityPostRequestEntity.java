package ru.radom.kabinet.model.communities.postrequest;

import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.askor.blagosfera.domain.community.CommunityPostRequestStatus;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс - сущность для хранения предложений назначений на должности в объединении
 * Created by vgusev on 28.08.2015.
 */
@Entity
@Table(name = "community_posts_requests")
public class CommunityPostRequestEntity extends LongIdentifiable {

    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityMemberEntity sender;

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityMemberEntity receiver;

    @JoinColumn(name = "community_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityEntity community;

    @JoinColumn(name = "community_post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityPostEntity communityPost;

    @Column(nullable = false)
    private CommunityPostRequestStatus status;

    public CommunityMemberEntity getSender() {
        return sender;
    }

    public void setSender(CommunityMemberEntity sender) {
        this.sender = sender;
    }

    public CommunityMemberEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(CommunityMemberEntity receiver) {
        this.receiver = receiver;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public CommunityPostEntity getCommunityPost() {
        return communityPost;
    }

    public void setCommunityPost(CommunityPostEntity communityPost) {
        this.communityPost = communityPost;
    }

    public CommunityPostRequestStatus getStatus() {
        return status;
    }

    public void setStatus(CommunityPostRequestStatus status) {
        this.status = status;
    }

    public CommunityPostRequest toDomain() {
        CommunityPostRequest result = new CommunityPostRequest();
        result.setId(getId());
        if (getCommunity() != null) {
            result.setCommunity(getCommunity().toDomain());
        }
        if (getCommunityPost() != null) {
            result.setCommunityPost(getCommunityPost().toDomain(false, false, false, false));
        }
        if (getReceiver() != null) {
            result.setReceiver(getReceiver().toDomain(false, true, false, false));
        }
        if (getSender() != null) {
            result.setSender(getSender().toDomain(false, true, false, false));
        }
        result.setStatus(getStatus());
        return result;
    }

    public static CommunityPostRequest toDomainSafe(CommunityPostRequestEntity entity) {
        CommunityPostRequest result = null;
        if (entity != null) {
            result = entity.toDomain();
        }
        return result;
    }

    public static List<CommunityPostRequest> toDomainList(List<CommunityPostRequestEntity> entities) {
        List<CommunityPostRequest> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (CommunityPostRequestEntity entity : entities) {
                result.add(toDomainSafe(entity));
            }
        }
        return result;
    }
}
