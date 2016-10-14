package ru.radom.kabinet.json.flowofdocuments;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.model.DocumentTemplateFilterValueEntity;
import ru.radom.kabinet.json.AbstractSerializer;

@Component("flowOfDocumentTemplateSerializer")
public class DocumentTemplateSerializer extends AbstractSerializer<DocumentTemplateEntity> {
	@Autowired
	private DocumentTypeSerializer documentTypeSerializer;

	@Autowired
	private DocumentTemplateFilterValueSerializer documentTemplateFilterValueSerializer;

	@Override
	public JSONObject serializeInternal(DocumentTemplateEntity documentTemplate) {
		return serializeSingleDocumentTemplate(documentTemplate);
	}

	public JSONObject serializeSingleDocumentTemplate(DocumentTemplateEntity documentTemplate) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", documentTemplate.getId());
		if (documentTemplate.getDocumentType() != null) {
			jsonObject.put("documentType", documentTypeSerializer.serializeSingleDocumentType(documentTemplate.getDocumentType()));
		}
		jsonObject.put("name", documentTemplate.getName());
		jsonObject.put("content", documentTemplate.getContent());

		if (documentTemplate.getFilters() != null) {
			JSONArray jsonArray = new JSONArray();
			for (DocumentTemplateFilterValueEntity filterValue : documentTemplate.getFilters()) {
				jsonArray.put(documentTemplateFilterValueSerializer.serializeSingleDocumentTemplateFilterValue(filterValue));
			}
			jsonObject.put("filters", jsonArray);
		}

		return jsonObject;
	}
}