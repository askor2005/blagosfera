package ru.radom.kabinet.model.applications;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "sharer_applications")
public class SharerApplication extends LongIdentifiable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity userEntity;

	@JoinColumn(name = "application_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Application application;

	@Column(nullable = false)
	private boolean installed;

	@Column(name = "show_in_menu", nullable = false)
	private boolean showInMenu;

	public SharerApplication(UserEntity userEntity, Application application) {
		super();
		this.userEntity = userEntity;
		this.application = application;
		this.installed = false;
		this.showInMenu = true;
	}

	public SharerApplication() {
		super();
	}

	public UserEntity getSharer() {
		return userEntity;
	}

	public void setSharer(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public boolean isShowInMenu() {
		return showInMenu;
	}

	public void setShowInMenu(boolean showInMenu) {
		this.showInMenu = showInMenu;
	}

}
