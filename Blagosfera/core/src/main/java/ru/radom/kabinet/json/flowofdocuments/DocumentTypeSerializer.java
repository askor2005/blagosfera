package ru.radom.kabinet.json.flowofdocuments;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTypeDao;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.json.AbstractSerializer;

import java.util.*;

@Component("documentTypeSerializer")
public class DocumentTypeSerializer extends AbstractSerializer<DocumentClassEntity> {
    @Autowired
    private DocumentTypeDao documentTypeDao;

    @Override
    public JSONObject serializeInternal(DocumentClassEntity documentType) {
        return serializeSingleDocumentTypeFullTree(documentType);
    }

    public JSONObject serializeSingleDocumentTypeFullTree(DocumentClassEntity documentType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", documentType.getId());

		if (documentType.getParent() != null) {
			jsonObject.put("parentId", documentType.getParent().getId());
			jsonObject.put("parentName", documentType.getParent().getName());
		}

        if (documentTypeDao.getChildrenCount(documentType) > 0) {
            JSONArray jsonArray = new JSONArray();
            /*List<DocumentClassEntity> list = documentTypeDao.getChildrenList(documentType);
            for (DocumentClassEntity item : list) {
                jsonArray.put(serializeSingleDocumentTypeFullTree(item));
            }*/
            jsonObject.put("expanded", false);
            //jsonObject.put("children", jsonArray);
        } else {
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("children", jsonArray);
        }

        jsonObject.put("name", documentType.getName());
        jsonObject.put("key", documentType.getKey());

        return jsonObject;
    }

	public JSONObject serializeSingleDocumentType(DocumentClassEntity documentType) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", documentType.getId());

		if (documentType.getParent() != null) {
			jsonObject.put("parentId", documentType.getParent().getId());
			jsonObject.put("parentName", documentType.getParent().getName());
		}

		jsonObject.put("name", documentType.getName());
		jsonObject.put("key", documentType.getKey());

		return jsonObject;
	}

    public Map<String, Object> serializeDocumentTypeFullTree(Long parentId, Set<Long> classOfDocumentsIds, String searchName, Map<Long, Integer> countDocumentsMap) {
        DocumentClassEntity parentDocumentType = null;
        if (parentId > -1) {
            parentDocumentType = documentTypeDao.getById(parentId);
        }

        Map<DocumentClassEntity, Boolean> levelDocumentTypes = new HashMap<>();
        for (Long classOfDocumentsId : classOfDocumentsIds) {
            DocumentClassEntity documentType = documentTypeDao.getById(classOfDocumentsId);
            if (documentType == null) {
                continue;
            }
            boolean hasChild = false;
            boolean find = false;
            if (searchName != null) {
                find = StringUtils.containsIgnoreCase(documentType.getName(), searchName);
            }
            if (parentDocumentType != null) {
                while (documentType.getParent() != null && parentDocumentType.getId().longValue() != documentType.getParent().getId().longValue()) {
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

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("text", ".");
        List<Map<String, Object>> items = new ArrayList<>();

        for (DocumentClassEntity documentType : levelDocumentTypes.keySet()) {
            boolean hasChild = levelDocumentTypes.get(documentType);
            Map<String, Object> item = new HashMap<>();
            String name = documentType.getName();
            if (countDocumentsMap != null && countDocumentsMap.get(documentType.getId()) != null && countDocumentsMap.get(documentType.getId()) > 0) {
                name = name + " (" + countDocumentsMap.get(documentType.getId()) + ")";
            }
            item.put("id", documentType.getId());
            item.put("name", name);
            item.put("position", documentType.getPosition());
            if (hasChild && !StringUtils.isBlank(searchName)) {
                item.put("expanded", true);
            } else if (hasChild) {
                item.put("expanded", false);
            } else {
                item.put("children", new ArrayList<String>());
            }
            items.add(item);
        }

        Collections.sort(items, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int result = 0;
                if (o1.containsKey("position") && o2.containsKey("position")) {
                    int position1 = o1.get("position") == null ? 0 : ((Integer)o1.get("position")).intValue();
                    int position2 = o2.get("position") == null ? 0 : ((Integer)o2.get("position")).intValue();

                    if (position1 == position2) {
                        result = 0;
                    } else if (position1 > position2) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                }
                return result;
            }
        });

        resultMap.put("children", items);
        return resultMap;
    }
}