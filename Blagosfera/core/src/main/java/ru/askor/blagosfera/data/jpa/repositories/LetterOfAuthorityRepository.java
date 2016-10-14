package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;

import java.util.List;

public interface LetterOfAuthorityRepository extends JpaRepository<LetterOfAuthorityEntity, Long> {
    // Найти все доверенности, выданные ownerId
    List<LetterOfAuthorityEntity> findByOwnerIdAndDelegateSearchStringLikeIgnoreCase(Long ownerId, String searchString, Pageable pageable);
    // Найти все доверенности, которые выдали delegateId
    List<LetterOfAuthorityEntity> findByDelegateIdAndOwnerSearchStringLikeIgnoreCase(Long delegateId, String searchString, Pageable pageable);
    // Найти доверенности по делегату и ключу роли
    List<LetterOfAuthorityEntity> findByDelegateIdAndLetterOfAuthorityRoleKey(Long delegateId, String roleKey);
}
