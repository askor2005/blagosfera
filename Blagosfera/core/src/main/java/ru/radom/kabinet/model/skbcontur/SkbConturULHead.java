package ru.radom.kabinet.model.skbcontur;

import java.util.Date;

/**
 * Created by vgusev on 24.06.2015.
 * Класс - сущность Глава организации
 */
public class SkbConturULHead {

    // ФИО
    private String fio;
    // ИНН физ лица
    private String inn;
    // Наименование должности главы
    private String post;
    // Дата внесения данных в ЕГРЮЛ
    private Date date;
    // Примерное количество организаций, в которых данное физ лицо находится в руководстве
    private int fioMentionsCountEstimate;

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFioMentionsCountEstimate() {
        return fioMentionsCountEstimate;
    }

    public void setFioMentionsCountEstimate(int fioMentionsCountEstimate) {
        this.fioMentionsCountEstimate = fioMentionsCountEstimate;
    }
}
