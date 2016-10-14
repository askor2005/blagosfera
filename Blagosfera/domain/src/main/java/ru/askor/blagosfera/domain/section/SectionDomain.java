package ru.askor.blagosfera.domain.section;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 14.03.2016.
 */
@Deprecated
@Data
public class SectionDomain implements Serializable {

    public static final long serialVersionUID = 1L;

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

    private List<SectionDomain> children;

    private boolean isPublished;

    private String helpLink;

    private String imageUrl;

    private boolean isVisible;

    private String forwardUrl;

    private Long pageId;

    private SectionAccessType accessType;

    private SectionType type;

    private boolean isCanSetForwardUrl;

    private Long parentId;

    private boolean isEditable;

    private Map<String, Object> details = new HashMap<>();
    private boolean openInNewLink = false;
    private Integer minRegistratorLevelToShow;
    private boolean showToAdminUsersOnly = false;
    //показывать данную секцию только админам
    private boolean showToVerfiedUsersOnly = false;
    private boolean disabled = false;
    private boolean showToAuthorizedUsersOnly = false;

}
