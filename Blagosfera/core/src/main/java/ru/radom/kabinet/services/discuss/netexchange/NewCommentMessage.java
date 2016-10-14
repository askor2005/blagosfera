package ru.radom.kabinet.services.discuss.netexchange;

public class NewCommentMessage {
    private String text;
    private Long parent;
    private boolean isOwn = false;
    private int rating = 0;

    public NewCommentMessage(){
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getParent() {
        return parent;
    }

	public boolean isOwn() {
		return isOwn;
	}

	public void setOwn(boolean isOwn) {
		this.isOwn = isOwn;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
    
}
