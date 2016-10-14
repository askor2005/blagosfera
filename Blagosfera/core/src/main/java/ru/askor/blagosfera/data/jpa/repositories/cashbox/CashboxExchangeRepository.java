package ru.askor.blagosfera.data.jpa.repositories.cashbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.cashbox.CashboxExchangeEntity;

import java.util.List;

public interface CashboxExchangeRepository extends JpaRepository<CashboxExchangeEntity, Long> {

    CashboxExchangeEntity findOneByRequestIdAndOperatorSession_Id(String requestId, Long sessionId);

    List<CashboxExchangeEntity> findByOperatorSession_IdAndAcceptedDateIsNotNull(Long sessionId);

    Page<CashboxExchangeEntity> findByOperatorSession_IdAndAcceptedDateIsNotNull(Long sessionId, Pageable pageable);

    Page<CashboxExchangeEntity> findByOperatorSession_Id(Long sessionId, Pageable pageable);
}
