package ru.radom.kabinet.services.web.sections.setters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommunitiesInvitesSectionDetailsSetter extends AbstractSectionDetailsSetter {

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Override
	public void set(SectionDomain section, Long userId) {
		long count = communityMemberDao.getInvitesCount(userId);
		section.getDetails().put("visible", count > 0);
		section.getDetails().put("titleSuffix", "(" + count + ")");
		Map<String, String> liAttrs = new HashMap<>();
		liAttrs.put("data-invites-count", Long.toString(count));
		section.getDetails().put("liAttrs", liAttrs);
	}

	@Override
	public String getSupportedSectionName() {
		return "communitiesInvites";
	}
}
