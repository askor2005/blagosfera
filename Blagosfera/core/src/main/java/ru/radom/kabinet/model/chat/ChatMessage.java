package ru.radom.kabinet.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends LongIdentifiable {

	@JoinColumn(name = "sender_id", nullable = false)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private UserEntity sender;

    @JsonIgnore
	@JoinColumn(name = "dialog_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private DialogEntity dialog;

	@Column(nullable = false, length = 50000)
	private String text;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = TimeStampDateSerializer.class)
	private Date date;

	@Column(nullable = true, name = "edit_date")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = TimeStampDateSerializer.class)
	private Date editDate;

	@Column(nullable = false, name = "edit_count")
	private int editCount;

	@Column(nullable = true)
	private boolean deleted;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "chatMessage")
	private Set<ChatMessageReceiver> chatMessageReceivers = new HashSet<>();

    @Transient
    private String uuid;

	@Transient
	private boolean allowEdit = false;

	// Флаг - сообщение является файлом. Размер и название файла хранится в тексте сообщения
	@Column(name = "file_message", nullable = true)
	private Boolean fileMessage;

	// Состояние загружаемого файла
	@Column(name = "file_message_state", nullable = true)
	private FileChatMessageState fileChatMessageState;

	// Размер загружаемого файла
	@Column(name = "file_size", nullable = true)
	private Long fileSize;

	// Процент загрузки файла
	@Column(name = "file_loaded_percent", nullable = true)
	private Integer fileLoadedPercent;

	// Дата обновления процента загрузки файла
	@Column(nullable = true, name = "file_date_update_percent")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fileDateUpdatePercent;

    public ChatMessage() {}

	public ChatMessage(UserEntity sender, DialogEntity dialog, String text) {
		this.sender = sender;
		this.dialog = dialog;
		this.text = text;
		this.date = new Date();
		this.editCount = 0;
	}

	public ChatMessage(UserEntity sender, DialogEntity dialog, String fileName, Long fileSize) {
		this.sender = sender;
		this.dialog = dialog;
		this.text = fileName;
		this.fileSize = fileSize;
		this.fileLoadedPercent = 0;
		this.fileDateUpdatePercent = new Date();
		this.fileMessage = true;
		this.fileChatMessageState = FileChatMessageState.UPLOAD_IN_PROCESS;
		this.date = new Date();
		this.editCount = 0;
	}

	public UserEntity getSender() {
		return sender;
	}

	public void setSender(UserEntity sender) {
		this.sender = sender;
	}

    public DialogEntity getDialog() {
        return dialog;
    }

    public void setDialog(DialogEntity dialog) {
        this.dialog = dialog;
    }

    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public int getEditCount() {
		return editCount;
	}

	public void setEditCount(int editCount) {
		this.editCount = editCount;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<ChatMessageReceiver> getChatMessageReceivers() {
		return chatMessageReceivers;
	}

	public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	public boolean isAllowEdit() {
		return allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	public Boolean getFileMessage() {
		return fileMessage;
	}

	public void setFileMessage(Boolean fileMessage) {
		this.fileMessage = fileMessage;
	}

	public Integer getFileLoadedPercent() {
		return fileLoadedPercent;
	}

	public void setFileLoadedPercent(Integer fileLoadedPercent) {
		this.fileLoadedPercent = fileLoadedPercent;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public FileChatMessageState getFileChatMessageState() {
		return fileChatMessageState;
	}

	public void setFileChatMessageState(FileChatMessageState fileChatMessageState) {
		this.fileChatMessageState = fileChatMessageState;
	}

	public Date getFileDateUpdatePercent() {
		return fileDateUpdatePercent;
	}

	public void setFileDateUpdatePercent(Date fileDateUpdatePercent) {
		this.fileDateUpdatePercent = fileDateUpdatePercent;
	}

	@Transient
    public Long getDialogId() {
        return getDialog() != null ? getDialog().getId() : null;
    }

    @Transient
    public String getDialogName() {
        return getDialog() != null ? getDialog().getName() : null;
    }
}
