package ru.radom.kabinet.module.blagosfera.bp.model;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Otts Alexey on 02.11.2015.<br/>
 * Модель бизнесс процесса в формате json
 */
@Entity
@Table(name = "bp_model")
public class BPModel extends LongIdentifiable {

    /**
     * JSON строка
     */
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
