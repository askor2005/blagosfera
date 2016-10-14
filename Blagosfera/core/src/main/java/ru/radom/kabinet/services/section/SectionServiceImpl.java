package ru.radom.kabinet.services.section;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.web.sections.SectionsDetailService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.admin.dto.SaveSectionDto;

import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by vgusev on 18.11.2015.
 */
@Transactional
@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionDomainService sectionDomainService;

    @Autowired
    private SectionsDetailService sectionsDetailService;

    @Autowired
    private CommunityDataService communityDomainService;

    private void validateSaveSectionDto(SaveSectionDto saveSectionDto) {
        ExceptionUtils.check(saveSectionDto.getId() == null, "Не выбран раздел");
        ExceptionUtils.check(StringUtils.isBlank(saveSectionDto.getTitle()), "Не установлено название раздела");
        ExceptionUtils.check(StringUtils.isBlank(saveSectionDto.getLink()), "Не установлена ссылка раздела");
    }

    /**
     * Позволяет получить uri секции по имени view
     * @param name имя view
     * @return String uri секции или null, если секцию или uri получить не удалось
     */
    public String getSectionLinkByName(String name) {
        return sectionDomainService.getSectionLinkByName(name);
    }

    @Override
    public void saveRootSection(SaveSectionDto saveSectionDto) {
        saveSectionDto.getId();
        validateSaveSectionDto(saveSectionDto);
        SectionDomain section  = sectionDomainService.getById(saveSectionDto.getId());
        section.setLink(saveSectionDto.getLink());
        section.setOpenInNewLink(saveSectionDto.isOpenInNewLink());
        section.setTitle(saveSectionDto.getTitle());
        section.setForwardUrl(saveSectionDto.getForwardUrl());
        section.setHint(saveSectionDto.getHint());
        section.setPublished(saveSectionDto.isPublished());
        if (saveSectionDto.getPageId() != null) {
            section.setPageId(saveSectionDto.getPageId());
        }
        sectionDomainService.save(section);
    }

    private void setDetails(SectionDomain section, Long userId) {
        section.setDetails(new HashMap<>());
        sectionsDetailService.setDetails(section, userId);
        if (section.getChildren() != null) {
            setDetails(section.getChildren(), userId);
        }
    }

    private void setDetails(List<SectionDomain> sections, Long userId) {
        if (sections != null) {
            for (SectionDomain section : sections) {
                setDetails(section, userId);
            }
        }
    }

    @Override
    public List<SectionDomain> getRoots(Long userId) {
        List<SectionDomain> rootSections = sectionDomainService.getRoots(userId);
        if (userId != null) {
            setDetails(rootSections, userId);
        }
        return rootSections;
    }
    @Override
    public List<SectionDomain> getRootsAll(Long userId) {
        List<SectionDomain> rootSections = sectionDomainService.getRootsAll();
        if (userId != null) {
            setDetails(rootSections, userId);
        }
        return rootSections;
    }
    @Override
    public SectionDomain getByLink(String link) {
        return sectionDomainService.getByLink(link);
    }

    @Override
    public SectionDomain getByName(String name) {
        return sectionDomainService.getByName(name);
    }

    @Override
    public List<SectionDomain> getHierarchy(Long sectionId, Long userId) {
        List<SectionDomain> hierarchy = sectionDomainService.getHierarchy(sectionId);
        setDetails(hierarchy, userId);
        return hierarchy;
    }

    @Override
    public SectionDomain findSectionByLink(String link, Long userId) {
        SectionDomain result = null;
        if (link != null && link.startsWith("/group/")) { // Создать путь для объединения
            String[] linkParts = link.split("/");
            Long communityId = null;
            if (linkParts.length > 1) {
                String seoLink = linkParts[2];
                communityId = communityDomainService.findCommunityId(seoLink);
            }
            if (communityId != null) {
                boolean isMember = (userId != null) ?  communityDomainService.isSharerMember(communityId, userId) : false;
                String sectionName = isMember ? "communitiesMy" : "communitiesAll";
                result = getByName(sectionName);
            }
        }
        if (result == null) {
            result = getByLink(link);
        }
        if (result == null) {
            if (link.startsWith("/menu_edit/ramera")){
                result = getByName("ramera");
            }
            else {
                result = getByName("blagosferaNews");
            }
        }
        return result;
    }
}
