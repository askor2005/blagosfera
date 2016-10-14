package ru.askor.blagosfera.data.jpa.repositories.contacts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.ContactEntity;

import java.util.List;

/**
 * Created by vtarasenko on 08.04.2016.
 */
public interface ContactsRepository extends JpaRepository<ContactEntity,Long>, JpaSpecificationExecutor<ContactEntity> {

    @Query("select c from ContactEntity c where c.user.id = :userId and c.other.id = :otherId")
    ContactEntity findByUserAndOther (@Param("userId") Long userId,@Param("otherId") Long otherId);

    /*@Query(value = "select * from ContactEntity c where c.contactsGroup.id = :groupId",nativeQuery = true)
    List<ContactEntity> findByGroupId(@Param("groupId") Long groupId);*/
}
