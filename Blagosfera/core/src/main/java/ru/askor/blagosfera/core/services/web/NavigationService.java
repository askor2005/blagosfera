package ru.askor.blagosfera.core.services.web;

import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItem;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;

import java.util.List;

/**
 * Created by mnikitin on 19.04.2016.
 */
public interface NavigationService {

    List<NavigationMenuItem> loadNavMenuRoots(NavigationMenuItemType type);

    List<NavigationMenuItem> loadNavMenu(Long menuItemId);

    NavigationMenuItem loadNavMenuItem(Long menuItemId);
}
