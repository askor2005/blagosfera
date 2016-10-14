package ru.radom.kabinet.json.invite;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.invite.InviteRelationshipType;

@Component("inviteRelationshipTypeSerializer")
public class InviteRelationshipTypeSerializer extends AbstractSerializer<InviteRelationshipType> {
    @Override
    public JSONObject serializeInternal(InviteRelationshipType inviteRelationshipType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", inviteRelationshipType.getId());
        jsonObject.put("name", inviteRelationshipType.getName());
        return jsonObject;
    }
}