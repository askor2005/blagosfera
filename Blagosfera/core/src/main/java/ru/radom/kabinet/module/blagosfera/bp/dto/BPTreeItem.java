package ru.radom.kabinet.module.blagosfera.bp.dto;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Узел дерева процессов
 */
public abstract class BPTreeItem {

    /**
     * Id
     */
    private Long id;

    /**
     * Текст узла
     */
    private String text;

    public BPTreeItem(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public BPTreeItem() {
    }

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

    /**
     * Тип узла
     */
    public abstract String getType();
}
