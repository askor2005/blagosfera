package ru.askor.blagosfera.web.controllers.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.askor.blagosfera.core.services.web.NavigationService;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItem;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;
import ru.askor.blagosfera.web.controllers.navigation.dto.NavigationMenuItemDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 14.04.2016.
 */
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @RequestMapping(value = "/navigationMenu.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public NavigationMenuItemDto navigationMenu(@RequestParam(name = "menuItemId", required = false) Long menuItemId,
                                                @RequestParam(name = "type", required = false) NavigationMenuItemType type) {
        NavigationMenuItemDto result = new NavigationMenuItemDto();
        List<NavigationMenuItem> menuRoots = new ArrayList<>();

        if (type != null) {
            menuRoots.addAll(navigationService.loadNavMenuRoots(type));
        } else if (menuItemId != null) {
            NavigationMenuItem item = navigationService.loadNavMenuItem(menuItemId);
            Assert.notNull(item);

            result = new NavigationMenuItemDto(item);

            if (item.isLazyLoad()) menuRoots.addAll(navigationService.loadNavMenu(menuItemId));
        }

        for (NavigationMenuItem item : menuRoots) {
            result.items.add(new NavigationMenuItemDto(item));
        }

        return result;
    }
}
