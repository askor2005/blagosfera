package ru.askor.blagosfera.domain.cms;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * модель статичной страницы
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Page {
    private Long id;
    private String content;
    private String title;
    private String description;
    private String keywords;
    private int editionsCount;
    private Date currentEditorEditDate;
    private Long currentEditorId;
}
