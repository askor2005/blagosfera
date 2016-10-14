package ru.radom.kabinet.document.web.dto;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 16.06.2016.
 */
public class DocumentClassDataSourceDto {

    public Long id;

    public String name;

    public ParticipantsTypes type;

    public DocumentClassDataSourceDto(DocumentClassDataSource documentClassDataSource) {
        id = documentClassDataSource.getId();
        name = documentClassDataSource.getName();
        type = documentClassDataSource.getType();
    }

    public static List<DocumentClassDataSourceDto> toDtoList(List<DocumentClassDataSource> documentClassDataSources) {
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
