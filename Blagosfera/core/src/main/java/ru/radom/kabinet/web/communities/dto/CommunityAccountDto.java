package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.account.Account;
import ru.radom.kabinet.json.BigDecimalSimpleSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 13.05.2016.
 */
public class CommunityAccountDto {

    public Long accountId;

    @JsonSerialize(using = BigDecimalSimpleSerializer.class)
    public BigDecimal balance;

    public Long accountTypeId;

    public String accountTypeName;

    public CommunityAccountDto(Account account) {
        accountId = account.getId();
        balance = account.getBalance();
        accountTypeId = account.getType().getId();
        accountTypeName = account.getType().getName();
    }

    public static List<CommunityAccountDto> toDtoList(List<Account> accounts) {
        List<CommunityAccountDto> result = null;
        if (accounts != null) {
            result = new ArrayList<>();
            for (Account account : accounts) {
                result.add(new CommunityAccountDto(account));
            }
        }
        return result;
    }
}
