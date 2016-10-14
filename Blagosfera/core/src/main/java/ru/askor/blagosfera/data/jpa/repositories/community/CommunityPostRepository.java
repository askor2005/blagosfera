package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.communities.CommunityPostEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 05.04.2016.
 */
public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {

    /**
     *
     * @param postIds
     * @return
     */
    List<CommunityPostEntity> findByIdIn(List<Long> postIds);
    @Query("select distinct p.mnemo from CommunityPostEntity p inner join p.members m where m.user.id = :userId and p.mnemo like :pattern% order by p.mnemo asc")
    List<String> getMnemosForUserByPatternOrderDesc(@Param("userId") Long userId,@Param("pattern") String pattern);

}
