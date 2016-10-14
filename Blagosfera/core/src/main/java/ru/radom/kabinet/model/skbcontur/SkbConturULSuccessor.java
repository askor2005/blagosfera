package ru.radom.kabinet.model.skbcontur;

import java.util.Date;

/**
 * Created by vgusev on 24.06.2015.
 * Класс сущность - приемник организации
 */
public class SkbConturULSuccessor {

    private String name;
    private String inn;
    // ОГРН
    private String ogrn;
    // Дата внесения записи в ЕГРЮЛ
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
