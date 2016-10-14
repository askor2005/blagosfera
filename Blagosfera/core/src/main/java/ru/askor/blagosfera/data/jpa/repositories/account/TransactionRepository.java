package ru.askor.blagosfera.data.jpa.repositories.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.domain.account.TransactionState;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> , JpaSpecificationExecutor<TransactionEntity> {

    TransactionEntity findOneByDocumentFolder_Id(Long folderId);

    @Query("select t from TransactionEntity t where t.parameters[?1] = ?2")
    TransactionEntity findByParameterValue(String key, String value);

    @Query("select t from TransactionEntity t where (t.parameters[?1] = ?2) and (t.state = ?3)")
    List<TransactionEntity> findByParameterValueAndState(String key, String value, TransactionState state);
}
