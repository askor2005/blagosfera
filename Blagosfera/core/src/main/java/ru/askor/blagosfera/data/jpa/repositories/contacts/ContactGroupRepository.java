package ru.askor.blagosfera.data.jpa.repositories.contacts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import java.util.List;

/**
 * Created by vtarasenko on 08.04.2016.
 */
public interface ContactGroupRepository extends JpaRepository<ContactsGroupEntity,Long> {
    List<ContactsGroupEntity> findByUser(UserEntity user);
    @Query("select g from ContactsGroupEntity g where g.id = :id and g.user.id = :userId")
    ContactsGroupEntity findByIdAndUserId(@Param("id") Long id,@Param("userId") Long userId);
    @Query("select case when count(g) > 0 then true else false end from ContactsGroupEntity g where g.user.id = :userId and g.name = :name")
    boolean nameExists(@Param("userId") Long userId,@Param("name") String name);
    @Query("select case when count(g) > 0 then true else false end from ContactsGroupEntity g where g.user.id = :userId and g.name = :name and g.id != :id")
    boolean nameExists(@Param("userId") Long userId,@Param("name") String name,@Param("id") Long id);
}
