package ru.askor.blagosfera.core.services.cms;

import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.domain.cms.Page;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface PagesDataService {

    Page getById(Long id);

    List<Page> findPages(String titleQuery);

    void savePage(Page page);

    void deletePage(Long id);

    PageEntity getByPath(String path);
}
