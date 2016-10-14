package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.document.model.DocumentEntity;

import java.util.Date;
import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query("select d.code from DocumentEntity d where d.id = :id")
    String getCodeById(@Param("id") Long id);

    @Query("select d.createDate from DocumentEntity d where d.id = :id")
    Date getCreatedDateById(@Param("id") Long id);

    /**
     * Найти по коду документа
     * @param hashCode
     * @return
     */
    DocumentEntity findByHashCode(String hashCode);

    /**
     *
     * @param code
     * @return
     */
    DocumentEntity findByCode(String code);

    @Query(
            "select distinct d from DocumentEntity d " +
            "join d.participants dp " +
            "where dp.isSigned <> true and dp.isNeedSignDocument = true and " +
                    "dp.participantTypeName in (:participantTypes) and " +
                    "dp.sourceParticipantId = :participantSourceId and d.active <> false"
    )
    List<DocumentEntity> findNotSignedDocuments(
            @Param("participantSourceId") Long participantSourceId,
            @Param("participantTypes") List<String> participantTypes);
}
