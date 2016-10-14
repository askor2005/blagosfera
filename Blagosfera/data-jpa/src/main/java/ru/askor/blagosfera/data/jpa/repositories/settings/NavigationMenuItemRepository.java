package ru.askor.blagosfera.data.jpa.repositories.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.askor.blagosfera.data.jpa.entities.web.NavigationMenuItemEntity;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;

import java.util.List;

public interface NavigationMenuItemRepository extends JpaRepository<NavigationMenuItemEntity, Long>, JpaSpecificationExecutor<NavigationMenuItemEntity> {

    List<NavigationMenuItemEntity> findAllByParentIdNullAndVisibleTrueAndTypeOrderByIdAsc(NavigationMenuItemType type);

    List<NavigationMenuItemEntity> findAllByParentIdAndVisibleTrueOrderByIdAsc(Long parentId);
}
