package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.UserEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findOneByEmail(String email);

    UserEntity findOneByEmailAndIdNot(String email, Long userId);

    UserEntity findOneByIkp(String ikp);

    UserEntity findFirstByPasswordRecoveryCode(String passwordRecoveryCode);

    // TODO удалить после миграции на новый PasswordEncoder
    @Modifying
    @Query("UPDATE UserEntity u SET u.bcryptPassword = :bcryptPassword WHERE u.password = :password")
    void updateBCryptPassword(@Param("password") String password, @Param("bcryptPassword") String bcryptPassword);

    /**
     * Поиск по флагу удален\не удалён
     *
     * @param isDeleted
     * @return
     */
    List<UserEntity> findByDeletedOrderBySearchStringAsc(boolean isDeleted);

    @Query(value = "select COALESCE(sum(a.total_balance),0) from accounts a where a.owner_id = :userId and a.owner_type='SHARER'", nativeQuery = true)
    BigDecimal getUserBalance(@Param("userId") Long userId);

    @Query(value = "select a.id from accounts a where a.owner_id = :userId and a.owner_type='SHARER'", nativeQuery = true)
    Long[] getUserAccountIds(@Param("userId") Long userId);

    @Query(value = "select a.id from accounts a inner join book_accounts book on a.owner_id = book.id and a.owner_type='SHARER_BOOK' and book.owner_id = :userId", nativeQuery = true)
    Long[] getUserSharebookAccountIds(@Param("userId") Long userId);

    @Query(value = "select distinct object_id from registration_requests where object_type='SHARER' and registrator_id = :registratorId and status = 0",nativeQuery = true)
    Long[] getUsersIdsWithRegistrationRequestsToRegistrator(@Param("registratorId") Long registratorId);

    List<UserEntity> findAllByDeletedFalse();
    @Query(value = "select distinct object_id from registration_requests where object_type='SHARER'  and status = 0",nativeQuery = true)
    Long[] getUsersIdsWithRegistrationRequests();
}
