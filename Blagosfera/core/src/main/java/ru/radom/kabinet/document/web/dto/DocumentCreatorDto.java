package ru.radom.kabinet.document.web.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.askor.blagosfera.domain.user.User;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentCreatorDto {

    private String ikp;

    private String avatar;

    private String name;

    private boolean isUser;

    public DocumentCreatorDto(DocumentCreator creator) {
        if (creator instanceof User) {
            User creatorUser = (User) creator;
            setIkp(creatorUser.getIkp());
            setAvatar(creatorUser.getAvatar());
            setName(creatorUser.getName());
            setUser(true);
        } else if (creator instanceof SystemAccount) {
            SystemAccount systemAccountCreator = (SystemAccount) creator;
            setAvatar(systemAccountCreator.getAvatar());
            setName(systemAccountCreator.getName());
            setUser(false);
        }
    }
}
