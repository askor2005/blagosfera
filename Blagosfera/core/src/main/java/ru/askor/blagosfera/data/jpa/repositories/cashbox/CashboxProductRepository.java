package ru.askor.blagosfera.data.jpa.repositories.cashbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.cashbox.CashboxProductEntity;

public interface CashboxProductRepository extends JpaRepository<CashboxProductEntity, Long> {

    @Modifying
    @Query("delete from CashboxProductEntity p where p.shop = :communityId")
    void deleteByShop(@Param("communityId") Long communityId);

    CashboxProductEntity findOneByCodeAndShop(String code, Long shop);

    Page<CashboxProductEntity> findByShop(Long communityId, Pageable pageable);

    CashboxProductEntity findByShopAndCode(Long comunityId, String code);
}
