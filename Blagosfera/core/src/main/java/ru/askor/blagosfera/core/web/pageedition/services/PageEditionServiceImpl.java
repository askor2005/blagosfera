package ru.askor.blagosfera.core.web.pageedition.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.cms.PageEditionDomain;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 01.04.2016.
 */
@Service
@Transactional
public class PageEditionServiceImpl implements PageEditionService {
    @Autowired
    private PageEditionDomainService pageEditionDomainService;
    @Override
    public List<PageEditionDomain> getByPage(Long pageId) {
        return pageEditionDomainService.getByPage(pageId);
    }

    @Override
    public void createRecordForPageAndUser(Long pageId, Long sharerId) {
        PageEditionDomain pageEditionDomain = new PageEditionDomain();
        pageEditionDomain.setEditorId(sharerId);
        pageEditionDomain.setDate(new Date());
        pageEditionDomain.setPageId(pageId);
        pageEditionDomainService.save(pageEditionDomain);
    }
}
