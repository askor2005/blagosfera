package ru.askor.blagosfera.domain.common;

import lombok.Data;

/**
 *
 * Domain класс для тегов
 */
@Data
public class Tag {

    private Long id;
    private String text;
    private Long usageCount = 0l;

    public Tag() { }

    public Tag(String text) {
        this.text = text;
    }

}
