package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = CyberbrainObject.TABLE_NAME)
public class CyberbrainObject extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_objects";

    public static class Columns {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    @Column(name = Columns.NAME, nullable = false)
    private String name;

    @Column(name = Columns.DESCRIPTION, nullable = false)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}