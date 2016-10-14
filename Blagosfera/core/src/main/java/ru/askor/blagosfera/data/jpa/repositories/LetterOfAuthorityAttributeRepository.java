package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityAttributeEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 08.10.2015.
 */
public interface LetterOfAuthorityAttributeRepository extends JpaRepository<LetterOfAuthorityAttributeEntity, Long> {

    List<LetterOfAuthorityAttributeEntity> findByLetterOfAuthority_IdAndNameLikeIgnoreCase(Long letterOfAuthorityId, String name, Pageable pageable);

    @Query("select count(attr) from LetterOfAuthorityAttributeEntity attr where attr.letterOfAuthority.id = :letterOfAuthorityId and lower(attr.name) like lower(:name)")
    int countByLetterOfAuthorityIdAndNameLikeIgnoreCase(@Param("letterOfAuthorityId") Long letterOfAuthorityId, @Param("name") String name);
}
