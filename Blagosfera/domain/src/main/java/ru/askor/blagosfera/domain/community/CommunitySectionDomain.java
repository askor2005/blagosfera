package ru.askor.blagosfera.domain.community;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Created by vgusev on 12.03.2016.
 */
@Data
public class CommunitySectionDomain implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    /**
     * для отображения пользователям, пишем по-русски
     */
    private String title;

    private String link;

    private String permission;

    private int position;

    private CommunitySectionDomain parent;

    private List<CommunitySectionDomain> children;

    private boolean guestAccess = false;
}
