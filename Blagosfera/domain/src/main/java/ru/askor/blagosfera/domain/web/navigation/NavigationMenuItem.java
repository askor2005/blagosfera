package ru.askor.blagosfera.domain.web.navigation;

import java.util.List;

/**
 * Created by Maxim Nikitin on 14.04.2016.
 */
public class NavigationMenuItem {

    private Long id;
    private NavigationMenuItemType type;
    private Long parentId;
    private String title;
    private String icon;
    private String path;
    private Boolean expandable;
    private Boolean collapsed;
    private Boolean switchMenu;
    private Boolean lazyLoad;
    private List<NavigationMenuItem> items;

    public NavigationMenuItem() {
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

    public List<NavigationMenuItem> getItems() {
        return items;
    }

    public void setItems(List<NavigationMenuItem> items) {
        this.items = items;
    }
}
