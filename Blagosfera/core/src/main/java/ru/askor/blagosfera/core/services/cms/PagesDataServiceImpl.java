package ru.askor.blagosfera.core.services.cms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.cms.PageRepository;
import ru.askor.blagosfera.domain.cms.Page;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 30.03.2016.
 */
@Service("pageDomainService")
@Transactional
public class PagesDataServiceImpl implements PagesDataService {

    private static final String GET_BY_ID_CACHE = "getByIdPage";
    private static final String GET_BY_TITLE = "getByTitle";

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private UserRepository userRepository;

    @Cacheable(GET_BY_ID_CACHE)
    @Override
    public Page getById(Long id) {
        PageEntity pageEntity = pageRepository.findOne(id);
        if (pageEntity != null)
            return pageEntity.toDomain();
        return null;
    }

    /**
     * Найти страницы по названию
     *
     * @param titleQuery
     * @return
     */
    @Cacheable(GET_BY_TITLE)
    @Override
    public List<Page> findPages(String titleQuery) {
        return pageRepository.findByTitleIgnoreCaseContaining(titleQuery).stream().map(p -> p.toDomain()).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {GET_BY_ID_CACHE, GET_BY_TITLE}, allEntries = true)
    public void savePage(Page page) {
        PageEntity pageEntity;
        boolean created = false;
        if (page.getId() == null) {
            created = true;
            pageEntity = new PageEntity();
        } else {
            pageEntity = pageRepository.findOne(page.getId());
        }
        if (pageEntity == null) {
            return;
        }
        pageEntity.setContent(page.getContent());
        pageEntity.setCurrentEditor(page.getCurrentEditorId() != null ? userRepository.findOne(page.getCurrentEditorId()) : null);
        pageEntity.setKeywords(page.getKeywords());
        pageEntity.setTitle(page.getTitle());
        pageEntity.setDescription(page.getDescription());
        pageEntity.setCurrentEditorEditDate(page.getCurrentEditorEditDate());
        pageRepository.saveAndFlush(pageEntity);
        if (created) {
            page.setId(pageEntity.getId());
        }
    }

    @Override
    @CacheEvict(value = {GET_BY_ID_CACHE, GET_BY_TITLE}, allEntries = true)
    public void deletePage(Long id) {
        if (id == null)
            return;
        pageRepository.delete(id);
    }

    @Override
    public PageEntity getByPath(String path) {
        return pageRepository.findOneByPath(path);
    }
}
