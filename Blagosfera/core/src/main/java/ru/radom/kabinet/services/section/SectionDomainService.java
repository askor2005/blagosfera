package ru.radom.kabinet.services.section;

import ru.askor.blagosfera.domain.section.SectionDomain;

import java.util.List;

/**
 *
 * Created by vgusev on 29.03.2016.
 */
public interface SectionDomainService {

    String getSectionLinkByName(String name);

    SectionDomain getById(Long id);

    SectionDomain getByLink(String link);

    SectionDomain getByName(String name);

    List<SectionDomain> getHierarchy(Long sectionId);

    List<SectionDomain> getRoots(Long userId);

    SectionDomain getByPageId(Long pageId);

    void save(SectionDomain section);

    void delete(Long id);

    int determineFirstPosition(Long sectionId);

    int determineLastPosition(Long sectionId);

    List<SectionDomain> getRootsAll();

}
