package ru.radom.kabinet.services.discuss;


import java.math.BigInteger;
import java.util.Date;

/**
 * Created by ahivin on 08.11.2014.
 */


public class CommentsTreeQueryResult {

    private final BigInteger id;
    private final BigInteger parentId;
    private final String message;
    private final Date createdAt;
    private final BigInteger ownerId;
    private final String ownerIkp;
    private final BigInteger insertAfter;
    private final Integer depth;
    private Long ratingSum = 0L;
    private Double ratingWeight = 0D;

    public CommentsTreeQueryResult(BigInteger id, 
    		BigInteger parentId,
    		String message,
    		Date createdAt,
    		BigInteger ownerId,
    		String ownerIkp,
    		BigInteger insertAfter,
    		Integer depth,
    		BigInteger rating) {
        this.id = id;
        this.parentId = parentId;
        this.message = message;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.ownerIkp = ownerIkp;
        this.insertAfter = insertAfter;
        this.depth = depth;
    }

    public BigInteger getId() {
        return id;
    }

    public BigInteger getParentId() {
        return parentId;
    }


    public String getMessage() {
        return message;
    }


    public Integer getDepth() {
        return depth;
    }


    public String getOwnerIkp() {
        return ownerIkp;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public BigInteger getInsertAfter() {
        return insertAfter;
    }

    public Long getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Number ratingSum) {
        this.ratingSum = (ratingSum == null) ? 0L : ratingSum.longValue();
    }

    public Double getRatingWeight() {
        return ratingWeight = (ratingWeight == null) ? 0D : ratingWeight;
    }

    public void setRatingWeight(Double ratingWeight) {
        this.ratingWeight = ratingWeight;
    }
}
