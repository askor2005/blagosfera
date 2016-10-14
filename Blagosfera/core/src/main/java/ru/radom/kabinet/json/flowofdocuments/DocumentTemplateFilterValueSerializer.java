package ru.radom.kabinet.json.flowofdocuments;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.document.model.DocumentTemplateFilterValueEntity;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.json.FieldSerializer;

@Component("documentTemplateFilterValueSerializer")
public class DocumentTemplateFilterValueSerializer extends AbstractSerializer<DocumentTemplateFilterValueEntity> {
	@Autowired
	private DocumentTemplateSerializer documentTemplateSerializer;

	@Autowired
	private FieldSerializer fieldSerializer;

	@Override
	public JSONObject serializeInternal(DocumentTemplateFilterValueEntity documentTemplateFilterValue) {
		return serializeSingleDocumentTemplateFilterValue(documentTemplateFilterValue);
	}

	public JSONObject serializeSingleDocumentTemplateFilterValue(DocumentTemplateFilterValueEntity documentTemplateFilterValue) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", documentTemplateFilterValue.getId());
		if (documentTemplateFilterValue.getDocumentTemplate() != null) {
			JSONObject jsonDocumentTemplate = new JSONObject();
			jsonDocumentTemplate.put("id", documentTemplateFilterValue.getDocumentTemplate().getId());
			jsonDocumentTemplate.put("name", documentTemplateFilterValue.getDocumentTemplate().getName());
			jsonObject.put("documentTemplate", jsonDocumentTemplate);
		}
		if (documentTemplateFilterValue.getFilterField() != null) {
			jsonObject.put("filterField", fieldSerializer.serializeSingleField(documentTemplateFilterValue.getFilterField()));
		}
		jsonObject.put("value", documentTemplateFilterValue.getValue());
		return jsonObject;
	}
}