package ru.radom.kabinet.web.chat.dto;

/**
 * Обёртка для ответа по запросу контактов с непрочитанными сообщениями
 * Created by vgusev on 25.11.2015.
 */
public class ContactUnreadMessagesDto {

    private Long sharerId;
    private int countMessages;

    public ContactUnreadMessagesDto(Long sharerId, int countMessages) {
        this.sharerId = sharerId;
        this.countMessages = countMessages;
    }

    public Long getSharerId() {
        return sharerId;
    }

    public int getCountMessages() {
        return countMessages;
    }
}
