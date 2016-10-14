package ru.radom.kabinet.model.cyberbrain;

public enum UserTaskLifecycle {
    NEW(0, "Новая"),
    SOLVED(1, "Решена"),
    REJECTED(2, "Отклонена"),
    CONFIRMED(3, "Подтверждена");

    private final Integer index;
    private final String description;

    UserTaskLifecycle(Integer index, String description) {
        this.index = index;
        this.description = description;
    }

    public Integer getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }
}