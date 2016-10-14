package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 07.07.2016.
 */
@Data
public class DocumentTemplateSaveRequestDto {

    public Long id;

    public Long documentClassId;

    //наименование шаблона
    public String name;

    public String documentShortName;

    // Полное название итогового документа
    public String documentName;

    //содержимое шаблона
    public String content;

    // Код шаблона. Нужен для получения шаблона в коде.
    public String code;

    // Участники шаблона документа
    public List<DocumentTemplateParticipantDto> templateParticipants;

    // Ссылка на страницу описания шаблона
    public String helpLink;

    // Сортировка
    public Integer position;

    public String pdfExportArguments;
}
