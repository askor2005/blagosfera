package ru.radom.kabinet.services;

import ru.radom.kabinet.model.fields.FieldEntity;

import java.util.Date;
import java.util.List;

public class ProfileFilling {

	private int percent;
	private int filledPoints;
	private int totalPoints;

	private boolean avatarLoaded;
	private boolean allReqiredFilled;
	private List<FieldEntity> filledFields;
	private List<FieldEntity> notFilledFields;

	private int treshold;
	private Integer hoursBeforeArchivation;
	private Integer hoursBeforeDeletion;

	private boolean archived;
	private boolean deleted;

	private Date deletionDate;

	public ProfileFilling(int percent, int filledPoints, int totalPoints, boolean avatarLoaded, boolean allReqiredFilled, List<FieldEntity> filledFields, List<FieldEntity> notFilledFields, int treshold, Integer hoursBeforeArchivation, Integer hoursBeforeDeletion, boolean archived, boolean deleted, Date deletionDate) {
		super();
		this.percent = percent;
		this.filledPoints = filledPoints;
		this.totalPoints = totalPoints;
		this.avatarLoaded = avatarLoaded;
		this.allReqiredFilled = allReqiredFilled;
		this.filledFields = filledFields;
		this.notFilledFields = notFilledFields;

		this.treshold = treshold;
		this.hoursBeforeArchivation = hoursBeforeArchivation;
		this.hoursBeforeDeletion = hoursBeforeDeletion;

		this.archived = archived;
		this.deleted = deleted;

		this.deletionDate = deletionDate;
	}

	public int getPercent() {
		return percent;
	}

	public int getFilledPoints() {
		return filledPoints;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public boolean isAvatarLoaded() {
		return avatarLoaded;
	}

	public boolean isAllReqiredFilled() {
		return allReqiredFilled;
	}

	public List<FieldEntity> getFilledFields() {
		return filledFields;
	}

	public List<FieldEntity> getNotFilledFields() {
		return notFilledFields;
	}

	public int getTreshold() {
		return treshold;
	}

	public Integer getHoursBeforeArchivation() {
		return hoursBeforeArchivation;
	}

	public Integer getHoursBeforeDeletion() {
		return hoursBeforeDeletion;
	}

	public boolean isArchived() {
		return archived;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

}
