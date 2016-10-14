package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.AssociationFormSearchType;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class DocumentClassDataSourceDto {

    private Long id;

    private String participantType;

    private String participantName;

    private List<String> filters;

    private DataSourceAssociationFormDto associationForm;

    private AssociationFormSearchType associationFormSearchType;

    public DocumentClassDataSourceDto(DocumentClassDataSourceEntity documentClassDataSource) {
        setId(documentClassDataSource.getId());
        setParticipantType(documentClassDataSource.getParticipantType());
        setParticipantName(documentClassDataSource.getParticipantName());
        setFilters(Collections.emptyList());
        setAssociationForm(new DataSourceAssociationFormDto(documentClassDataSource.getAssociationForm()));
        setAssociationFormSearchType(
                documentClassDataSource.getAssociationFormSearchType() == null ?
                        AssociationFormSearchType.SEARCH_SUB_STRUCTURES : documentClassDataSource.getAssociationFormSearchType()
        );
    }

    public static List<DocumentClassDataSourceDto> toListDto(List<DocumentClassDataSourceEntity> documentClassDataSources) {
        List<DocumentClassDataSourceDto> result = null;
        if (documentClassDataSources != null) {
            result = new ArrayList<>();
            for (DocumentClassDataSourceEntity documentClassDataSource : documentClassDataSources) {
                result.add(new DocumentClassDataSourceDto(documentClassDataSource));
            }
        }
        return result;
    }


    public DocumentClassDataSourceDto(DocumentClassDataSource documentClassDataSource) {
        setId(documentClassDataSource.getId());
        setParticipantType(documentClassDataSource.getType().getName());
        setParticipantName(documentClassDataSource.getName());
        setFilters(Collections.emptyList());
        setAssociationForm(new DataSourceAssociationFormDto(documentClassDataSource.getAssociationForm()));
        setAssociationFormSearchType(
                documentClassDataSource.getAssociationFormSearchType() == null ?
                        AssociationFormSearchType.SEARCH_SUB_STRUCTURES : documentClassDataSource.getAssociationFormSearchType()
        );
    }

    public static List<DocumentClassDataSourceDto> toListDtoFromDomainList(List<DocumentClassDataSource> documentClassDataSources) {
        List<DocumentClassDataSourceDto> result = null;
        if (documentClassDataSources != null) {
            result = new ArrayList<>();
            for (DocumentClassDataSource documentClassDataSource : documentClassDataSources) {
                result.add(new DocumentClassDataSourceDto(documentClassDataSource));
            }
        }
        return result;
    }
}
