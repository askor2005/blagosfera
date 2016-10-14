package ru.radom.kabinet.services.section;

import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.web.admin.dto.SaveSectionDto;

import java.util.List;

/**
 *
 * Created by vgusev on 14.03.2016.
 */
public interface SectionService {

    String getSectionLinkByName(String name);

    void saveRootSection(SaveSectionDto saveSectionDto);

    List<SectionDomain> getRoots(Long userId);

    List<SectionDomain> getRootsAll(Long userId);

    /**
     * Загрузить раздел по ссылке
     * @param uri
     * @return
     */
    SectionDomain getByLink(String uri);

    /**
     * Загрузить раздел по названию
     * @param name
     * @return
     */
    SectionDomain getByName(String name);

    /**
     *
     * @param sectionId
     * @param userId
     * @return
     */
    List<SectionDomain> getHierarchy(Long sectionId, Long userId);

    /**
     * Найти раздел по ссылке
     * отличается от getByLink тем, что производится анализ пользователя
     * @param link
     * @param userId
     * @return
     */
    SectionDomain findSectionByLink(String link, Long userId);
}
