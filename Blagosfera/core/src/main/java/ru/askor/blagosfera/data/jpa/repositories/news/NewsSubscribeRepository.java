package ru.askor.blagosfera.data.jpa.repositories.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.news.NewsSubscribe;

import java.util.List;

/**
 * Хранилище подписок на новости объекта привязки
 */
public interface NewsSubscribeRepository extends JpaRepository<NewsSubscribe, Long> {

    List<NewsSubscribe> findAllByUser_Id(Long userId);
    @Modifying
    @Query(value = "delete from news_subscribes where (scope_type = 'SHARER' and  scope_id =:userId )",nativeQuery = true)
    public void deleteNewsSubscribesToUser(@Param("userId") Long userId);
    @Modifying
    @Query(value = "delete from news_subscribes where (scope_type = 'COMMUNITY' and  scope_id = :communityId )",nativeQuery = true)
    public void deleteNewsSubscribesToCommunity(@Param("communityId") Long communityId);
}
