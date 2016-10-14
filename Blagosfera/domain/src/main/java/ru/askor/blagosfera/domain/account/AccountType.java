package ru.askor.blagosfera.domain.account;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 13.03.2016.
 */
@Data
public class AccountType implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String discriminator;
}
