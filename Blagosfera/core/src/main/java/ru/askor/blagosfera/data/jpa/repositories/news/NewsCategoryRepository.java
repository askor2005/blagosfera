package ru.askor.blagosfera.data.jpa.repositories.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.news.NewsCategory;

import java.util.List;

/**
 * Хранилище сущностей класса NewsCategory
 */
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {

    /**
     * Позволяет получить список всех корневых категорий
     * @return List<NewsCategory>
     */
    @Query(value = "SELECT n FROM NewsCategory n where n.parent IS NULL ORDER BY n.position ASC")
    List<NewsCategory> findAllRoot();

    /**
     * Позволяет получить список всех категорий-детей указанного родителя
     * @param parent родитель, чьих детей требуется найти
     * @return List<NewsCategory>
     */
    @Query(value = "SELECT n FROM NewsCategory n where n.parent = :parent ORDER BY n.position ASC")
    List<NewsCategory> findAllByParent(@Param("parent") NewsCategory parent);

    /**
     * Позволяет получить максимальное значение позиции среди детей указанного узла
     * @param parent родитель, среди детей которого осуществляется выборка
     * @return Integer или null, если детей нет или состояние базы не актуально
     */
    @Query(value = "SELECT MAX(n.position) FROM NewsCategory n where n.parent = :parent")
    Integer findMaxPositionByParent(@Param("parent") NewsCategory parent);

    @Query(value = "SELECT MAX(n.position) FROM NewsCategory n where n.parent IS NULL")
    Integer findMaxPositionOfRoot();

    /**
     * Позволяет проверить, есть ли в БД категория новостей с указанным ключом
     * @param key ключ для поиска
     * @return true - категория с таким ключом существует, false - иначе
     */
    @Query(value = "SELECT CASE WHEN COUNT(n) > 0 THEN 'true' ELSE 'false' END FROM NewsCategory n WHERE n.key = :key")
    Boolean existsByKey(@Param("key") String key);
}
