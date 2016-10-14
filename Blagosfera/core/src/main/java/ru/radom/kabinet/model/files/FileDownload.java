package ru.radom.kabinet.model.files;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "file_downloads")
public class FileDownload extends LongIdentifiable {

	@JoinColumn(name = "file_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private File file;

	@JoinColumn(name = "downloader_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity downloader;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public FileDownload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileDownload(File file, UserEntity downloader, Date date) {
		super();
		this.file = file;
		this.downloader = downloader;
		this.date = date;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public UserEntity getDownloader() {
		return downloader;
	}

	public void setDownloader(UserEntity downloader) {
		this.downloader = downloader;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
