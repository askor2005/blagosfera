package ru.radom.kabinet.model.log;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by ebelyaev on 05.08.2015.
 */
@MappedSuperclass
public abstract class VerifiableLog extends LongIdentifiable {

    @Column(length = 1000)
    private String hash;

    public abstract String getStringFromFields();

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
