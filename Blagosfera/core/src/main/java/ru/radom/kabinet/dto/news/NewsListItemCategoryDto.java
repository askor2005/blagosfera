package ru.radom.kabinet.dto.news;

import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

public class NewsListItemCategoryDto {

    public Long id;
    public String text;
    public NewsListItemCategoryDto parent;

    public NewsListItemCategoryDto() {
    }

    public static NewsListItemCategoryDto toDto(ListEditorItem listEditorItem) {
        NewsListItemCategoryDto result = new NewsListItemCategoryDto();

        result.id = listEditorItem.getId();
        result.text = listEditorItem.getText();

        if (listEditorItem.getParent() != null) {
            result.parent = toDto(listEditorItem.getParent());
        }

        return result;
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
}
