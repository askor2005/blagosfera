package ru.askor.blagosfera.core.services.cms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.data.jpa.repositories.cms.HelpSectionRepository;
import ru.askor.blagosfera.data.jpa.repositories.cms.PageRepository;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.askor.blagosfera.data.jpa.entities.cms.HelpSectionEntity;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vtarasenko on 04.04.2016.
 */
@Service
@Transactional
public class HelpSectionDataServiceImpl implements HelpSectionDataService {
    private static final String  FIND_BY_NAME_CACHE = "FIND_BY_NAME_CACHE_HELP";
    private static final String GET_BY_ID_CACHE = "GET_BY_ID_CACHE_HELP";
    private static final String GET_HIERARCHY_CACHE = "GET_HIERARCHY_CACHE_HELP";
    private static final String GET_BY_PARENT_CACHE = "GET_BY_PARENT_CACHE_HELP";
    private static final String GET_BY_PARENT_AND_PUBLISHED_CACHE = "GET_BY_PARENT_AND_PUBLISHED_CACHE_HELP";
    private static final String EXISTS_CACHE_EXCLUDE = "EXISTS_CACHE_EXCLUDE_HELP";
    private static final String EXISTS_CACHE = "EXISTS_CACHE_HELP";
    private static final String FIND_ALL_CACHE = "FIND_ALL_CACHE_HELP";
    @Autowired
    private HelpSectionRepository helpSectionRepository;
    @Autowired
    private PageRepository pageRepository;
    @Override
    @CacheEvict(value = {FIND_BY_NAME_CACHE,GET_BY_ID_CACHE,GET_HIERARCHY_CACHE, GET_BY_PARENT_CACHE,GET_BY_PARENT_AND_PUBLISHED_CACHE,EXISTS_CACHE_EXCLUDE,EXISTS_CACHE,FIND_ALL_CACHE}, allEntries=true)
    public void save(HelpSectionDomain helpSectionDomain) {
        boolean created = false;
        HelpSectionEntity helpSection;
        if (helpSectionDomain.getId() != null) {
            helpSection = helpSectionRepository.findOne(helpSectionDomain.getId());
        }
        else {
            helpSection = new HelpSectionEntity();
            created = true;
        }
        helpSection.setName(helpSectionDomain.getName());
        helpSection.setPage(helpSectionDomain.getPageId() != null ? pageRepository.findOne(helpSectionDomain.getPageId()) : null);
        helpSection.setPublished(helpSectionDomain.isPublished());
        helpSection.setParent(helpSectionDomain.getParentId() != null ? helpSectionRepository.findOne(helpSectionDomain.getParentId()) : null);
        helpSectionRepository.saveAndFlush(helpSection);
        if (created) {
            helpSectionDomain.setId(helpSection.getId());
        }
    }
    @Override
    @CacheEvict(value = {FIND_BY_NAME_CACHE,GET_BY_ID_CACHE,GET_HIERARCHY_CACHE, GET_BY_PARENT_CACHE,GET_BY_PARENT_AND_PUBLISHED_CACHE,EXISTS_CACHE_EXCLUDE,EXISTS_CACHE,FIND_ALL_CACHE}, allEntries=true)
    public void delete(Long id) {
        HelpSectionEntity helpSectionEntity = helpSectionRepository.findOne(id);
        assert  helpSectionEntity != null;
        for (HelpSectionEntity child : helpSectionEntity.getChildren()) {
            delete(child.getId());
        }
        PageEntity page = helpSectionEntity.getPage();
        helpSectionRepository.delete(helpSectionEntity);
        pageRepository.delete(page);
    }
    @Override
    @Cacheable(GET_BY_ID_CACHE)
    public HelpSectionDomain getById(Long id) {
        HelpSectionEntity helpSectionEntity = helpSectionRepository.findOne(id);
        return helpSectionEntity != null ? helpSectionEntity.toDomain() : null;
    }
    @Override
    @Cacheable(GET_HIERARCHY_CACHE)
    public List<HelpSectionDomain> getHierarchy(HelpSectionDomain helpSectionDomain) {
        LinkedList<HelpSectionDomain> result = new LinkedList<>();
        HelpSectionEntity helpSection = helpSectionRepository.findOne(helpSectionDomain.getId());
        // проходим по иерархии до того как родитель будет null
        while (helpSection != null) {
            result.addFirst(helpSection.toDomain());
            helpSection = helpSection.getParent();
        }
        return result;
    }
    @Override
    @Cacheable(GET_BY_PARENT_CACHE)
    public List<HelpSectionDomain> findByParent(HelpSectionDomain parent){
        HelpSectionEntity parentEntity = (parent != null) ?  helpSectionRepository.findOne(parent.getId()) : null;
        return HelpSectionEntity.toDomainList(helpSectionRepository.findByParent(parentEntity));
    }
    @Override
    @Cacheable(GET_BY_PARENT_AND_PUBLISHED_CACHE)
    public List<HelpSectionDomain> findByParentAndPublished(HelpSectionDomain parent, boolean published){
        HelpSectionEntity parentEntity = (parent != null) ?  helpSectionRepository.findOne(parent.getId()) : null;
        return HelpSectionEntity.toDomainList(helpSectionRepository.findByParentAndPublished(parentEntity, published));
    }
    @Override
    @Cacheable(EXISTS_CACHE_EXCLUDE)
    public boolean exists(String name, Long excludeId) {
        HelpSectionEntity exclude = helpSectionRepository.findOne(excludeId);
        assert exclude != null;
        return helpSectionRepository.checkExists(name, exclude);
    }
    @Override
    @Cacheable(EXISTS_CACHE)
    public boolean exists(String name) {
        return helpSectionRepository.checkExists(name);
    }
    @CacheEvict(value = {FIND_BY_NAME_CACHE,GET_BY_ID_CACHE,GET_HIERARCHY_CACHE, GET_BY_PARENT_CACHE,GET_BY_PARENT_AND_PUBLISHED_CACHE,EXISTS_CACHE_EXCLUDE,EXISTS_CACHE,FIND_ALL_CACHE}, allEntries=true)
    @Override
    public void setParent(HelpSectionDomain helpSection, HelpSectionDomain helpSectionParent) {
        HelpSectionEntity helpSectionEntity = helpSectionRepository.findOne(helpSection.getId());
        HelpSectionEntity helpSectionEntityParent = (helpSectionParent != null) ?  helpSectionRepository.findOne(helpSectionParent.getId()) : null;
        helpSectionEntity.setParent(helpSectionEntityParent);
    }
    @Override
    @Cacheable(FIND_ALL_CACHE)
    public List<HelpSectionDomain> findAll() {
        return HelpSectionEntity.toDomainList(helpSectionRepository.findAll());
    }
    @Override
    @Cacheable(FIND_BY_NAME_CACHE)
    public HelpSectionDomain findByName(String name) {
        HelpSectionEntity helpSectionEntity = helpSectionRepository.findByName(name);
        return  helpSectionEntity != null ? helpSectionEntity.toDomain() : null;
    }
}
