package ru.radom.kabinet.web.admin.dto;

/**
 * Данные для построения таблицы с сессиями на клиенте
 * Created by vgusev on 04.12.2015.
 */
public class ActiveSessionDto {

    public ActiveSessionDto(String visibleSessionId) {
        this.visibleSessionId = visibleSessionId;
    }

    // Форматированное значение ИД сессии
    private String visibleSessionId;

    public String getVisibleSessionId() {
        return visibleSessionId;
    }

    public void setVisibleSessionId(String visibleSessionId) {
        this.visibleSessionId = visibleSessionId;
    }
}
