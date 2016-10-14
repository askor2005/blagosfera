package ru.askor.blagosfera.domain.user;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 17.08.2016.
 */
@Data
public class UserRole implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;
}
