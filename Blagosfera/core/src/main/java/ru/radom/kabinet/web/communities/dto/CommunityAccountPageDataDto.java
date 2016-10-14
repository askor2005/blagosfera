package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.account.Account;

import java.util.List;

/**
 *
 * Created by vgusev on 13.05.2016.
 */
@Data
public class CommunityAccountPageDataDto {

    private CommunityAnyPageDto community;

    private List<CommunityAccountDto> communityAccounts;

    public CommunityAccountPageDataDto(CommunityAnyPageDto community, List<Account> accounts) {
        setCommunity(community);
        setCommunityAccounts(CommunityAccountDto.toDtoList(accounts));
    }
}
