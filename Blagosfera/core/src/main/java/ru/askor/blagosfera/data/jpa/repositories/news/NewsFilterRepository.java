package ru.askor.blagosfera.data.jpa.repositories.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.news.NewsFilterEntity;

/**
 * Хранилище сущностей класса NewsFilterEntity
 */
public interface NewsFilterRepository extends JpaRepository<NewsFilterEntity, Long> {

    NewsFilterEntity findOneByUser_IdAndCommunity_Id(Long userId, Long communityId);

    @Query("SELECT n FROM NewsFilterEntity AS n WHERE n.user.id = :userId AND n.community IS NULL")
    NewsFilterEntity findOneBySharerWithoutCommunity(@Param(value = "userId") Long userId);
}
