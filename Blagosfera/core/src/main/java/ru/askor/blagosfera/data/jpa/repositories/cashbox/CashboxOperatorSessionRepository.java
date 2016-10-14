package ru.askor.blagosfera.data.jpa.repositories.cashbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.cashbox.CashboxOperatorSessionEntity;

import java.util.List;

public interface CashboxOperatorSessionRepository extends JpaRepository<CashboxOperatorSessionEntity, Long>, JpaSpecificationExecutor<CashboxOperatorSessionEntity> {

    CashboxOperatorSessionEntity findOneByWorkplaceIdAndActive(String workplaceId, boolean active);

    Page<CashboxOperatorSessionEntity> findByWorkplaceIdIn(List<String> workplaceGuids, Pageable pageable);
}
