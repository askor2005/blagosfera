package ru.askor.blagosfera.data.jpa.repositories.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query(value = "SELECT a FROM AccountEntity a WHERE (owner_id = :ownerId) AND (owner_type = 'SHARER') ORDER BY a.type.position")
    List<AccountEntity> getUserAccounts(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT a FROM AccountEntity a WHERE (owner_id = :ownerId) AND (owner_type = 'SHARER') AND (a.type.id = :typeId) ORDER BY a.type.position")
    AccountEntity getUserAccount(@Param("ownerId") Long ownerId, @Param("typeId") Long typeId);

    @Query(value = "SELECT a FROM AccountEntity a WHERE (owner_id = :ownerId) AND (owner_type = 'COMMUNITY') ORDER BY a.type.position")
    List<AccountEntity> getCommunityAccounts(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT a FROM AccountEntity a WHERE (owner_id = :ownerId) AND (owner_type = 'COMMUNITY') AND (a.type.id = :typeId) ORDER BY a.type.position")
    AccountEntity getCommunityAccount(@Param("ownerId") Long ownerId, @Param("typeId") Long typeId);
}
