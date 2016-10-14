package ru.radom.kabinet.services.discuss;

import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;

/**
 * Created by ahivin on 04.11.2014.
 */
public class CommentBuilder {
    private UserEntity owner;
	private CommentEntity parent;
    private String message;

    public CommentBuilder(UserEntity owner) {
    	this.owner = owner;
    }
    
    public CommentBuilder parent(final CommentEntity parent) {
        this.parent = parent;
        return this;
    }

    public CommentBuilder parent(final Discussion parent) {
        return parent(parent.getRoot());
    }

    public CommentBuilder message(final String message) {
        this.message = message;
        return this;
    }

    protected CommentEntity build() {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setMessage(message);
        commentEntity.setOwner(owner);
        commentEntity.setParent(this.parent);
        commentEntity.setParentDiscussion(this.parent.getParentDiscussion());
        return commentEntity;
    }
}
