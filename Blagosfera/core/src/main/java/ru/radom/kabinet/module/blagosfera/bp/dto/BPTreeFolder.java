package ru.radom.kabinet.module.blagosfera.bp.dto;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Каталог в дереве процессов
 */
public class BPTreeFolder extends BPTreeItem {

    /**
     * Есть ли в этом каталоги дети
     */
    private boolean children = false;

    public BPTreeFolder() {
        super();
    }

    public BPTreeFolder(Long id, String text, boolean children) {
        super(id, text);
        this.children = children;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    @Override
    public String getType() {
        return "folder";
    }
}
