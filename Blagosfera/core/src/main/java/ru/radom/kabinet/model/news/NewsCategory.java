package ru.radom.kabinet.model.news;

import ru.askor.blagosfera.domain.news.NewsCategoryTreeNode;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 * Сущность, олицетворяющая категорию новости
 */
@Deprecated
@Entity
@Table (name = "news_categories")
public class NewsCategory extends LongIdentifiable {


    /**
     * Наименование категории новостей
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * Описание категории новостей
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Ключ категории новостей
     */
    @Column(name = "key", length = 200, unique = true, nullable = false)
    private String key;

    /**
     * Родительская категория, находящаяся на один уровень выше по иерархии дерева
     */
    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private NewsCategory parent;

    /**
     * Позиция в дереве (используется в процессе визуализации)
     */
    @Column(name = "position", columnDefinition = "int default 0")
    private Integer position;


    /*
     * --------->CONSTRUCTORS REGION<-------------
     */
    public NewsCategory() {}

    public NewsCategory(NewsCategoryTreeNode domain, NewsCategory parent) {
        this.title = domain.getTitle();
        this.description = domain.getDescription();
        this.key = domain.getKey();
        this.position = domain.getPosition();
        this.parent = parent;
    }
    /*
     * --------->END CONSTRUCTORS REGION<-------------
     */


    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
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

    public NewsCategory getParent() {
        return parent;
    }

    public void setParent(NewsCategory parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */


    public NewsCategoryTreeNode toDomain() {
        NewsCategoryTreeNode result = new NewsCategoryTreeNode();

        result.setId(getId());
        result.setTitle(title);
        result.setDescription(description);
        result.setKey(key);
        result.setPosition(position);

        return result;
    }
}
