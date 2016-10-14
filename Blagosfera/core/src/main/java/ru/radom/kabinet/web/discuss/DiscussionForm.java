package ru.radom.kabinet.web.discuss;

import org.springframework.format.annotation.DateTimeFormat;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiscussionForm {
	private Long id;
	
	private String title;
	private String content;
	private String description;
	
	private Long area;
	private Long field;
	private Long topic;
	
	private Boolean timeLimited = false;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private Calendar timeLimit;
	
	private Boolean commentsLimited = false;
	private Long commentsLimit = 0L;
	
	private AccessType accessType = AccessType.ALL;
	private Boolean visible = true;
	
	private Boolean publicEvaluation = false;
	private Boolean mandatory = false;
	private Integer mandatoryPeriod = 0;
	private List<UserEntity> participants = new ArrayList<>();
	private List<CommunityEntity> communities = new ArrayList<>();
	
	public DiscussionForm() {}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Long getArea() {
		return area;
	}


	public void setArea(Long area) {
		this.area = area;
	}


	public Long getField() {
		return field;
	}


	public void setField(Long field) {
		this.field = field;
	}


	public Long getTopic() {
		return topic;
	}


	public void setTopic(Long topic) {
		this.topic = topic;
	}


	public Boolean getTimeLimited() {
		return timeLimited;
	}


	public void setTimeLimited(Boolean timeLimited) {
		this.timeLimited = timeLimited;
	}


	public Calendar getTimeLimit() {
		return timeLimited ? timeLimit : null;
	}


	public void setTimeLimit(Calendar timeLimit) {
		this.timeLimit = timeLimit;
	}


	public Boolean getCommentsLimited() {
		return commentsLimited;
	}


	public void setCommentsLimited(Boolean commentsLimited) {
		this.commentsLimited = commentsLimited;
	}


	public Long getCommentsLimit() {
		return commentsLimited ? commentsLimit : 0L;
	}


	public void setCommentsLimit(Long commentsLimit) {
		this.commentsLimit = commentsLimit;
	}


	public Boolean getVisible() {
		return visible;
	}


	public void setVisible(Boolean visible) {
		this.visible = visible;
	}


	public Boolean getPublicEvaluation() {
		return publicEvaluation;
	}


	public void setPublicEvaluation(Boolean publicEvaluation) {
		this.publicEvaluation = publicEvaluation;
	}


	public Boolean getMandatory() {
		return mandatory;
	}


	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}


	public int getMandatoryPeriod() {
		return mandatory ? mandatoryPeriod : 0;
	}


	public void setMandatoryPeriod(Integer mandatoryPeriod) {
		this.mandatoryPeriod = mandatoryPeriod;
	}


	public List<UserEntity> getParticipants() {
		return participants;
	}


	public void setParticipants(List<UserEntity> participants) {
		this.participants = participants;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public List<CommunityEntity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<CommunityEntity> communities) {
		this.communities = communities;
	}
    	
}
