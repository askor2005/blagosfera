package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.services.communities.CommunitiesService;

@Component("communitySerializer")
// TODO Удалить
public class CommunitySerializer extends AbstractSerializer<CommunityEntity> {

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Autowired
	private CommunitiesService communitiesService;
	
	@Autowired
	private SharerSerializer sharerSerializer;
	
	@Override
	public JSONObject serializeInternal(CommunityEntity community) {
		JSONObject jsonObject = new JSONObject();
		/*jsonObject.put("id", community.getId());
		jsonObject.put("name", community.getName());
		jsonObject.put("open", community.isOpen());
		jsonObject.put("announcement", community.getAnnouncement());
		jsonObject.put("avatar", community.getAvatar());
		jsonObject.put("link", community.getLink());
		jsonObject.put("creator", sharerSerializer.serializeSingleSharer(community.getCreator(), null));
		jsonObject.put("membersCount", community.getMembersCount());
		jsonObject.put("subgroupsCount", community.getSubgroupsCount());
		jsonObject.put("accessType", community.getAccessType() != null ? community.getAccessType().name() : null);
		jsonObject.put("invisible", community.isInvisible());
		jsonObject.put("createdAt", DateUtils.formatDate(community.getCreatedAt(), "dd.MM.yyyy HH:mm:ss"));
		jsonObject.put("isRoot", community.isRoot());
		
		jsonObject.put("deleted", community.isDeleted());
		jsonObject.put("deleteComment", community.getDeleteComment());
		jsonObject.put("deleter", serializationManager.serialize(community.getDeleter()));
		jsonObject.put("verified", community.getVerified() != null && community.getVerified());*/
		return jsonObject;

	}

}
