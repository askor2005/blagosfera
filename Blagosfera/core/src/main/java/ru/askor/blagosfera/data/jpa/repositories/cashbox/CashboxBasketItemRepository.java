package ru.askor.blagosfera.data.jpa.repositories.cashbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.cashbox.CashboxBasketItemEntity;

import java.util.List;

public interface CashboxBasketItemRepository extends JpaRepository<CashboxBasketItemEntity, Long> {

    Page<CashboxBasketItemEntity> findByTotals_ExchangeOperation_IdIn(List<Long> exchangeIds, Pageable pageable);
}
