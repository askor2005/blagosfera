package ru.askor.blagosfera.data.jpa.repositories.account;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;

public interface AccountTypeRepository extends JpaRepository<AccountTypeEntity, Long> {

    AccountTypeEntity findFirstByOwnerDiscriminatorOrderByPositionAsc(String ownerDiscriminator);
}
