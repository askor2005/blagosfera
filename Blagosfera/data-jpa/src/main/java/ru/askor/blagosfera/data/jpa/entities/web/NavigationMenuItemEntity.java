package ru.askor.blagosfera.data.jpa.entities.web;

import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItem;
import ru.askor.blagosfera.domain.web.navigation.NavigationMenuItemType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 19.04.2016.
 */
@Entity
@Table(name = "nav_menu_item")
public class NavigationMenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nav_menu_item_id_generator")
    @SequenceGenerator(name = "nav_menu_item_id_generator", sequenceName = "nav_menu_item_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NavigationMenuItemType type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "title")
    private String title;

    @Column(name = "icon")
    private String icon;

    @Column(name = "path")
    private String path;

    @Column(name = "expandable")
    private Boolean expandable;

    @Column(name = "collapsed")
    private Boolean collapsed;

    @Column(name = "switch_menu")
    private Boolean switchMenu;

    @Column(name = "lazy_load")
    private Boolean lazyLoad;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NavigationMenuItemEntity> items = new ArrayList<>();

    @Column(name = "visible")
    private Boolean visible;

    public NavigationMenuItemEntity() {
    }

    public NavigationMenuItem toDomain() {
        NavigationMenuItem navigationMenuItem = new NavigationMenuItem();
        navigationMenuItem.setId(getId());
        navigationMenuItem.setType(getType());
        navigationMenuItem.setParentId(getParentId());
        navigationMenuItem.setTitle(getTitle());
        navigationMenuItem.setIcon(getIcon());
        navigationMenuItem.setPath(getPath());
        navigationMenuItem.setExpandable(isExpandable());
        navigationMenuItem.setCollapsed(isCollapsed());
        navigationMenuItem.setSwitchMenu(isSwitchMenu());
        navigationMenuItem.setLazyLoad(isLazyLoad());

        if (!isLazyLoad()) {
            List<NavigationMenuItem> items = new ArrayList<>();

            for (NavigationMenuItemEntity itemEntity : getItems()) {
                items.add(itemEntity.toDomain());
            }

            navigationMenuItem.setItems(items);
        }

        return navigationMenuItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NavigationMenuItemEntity)) return false;

        NavigationMenuItemEntity that = (NavigationMenuItemEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NavigationMenuItemType getType() {
        return type;
    }

    public void setType(NavigationMenuItemType type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(Boolean expandable) {
        this.expandable = expandable;
    }

    public Boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    public Boolean isSwitchMenu() {
        return switchMenu;
    }

    public void setSwitchMenu(Boolean switchMenu) {
        this.switchMenu = switchMenu;
    }

    public Boolean isLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(Boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public List<NavigationMenuItemEntity> getItems() {
        return items;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
