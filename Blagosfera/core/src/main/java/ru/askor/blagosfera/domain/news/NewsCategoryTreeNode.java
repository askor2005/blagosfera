package ru.askor.blagosfera.domain.news;

import ru.radom.kabinet.dto.news.NewsCategoryDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Узел дерева категорий новостей в представлении domain слоя
 */
public class NewsCategoryTreeNode {

    private Long id;
    private String title;
    private String description;
    private String key;
    private Integer position;
    private List<NewsCategoryTreeNode> childrenNodes = new ArrayList<>();


    /*
     * --------->CONSTRUCTORS REGION<-------------
     */
    public NewsCategoryTreeNode() {}

    public NewsCategoryTreeNode(NewsCategoryDto dto) {
        id = dto.getId();
        title = dto.getText();
        description = dto.getDescription();
        key = dto.getKey();
        position = dto.getPosition();
    }

    public NewsCategoryTreeNode(ru.radom.kabinet.dto.news.editing.NewsCategoryDto dto) {
        id = dto.getId();
    }
    /*
     * --------->CONSTRUCTORS REGION<-------------
     */

    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<NewsCategoryTreeNode> getChildrenNodes() {
        return childrenNodes;
    }

    public void setChildrenNodes(List<NewsCategoryTreeNode> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }
    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */


    public NewsCategoryDto toAdminsDto() {
        NewsCategoryDto result = new NewsCategoryDto();

        result.setId(id);
        result.setText(title);
        result.setExpanded(false);

        for (NewsCategoryTreeNode newsCategoryTreeNode : childrenNodes) {
            result.getChildren().add(newsCategoryTreeNode.toAdminsDto());
        }

        result.setDescription(description);
        result.setKey(key);
        result.setPosition(position);

        return result;
    }

    public ru.radom.kabinet.dto.news.editing.NewsCategoryDto toUsersDto() {
        ru.radom.kabinet.dto.news.editing.NewsCategoryDto result = new ru.radom.kabinet.dto.news.editing.NewsCategoryDto();

        result.setId(id);
        result.setTitle(title);

        return result;
    }
}
