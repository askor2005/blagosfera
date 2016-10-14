package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityRoleEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 21.09.2015.
 */
public interface LetterOfAuthorityRoleRepository extends JpaRepository<LetterOfAuthorityRoleEntity, Long> {

    List<LetterOfAuthorityRoleEntity> findByScopeType(String scopeType);
    LetterOfAuthorityRoleEntity getByKey(String key);
    List<LetterOfAuthorityRoleEntity> findByNameContaining(String nameSearchString, Pageable pageable);
    int countByNameContaining(String nameSearchString);

}
