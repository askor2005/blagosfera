package ru.askor.blagosfera.core.services.cms;

import ru.askor.blagosfera.domain.section.HelpSectionDomain;

import java.util.List;

/**
 * Created by vtarasenko on 05.04.2016.
 */
public interface HelpSectionService {
    List<HelpSectionDomain> getRoots();
    List<HelpSectionDomain> getRoots(boolean published);
    List<HelpSectionDomain> getChildren(HelpSectionDomain parent);
    List<HelpSectionDomain> getChildren(HelpSectionDomain parent,Boolean published);

    HelpSectionDomain getById(Long parentId);

    List<HelpSectionDomain> getHierarchy(HelpSectionDomain helpSection);

    String getTitle(HelpSectionDomain helpSectionDomain);

    public void setParent(Long helpSectionId, Long parentHelpSectionId);


    void updateHelpSection(Long helpSectionId, String name, String title, String description, String keywords, String content, Long userId) throws Exception;

    void publishPage(Long helpSectionId);
    void unPublishPage(Long helpSectionId);

    HelpSectionDomain generateHelpSection(Long sectionId);


    HelpSectionDomain createHelpSection(Long parentId, String name);

    void deleteHelpSection(Long helpSectionId);

    List<HelpSectionDomain> getAllHelpSections();

    HelpSectionDomain getByName(String name);
}
