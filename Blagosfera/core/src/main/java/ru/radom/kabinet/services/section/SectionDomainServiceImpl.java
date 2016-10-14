package ru.radom.kabinet.services.section;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.data.jpa.repositories.cms.SectionRepository;
import ru.askor.blagosfera.data.jpa.specifications.cms.SectionSpecifications;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.web.PageDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.Roles;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 29.03.2016.
 */
@Service
@Transactional
public class SectionDomainServiceImpl implements SectionDomainService {
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.createLogger(SectionDomainService.class);

    private static final String GET_BY_LINK_CACHE = "getByLink";

    private static final String GET_BY_ID_CACHE = "getById";

    private static final String GET_BY_NAME_CACHE = "getByName";

    private static final String GET_SECTION_LINK_BY_NAME_CACHE = "getSectionLinkByName";

    private static final String GET_ROOTS_CACHE = "getRoots";

    private static final String GET_ROOTS_CACHE_ALL = "getRootsAll";

    private static final String GET_BY_PAGE_CACHE = "getByPageId";

    private static final String GET_HIERARCHY_CACHE = "getHierarchy";

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private PageDao pageDao;

    @Override
    public String getSectionLinkByName(String name) {
        Section section = sectionDao.getByName(name);
        return (section == null || section.getLink() == null) ? null : section.getLink();
    }

    @Override
    @Cacheable(GET_BY_ID_CACHE)
    public SectionDomain getById(Long id) {
        return Section.toDomainSafe(sectionDao.getById(id));
    }

    @Override
    @Cacheable(GET_BY_LINK_CACHE)
    public SectionDomain getByLink(String link) {
        return Section.toDomainSafe(sectionDao.getByLink(link));
    }

    @Override
    @Cacheable(GET_BY_NAME_CACHE)
    public SectionDomain getByName(String name) {
        return Section.toDomainSafe(sectionDao.getByName(name));
    }

    @Override
    @Cacheable(GET_HIERARCHY_CACHE)
    public List<SectionDomain> getHierarchy(Long sectionId) {
        Section section = sectionDao.getById(sectionId);
        return Section.toDomainList(sectionDao.getHierarchy(section));
    }

