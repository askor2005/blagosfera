package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Created by vgusev on 20.03.2016.
 */
@Data
public class CommunityPermission implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String name;

    private int position;

    private String description;

    /**
     * Формы объединения групп
     */
    private List<ListEditorItem> associationForms;

    /**
     * Коллекция объединений, которым вне зависимости от формы объединения выдана роль (сделано для security ролей)
     */
    private List<Community> communities;

    private boolean securityRole;


}
