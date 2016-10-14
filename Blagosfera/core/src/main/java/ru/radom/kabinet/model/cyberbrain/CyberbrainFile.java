package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = CyberbrainFile.TABLE_NAME)
public class CyberbrainFile extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_files";

    public static class Columns {
        public static final String CREATION_DATE = "creation_date";
        public static final String NAME = "name";
        public static final String OBJECT = "object_id";
        public static final String SUCCESSFULLY_LOADED = "successfully_loaded";
        public static final String SHARER = "sharer_id";
        public static final String COMMUNITY = "community_id";
    }

    @Column(name = Columns.CREATION_DATE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate; // Дата создания записи

    @Column(name = Columns.NAME, nullable = false)
    private String name; // имя файла

    @JoinColumn(name = Columns.OBJECT)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject object; // объект кибер мозга

    @Column(name = Columns.SUCCESSFULLY_LOADED)
    private boolean successfullyLoaded; // успешно загружен

    @JoinColumn(name = Columns.SHARER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity userEntity;

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CyberbrainObject getObject() {
        return object;
    }

    public void setObject(CyberbrainObject object) {
        this.object = object;
    }

    public boolean isSuccessfullyLoaded() {
        return successfullyLoaded;
    }

    public void setSuccessfullyLoaded(boolean successfullyLoaded) {
        this.successfullyLoaded = successfullyLoaded;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }
}