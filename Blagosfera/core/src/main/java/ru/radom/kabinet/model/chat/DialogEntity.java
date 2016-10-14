package ru.radom.kabinet.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Type;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "dialogs")
public class DialogEntity extends LongIdentifiable {

    @Column(name = "name", unique = false, nullable = true)
    @Type(type="text")
    private String name;

    @Column(name = "admin_id", unique = false, nullable = true)
    private Long adminId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "dialogs_sharers",
            joinColumns = {
                    @JoinColumn(name = "dialog_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "sharer_id", nullable = false, updatable = false)})
	private Set<UserEntity> users = new HashSet<>();

    @Column(name = "closed")
    private Boolean isClosed;

    @Transient
    private ChatMessage lastMessage;

    /**
     * Количество непрочитанных сообщений
     */
    @Transient
    private int countUnreadMessages;

    public DialogEntity() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DialogEntity)) return false;

        DialogEntity that = (DialogEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public UserEntity getUser(Long userId) {
        for (UserEntity user : getUsers()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }

        return null;
    }

    public boolean hasUser(Long sharerId) {
        return getUser(sharerId) != null;
    }

    @JsonIgnore
    public List<Long> getUsersIds() {
        List<Long> ids = new ArrayList<>();

        for (UserEntity userEntity : getUsers()) {
            if (userEntity.getId() != null) ids.add(userEntity.getId());
        }

        return ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getCountUnreadMessages() {
        return countUnreadMessages;
    }

    public void setCountUnreadMessages(int countUnreadMessages) {
        this.countUnreadMessages = countUnreadMessages;
    }

    public boolean isClosed() {
        return BooleanUtils.toBooleanDefaultIfNull(isClosed, false);
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
