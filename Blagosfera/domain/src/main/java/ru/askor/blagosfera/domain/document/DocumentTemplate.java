package ru.askor.blagosfera.domain.document;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 16.06.2015.
 * Вспомогательный POJO класс для работы с шаблонами документов.
 */
@Data
public class DocumentTemplate {

    private Long id;

    private User creator;

    private DocumentClass documentClass;

    //наименование шаблона
    private String name;

    private String documentShortName;

    // Полное название итогового документа
    private String documentName;

    //содержимое шаблона
    private String content;

    // Код шаблона. Нужен для получения шаблона в коде.
    private String code;

    //фильтры шаблона
    //private List<DocumentTemplateFilterValue> filters;

    // Участники шаблона документа
    private List<DocumentTemplateParticipant> documentTemplateParticipants;

    // Ссылка на страницу описания шаблона
    private String helpLink;

    // Сортировка
    private Integer position;

    private String pdfExportArguments;

}
