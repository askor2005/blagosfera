package ru.askor.blagosfera.web.controllers.navigation.dto;

import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItem;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 14.04.2016.
 */
public class NavigationMenuItemDto {

    public Long id = 0L;
    public NavigationMenuItemType type = null;
    public Long parentId = null;
    public String title = null;
    public String icon = null;
    public String path = null;
    public Boolean expandable = false;
    public Boolean collapsed = false;
    public Boolean switchMenu = false;
    public Boolean lazyLoad = false;
    public List<NavigationMenuItemDto> items = new ArrayList<>();

    public NavigationMenuItemDto() {
    }

    public NavigationMenuItemDto(NavigationMenuItem navigationMenuItem) {
        id = navigationMenuItem.getId();
        type = navigationMenuItem.getType();
        parentId = navigationMenuItem.getParentId();
        title = navigationMenuItem.getTitle();
        icon = navigationMenuItem.getIcon();
        path = navigationMenuItem.getPath();
        expandable = navigationMenuItem.isExpandable();
        collapsed = navigationMenuItem.isCollapsed();
        switchMenu = navigationMenuItem.isSwitchMenu();
        lazyLoad = navigationMenuItem.isLazyLoad();

        if (navigationMenuItem.getItems() != null) {
            for (NavigationMenuItem item :navigationMenuItem.getItems()) {
                items.add(new NavigationMenuItemDto(item));
            }
        }
    }
}
