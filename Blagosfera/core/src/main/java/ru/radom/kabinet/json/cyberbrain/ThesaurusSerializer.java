package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.Thesaurus;
import ru.radom.kabinet.utils.DateUtils;

@Component("thesaurusSerializer")
public class ThesaurusSerializer extends AbstractSerializer<Thesaurus> {

	@Override
	public JSONObject serializeInternal(Thesaurus thesaurus) {
		JSONObject jsonThesaurus = new JSONObject();
		jsonThesaurus.put("id", thesaurus.getId());
		jsonThesaurus.put("is_service_tag", thesaurus.getIsServiceTag());
		jsonThesaurus.put("essence", thesaurus.getEssence());
		jsonThesaurus.put("sinonim", thesaurus.getSinonim());
		jsonThesaurus.put("fix_date_essence", DateUtils.dateToString(thesaurus.getFixDateEssence(), "yyyy/MM/dd HH:mm:ss"));

		if (thesaurus.getEssenceOwner() != null) {
			JSONObject jsonEssenceOwner = new JSONObject();
			jsonEssenceOwner.put("id", thesaurus.getEssenceOwner().getId());
			jsonEssenceOwner.put("name", thesaurus.getEssenceOwner().getFullName());
			jsonThesaurus.put("essence_owner", jsonEssenceOwner);
		}

		jsonThesaurus.put("frequency_essence", thesaurus.getFrequencyEssence());
		jsonThesaurus.put("attention_frequency", thesaurus.getAttentionFrequency());
		jsonThesaurus.put("is_personal_data", thesaurus.getIsPersonalData());
		jsonThesaurus.put("is_numbered", thesaurus.getIsNumbered());

        if (thesaurus.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", thesaurus.getCommunity().getId());
            jsonCommunity.put("name", thesaurus.getCommunity().getName());
            jsonThesaurus.put("community", jsonCommunity);
        }

		return jsonThesaurus;
	}
}