package ru.askor.blagosfera.domain.document;

/**
 * Created by vgusev on 20.07.2015.
 * Права доступа к документу у участника документа.
 */
public enum ParticipantRight {

    VIEW("Просмотр документа"), FILL_USER_FIELDS("Заполнение пользовательских полей"), SIGN("Подписание документа");

    private String description;

    ParticipantRight(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
