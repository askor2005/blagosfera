package ru.askor.blagosfera.core.services.cms;

import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 * Created by vtarasenko on 31.03.2016.
 */
public interface PagesService {
    void releasePage(Long id);

    void editPage(Long pageId, Long userId, String title, String description, String keywords, String content, String accessType) throws Exception;

    void publishPage(Long pageId) throws Exception;

    void unPublishPage(Long pageId) throws Exception;

    void prolongPageEdition(Long pageId);

    void tryLockPage(Long pageId);

    List<Page> findPages(String titleQuery);

    Page getById(Long pageId);

    Page getByPath(String path);

    User getCurrentEditor(Long pageId);

    boolean isAllowedEditStaticPage(Long pageId);

    String getPageEditLink(Long pageId);


    SectionDomain getSectionForPage(Long pageId);
}
