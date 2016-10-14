package ru.askor.blagosfera.domain.section;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 15.03.2016.
 */
@Data
public class HelpSectionDomain implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private boolean published;

    private Long pageId;

    private Long parentId;

    private String title;
}