    @Override
    @Cacheable(GET_ROOTS_CACHE)
    public List<SectionDomain> getRoots(Long userId) {
        User user = (userId != null) ?  userService.getUserById(userId) : null;
        return getSectionsRecursive(null, user);
        //return Section.toDomainList(sectionDao.getRoots());
    }
    private List<SectionDomain> getSectionsRecursive(Section parent,User user) {
        Specifications<Section> sectionSpecifications = buildSearchSpecifications(user, parent);
        List<Section> sections = sectionRepository.findAll(sectionSpecifications,new Sort(Sort.Direction.ASC,"position"));
        List<SectionDomain> result = new ArrayList<>();
        sections.forEach(section -> {
            SectionDomain sectionDomain = section.toDomainNoChildren();
            sectionDomain.setChildren(getSectionsRecursive(section,user));
            result.add(sectionDomain);
        });
        return result;
        //return Section.toDomainList(sectionDao.getRoots());
    }
    private List<SectionDomain> getSectionsRecursive(Section parent) {
        Specifications<Section> sectionSpecifications = buildSearchSpecifications(parent);
        List<Section> sections = sectionRepository.findAll(sectionSpecifications,new Sort(Sort.Direction.ASC,"position"));
        List<SectionDomain> result = new ArrayList<>();
        sections.forEach(section -> {
            SectionDomain sectionDomain = section.toDomainNoChildren();
            sectionDomain.setChildren(getSectionsRecursive(section));
            result.add(sectionDomain);
        });
        return result;
        //return Section.toDomainList(sectionDao.getRoots());
    }
    private List<SectionDomain> getSectionsRecursiveAll(Section parent,User user) {
        Specifications<Section> sectionSpecifications = buildSearchSpecifications(user, parent);
        List<Section> sections = sectionRepository.findAll(sectionSpecifications, new Sort(Sort.Direction.ASC, "position"));
        List<SectionDomain> result = new ArrayList<>();
        sections.forEach(section -> {
            SectionDomain sectionDomain = section.toDomainNoChildren();
            sectionDomain.setChildren(getSectionsRecursive(section, user));
            result.add(sectionDomain);
        });
        return result;
        //return Section.toDomainList(sectionDao.getRoots());
    }
    private Specifications<Section> buildSearchSpecifications(User user, Section parent) {
        boolean verified = (user != null) ? user.isVerified() : false;
        boolean isAdmin = (user != null) ?  SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)  : false;
        Integer registratorLevel = null;
        if (user != null) {
            RegistratorLevel registratorLvl = userDataService.getRegistratorLevel(user.getId());
            if (registratorLvl != null) {
                registratorLevel = getRegistratorLevel(registratorLvl.getMnemo());
            }
        }
        Specifications<Section> sectionSpecifications = Specifications.where(SectionSpecifications.conjunction());
        if (parent == null) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.isRoot());
        }
        else {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.parent(parent));
        }
        if (!verified) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.showToVerifiedUsersOnly(false));
        }
        if (!isAdmin) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.showToAdminUsersOnly(false));
        }
        if (user == null) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.showToAuthorizedUsersOnly(false));
        }
        if (registratorLevel == null) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.minRegistratiorLevelToShowIsNull());
        }
        else {
            sectionSpecifications = sectionSpecifications.and(Specifications.where(SectionSpecifications.minRegistratiorLevelToShow(registratorLevel)).or(SectionSpecifications.minRegistratiorLevelToShowIsNull()));
        }
        return sectionSpecifications;

    }
    private Specifications<Section> buildSearchSpecifications(Section parent) {
        Specifications<Section> sectionSpecifications = Specifications.where(SectionSpecifications.conjunction());
        if (parent == null) {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.isRoot());
        }
        else {
            sectionSpecifications = sectionSpecifications.and(SectionSpecifications.parent(parent));
        }
        return sectionSpecifications;

    }
    private Integer getRegistratorLevel(String registratorLevelMnemo) {
        Integer registratorLevel = null;
        if(registratorLevelMnemo != null) {
            switch(registratorLevelMnemo) {
                case "registrator.level0":
                    registratorLevel = 0;
                    break;
                case "registrator.level1":
                    registratorLevel = 1;
                    break;
                case "registrator.level2":
                    registratorLevel = 2;
                    break;
                case "registrator.level3":
                    registratorLevel = 3;
                    break;
                default:
                    registratorLevel = -1;
                    break;
            }
        }
        return registratorLevel;
    }

    @Override
    @Cacheable(GET_BY_PAGE_CACHE)
    public SectionDomain getByPageId(Long pageId) {
        PageEntity page = pageDao.loadById(pageId);
        return Section.toDomainSafe(sectionDao.getByPage(page));
    }

    @Override
    @CacheEvict(value = {GET_BY_LINK_CACHE,GET_BY_NAME_CACHE,GET_SECTION_LINK_BY_NAME_CACHE,GET_ROOTS_CACHE,GET_HIERARCHY_CACHE,GET_BY_PAGE_CACHE,GET_BY_ID_CACHE,GET_ROOTS_CACHE_ALL}, allEntries=true)
    public void save(SectionDomain section) {
        Section sectionEntity;
        if (section.getId() == null) {
            sectionEntity = new Section();
        } else {
            sectionEntity = sectionDao.getById(section.getId());
        }
        sectionEntity.setDisabled(section.isDisabled());
        sectionEntity.setShowToAdminUsersOnly(section.isShowToAdminUsersOnly());
        sectionEntity.setShowToVerifiedUsersOnly(section.isShowToVerfiedUsersOnly());
        sectionEntity.setMinRegistratorLevelToShow(section.getMinRegistratorLevelToShow());
        sectionEntity.setOpenInNewLink(section.isOpenInNewLink());
        sectionEntity.setName(section.getName());
        sectionEntity.setTitle(section.getTitle());
        sectionEntity.setPosition(section.getPosition());
        sectionEntity.setPublished(section.isPublished());
        sectionEntity.setForwardUrl(section.getForwardUrl());
        sectionEntity.setAccessType(section.getAccessType());
        sectionEntity.setCanSetForwardUrl(section.isCanSetForwardUrl());
        sectionEntity.setHelpLink(section.getHelpLink());
        sectionEntity.setType(section.getType());
        sectionEntity.setLink(section.getLink());
        sectionEntity.setIcon(section.getIcon());
        sectionEntity.setHint(section.getHint());
        sectionEntity.setImageUrl(section.getImageUrl());
        sectionEntity.setShowToAuthorizedUsersOnly(section.isShowToAuthorizedUsersOnly());

        if (section.getParentId() != null) {
            sectionEntity.setParent(sectionDao.loadById(section.getParentId()));
        }

        if (section.getPageId() != null) {
            sectionEntity.setPage(pageDao.loadById(section.getPageId()));
        }
        sectionDao.saveOrUpdate(sectionEntity);
    }

    @Override
    @CacheEvict(value = {GET_BY_LINK_CACHE,GET_BY_NAME_CACHE,GET_SECTION_LINK_BY_NAME_CACHE,GET_ROOTS_CACHE,GET_HIERARCHY_CACHE,GET_BY_PAGE_CACHE,GET_BY_ID_CACHE,GET_ROOTS_CACHE_ALL}, allEntries=true)
    public void delete(Long id) {
        sectionDao.delete(id);
    }

    @Override
    public int determineFirstPosition(Long sectionId) {
        return sectionDao.determineFirstPosition(sectionId);
    }

    @Override
    public int determineLastPosition(Long sectionId) {
        return sectionDao.determineFirstPosition(sectionId);
    }

    @Override
    @Cacheable(GET_ROOTS_CACHE_ALL)
    public List<SectionDomain> getRootsAll() {
        return getSectionsRecursive(null);
    }
}
