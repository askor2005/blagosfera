package ru.askor.blagosfera.domain.systemaccount;

import lombok.Data;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.notification.NotificationSender;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
@Data
public class SystemAccount implements DocumentCreator, NotificationSender, Serializable, RadomAccount {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String avatar;

    private String ikp;

    @Override
    public String getLink() {
        return "";
    }

    @Override
	public String getObjectType() {
		return "SYSTEM_ACCOUNT";
	}
}
