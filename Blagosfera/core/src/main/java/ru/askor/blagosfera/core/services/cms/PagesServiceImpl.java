package ru.askor.blagosfera.core.services.cms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.web.pageedition.services.PageEditionService;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.SectionAccessType;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.Roles;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 31.03.2016.
 */
@Transactional
@Service("pagesServiceImpl")
public class PagesServiceImpl implements PagesService {

    @Autowired
    private PagesDataService pagesDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SectionDomainService sectionDomainService;

    @Autowired
    private PageEditionService pageEditionService;

    public PagesServiceImpl() {
    }

    @Override
    public void releasePage(Long id) {
        if (id == null)
            return;
        Page page = pagesDataService.getById(id);
        if (page == null)
            return;
        // Если редактор текущий участник
        if (page.getCurrentEditorId() != null && page.getCurrentEditorEditDate() != null && page.getCurrentEditorId().equals(SecurityUtils.getUser().getId())) {
            // устанавливаем currentEditor и currentEditorEditDate null
            page.setCurrentEditorEditDate(null);
            page.setCurrentEditorId(null);
            pagesDataService.savePage(page);
        } else {
            throw new RuntimeException("Текущий редактор не вы!");
        }
    }

    /**
     * редактирование страницы
     */
    @Override
    public void editPage(Long pageId, Long userId, String title, String description, String keywords, String content, String accessType) throws Exception {
        Page page = pagesDataService.getById(pageId);
        if (page == null)
            return;
        page.setContent(content);
        page.setKeywords(keywords);
        page.setTitle(title);
        page.setDescription(description);
        pagesDataService.savePage(page);
        if (userId != null) {
            pageEditionService.createRecordForPageAndUser(pageId, userId);
        }
        if (accessType != null) {
            //TODO работа с с секциями(было в AdminController), убрать после рефакторинга
            try {
                // пытаемся выставить уровень доступа к секции по пришедшему ключу
                SectionAccessType sectionAccessType = SectionAccessType.valueOf(accessType);
                SectionDomain section = sectionDomainService.getByPageId(pageId);
                if (section != null) {
                    section.setAccessType(sectionAccessType);
                    sectionDomainService.save(section);
                } else {
                    // что-то пошло не так...
                }
            } catch (IllegalArgumentException e) {
                // если получить значение по ключу не удалось, то не выставляем доступ, схраняем страницу, но пишем в консоль ошибку
                e.printStackTrace();
            }
        }
    }
    @Override
    public void publishPage(Long pageId) throws Exception {
        SectionDomain section = sectionDomainService.getByPageId(pageId);
        section.setPublished(true);
        sectionDomainService.save(section);
    }
    @Override
    public void unPublishPage(Long pageId) throws Exception {
        SectionDomain section = sectionDomainService.getByPageId(pageId);
        section.setPublished(false);
        sectionDomainService.save(section);
    }
    @Override
    public void prolongPageEdition(Long pageId) {
        Page page = pagesDataService.getById(pageId);
        // Если редактор текущий участник
        if (page.getCurrentEditorId() != null && page.getCurrentEditorEditDate() != null && page.getCurrentEditorId().equals(SecurityUtils.getUser().getId())) {
            page.setCurrentEditorEditDate(new Date());
            pagesDataService.savePage(page);
        } else {
            throw new RuntimeException("Текущий редактор не вы!");
        }
    }//если можно редактировать страницу блокируем ее
    @Override
    public void tryLockPage(Long pageId) {
        Page page = pagesDataService.getById(pageId);
        if ((page.getCurrentEditorEditDate() == null) || (new Date().getTime() - page.getCurrentEditorEditDate().getTime() > PageEntity.MILLISECONDS_FOR_RELEASE_PAGE) ||
                (SecurityUtils.getUserDetails() != null && page.getCurrentEditorId().equals(SecurityUtils.getUser().getId()))) {
                page.setCurrentEditorEditDate(new Date());
                page.setCurrentEditorId(SecurityUtils.getUser().getId());
                pagesDataService.savePage(page);
        }
    }
    @Override
    public List<Page> findPages(String titleQuery) {
        return pagesDataService.findPages(titleQuery);
    }
    @Override
    public Page getById(Long pageId) {
        return pagesDataService.getById(pageId);
    }

    @Override
    public Page getByPath(String path) {
        PageEntity pageEntity = pagesDataService.getByPath(path);
        return pageEntity == null ? null : pageEntity.toDomain();
    }

    @Override
    public User getCurrentEditor(Long pageId) {
     Page page = pagesDataService.getById(pageId);
     if (page == null)
         return null;
     if (page.getCurrentEditorId() == null)
         return null;
        Date date = new Date();
        //прошло нужное число времени с последнего редактирования
        if ((page.getCurrentEditorEditDate() == null) || (new Date().getTime() - page.getCurrentEditorEditDate().getTime() > PageEntity.MILLISECONDS_FOR_RELEASE_PAGE) ||
                (SecurityUtils.getUserDetails() != null && page.getCurrentEditorId().equals(SecurityUtils.getUser().getId()))) {
            return null;
        }
        return userDataService.getByIdMinData(page.getCurrentEditorId());
    }
    @Override
    public boolean isAllowedEditStaticPage(Long pageId) {
        boolean isAdmin = SecurityUtils.getUserDetails() != null && SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
        if (isAdmin) {
            return true;
        }
        SectionDomain section = sectionDomainService.getByPageId(pageId);
        if (section == null) {
            return false;
        }
        return SecurityUtils.getUserDetails() != null && (SecurityUtils.getUserDetails().hasRole(Roles.ROLE_BLAGOSFERA_PAGES_EDITOR) &&
                                sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera"));
    }
    @Override
    public String getPageEditLink(Long pageId) {
        Page page = pagesDataService.getById(pageId);
        SectionDomain section = sectionDomainService.getByPageId(pageId);
        boolean isAdmin = SecurityUtils.getUserDetails() != null && SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
        boolean isBlagosferaEditor =(section != null) && SecurityUtils.getUserDetails() != null && (SecurityUtils.getUserDetails().hasRole(Roles.ROLE_BLAGOSFERA_PAGES_EDITOR) &&
                sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera"));
        if (isAdmin) {
            return  "/admin/page/" + page.getId() + "/edit";
        } else if (isBlagosferaEditor) {
            return  "/Благосфера/редактор/страница/" + page.getId();
        }
        return null;
    }
    @Override
    public SectionDomain getSectionForPage(Long pageId) {
        SectionDomain section = sectionDomainService.getByPageId(pageId);
        return section;
    }
}
