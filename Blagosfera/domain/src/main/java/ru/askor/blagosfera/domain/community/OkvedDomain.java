package ru.askor.blagosfera.domain.community;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
@Data
public class OkvedDomain implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String shortName;

    private String longName;
}
