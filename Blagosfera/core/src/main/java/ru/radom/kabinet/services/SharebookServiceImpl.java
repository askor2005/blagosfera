package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.repositories.SharebookRepository;

import java.math.BigDecimal;
import java.util.List;

@Transactional
@Service("shareBookService")
public class SharebookServiceImpl implements SharebookService {

    @Autowired
    private SharebookRepository sharebookRepository;

    public SharebookServiceImpl() {
    }

    @Override
    public BigDecimal getCommunitySharebooksTotalBalance(Long communityId) {
        BigDecimal balance = BigDecimal.ZERO;
        List<SharebookEntity> sharebooks = sharebookRepository.findAllByCommunity_Id(communityId);

        for (SharebookEntity sharebook : sharebooks) {
            balance = balance.add(sharebook.getBalance());
        }

        return balance;
    }
}
