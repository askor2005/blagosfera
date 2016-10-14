package ru.radom.kabinet.model.files;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "files")
public class File extends LongIdentifiable {

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "title", length = 255)
	private String title;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "protected_url", length = 1000)
	private String protectedUrl;

	@JoinColumn(name = "owner_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity owner;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "file")
	@OrderBy("date")
	private List<FileDownload> downloads;

	public File(String name, String title, UserEntity owner) {
		super();
		this.name = name;
		this.title = title;
		this.owner = owner;
	}

	public File() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProtectedUrl() {
		return protectedUrl;
	}

	public void setProtectedUrl(String protectedUrl) {
		this.protectedUrl = protectedUrl;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public void setOwner(UserEntity owner) {
		this.owner = owner;
	}

	public List<FileDownload> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<FileDownload> downloads) {
		this.downloads = downloads;
	}

}
