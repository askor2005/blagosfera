package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.UserTask;
import ru.radom.kabinet.model.cyberbrain.UserTaskLifecycle;
import ru.radom.kabinet.utils.DateUtils;

import java.util.Objects;

@Component("userTaskSerializer")
public class UserTaskSerializer extends AbstractSerializer<UserTask> {

    @Override
	public JSONObject serializeInternal(UserTask userTask) {
		JSONObject jsonUserTask = new JSONObject();
		jsonUserTask.put("id", userTask.getId());

		if (userTask.getPerformer() != null) {
			JSONObject jsonPerformer = new JSONObject();
			jsonPerformer.put("id", userTask.getPerformer().getId());
			jsonPerformer.put("name", userTask.getPerformer().getFullName());
			jsonUserTask.put("performer", jsonPerformer);
		}

		jsonUserTask.put("date_execution", DateUtils.dateToString(userTask.getDateExecution(), "yyyy/MM/dd HH:mm:ss"));

        if (userTask.getLifecycle() != null) {
            JSONObject jsonLifecycle = new JSONObject();
            jsonLifecycle.put("id", userTask.getLifecycle());

            if (Objects.equals(userTask.getLifecycle(), UserTaskLifecycle.NEW.getIndex())) {
                jsonLifecycle.put("name", UserTaskLifecycle.NEW.getDescription());
            } else if (Objects.equals(userTask.getLifecycle(), UserTaskLifecycle.SOLVED.getIndex())) {
                jsonLifecycle.put("name", UserTaskLifecycle.SOLVED.getDescription());
            } else if (Objects.equals(userTask.getLifecycle(), UserTaskLifecycle.REJECTED.getIndex())) {
                jsonLifecycle.put("name", UserTaskLifecycle.REJECTED.getDescription());
            } else if (Objects.equals(userTask.getLifecycle(), UserTaskLifecycle.CONFIRMED.getIndex())) {
                jsonLifecycle.put("name", UserTaskLifecycle.CONFIRMED.getDescription());
            }

            jsonUserTask.put("lifecycle", jsonLifecycle);
        }


		jsonUserTask.put("description", userTask.getDescription());

		if (userTask.getCustomer() != null) {
			JSONObject jsonCustomer = new JSONObject();
			jsonCustomer.put("id", userTask.getCustomer().getId());
			jsonCustomer.put("name", userTask.getCustomer().getFullName());
			jsonUserTask.put("customer", jsonCustomer);
		}

		if (userTask.getTrack() != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", userTask.getTrack().getId());
			jsonObject.put("name", userTask.getTrack().getTagOwner().getEssence());
			jsonUserTask.put("object", jsonObject);
		}

        if (userTask.getStatusFrom() != null) {
            JSONObject jsonStatusFrom = new JSONObject();
            jsonStatusFrom.put("id", userTask.getStatusFrom().getId());
            jsonStatusFrom.put("name", userTask.getStatusFrom().getEssence());
            jsonUserTask.put("status_from", jsonStatusFrom);
        }

        if (userTask.getStatusTo() != null) {
            JSONObject jsonStatusTo = new JSONObject();
            jsonStatusTo.put("id", userTask.getStatusTo().getId());
            jsonStatusTo.put("name", userTask.getStatusTo().getEssence());
            jsonUserTask.put("status_to", jsonStatusTo);
        }

        if (userTask.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", userTask.getCommunity().getId());
            jsonCommunity.put("name", userTask.getCommunity().getName());
            jsonUserTask.put("community", jsonCommunity);
        }

		return jsonUserTask;
	}
}