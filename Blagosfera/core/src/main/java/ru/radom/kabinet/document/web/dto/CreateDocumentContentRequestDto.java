package ru.radom.kabinet.document.web.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.web.TemplateParameters;

import java.util.List;

/**
 * Запрос на создание контента документа в админке
 * Created by vgusev on 07.04.2016.
 */
@Data
public class CreateDocumentContentRequestDto {

    private Long templateId = null;

    private List<CreateDocumentParameter> createDocumentParameters;
}
