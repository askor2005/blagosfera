package ru.radom.kabinet.web.flowofdocuments.dto;

import ru.askor.blagosfera.domain.document.DocumentClass;
import ru.radom.kabinet.document.model.DocumentClassEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 31.05.2016.
 */
public class DocumentClassDto {

    public Long id;
    public Long parentId;
    public String parentName;
    public String name;
    public String key;
    public List<DocumentClassDataSourceDto> documentClassDataSources;

    public static DocumentClassDto toDto(DocumentClassEntity documentClassEntity) {
        DocumentClassDto result = null;
        if (documentClassEntity != null) {
            result = new DocumentClassDto();
            result.id = documentClassEntity.getId();
            if (documentClassEntity.getParent() != null) {
                result.parentId = documentClassEntity.getParent().getId();
                result.parentName = documentClassEntity.getParent().getName();
            }
            result.name = documentClassEntity.getName();
            result.key = documentClassEntity.getKey();
        }
        return result;
    }

    public static DocumentClassDto toDto(DocumentClass documentClass) {
        DocumentClassDto result = null;
        if (documentClass != null) {
            result = new DocumentClassDto();
            result.id = documentClass.getId();
            result.name = documentClass.getName();
            result.key = documentClass.getKey();
            result.documentClassDataSources = DocumentClassDataSourceDto.toListDtoFromDomainList(documentClass.getDataSources());
        }
        return result;
    }
}
