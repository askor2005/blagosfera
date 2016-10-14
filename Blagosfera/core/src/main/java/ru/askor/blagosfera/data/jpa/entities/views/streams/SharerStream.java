package ru.askor.blagosfera.data.jpa.entities.views.streams;

import org.hibernate.annotations.Immutable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by vtarasenko on 16.04.2016.
 */
//@Entity
//@Table(name = "sharers_streams")
//@Immutable
public class SharerStream {
    @Column(name="sharer_id")
    private UserEntity user;
    @Column(name = "sharers_streams")
    private Long sharersStreams;

    public Long getSharersStreams() {
        return sharersStreams;
    }

    public void setSharersStreams(Long sharersStreams) {
        this.sharersStreams = sharersStreams;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
