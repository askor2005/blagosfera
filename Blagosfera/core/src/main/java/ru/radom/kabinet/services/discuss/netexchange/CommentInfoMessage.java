package ru.radom.kabinet.services.discuss.netexchange;

import ru.radom.kabinet.model.discussion.CommentEntity;

import java.text.SimpleDateFormat;

/**
 *
 * Created by ahivin on 11.11.2014.
 */
public class CommentInfoMessage implements Comparable<CommentInfoMessage>{

    private final static String PATTERN = "dd.MM.yyyy HH:mm";

    private Long id;
    private String text;
    private Long parent;
    private String createdAt;
    private String ownerName;
    private String ownerIkp;
    private Long ownerId;
    private Long discussionId;
    private String ownerAvatar;
    private Long insertAfter;
    private Long ratingSum = 0L;
    private Double ratingWeight = 0D;

	private boolean isOwn;


    public CommentInfoMessage(CommentEntity commentEntity){
        this.setId(commentEntity.getId());
        this.setText(commentEntity.getMessage());
        this.setCreatedAt(new SimpleDateFormat(PATTERN).format(commentEntity.getCreatedAt()));
        this.setParent(commentEntity.getParent().getId());
        this.setOwnerId(commentEntity.getOwner().getId());
        this.setOwnerName(commentEntity.getOwner().getShortName());
        this.setOwnerIkp(commentEntity.getOwner().getIkp());
        this.setOwnerAvatar(commentEntity.getOwner().getAvatar());
        this.setRatingSum(0L);
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIkp() {
        return ownerIkp;
    }

    public void setOwnerIkp(String ownerIkp) {
        this.ownerIkp = ownerIkp;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Long discussionId) {
        this.discussionId = discussionId;
    }

    public Long getInsertAfter() {
        return insertAfter;
    }

    public void setInsertAfter(Long insertAfter) {
        this.insertAfter = insertAfter;
    }

    public Long getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Long ratingSum) {
        this.ratingSum = ratingSum;
    }

    public Double getRatingWeight() {
        return ratingWeight;
    }

    public void setRatingWeight(Double ratingWeight) {
        this.ratingWeight = ratingWeight;
    }

    @Override
    public int compareTo(CommentInfoMessage o) {
        return this.getId().compareTo(o.getId());
    }

	public void setIsOwn(boolean isOwn) {
		this.isOwn = isOwn;
	}
	
	public boolean getIsOwn() {
		return this.isOwn;
	}
}
