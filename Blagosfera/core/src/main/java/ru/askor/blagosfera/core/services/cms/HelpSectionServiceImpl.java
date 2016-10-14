package ru.askor.blagosfera.core.services.cms;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.services.HelpSectionException;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.utils.Transliterator;

import javax.transaction.Transactional;
import java.util.List;


/**
 * Created by vtarasenko on 05.04.2016.
 */
@Service
@Transactional
public class HelpSectionServiceImpl implements HelpSectionService{
    private static final Logger logger = LoggerFactory.createLogger(HelpSectionService.class);
    @Autowired
    private PagesService pagesService;
    @Autowired
    private HelpSectionDataService helpSectionDataService;
    @Autowired
    private PagesDataService pagesDataService;
    @Autowired
    private SectionDomainService sectionDomainService;
    @Override
    public List<HelpSectionDomain> getRoots() {
        return helpSectionDataService.findByParent(null);
    }
    //поиск тех секций, у которых нет родителя(корневые)
    @Override
    public List<HelpSectionDomain> getRoots(boolean published) {
        return helpSectionDataService.findByParentAndPublished(null, published);
    }

    @Override
    public List<HelpSectionDomain> getChildren(HelpSectionDomain parent) {
        return helpSectionDataService.findByParent(parent);
    }

    @Override
    public List<HelpSectionDomain> getChildren(HelpSectionDomain parent, Boolean published) {
        return helpSectionDataService.findByParentAndPublished(parent, published);
    }

    @Override
    public HelpSectionDomain getById(Long parentId) {
        return helpSectionDataService.getById(parentId);
    }

    @Override
    public List<HelpSectionDomain> getHierarchy(HelpSectionDomain helpSection) {
        return helpSectionDataService.getHierarchy(helpSection);
    }
    @Override
    public String getTitle(HelpSectionDomain helpSectionDomain) {
        Page page = pagesDataService.getById(helpSectionDomain.getPageId());
        return page != null ? page.getTitle() : null;
    }

    @Override
    public void setParent(Long helpSectionId, Long parentHelpSectionId) {
        HelpSectionDomain helpSection = helpSectionDataService.getById(helpSectionId);
        HelpSectionDomain helpSectionParent = parentHelpSectionId != null ? helpSectionDataService.getById(parentHelpSectionId) : null ;
        assert helpSection != null;
        helpSectionDataService.setParent(helpSection, helpSectionParent);

    }

    @Override
    public void updateHelpSection(Long helpSectionId, String name, String title, String description, String keywords, String content, Long userId) throws Exception {
        HelpSectionDomain helpSection = helpSectionDataService.getById(helpSectionId);
        assert helpSection != null;
        assert helpSection.getPageId() != null;
        if (helpSectionDataService.exists(name, helpSection.getId())) {
            throw new HelpSectionException("Такое имя раздела уже используется");
        }
        helpSection.setName(name);
        helpSectionDataService.save(helpSection);
        pagesService.editPage(helpSection.getPageId(), userId, title, description, keywords, content, null);
    }

    @Override
    public void publishPage(Long helpSectionId) {
        HelpSectionDomain helpSectionDomain = helpSectionDataService.getById(helpSectionId);
        helpSectionDomain.setPublished(true);
        helpSectionDataService.save(helpSectionDomain);

    }

    @Override
    public void unPublishPage(Long helpSectionId) {
        HelpSectionDomain helpSectionDomain = helpSectionDataService.getById(helpSectionId);
        helpSectionDomain.setPublished(false);
        helpSectionDataService.save(helpSectionDomain);
    }
    @Override
    public HelpSectionDomain generateHelpSection(Long sectionId) {
        SectionDomain section = sectionDomainService.getById(sectionId);
        if (section == null) {
            throw new RuntimeException("Раздел с ИД " + sectionId + " не найден!");
        }
        if (section.getHelpLink() != null && !section.getHelpLink().equals("") && !helpSectionDataService.exists(section.getHelpLink())) {
            throw new RuntimeException("Ссылка на страницу справки уже установлена!");
        }
        String helpSectionName;
        if (section.getName() != null && !section.getName().equals("")) {
            helpSectionName = section.getName();
        } else {
            helpSectionName = Transliterator.transliterate(section.getTitle());
        }
        // Подбираем имя раздела пока оно не будет уникальным
        int index = 0;
        helpSectionName = helpSectionName.replaceAll(" ", "_");
        while (helpSectionDataService.exists(helpSectionName)) {
            helpSectionName = helpSectionName + "_" + index;
            index++;
        }
        logger.logError("helpSectionName "+helpSectionName);
        Page page = new Page();
        pagesDataService.savePage(page);
        HelpSectionDomain helpSectionDomain = new HelpSectionDomain();
        helpSectionDomain.setName(helpSectionName);
        helpSectionDomain.setPublished(false);
        helpSectionDomain.setParentId(null);
        helpSectionDomain.setPageId(page.getId());
        helpSectionDataService.save(helpSectionDomain);
        section.setHelpLink(helpSectionDomain.getName());
        section.setOpenInNewLink(false);
        sectionDomainService.save(section);
        return helpSectionDomain;


    }

    @Override
    public HelpSectionDomain createHelpSection(Long parentId, String name) {
        if (helpSectionDataService.exists(name)) {
            throw new HelpSectionException("Такое имя раздела уже используется");
        }
        Page page = new Page();
        pagesDataService.savePage(page);
        HelpSectionDomain helpSectionDomain = new HelpSectionDomain();
        helpSectionDomain.setName(name);
        helpSectionDomain.setPublished(false);
        helpSectionDomain.setParentId(parentId);
        helpSectionDomain.setPageId(page.getId());
        helpSectionDataService.save(helpSectionDomain);
        return helpSectionDomain;
    }

    @Override
    public void deleteHelpSection(Long helpSectionId) {
        helpSectionDataService.delete(helpSectionId);
    }
    @Override
    public List<HelpSectionDomain> getAllHelpSections() {
       return helpSectionDataService.findAll();
    }

    @Override
    public HelpSectionDomain getByName(String name) {
        return helpSectionDataService.findByName(name);
    }
}
