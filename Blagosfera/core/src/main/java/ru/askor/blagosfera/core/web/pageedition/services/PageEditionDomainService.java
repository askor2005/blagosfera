package ru.askor.blagosfera.core.web.pageedition.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.cms.PageRepository;
import ru.askor.blagosfera.data.jpa.repositories.cms.PageEditionRepository;
import ru.askor.blagosfera.domain.cms.PageEditionDomain;
import ru.radom.kabinet.model.web.PageEdition;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 01.04.2016.
 */
@Service
@Transactional
public class PageEditionDomainService {
    @Autowired
    private PageEditionRepository pageEditionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    public void save(PageEditionDomain pageEditionDomain) {
        PageEdition pageEdition = pageEditionDomain.getId() != null ? pageEditionRepository.findOne(pageEditionDomain.getId()) : new PageEdition();
        if (pageEdition == null)
            return;
        pageEdition.setDate(pageEditionDomain.getDate());
        pageEdition.setEditor(pageEditionDomain.getEditorId() != null ? userRepository.findOne(pageEditionDomain.getEditorId()) : null);
        pageEdition.setPage(pageEditionDomain.getPageId() != null ? pageRepository.findOne(pageEditionDomain.getPageId()) : null);
        pageEditionRepository.save(pageEdition);

    }
    public List<PageEditionDomain> getByPage(Long pageId) {
        PageEntity pageEntity = pageRepository.findOne(pageId);
        if (pageEntity == null)
            return null;
        return pageEntity.getEditions().stream().map(pageEdition -> pageEdition.toDomain()).collect(Collectors.toList());
    }
}
