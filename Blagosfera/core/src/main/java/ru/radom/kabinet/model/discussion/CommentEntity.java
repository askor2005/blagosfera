package ru.radom.kabinet.model.discussion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.rating.Ratable;
import ru.radom.kabinet.services.discuss.CommentsTreeQueryResult;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ahivin on 02.11.2014.
 */

@Entity
@Table(name = "comments")
@SqlResultSetMapping(
        name="CommentsTreeQueryResult",
        classes={
                @ConstructorResult(
                        targetClass=CommentsTreeQueryResult.class,
                        columns={
                                @ColumnResult(name="id"),
                                @ColumnResult(name="parent_id"),
                                @ColumnResult(name="message"),
                                @ColumnResult(name="createdAt",type = Date.class),
                                @ColumnResult(name="owner_id"),
                                @ColumnResult(name="owner_ikp"),
                                @ColumnResult(name="insert_after"),
                                @ColumnResult(name="depth"),
                                @ColumnResult(name="rating")
                        }
                )
        }
)
@JsonIgnoreProperties({"parent", "children", "owner", "parentDiscussion"})
public class CommentEntity extends LongIdentifiable  implements Ratable {
    /**
     * Дата создания
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * Родительский комментарий, если есть
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false)
    @JsonIgnore
    private CommentEntity parent;

    /**
     * Ответы на этот комментарий
     */
    @OneToMany(mappedBy = "parent",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    @JsonIgnore
    private List<CommentEntity> children = new LinkedList<>();

    /**
     * Собственно текст комментария
     */
    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Создатель комментария
     */
    @JoinColumn(name = "sharer_id", nullable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_discussion_id")
    @JsonIgnore
    private Discussion parentDiscussion;
    
    public CommentEntity() {
        createdAt = new Date();
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setParent(CommentEntity parent) {
        this.parent = parent;
    }

    public List<CommentEntity> getChildren() {
        return children;
    }

    public void setChildren(List<CommentEntity> children) {
        this.children = children;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public CommentEntity getParent() {
        return parent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public Discussion getParentDiscussion() {
		return parentDiscussion;
	}

	public void setParentDiscussion(Discussion parentDiscussion) {
		this.parentDiscussion = parentDiscussion;
	}

	public boolean isRoot() {
		return parent == null;
	}
	
}
