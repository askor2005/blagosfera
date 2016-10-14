package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsCategoryRepository;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.news.NewsCategoryService;

@Controller("subportalsController")
public class SubportalsController {
	@Autowired
	private ListEditorItemDomainService listEditorItemDomainService;

	/**
	 * Страница с новостями
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/news", method = RequestMethod.GET)
	public String showNewsPage(Model model) {
		ListEditorItem listEditorItem = listEditorItemDomainService.getByCode("common_news_category");
		model.addAttribute("defaultCategoryId",listEditorItem != null ? listEditorItem.getId() : null);
		return "blagosferaNews";
	}


/*
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showNewsPage(Model model) {
		return "blagosferaNews";
	}
	
	@RequestMapping(value = "/radom", method = RequestMethod.GET)
	public String showRadomPage(Model model) {
		return "radom";
	}
	
	@RequestMapping(value = "/ramera", method = RequestMethod.GET)
	public String showRameraPage(Model model) {
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Лента новостей", "/feed"));
		return "ramera";
	}

	@RequestMapping(value = "/razum", method = RequestMethod.GET)
	public String showRazumPage(Model model) {
		return "razum";
	}
	
	@RequestMapping(value = "/raven", method = RequestMethod.GET)
	public String showRavenPage(Model model) {
		return "raven";
	}
	
	@RequestMapping(value = "/radost", method = RequestMethod.GET)
	public String showRadostPage(Model model) {
		return "radost";
	}

	@RequestMapping(value = "/blagosfera_citizenship", method = RequestMethod.GET)
	public String showSitizenshipPage(Model model) {
		return "blagosferaCitizenship";
	}*/
	
	
	
}
