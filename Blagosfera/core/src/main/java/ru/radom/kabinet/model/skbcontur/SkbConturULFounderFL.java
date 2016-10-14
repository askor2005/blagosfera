package ru.radom.kabinet.model.skbcontur;

import java.util.Date;

/**
 * Created by vgusev on 24.06.2015.
 * Физ лицо - учредитель ЮЛ
 */
public class SkbConturULFounderFL {
    //ФИО
    private String fio;
    // ИННфиз.лица
    private String inn;
    // Сумма в уставномкапитале
    private double share;
    // Дола в процентах
    private float sharePercent;
    // Примерное количество компаний, где данное ФИО упоминается в качестве руководителя или учредителя
    private int fioMentionsCountEstimate;
    // Дата внесения записи в ЕГРЮЛ
    private Date date;

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

    public int getFioMentionsCountEstimate() {
        return fioMentionsCountEstimate;
    }

    public void setFioMentionsCountEstimate(int fioMentionsCountEstimate) {
        this.fioMentionsCountEstimate = fioMentionsCountEstimate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
