package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.StringUtils;

@Component
public class RegistrationRequestSerializer extends AbstractSerializer<RegistrationRequest>{

    @Autowired
    private CommunitiesService communitiesService;

	@Override
	public JSONObject serializeInternal(RegistrationRequest request) {
		final JSONObject json = new JSONObject();
        // TODO Переделать на Dto и удалить
        json.put("id", request.getId());
        json.put("object", serializationManager.serialize(request.getObject()));
        String objectType = null;
        if (request.getObject() instanceof UserEntity) {
            objectType = Discriminators.SHARER;
            json.put("requestOwnerId", request.getObject().getId());
        } else if (request.getObject() instanceof CommunityEntity) {
            objectType = Discriminators.COMMUNITY;
            CommunityEntity communityEntity = (CommunityEntity)request.getObject();
            Long directorId = communitiesService.getCommunityDirectorId(communityEntity.toDomain());
            json.put("requestOwnerId", directorId);
        }
        json.put("objectType", objectType);
        json.put("createdDate", DateUtils.formatDate(request.getCreated(), DateUtils.Format.DATE));
        json.put("createdTime", DateUtils.formatDate(request.getCreated(), DateUtils.Format.TIME_SHORT));
        if(request.getUpdated() != null){
            json.put("updatedDate", DateUtils.formatDate(request.getUpdated(), DateUtils.Format.DATE));
            json.put("updatedTime", DateUtils.formatDate(request.getUpdated(), DateUtils.Format.TIME_SHORT));
        }
        json.put("status", request.getStatus().name());
        json.put("registrator", serializationManager.serialize(request.getRegistrator()));
        if(!StringUtils.isEmpty(request.getComment())){
            json.put("comment", request.getComment());
        }
		return json;
	}

}
