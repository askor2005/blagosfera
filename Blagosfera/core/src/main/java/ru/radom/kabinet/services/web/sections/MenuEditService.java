package ru.radom.kabinet.services.web.sections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.SectionException;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.services.section.SectionService;
import ru.radom.kabinet.utils.StringUtils;

import java.util.List;

@Service
@Transactional
public class MenuEditService {

	/*@Autowired
	private ProfileService profileService;

	@Autowired
	private SectionDao sectionDao;*/

	@Autowired
	private SectionService sectionService;

	@Autowired
	private SectionDomainService sectionDomainService;

	private void checkLink(SectionDomain root, SectionDomain section) {
		if (root.getName().equals("blagosfera") && section.getLink() != null && !section.getLink().startsWith("/Благосфера/")) {
			throw new SectionException("Ссылка должна начинаться с /Благосфера/");
		}
		SectionDomain byLink = sectionService.getByLink(section.getLink());
		if (byLink != null && !byLink.getId().equals(section.getId())) {
			throw new SectionException("Такая ссылка уже используется");
		}
	}

	private void checkRoot(SectionDomain root, Long sectionId) {
		List<SectionDomain> hierarchy = sectionDomainService.getHierarchy(sectionId);
		if (hierarchy == null || hierarchy.size() == 0 || !hierarchy.get(0).getId().equals(root.getId())) {
			throw new SectionException("Неверный корневой раздел");
		}
	}

	public SectionDomain saveSection(SectionDomain section, SectionDomain root, User editor) {
		Long parentId = section.getParentId();
		if (parentId == null) {
			throw new SectionException("Не задан родительский раздел");
		}
		if (StringUtils.isEmpty(section.getTitle())) {
			throw new SectionException("Не задано название раздела");
		}

		checkRoot(root, parentId);
		sectionDomainService.save(section);
		return section;
	}

	public SectionDomain deleteSection(SectionDomain section, SectionDomain root, User editor) {
		if (section.getParentId() == null) {
			throw new SectionException("Запрещено удаление корневых разделов");
		}
		if (section.getChildren().size() > 0) {
			throw new SectionException("Имеются вложенные разделы, удаление невозможно");
		}
		checkRoot(root, section.getParentId());
		sectionDomainService.delete(section.getId());
		return section;
	}

	public SectionDomain editSection(SectionDomain section, SectionDomain root, User editor) {
		if (StringUtils.isEmpty(section.getTitle())) {
			throw new SectionException("Не задано название раздела");
		}

		/*if (section.getApplication() != null) {
			Section applicationSection = sectionDao.getByApplication(section.getApplication());
			if (applicationSection != null && !applicationSection.equals(section)) {
				String hierarchy = "";
				for (Section s : sectionDao.getHierarchy(applicationSection)) {
					if (!StringUtils.isEmpty(hierarchy)) {
						hierarchy += " > ";
					}
					hierarchy += s.getTitle();
				}
				throw new SectionException("Выбранное приложение уже привязано к разделу меню " + hierarchy);
			}
		}*/

		if (StringUtils.isEmpty(section.getLink())) {
			section.setLink(null);
		}
		checkRoot(root, section.getId());
		checkLink(root, section);
		sectionDomainService.save(section);
		return section;
	}

}
