package ru.askor.blagosfera.data.jpa.repositories.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionDetailEntity;

import java.util.List;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetailEntity, Long> , JpaSpecificationExecutor<TransactionDetailEntity> {

    List<TransactionDetailEntity> findAllByAccount_Id(Long accountId);
}
