package ru.askor.blagosfera.data.jpa.repositories.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.common.TagEntity;

/**
 * Хранилище тегов
 */
public interface TagRepository extends JpaRepository<TagEntity, Long>, JpaSpecificationExecutor<TagEntity> {

    /**
     * Позволяет найти один тег по его тексту
     * @param text текст тега
     * @return найденный тег или null
     */
    TagEntity findOneByText(String text);

}
