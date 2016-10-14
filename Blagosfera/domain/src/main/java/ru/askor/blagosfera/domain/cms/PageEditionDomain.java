package ru.askor.blagosfera.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by vtarasenko on 01.04.2016.
 * модель для записи о редактировании страницы
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEditionDomain {
    private Long id;
    private Long pageId;
    private Long editorId;
    private Date date;
}
