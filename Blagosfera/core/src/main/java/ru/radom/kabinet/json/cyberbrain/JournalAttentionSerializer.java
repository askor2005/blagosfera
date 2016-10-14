package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.JournalAttention;
import ru.radom.kabinet.utils.DateUtils;

@Component("journalAttentionSerializer")
public class JournalAttentionSerializer extends AbstractSerializer<JournalAttention> {

	@Override
	public JSONObject serializeInternal(JournalAttention journalAttention) {
		JSONObject jsonJournalAttention = new JSONObject();
		jsonJournalAttention.put("id", journalAttention.getId());
		jsonJournalAttention.put("fixTimeKvant", DateUtils.dateToString(journalAttention.getFixTimeKvant(), "yyyy/MM/dd HH:mm:ss"));
		jsonJournalAttention.put("textKvant", journalAttention.getTextKvant());
		jsonJournalAttention.put("tagKvant", journalAttention.getTagKvant());

		if (journalAttention.getPerformerKvant() != null) {
			JSONObject jsonPerformer = new JSONObject();
			jsonPerformer.put("id", journalAttention.getPerformerKvant().getId());
			jsonPerformer.put("name", journalAttention.getPerformerKvant().getFullName());
			jsonJournalAttention.put("performerKvant", jsonPerformer);
		}

		jsonJournalAttention.put("attentionKvant", journalAttention.getAttentionKvant());

        if (journalAttention.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", journalAttention.getCommunity().getId());
            jsonCommunity.put("name", journalAttention.getCommunity().getName());
            jsonJournalAttention.put("community", jsonCommunity);
        }

		return jsonJournalAttention;
	}
}