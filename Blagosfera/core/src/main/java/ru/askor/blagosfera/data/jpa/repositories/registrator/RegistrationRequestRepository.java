package ru.askor.blagosfera.data.jpa.repositories.registrator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;

/**
 * Created by vtarasenko on 13.04.2016.
 */
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest,Long>, JpaSpecificationExecutor<UserEntity> {
    @Query(value = "select case when count(id) > 0 then true  else  false end from registration_requests where object_id = :userId and object_type = 'SHARER' and status=0",nativeQuery = true)
    public boolean existsForUser(@Param("userId") Long userId);
    @Query(value = "select case when count(id) > 0 then true  else false end from registration_requests where object_id = :userId and object_type = 'SHARER' and registrator_id = :registratorId and status=0",nativeQuery = true)
    public boolean existsForUserAndRegistrator(@Param("userId") Long userId, @Param("registratorId") Long registratorId);
}
