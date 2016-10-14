package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.user.User;

/**
 * Событие создания файла объединения
 * Created by vgusev on 02.03.2016.
 */
@Getter
public class CommunityCreateFileEvent extends CommunityEvent {

    /**
     * Название файла
     */
    private String fileName;

    /**
     * Ссылка на файл
     */
    private String fileLink;

    /**
     * Получатель файла
     */
    private User receiver;

    public CommunityCreateFileEvent(Object source, CommunityEventType type, Community community, String fileName, String fileLink, User receiver) {
        super(source, type, community);
        this.fileName = fileName;
        this.fileLink = fileLink;
        this.receiver = receiver;
    }
}
