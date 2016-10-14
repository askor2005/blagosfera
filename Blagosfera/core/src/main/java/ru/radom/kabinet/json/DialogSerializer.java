package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;

@Component("dialogSerializer")
public class DialogSerializer extends AbstractSerializer<DialogEntity> {

	//@Autowired
    //private ChatMessageRepository chatMessageRepository;
	
	@Override
	public JSONObject serializeInternal(DialogEntity dialog) {
		JSONObject dialogJson = new JSONObject();
	    dialogJson.put("id", dialog.getId());
        dialogJson.put("name", dialog.getName());
        dialogJson.put("adminId", dialog.getAdminId());

        /*ChatMessage lastMessage = chatMessageRepository.findFirstByDialogIdOrderByDateDesc(dialog.getId());
        if (lastMessage != null) {
            dialogJson.put("lastMessage", serializationManager.serialize(lastMessage));
        }*/

        JSONArray sharersJson = new JSONArray();
        for (UserEntity userEntity : dialog.getUsers()) {
            sharersJson.put(serializationManager.serialize(userEntity));
        }

        dialogJson.put("sharers", sharersJson);

        return dialogJson;
	}

}
