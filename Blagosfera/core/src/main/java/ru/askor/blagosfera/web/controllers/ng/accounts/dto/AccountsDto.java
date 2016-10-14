package ru.askor.blagosfera.web.controllers.ng.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.account.Account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 25.04.2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountsDto {
    private BigDecimal total;
    private List<Account> accounts;
    private Long totalTransactions;
}
