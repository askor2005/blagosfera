package ru.askor.blagosfera.data.jpa.repositories.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.news.News;

/**
 * Хранилище сущностей класса News
 */
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
}
