package ru.askor.blagosfera.domain.registrator;

import lombok.Data;
import org.springframework.data.domain.Sort;

/**
 * Created by vtarasenko on 10.06.2016.
 */

public class RegistratorSort {
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    private String property;
    private String direction;
}

