package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.account.Account;
import ru.radom.kabinet.utils.StringUtils;

/**
 *
 * Created by vgusev on 11.03.2016.
 */
@Data
public class CommunityMenuAccountDto {

    private Long id;

    private Long typeId;

    private String typeName;

    private String balance;

    public CommunityMenuAccountDto(Account account) {
        this.setId(account.getId());
        this.setTypeId(account.getType() != null ? account.getType().getId() : null);
        this.setTypeName(account.getType() != null ? account.getType().getName() : null);
        this.setBalance(StringUtils.formatMoney(account.getBalance()));
    }
}
