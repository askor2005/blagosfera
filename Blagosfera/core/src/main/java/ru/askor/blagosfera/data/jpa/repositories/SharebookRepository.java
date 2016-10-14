package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;

import java.util.List;

public interface SharebookRepository extends JpaRepository<SharebookEntity, Long> {

    @Query(value = "select s from SharebookEntity s where owner_id = :ownerId and s.community.id = :communityId")
    SharebookEntity findOneByOwner_IdAndCommunity_Id(@Param("ownerId") Long ownerId, @Param("communityId") Long communityId);

    List<SharebookEntity> findAllByCommunity_Id(Long communityId);
}
