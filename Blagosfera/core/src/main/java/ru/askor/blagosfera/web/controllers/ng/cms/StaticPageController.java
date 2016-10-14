package ru.askor.blagosfera.web.controllers.ng.cms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.core.services.web.NavigationService;
import ru.askor.blagosfera.domain.cms.Page;


/**
 * Created by Maxim Nikitin on 31.03.2016.
 */
@RestController
@RequestMapping("/api/p")
public class StaticPageController {

    public static final String PAGE_NOT_FOUND_MESSAGE = "Страница не найдена";

    @Autowired
    private PagesService pagesService;

    @Autowired
    private NavigationService navigationService;

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/welcome", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String welcome() {
        return getByPath("welcome");
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/contacts", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String contacts() {
        return getByPath("contacts");
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/partners", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String partners() {
        return getByPath("partners");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{pageId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String community(@PathVariable("pageId") Long pageId) {
        Page page = pagesService.getById(pageId);
        return page != null ? page.getContent() : PAGE_NOT_FOUND_MESSAGE;
    }

    private String getByPath(String path) {
        Page page = pagesService.getByPath(path);
        return page != null ? page.getContent() : PAGE_NOT_FOUND_MESSAGE;
    }
}
