package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.communities.postrequest.CommunityPostRequestEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 05.04.2016.
 */
public interface CommunityPostRequestRepository extends JpaRepository<CommunityPostRequestEntity, Long> {

    List<CommunityPostRequestEntity> findByReceiver_Id(Long receiverId);

    List<CommunityPostRequestEntity> findBySender_Id(Long senderId);

}
