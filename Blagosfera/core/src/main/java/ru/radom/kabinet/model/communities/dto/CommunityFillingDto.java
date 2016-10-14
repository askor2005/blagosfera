package ru.radom.kabinet.model.communities.dto;

import ru.askor.blagosfera.domain.field.Field;

import java.util.List;

/**
 * Обёртка для данных по заполнению полеё юр лица
 * Created by vgusev on 03.11.2015.
 */
public class CommunityFillingDto {

    private int percent; // Процент заполнения
    private int filledPoints; // Количество баллов заполнения у полей
    private int totalPoints; // Общее количество баллов при 100% заполнении

    private boolean avatarLoaded; // Загружен или нет аватар объединения
    private List<Field> requiredFields; // Поля, которые необходимо заполнить обязательно
    private List<Field> filledFields; // Заполненные поля
    private List<Field> notFilledFields; // Незаполненные поля

    private int threshold; // Порог заполнения объединения при котором можно подать заявку на сертификацию

    public CommunityFillingDto(int percent, int filledPoints, int totalPoints, boolean avatarLoaded, List<Field> requiredFields, List<Field> filledFields, List<Field> notFilledFields, int threshold) {
        this.percent = percent;
        this.filledPoints = filledPoints;
        this.totalPoints = totalPoints;
        this.avatarLoaded = avatarLoaded;
        this.requiredFields = requiredFields;
        this.filledFields = filledFields;
        this.notFilledFields = notFilledFields;
        this.threshold = threshold;
    }

    public int getPercent() {
        return percent;
    }

    public int getFilledPoints() {
        return filledPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public boolean isAvatarLoaded() {
        return avatarLoaded;
    }

    public List<Field> getRequiredFields() {
        return requiredFields;
    }

    public List<Field> getFilledFields() {
        return filledFields;
    }

    public List<Field> getNotFilledFields() {
        return notFilledFields;
    }

    public int getThreshold() {
        return threshold;
    }
}
