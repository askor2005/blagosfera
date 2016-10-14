package ru.radom.kabinet.json.flowofdocuments;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.document.AssociationFormSearchType;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.json.FieldSerializer;
import ru.radom.kabinet.model.fields.FieldEntity;

@Component("documentTypeParticipantSerializer")
public class DocumentTypeParticipantSerializer extends AbstractSerializer<DocumentClassDataSourceEntity> {
	@Autowired
	private DocumentTypeSerializer documentTypeSerializer;

	@Autowired
	private FieldSerializer fieldSerializer;

    @Override
    public JSONObject serializeInternal(DocumentClassDataSourceEntity participant) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", participant.getId());
		if (participant.getDocumentType() != null) {
			jsonObject.put("documentType", documentTypeSerializer.serializeSingleDocumentType(participant.getDocumentType()));
		}
		jsonObject.put("participantType", participant.getParticipantType());
		jsonObject.put("participantName", participant.getParticipantName());

		JSONArray jsonArray = new JSONArray();
		for (FieldEntity field : participant.getFieldsFilters()) {
			jsonArray.put(fieldSerializer.serializeSingleField(field));
		}
		jsonObject.put("filters", jsonArray);

		if (participant.getAssociationForm() != null) {
			JSONObject associationForm = new JSONObject();
			associationForm.put("id", participant.getAssociationForm().getId());
			associationForm.put("name", participant.getAssociationForm().getText());
			jsonObject.put("associationForm", associationForm);
			jsonObject.put("associationFormSearchType", participant.getAssociationFormSearchType() == null ? AssociationFormSearchType.SEARCH_SUB_STRUCTURES : participant.getAssociationFormSearchType());
		} else {
			JSONObject associationForm = new JSONObject();
			associationForm.put("id", -1);
			associationForm.put("name", "");
			jsonObject.put("associationForm", associationForm);
			jsonObject.put("associationFormSearchType", participant.getAssociationFormSearchType() == null ? AssociationFormSearchType.SEARCH_SUB_STRUCTURES : participant.getAssociationFormSearchType());
		}

        return jsonObject;
    }
}