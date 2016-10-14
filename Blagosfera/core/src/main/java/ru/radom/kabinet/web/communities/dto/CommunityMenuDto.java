package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.community.CommunitySectionDomain;
import ru.radom.kabinet.utils.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 11.03.2016.
 */
@Data
public class CommunityMenuDto {

    private String sharerBookAccountBalance;

    private String communityBookAccountsBalance;

    private List<CommunityMenuAccountDto> communityAccounts;

    private List<CommunitySectionDto> communityRootSections;

    private boolean isConsumerSociety;

    public CommunityMenuDto(
            List<CommunitySectionDomain> communitySections, List<Long> visibleSections, List<Account> accounts,
            BigDecimal sharerBookAccountBalanceBigDecimal, BigDecimal communityBookAccountsBalanceBigDecimal, boolean isConsumerSociety) {
        if (sharerBookAccountBalanceBigDecimal != null) {
            String sharerBookAccountBalance = StringUtils.formatMoney(sharerBookAccountBalanceBigDecimal);
            this.setSharerBookAccountBalance(sharerBookAccountBalance);
        }
        if (communityBookAccountsBalanceBigDecimal != null) {
            String communityBookAccountsBalance = StringUtils.formatMoney(communityBookAccountsBalanceBigDecimal);
            this.setCommunityBookAccountsBalance(communityBookAccountsBalance);
        }

        this.setCommunityRootSections(CommunitySectionDto.toDtoList(communitySections, visibleSections));
        if (accounts != null) {
            List<CommunityMenuAccountDto> communityAccounts = new ArrayList<>();
            for (Account account : accounts) {
                communityAccounts.add(new CommunityMenuAccountDto(account));
            }
            this.setCommunityAccounts(communityAccounts);
        }
        this.setConsumerSociety(isConsumerSociety);
    }
}
