package ru.radom.kabinet.web.flowofdocuments.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

import java.util.*;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class DocumentClassGridDto {

    private String text;

    private boolean isSuccess;

    private List<DocumentClassGridItemDto> children = new ArrayList<>();

    private DocumentClassGridDto(){}

    public DocumentClassGridDto(List<DocumentClassEntity> documentClasses, List<DocumentClassEntity> searchList, Set<Long> elementIdsForExpand) {
        setText(".");

        for (DocumentClassEntity documentClass : documentClasses) {
            DocumentClassGridItemDto documentClassGridItemDto = new DocumentClassGridItemDto();
            boolean needAddItem = true;

            boolean hasChild = documentClass.getChild() != null && documentClass.getChild().size() > 0;

            if (searchList != null) {
                if (hasChild && elementIdsForExpand.contains(documentClass.getId())) {
                    documentClassGridItemDto.setExpanded(true);
                } else if (searchList.contains(documentClass)) {
                    if (hasChild) {
                        documentClassGridItemDto.setExpanded(false);
                    } else {
                        documentClassGridItemDto.setChildren(Collections.emptyList());
                    }
                } else {
                    needAddItem = false;
                }
            } else {
                if (hasChild) {
                    documentClassGridItemDto.setExpanded(false);
                } else {
                    documentClassGridItemDto.setChildren(Collections.emptyList());
                }
            }

            documentClassGridItemDto.setId(documentClass.getId());
            documentClassGridItemDto.setKey(documentClass.getKey());
            documentClassGridItemDto.setName(documentClass.getName());
            documentClassGridItemDto.setPosition(documentClass.getPosition());
            if (documentClass.getParent() == null) {
                documentClassGridItemDto.setPathName("");
            } else {
                documentClassGridItemDto.setPathName(getPathNameDocumentType(documentClass.getParent()));
            }
            if (documentClass.getParent() != null) {
                documentClassGridItemDto.setParentId(documentClass.getParent().getId());
                documentClassGridItemDto.setParentName(documentClass.getParent().getName());
            }
            if (needAddItem ) {
                children.add(documentClassGridItemDto);
            }
        }
        setSuccess(true);
    }

    public static DocumentClassGridDto toError() {
        DocumentClassGridDto result = new DocumentClassGridDto();
        result.setSuccess(false);
        return result;
    }

    private String getPathNameDocumentType(DocumentClassEntity documentType){
        List<String> list = new LinkedList<>();
        list.add(documentType.getName());
        while(documentType.getParent() != null) {
            documentType = documentType.getParent();
            list.add(documentType.getName());
        }
        list = Lists.reverse(list);
        return StringUtils.join(list, " / ");
    }

}
