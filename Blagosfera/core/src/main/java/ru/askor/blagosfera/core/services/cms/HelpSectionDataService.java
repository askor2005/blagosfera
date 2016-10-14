package ru.askor.blagosfera.core.services.cms;

import ru.askor.blagosfera.domain.section.HelpSectionDomain;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface HelpSectionDataService {
    void save(HelpSectionDomain helpSectionDomain);
    void delete(Long id);
    HelpSectionDomain getById(Long id);
    List<HelpSectionDomain> getHierarchy(HelpSectionDomain helpSectionDomain);
    List<HelpSectionDomain> findByParent(HelpSectionDomain parent);
    List<HelpSectionDomain> findByParentAndPublished(HelpSectionDomain parent, boolean published);
    boolean exists(String name, Long excludeId);
    boolean exists(String name);
    void setParent(HelpSectionDomain helpSection, HelpSectionDomain helpSectionParent);
    List<HelpSectionDomain> findAll();
    HelpSectionDomain findByName(String name);
}
