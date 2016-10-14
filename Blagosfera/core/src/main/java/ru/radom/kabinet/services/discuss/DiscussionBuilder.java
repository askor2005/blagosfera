package ru.radom.kabinet.services.discuss;

import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.askor.blagosfera.domain.RadomAccount;

/**
 * 
 *
 */
public class DiscussionBuilder {
    private String title;
    private String content;
    private String recommendations;
	private UserEntity owner;
	private RadomAccount scope;
    
    public DiscussionBuilder(UserEntity owner) {
    	this.owner = owner;
    }

    public DiscussionBuilder title(final String title) {
        this.title = title;
        return this;
    }
    
    public DiscussionBuilder scope(RadomAccount scope) {
		this.scope = scope;
		return this;
	}
    
    public DiscussionBuilder content(final String content) {
        this.content = content;
        return this;
    }
    public DiscussionBuilder recommendations(final String recommendations) {
        this.recommendations = recommendations;
        return this;
    }

    public Discussion build(){
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setMessage(content);
        commentEntity.setOwner(owner);

        Discussion result = new Discussion();
        result.setTitle(this.title);
        result.setRecommendations(this.recommendations);
        result.setOwner(this.owner);
        result.setRoot(commentEntity);
        result.setScope(this.scope);
        commentEntity.setParentDiscussion(result);
        return result;
    }
}
