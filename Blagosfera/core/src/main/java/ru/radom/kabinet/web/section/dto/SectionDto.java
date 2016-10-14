package ru.radom.kabinet.web.section.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.section.SectionAccessType;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.section.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.03.2016.
 */
@Data
public class SectionDto {

    private Long id;

    private String name;

    /**
     * для отображения пользователям, пишем по-русски
     */
    private String title;

    private String link;

    private String icon;

    private String hint;

    private int position;

    private List<SectionDto> children;

    private boolean isPublished;

    private String helpLink;

    private String imageUrl;

    private boolean isVisible;

    private Map<String, Object> details;

    private boolean isHelpExists;

    private boolean isHelpPublished;

    private boolean isActive;

    private boolean isEditable;

    private SectionAccessType accessType;

    private SectionType type;
    private boolean openInNewLink;
    private boolean disabled;
    private Integer minRegistratorLevelToShow;
    private boolean showToAdminUsersOnly;
    private boolean showToVerfiedUsersOnly;
    private boolean showToAuthorizedUsersOnly;


    public static SectionDto toDto(SectionDomain section, boolean withChild) {
        SectionDto result = new SectionDto();
        result.setMinRegistratorLevelToShow(section.getMinRegistratorLevelToShow());
        result.setShowToAdminUsersOnly(section.isShowToAdminUsersOnly());
        result.setShowToVerfiedUsersOnly(section.isShowToVerfiedUsersOnly());
        result.setDisabled(section.isDisabled());
        result.setOpenInNewLink(section.isOpenInNewLink());
        result.setId(section.getId());
        result.setName(section.getName());
        result.setTitle(section.getTitle());
        result.setLink(section.getLink());
        result.setIcon(section.getIcon());
        result.setHint(section.getHint());
        if (withChild) {
            result.setChildren(toDtoList(section.getChildren(), false));
        }
        result.setPublished(section.isPublished());
        result.setHelpLink(section.getHelpLink());
        result.setImageUrl(section.getImageUrl());
        result.setVisible(section.isVisible());
        result.setDetails(section.getDetails());
        result.setEditable(section.isEditable());
        result.setType(section.getType());
        result.setAccessType(section.getAccessType());
        result.setMinRegistratorLevelToShow(section.getMinRegistratorLevelToShow());
        result.setShowToAdminUsersOnly(section.isShowToAdminUsersOnly());
        result.setShowToVerfiedUsersOnly(section.isShowToVerfiedUsersOnly());
        result.setDisabled(section.isDisabled());
        result.setShowToAuthorizedUsersOnly(section.isShowToAuthorizedUsersOnly());
        return result;
    }

    public static List<SectionDto> toDtoList(List<SectionDomain> sections, boolean withChild) {
        List<SectionDto> result = null;
        if (sections != null) {
            result = new ArrayList<>();
            for (SectionDomain section : sections) {
                result.add(toDto(section, withChild));
            }
        }
        return result;
    }

    public void setOpenInNewLink(boolean openInNewLink) {
        this.openInNewLink = openInNewLink;
    }


    public void setMinRegistratorLevelToShow(Integer minRegistratorLevelToShow) {
        this.minRegistratorLevelToShow = minRegistratorLevelToShow;
    }

    public void setShowToAdminUsersOnly(boolean showToAdminUsersOnly) {
        this.showToAdminUsersOnly = showToAdminUsersOnly;
    }

    public void setShowToVerfiedUsersOnly(boolean showToVerfiedUsersOnly) {
        this.showToVerfiedUsersOnly = showToVerfiedUsersOnly;
    }
}
