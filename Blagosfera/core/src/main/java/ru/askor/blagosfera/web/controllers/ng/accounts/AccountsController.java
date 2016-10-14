package ru.askor.blagosfera.web.controllers.ng.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.web.controllers.ng.accounts.dto.AccountsDto;
import ru.askor.blagosfera.web.controllers.ng.accounts.dto.TransactionsSearchDto;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.web.admin.dto.TransactionListDto;
import ru.radom.kabinet.web.admin.dto.TransactionPlainModel;

import java.util.List;

/**
 * Created by Maxim Nikitin on 07.03.2016.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/account.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public AccountsDto accountList() {
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccounts(accountService.getUserAccounts(SecurityUtils.getUser().getId()));
        accountsDto.setTotal(userDataService.getUserBalance(SecurityUtils.getUser()));
        accountsDto.setTotalTransactions(accountsDto.getAccounts().size() > 0 ? accountService.getUserAccountsCounts(SecurityUtils.getUser().getId()) : 0);
        return accountsDto;
    }

    @RequestMapping(value = "/transactions.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public TransactionListDto getTransactionsList(@RequestBody TransactionsSearchDto transactionsSearchDto) {
        return accountService.searchTransactions(SecurityUtils.getUser().getId(), transactionsSearchDto.getPage() - 1,
                transactionsSearchDto.getPerPage(), transactionsSearchDto.getFromDate(),
                transactionsSearchDto.getToDate(), transactionsSearchDto.getAccountTypeId(),
                transactionsSearchDto.getType(), transactionsSearchDto.getState());
    }
}
