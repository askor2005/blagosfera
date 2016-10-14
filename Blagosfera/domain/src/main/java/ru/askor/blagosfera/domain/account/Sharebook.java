package ru.askor.blagosfera.domain.account;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 14.03.2016.
 */
@Data
public class Sharebook implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private Account account;

    private Account bonusAccount;

    private Object sharebookOwner;

    private String sharebookOwnerType;
}
