package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.KnowledgeRepository;
import ru.radom.kabinet.utils.DateUtils;

@Component("knowledgeRepositorySerializer")
public class KnowledgeRepositorySerializer extends AbstractSerializer<KnowledgeRepository> {

	@Override
	public JSONObject serializeInternal(KnowledgeRepository knowledgeRepository) {
		JSONObject jsonKnowledgeRepository = new JSONObject();
		jsonKnowledgeRepository.put("id", knowledgeRepository.getId());
		jsonKnowledgeRepository.put("fix_time_change", DateUtils.dateToString(knowledgeRepository.getFixTimeChange(), "yyyy/MM/dd HH:mm:ss"));
		jsonKnowledgeRepository.put("time_ready", DateUtils.dateToString(knowledgeRepository.getTimeReady(), "yyyy/MM/dd HH:mm:ss"));

		if (knowledgeRepository.getOwner() != null) {
			JSONObject jsonOwner = new JSONObject();
			jsonOwner.put("id", knowledgeRepository.getOwner().getId());
			jsonOwner.put("name", knowledgeRepository.getOwner().getFullName());
			jsonKnowledgeRepository.put("owner", jsonOwner);
		}

		if (knowledgeRepository.getTagOwner() != null) {
			JSONObject jsonTagOwner = new JSONObject();
			jsonTagOwner.put("id", knowledgeRepository.getTagOwner().getId());
			jsonTagOwner.put("name", knowledgeRepository.getTagOwner().getEssence());
			jsonKnowledgeRepository.put("tag_owner", jsonTagOwner);
			jsonKnowledgeRepository.put("tag_owner_name", knowledgeRepository.getTagOwner().getEssence());
			jsonKnowledgeRepository.put("tag_owner_is_numbered", knowledgeRepository.getTagOwner().getIsNumbered());
		}

		if (knowledgeRepository.getAttribute() != null) {
			JSONObject jsonAttribute = new JSONObject();
			jsonAttribute.put("id", knowledgeRepository.getAttribute().getId());
			jsonAttribute.put("name", knowledgeRepository.getAttribute().getEssence());
			jsonKnowledgeRepository.put("attribute", jsonAttribute);
		}

		if (knowledgeRepository.getTag() != null) {
			JSONObject jsonTag = new JSONObject();
			jsonTag.put("id", knowledgeRepository.getTag().getId());
			jsonTag.put("name", knowledgeRepository.getTag().getEssence());
			jsonKnowledgeRepository.put("tag", jsonTag);
			jsonKnowledgeRepository.put("tag_name", knowledgeRepository.getTag().getEssence());
		}

		jsonKnowledgeRepository.put("mera", knowledgeRepository.getMera());

		if (knowledgeRepository.getTask() != null) {
			JSONObject jsontTask = new JSONObject();
			jsontTask.put("id", knowledgeRepository.getTask().getId());
			jsontTask.put("name", knowledgeRepository.getTask().getDescription());
			jsonKnowledgeRepository.put("task", jsontTask);
		}

		jsonKnowledgeRepository.put("next", knowledgeRepository.getNext());
		jsonKnowledgeRepository.put("change_if", knowledgeRepository.getChangeIf());

		jsonKnowledgeRepository.put("status", knowledgeRepository.getStatus());
		jsonKnowledgeRepository.put("stress", knowledgeRepository.getStress());
		jsonKnowledgeRepository.put("attention", knowledgeRepository.getAttention());
		jsonKnowledgeRepository.put("show_in_questions", knowledgeRepository.getShowInQuestions());

        if (knowledgeRepository.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", knowledgeRepository.getCommunity().getId());
            jsonCommunity.put("name", knowledgeRepository.getCommunity().getName());
            jsonKnowledgeRepository.put("community", jsonCommunity);
        }

        if (knowledgeRepository.getLifecycleStatus() != null) {
            jsonKnowledgeRepository.put("lifecycle_status", knowledgeRepository.getLifecycleStatus().toString());
        }

        if (knowledgeRepository.getLifecycleStatus() != null) {
            jsonKnowledgeRepository.put("is_topical", knowledgeRepository.getIsTopical());
        }

		return jsonKnowledgeRepository;
	}
}