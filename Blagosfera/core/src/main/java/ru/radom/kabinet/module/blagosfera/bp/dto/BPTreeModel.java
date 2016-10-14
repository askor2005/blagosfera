package ru.radom.kabinet.module.blagosfera.bp.dto;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Модель бизнесс процесса в дереве процессов
 */
public class BPTreeModel extends BPTreeItem {
    /**
     * Id модели на которую ссылается элемент дерева
     */
    private Long modelId;

    public BPTreeModel() {
        super();
    }

    public BPTreeModel(Long id, String text, Long modelId) {
        super(id, text);
        this.modelId = modelId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Override
    public String getType() {
        return "model";
    }
}
