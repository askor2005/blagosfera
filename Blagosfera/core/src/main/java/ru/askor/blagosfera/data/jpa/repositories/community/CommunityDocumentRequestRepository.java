package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.communities.CommunityDocumentRequestEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 21.07.2016.
 */
public interface CommunityDocumentRequestRepository extends JpaRepository<CommunityDocumentRequestEntity, Long>, JpaSpecificationExecutor<CommunityDocumentRequestEntity> {

    CommunityDocumentRequestEntity findByCommunity_IdAndUser_Id(Long communityId, Long userId);

    List<CommunityDocumentRequestEntity> findByUser_Id(Long userId);

    List<CommunityDocumentRequestEntity> findByCommunity_Id(Long communityId);
}
