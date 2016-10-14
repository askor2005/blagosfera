package ru.radom.kabinet.model.skbcontur;

import java.util.Date;

/**
 * Created by vgusev on 24.06.2015.
 * Класс сущность - учредитель - ЮР лицо
 */
public class SkbConturULFounderUL {
    // Наименование организации
    private String name;
    // ИНН
    private String inn;
    // ОГРН
    private String ogrn;
    // Сумма в уставномкапитале
    private double share;
    // Доля в процентах
    private float sharePercent;
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

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public float getSharePercent() {
        return sharePercent;
    }

    public void setSharePercent(float sharePercent) {
        this.sharePercent = sharePercent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
