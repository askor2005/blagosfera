package ru.radom.kabinet.model.discussion;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "comment_votes")
public class CommentVote extends LongIdentifiable {
	
	public enum Vote {
		UP(1),
		DOWN(-1);
		
		public final int value;
		
		private Vote(int value) {
			this.value = value;
		}
	}
    
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "sharer_id", foreignKey = @ForeignKey(name = "fk_comment_votes_sharer_id"))
	private UserEntity userEntity;
	
	@Enumerated(EnumType.STRING)
	private Vote vote;
	
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "fk_comment_votes_comment_id"))
	private CommentEntity commentEntity;

	public UserEntity getSharer() {
		return userEntity;
	}

	public void setSharer(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public CommentEntity getCommentEntity() {
		return commentEntity;
	}

	public void setCommentEntity(CommentEntity commentEntity) {
		this.commentEntity = commentEntity;
	}
	
}
