package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.PaymentSystem;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.TransactionException;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class AccountController {

    @Autowired
    private AccountTypeDao accountTypeDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

    @Autowired
    private SharerDao sharerDao;

    @RequestMapping("/account/list.json")
    @ResponseBody
    public Map<String, Object> accountList() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("accounts", accountService.getUserAccounts(SecurityUtils.getUser().getId()));
        payload.put("total", StringUtils.formatMoney(sharerDao.getBalance(SecurityUtils.getUser().getId())));
        return payload;
    }

    @RequestMapping("/account/paymentsystems.json")
    @ResponseBody
    public List<PaymentSystem> paymentSystems() {
        return accountService.getPaymentSystems();
    }

    @RequestMapping("/account")
    public String showAccountPage(Model model, HttpServletRequest request, @RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate, @RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        if (toDate == null) {
            toDate = new Date();
        }
        if (fromDate == null) {
            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTime(toDate);
            startDateCalendar.add(Calendar.MONTH, -1);
            fromDate = startDateCalendar.getTime();
        }

        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        List<Account> accounts = new ArrayList<>();
        for (AccountTypeEntity type : accountTypeDao.getList(UserEntity.class)) {
			Account account = accountService.getUserAccount(SecurityUtils.getUser().getId(), type.getId());
            accounts.add(account);
        }

        model.addAttribute("accounts", accounts);

        model.addAttribute("currentPageTitle", "Баланс");
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Баланс", "/account"));
        return "account";
    }

    @RequestMapping("/account/transaction/{id}")
    public String showTransactionPage(Model model, @PathVariable("id") Long transactionId) {
        Transaction transaction = accountDataService.getTransaction(transactionId, SecurityUtils.getUser().getId());
        if (transaction == null) throw new AccessDeniedException("Доступ запрещен");

        model.addAttribute("transaction", transaction);
        model.addAttribute("currentPageTitle", "Транзакция " + transaction.getId());
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Баланс", "/account").add("Транзакция " + transaction.getId(), "/account/transaction/" + transaction.getId()));
        return "transaction";
    }

    @RequestMapping(value = "/account/transactions.json", method = RequestMethod.GET)
    @ResponseBody
    public List<Transaction> getTransactionsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "account_type_id", required = false) AccountTypeEntity accountType,
            @RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
            @RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        return accountService.getTransactions(SecurityUtils.getUser().getId(), page, perPage,fromDate ,toDate,accountType != null ? accountType.getId() : null );
    }

    @TokenProtected
    @RequestMapping(value = "/account/self_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doSelfMove(@RequestParam("from_account_type_id") AccountTypeEntity fromAccountType,
                             @RequestParam("to_account_type_id") AccountTypeEntity toAccountType,
                             @RequestParam("amount") BigDecimal amount,
                             @RequestParam(value = "comment", required = false) String comment) {
        try {
            accountService.createTransactionUser2User(SecurityUtils.getUser().getId(), fromAccountType.getId(), SecurityUtils.getUser().getId(), toAccountType.getId(), amount, comment);
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @TokenProtected
    @RequestMapping(value = "/account/move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doMove(@RequestParam("from_account_type_id") AccountTypeEntity fromAccountType,
                         @RequestParam("to_sharer_id") UserEntity toUserEntity,
                         @RequestParam("amount") BigDecimal amount,
                         @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (fromAccountType == null) throw new TransactionException("Не указан счёт для списания средств");
            if (toUserEntity == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            accountService.createTransactionUser2User(SecurityUtils.getUser().getId(), fromAccountType.getId(), toUserEntity.getId(), accountDataService.getPrimaryAccountType(Discriminators.SHARER).getId(), amount, comment);
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @TokenProtected
    @RequestMapping(value = "/account/sharer_community_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doSharerToCommunityMove(
            @RequestParam("from_account_type_id") AccountTypeEntity fromAccountType,
            @RequestParam("to_community_id") CommunityEntity community,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (community == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            accountService.createTransactionUser2Community(SecurityUtils.getUser().getId(), fromAccountType.getId(), community.getId(), amount, comment);
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @TokenProtected
    @RequestMapping(value = "/account/community_sharer_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doCommunityToSharerMove(@RequestParam("from_community_id") CommunityEntity fromCommunity,
                                          @RequestParam("to_sharer_id") UserEntity toUserEntity,
                                          @RequestParam("amount") BigDecimal amount,
                                          @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (fromCommunity == null) throw new TransactionException("Не указан отправитель для перевода денежных знаков");
            if (toUserEntity == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            accountService.createTransactionCommunity2User(SecurityUtils.getUser().getId(), fromCommunity.getId(), toUserEntity.getId(), amount, comment);
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @TokenProtected
    @RequestMapping(value = "/account/community_community_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doCommunityToCommunityMove(@RequestParam("from_community_id") CommunityEntity fromCommunity,
                                             @RequestParam("to_community_id") CommunityEntity toCommunity,
                                             @RequestParam("amount") BigDecimal amount,
                                             @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (fromCommunity == null) throw new TransactionException("Не указан отправитель для перевода денежных знаков");
            if (toCommunity == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            accountService.createTransactionCommunity2Community(SecurityUtils.getUser().getId(), fromCommunity.getId(), toCommunity.getId(), amount, comment);
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    // Перевод денег: пайщик -> книжка
    @TokenProtected
    @RequestMapping(value = "/account/sharer_book_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doSharerToBookMove(@RequestParam("from_account_type_id") AccountTypeEntity fromAccountType,
                                     @RequestParam("to_community_id") CommunityEntity toCommunity,
                                     @RequestParam("amount") BigDecimal amount,
                                     @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (toCommunity == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            UserEntity fromUserEntity = sharerDao.getById(SecurityUtils.getUser().getId());
            Account fromAccount = accountService.getUserAccount(SecurityUtils.getUser().getId(), fromAccountType.getId());
            SharebookEntity toSharebook = accountService.getSharebook(fromUserEntity, toCommunity.getId());
            Transaction transaction = accountService.createTransactionAccount2Sharebook(fromAccount.getId(), toSharebook.getId(), amount, comment);
            accountService.postTransaction(transaction.getId());
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    // Перевод денег: книжка -> пайщик
    @TokenProtected
    @RequestMapping(value = "/account/book_sharer_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doBookToSharerMove(@RequestParam("from_community_id") CommunityEntity fromCommunity,
                                     @RequestParam("amount") BigDecimal amount,
                                     @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (fromCommunity == null) throw new TransactionException("Не указан отправитель для перевода денежных знаков");

            UserEntity toUserEntity = sharerDao.getById(SecurityUtils.getUser().getId());
            SharebookEntity fromSharebook = accountService.getSharebook(toUserEntity, fromCommunity.getId());
            Account toAccount = accountService.getUserAccount(SecurityUtils.getUser().getId(), accountDataService.getPrimaryAccountType(Discriminators.SHARER).getId());
            Transaction transaction = accountService.createTransactionSharebook2Account(fromSharebook.getId(), toAccount.getId(), amount, comment);
            accountService.postTransaction(transaction.getId());
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    // Перевод денег: ПО -> книжка пайщика в этом ПО
    @TokenProtected
    @RequestMapping(value = "/account/community_book_move.json", method = RequestMethod.POST)
    @ResponseBody
    public String doCommunityToBookMove(@RequestParam("from_community_id") CommunityEntity fromCommunity,
                                        @RequestParam("to_sharer_id") UserEntity toUserEntity,
                                        @RequestParam("amount") BigDecimal amount,
                                        @RequestParam(value = "sender_comment", required = false) String comment) {
        try {
            if (fromCommunity == null) throw new TransactionException("Не указан отправитель для перевода денежных знаков");
            if (toUserEntity == null) throw new TransactionException("Не указан адресат для перевода денежных знаков");

            Account fromAccount = accountService.getCommunityAccount(fromCommunity.getId(), accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY).getId());
            SharebookEntity toSharebook = accountService.getSharebook(toUserEntity, fromCommunity.getId());
            Transaction transaction = accountService.createTransactionAccount2Sharebook(fromAccount.getId(), toSharebook.getId(), amount, comment);
            accountService.postTransaction(transaction.getId());
            return JsonUtils.getSuccessJson().toString();
        } catch (TransactionException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }
}
