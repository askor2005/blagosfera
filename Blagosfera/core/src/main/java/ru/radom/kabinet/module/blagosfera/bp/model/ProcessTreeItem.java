package ru.radom.kabinet.module.blagosfera.bp.model;

import org.hibernate.annotations.Formula;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Узел дерева процессов
 */
@Entity
@Table(name = "bp_process_tree")
public class ProcessTreeItem extends LongIdentifiable {

    /**
     * Имя узла
     */
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    /**
     * Позиция узла в предке
     */
    @Column(name = "position", nullable = false)
    private long position;

    /**
     * Id модели бизнесс процесса, у папки пустой
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private BPModel model;

    /**
     * Родительская папка
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    private ProcessTreeItem parent;

    @OrderBy("position")
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<ProcessTreeItem> children;

    @Formula("(select count(1) from bp_process_tree c where c.parent_id = id)")
    private int childrenCount = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public BPModel getModel() {
        return model;
    }

    public void setModel(BPModel model) {
        this.model = model;
    }

    public ProcessTreeItem getParent() {
        return parent;
    }

    public void setParent(ProcessTreeItem parent) {
        this.parent = parent;
    }

    public List<ProcessTreeItem> getChildren() {
        return children;
    }

    public void setChildren(List<ProcessTreeItem> children) {
        this.children = children;
    }

    public int getChildrenCount() {
        return childrenCount;
    }
}
