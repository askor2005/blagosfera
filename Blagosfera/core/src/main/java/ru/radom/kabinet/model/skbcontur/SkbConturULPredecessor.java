package ru.radom.kabinet.model.skbcontur;

import java.util.Date;

/**
 * Created by vgusev on 24.06.2015.
 * Класс - сущность предшественники ЮЛ.
 */
public class SkbConturULPredecessor {
    // 	string	Наименование организации
    private String name;
    // 	string	ИНН
    private String inn;
    // 	string	ОГРН
    private String ogrn;
    //  string	Дата внесения записи в ЕГРЮЛ
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
