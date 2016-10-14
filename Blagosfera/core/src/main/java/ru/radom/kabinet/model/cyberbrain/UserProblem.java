package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = UserProblem.TABLE_NAME)
public class UserProblem extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_user_problems";

    public static class Columns {
        public static final String DESCRIPTION = "description";
        public static final String TAG_OBJECT = "tag_object_id";
        public static final String TAG_MANY = "tag_many_id";
        public static final String SHARER = "sharer_id";
        public static final String COMMUNITY = "community_id";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    @Column(name = Columns.DESCRIPTION)
    private String description;

    @JoinColumn(name = Columns.TAG_OBJECT)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus tagObject;

    @JoinColumn(name = Columns.TAG_MANY)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus tagMany;

    @JoinColumn(name = Columns.SHARER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "userProblem", fetch = FetchType.LAZY)
    private List<UserProblemPerformer> userProblemPerformer;

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin; // Источник происхождения записи

    @Column(name = Columns.SOURCE)
    private Long source; // Идентификатор записи источника

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thesaurus getTagObject() {
        return tagObject;
    }

    public void setTagObject(Thesaurus tagObject) {
        this.tagObject = tagObject;
    }

    public Thesaurus getTagMany() {
        return tagMany;
    }

    public void setTagMany(Thesaurus tagMany) {
        this.tagMany = tagMany;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public List<UserProblemPerformer> getUserProblemPerformer() {
        return userProblemPerformer;
    }

    public void setUserProblemPerformer(List<UserProblemPerformer> userProblemPerformer) {
        this.userProblemPerformer = userProblemPerformer;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public CyberbrainObject getSourceOrigin() {
        return sourceOrigin;
    }

    public void setSourceOrigin(CyberbrainObject sourceOrigin) {
        this.sourceOrigin = sourceOrigin;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }
}