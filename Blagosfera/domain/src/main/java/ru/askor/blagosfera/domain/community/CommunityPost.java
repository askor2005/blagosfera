package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 20.03.2016.
 */
@Data
public class CommunityPost implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private int position;

    private int vacanciesCount;

    private String mnemo;

    private Community community;

    private List<CommunityMember> members;

    private List<CommunityPermission> permissions;

    private boolean isCeo;

    private CommunitySchemaUnit schemaUnit;

    private String appointBehavior;

    /**
     *
     */
    private List<DocumentTemplateSetting> documentTemplateSettings = new ArrayList<>();
}
