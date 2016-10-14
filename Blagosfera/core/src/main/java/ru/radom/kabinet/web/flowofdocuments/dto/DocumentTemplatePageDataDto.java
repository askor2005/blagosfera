package ru.radom.kabinet.web.flowofdocuments.dto;

import ru.askor.blagosfera.domain.document.DocumentTemplate;

import java.util.List;

/**
 *
 * Created by vgusev on 07.07.2016.
 */
public class DocumentTemplatePageDataDto {

    public Long id;

    //public User creator;

    //public DocumentClass documentClass;

    public String creatorFullName;

    public Long documentClassId;

    public String documentClassName;

    public String classDocumentPath;

    //наименование шаблона
    public String name;

    public String documentShortName;

    // Полное название итогового документа
    public String documentName;

    //содержимое шаблона
    public String content;

    // Код шаблона. Нужен для получения шаблона в коде.
    public String code;

    //фильтры шаблона
    //public List<DocumentTemplateFilterValue> filters;

    // Участники шаблона документа
    public List<DocumentTemplateParticipantDto> templateParticipants;

    // Ссылка на страницу описания шаблона
    public String helpLink;

    // Сортировка
    public Integer position;

    public String pdfExportArguments;
    
    public DocumentTemplatePageDataDto(DocumentTemplate documentTemplate) {
        id = documentTemplate.getId();
        //public User creator;
        creatorFullName = documentTemplate.getCreator().getFullName();
        documentClassId = documentTemplate.getDocumentClass().getId();
        documentClassName = documentTemplate.getDocumentClass().getName();
        name = documentTemplate.getName();
        documentShortName = documentTemplate.getDocumentShortName();
        documentName = documentTemplate.getDocumentName();
        content = documentTemplate.getContent();
        code = documentTemplate.getCode();
        helpLink = documentTemplate.getHelpLink();
        position = documentTemplate.getPosition();
        templateParticipants = DocumentTemplateParticipantDto.toDtoList(documentTemplate.getDocumentTemplateParticipants());
        classDocumentPath = documentTemplate.getDocumentClass().getName();
        pdfExportArguments = documentTemplate.getPdfExportArguments();
    }
}
