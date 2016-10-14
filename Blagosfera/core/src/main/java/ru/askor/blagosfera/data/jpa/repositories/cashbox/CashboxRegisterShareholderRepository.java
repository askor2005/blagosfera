package ru.askor.blagosfera.data.jpa.repositories.cashbox;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.cashbox.CashboxRegisterShareholderEntity;

public interface CashboxRegisterShareholderRepository extends JpaRepository<CashboxRegisterShareholderEntity, Long> {

    CashboxRegisterShareholderEntity findFirstBySharerIkpAndCommunityIdOrderByRequestCreatedDateDesc(String sharerIkp, Long communityId);
    CashboxRegisterShareholderEntity findOneBySharerIkpAndCommunityIdAndRequestAcceptedDateIsNull(String sharerIkp, Long communityId);
}
