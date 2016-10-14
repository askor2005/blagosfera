package ru.askor.blagosfera.core.services.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.entities.web.NavigationMenuItemEntity;
import ru.askor.blagosfera.data.jpa.repositories.settings.NavigationMenuItemRepository;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItem;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 19.04.2016.
 */
@Transactional
@Service("navigationService")
public class NavigationServiceImpl implements NavigationService {

    @Autowired
    private NavigationMenuItemRepository navigationMenuItemRepository;

    public NavigationServiceImpl() {
    }

    @Override
    public List<NavigationMenuItem> loadNavMenuRoots(NavigationMenuItemType type) {
        return toDomain(navigationMenuItemRepository.findAllByParentIdNullAndVisibleTrueAndTypeOrderByIdAsc(type));
    }

    @Override
    public List<NavigationMenuItem> loadNavMenu(Long menuItemId) {
        return toDomain(navigationMenuItemRepository.findAllByParentIdAndVisibleTrueOrderByIdAsc(menuItemId));
    }

    @Override
    public NavigationMenuItem loadNavMenuItem(Long menuItemId) {
        NavigationMenuItemEntity item = navigationMenuItemRepository.findOne(menuItemId);
        return item == null ? null : item.toDomain();
    }

    private List<NavigationMenuItem> toDomain(List<NavigationMenuItemEntity> entities) {
        List<NavigationMenuItem> result = new ArrayList<>();

        for (NavigationMenuItemEntity entity : entities) {
            result.add(entity.toDomain());
        }

        return result;
    }
}
