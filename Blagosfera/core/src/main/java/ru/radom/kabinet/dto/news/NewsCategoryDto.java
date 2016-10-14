package ru.radom.kabinet.dto.news;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO категории новостей, представляющий собой один из узлов дерева ExtJS tree
 */
public class NewsCategoryDto {

    //Идентификатор узла
    private Long id;

    //Текст узла
    private String text;

    //Указание на то, что узел развернут
    private boolean expanded;

    //Дети узла
    private List<NewsCategoryDto> children = new ArrayList<>();

    //Описание узла
    private String description;

    //Ключ узла
    private String key;

    //Позиция узла на своем уровне
    private int position;



    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<NewsCategoryDto> getChildren() {
        return children;
    }

    public void setChildren(List<NewsCategoryDto> children) {
        this.children = children;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */
}
