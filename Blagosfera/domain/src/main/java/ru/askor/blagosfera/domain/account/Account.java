package ru.askor.blagosfera.domain.account;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * Created by vgusev on 13.03.2016.
 */
@Data
public class Account implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private AccountType type;

    private BigDecimal balance;

    private Object owner;

    private String ownerType;

}
