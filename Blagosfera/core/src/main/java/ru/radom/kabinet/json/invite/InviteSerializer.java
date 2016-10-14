package ru.radom.kabinet.json.invite;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.json.SharerSerializer;
import ru.radom.kabinet.model.invite.InvitationEntity;

@Component("inviteSerializer")
public class InviteSerializer extends AbstractSerializer<InvitationEntity> {
    @Autowired
    private SharerSerializer sharerSerializer;

    @Override
    public JSONObject serializeInternal(InvitationEntity invite) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", invite.getId());
        jsonObject.put("creationDate", invite.getCreationDate());
        jsonObject.put("expireDate", invite.getExpireDate());
        jsonObject.put("sharer", sharerSerializer.serializeSingleSharer(invite.getUser(), null));
        jsonObject.put("email", invite.getEmail());
        jsonObject.put("invitedLastName", invite.getInvitedLastName());
        jsonObject.put("invitedFirstName", invite.getInvitedFirstName());
        jsonObject.put("invitedFatherName", invite.getInvitedFatherName());
        jsonObject.put("invitedGender", invite.getInvitedGender());
        jsonObject.put("guarantee", invite.getGuarantee());
        jsonObject.put("howLongFamiliar", invite.getHowLongFamiliar());
        jsonObject.put("hashUrl", invite.getHashUrl());
        jsonObject.put("status", invite.getStatus());
        jsonObject.put("lastDateSending", invite.getLastDateSending());

        if (invite.getInvitedSharer() != null) {
            jsonObject.put("invitedSharer", sharerSerializer.serializeSingleSharer(invite.getInvitedSharer(), null));
        } else {
            jsonObject.put("invitedSharer", "");
        }

        return jsonObject;
    }
}