package ru.radom.kabinet.services.web.sections.setters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;

@Component
public class CommunitiesMyRequestsSectionDetailsSetter extends AbstractSectionDetailsSetter {

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Override
	public void set(SectionDomain section, Long userId) {
		long count = communityMemberDao.getMyRequestsCount(userId);
		section.getDetails().put("visible", count > 0);
		section.getDetails().put("titleSuffix", "(" + count + ")");
		section.getDetails().put("liAttrs", "data-my-requests-count=" + Long.toString(count));
	}

	@Override
	public String getSupportedSectionName() {
		return "communitiesMyRequests";
	}

}
