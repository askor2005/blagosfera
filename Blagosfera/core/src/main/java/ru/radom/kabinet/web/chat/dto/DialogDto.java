package ru.radom.kabinet.web.chat.dto;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;

import java.util.*;

/**
 *
 * Created by vgusev on 23.11.2015.
 */
@Getter
public class DialogDto {

    private Long id;
    private String name;
    private Long companionId;
    private String companionName;
    private String companionLink;
    private String companionAvatar;
    private int countSharers;

    private String lastMessageText;
    private Long lastMessageSenderId;
    private String lastMessageSenderShortName;
    private Date lastMessageDate;

    private int countUnreadMessages;

    private Set<DialogUserDto> users;

    private boolean isClosed;

    public DialogDto(Long id, String name, Long companionId, String companionName, String companionLink,
                     String companionAvatar, int countSharers, String lastMessageText, Long lastMessageSenderId,
                     String lastMessageSenderShortName, Date lastMessageDate, int countUnreadMessages, Set<DialogUserDto> users, boolean isClosed) {
        this.id = id;
        this.name = name;
        this.companionId = companionId;
        this.companionName = companionName;
        this.companionLink = companionLink;
        this.companionAvatar = companionAvatar;
        this.countSharers = countSharers;
        this.lastMessageText = lastMessageText;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageSenderShortName = lastMessageSenderShortName;
        this.lastMessageDate = lastMessageDate;
        this.countUnreadMessages = countUnreadMessages;
        this.users = users;
        this.isClosed = isClosed;
    }

    public static List<DialogDto> toDtoList(List<DialogEntity> dialogs, User currentUser, Map<Long, Boolean> onlineMap) {
        List<DialogDto> result = new ArrayList<>();
        for (DialogEntity dialog : dialogs) {
            result.add(toDto(dialog, currentUser, onlineMap));
        }
        return result;
    }

    public static DialogDto toDto(DialogEntity dialog, User currentUser, Map<Long, Boolean> onlineMap) {
        String name = dialog.getName();
        UserEntity findCompanion = null;
        if (dialog.getUsers() != null && dialog.getUsers().size() == 2) {
            for (UserEntity companion : dialog.getUsers()) {
                if (!companion.getId().equals(currentUser.getId())) {
                    findCompanion = companion;
                    name = companion.getMediumName();
                    break;
                }
            }
        } else if (dialog.getLastMessage() != null && dialog.getLastMessage().getSender() != null) {
            findCompanion = dialog.getLastMessage().getSender();
        } else if (dialog.getUsers() != null && dialog.getUsers().size() > 0) {
            for (UserEntity companion : dialog.getUsers()) {
                if (!companion.getId().equals(currentUser.getId())) {
                    findCompanion = companion;
                    break;
                }
            }
        }
        Long companionId = null;
        String companionName = null;
        String companionLink = null;
        String companionAvatar = null;

        if (findCompanion != null) {
            companionId = findCompanion.getId();
            companionName = findCompanion.getMediumName();
            companionLink = findCompanion.getLink();
            companionAvatar = findCompanion.getAvatar();
        }

        int countSharers = 0;
        if (dialog.getUsers() != null) {
            countSharers = dialog.getUsers().size();
        }

        String lastMessageText = null;
        Long lastMessageSenderId = null;
        String lastMessageSenderShortName = null;
        Date lastMessageDate = null;

        if (dialog.getLastMessage() != null) {
            lastMessageText = dialog.getLastMessage().getText();
            lastMessageSenderId = dialog.getLastMessage().getSender().getId();
            lastMessageSenderShortName = dialog.getLastMessage().getSender().getShortName();
            lastMessageDate = dialog.getLastMessage().getDate();
        }

        Set<DialogUserDto> dialogUsers = new HashSet<>();
        for (UserEntity userEntity : dialog.getUsers()) {
            dialogUsers.add(new DialogUserDto(userEntity.toDomain(), BooleanUtils.toBooleanDefaultIfNull(onlineMap.get(userEntity.getId()), false)));
        }

        return new DialogDto(dialog.getId(), name, companionId, companionName, companionLink, companionAvatar,
                countSharers, lastMessageText, lastMessageSenderId, lastMessageSenderShortName, lastMessageDate,
                dialog.getCountUnreadMessages(), dialogUsers, dialog.isClosed());
    }
}
