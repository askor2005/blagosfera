package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import ru.radom.kabinet.document.model.DocumentClassEntity;

import java.util.*;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class DocumentClassTreeForTemplatesDto {

    private String text;

    private boolean isSuccess;

    private List<DocumentClassGridItemDto> children = new ArrayList<>();

    private DocumentClassTreeForTemplatesDto(){}

    public DocumentClassTreeForTemplatesDto(DocumentClassEntity parentClass, Set<Long> classOfDocumentsIds, Map<Long, DocumentClassEntity> documentClasses, String searchName, Map<Long, Integer> countDocumentsMap) {
        Map<DocumentClassEntity, Boolean> levelDocumentTypes = new HashMap<>();
        for (Long classOfDocumentsId : classOfDocumentsIds) {
            DocumentClassEntity documentType = documentClasses.get(classOfDocumentsId);
            if (documentType == null) {
                continue;
            }
            boolean hasChild = false;
            boolean find = false;
            if (searchName != null) {
                find = StringUtils.containsIgnoreCase(documentType.getName(), searchName);
            }
            if (parentClass != null) {
                while (documentType.getParent() != null && parentClass.getId().longValue() != documentType.getParent().getId().longValue()) {
                    documentType = documentType.getParent();
                    hasChild = true;
                    if (searchName != null) {
                        find = find || StringUtils.containsIgnoreCase(documentType.getName(), searchName);
                    }
                }
                if (documentType.getParent() == null) {
                    documentType = null;
                }
            } else {
                while (documentType.getParent() != null) {
                    documentType = documentType.getParent();
                    hasChild = true;
                    if (searchName != null) {
                        find = find || StringUtils.containsIgnoreCase(documentType.getName(), searchName);
                    }
                }
            }

            if (searchName != null && !find) {
                documentType = null;
            }

            if (documentType != null) {
                if (levelDocumentTypes.containsKey(documentType)) {
                    levelDocumentTypes.put(documentType, levelDocumentTypes.get(documentType) || hasChild);
                } else {
                    levelDocumentTypes.put(documentType, hasChild);
                }
            }
        }


        setText(".");


        for (DocumentClassEntity documentType : levelDocumentTypes.keySet()) {
            boolean hasChild = levelDocumentTypes.get(documentType);
            DocumentClassGridItemDto documentClassGridItemDto = new DocumentClassGridItemDto();
            String name = documentType.getName();
            if (countDocumentsMap != null && countDocumentsMap.get(documentType.getId()) != null && countDocumentsMap.get(documentType.getId()) > 0) {
                name = name + " (" + countDocumentsMap.get(documentType.getId()) + ")";
            }
            documentClassGridItemDto.setId(documentType.getId());
            documentClassGridItemDto.setName(name);
            documentClassGridItemDto.setPosition(documentType.getPosition());
            if (hasChild && searchName != null) {
                documentClassGridItemDto.setExpanded(true);
            } else if (hasChild) {
                documentClassGridItemDto.setExpanded(false);
            } else {
                documentClassGridItemDto.setChildren(Collections.emptyList());
            }
            children.add(documentClassGridItemDto);
        }

        Collections.sort(children, (o1, o2) -> {
            int result;
            int position1 = o1.getPosition();
            int position2 = o2.getPosition();

            if (position1 == position2) {
                result = 0;
            } else if (position1 > position2) {
                result = 1;
            } else {
                result = -1;
            }

            return result;
        });
        setSuccess(true);
    }

    public static DocumentClassTreeForTemplatesDto toError() {
        DocumentClassTreeForTemplatesDto result = new DocumentClassTreeForTemplatesDto();
        result.setSuccess(false);
        return result;
    }
}
