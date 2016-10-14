package ru.askor.blagosfera.core.web.pageedition.services;

import ru.askor.blagosfera.domain.cms.PageEditionDomain;

import java.util.List;

/**
 * Created by vtarasenko on 01.04.2016.
 */
public interface PageEditionService {
    public List<PageEditionDomain> getByPage(Long pageId);
    public void createRecordForPageAndUser(Long pageId,Long sharerId);
}
