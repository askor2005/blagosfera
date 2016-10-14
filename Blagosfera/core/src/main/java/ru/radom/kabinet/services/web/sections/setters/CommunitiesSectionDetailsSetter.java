package ru.radom.kabinet.services.web.sections.setters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;

@Component
public class CommunitiesSectionDetailsSetter extends AbstractSectionDetailsSetter {

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Override
	public void set(SectionDomain section, Long userId) {
		long requestsCount = communityMemberDao.getRequestsCount(userId);
		long invitesCount = communityMemberDao.getInvitesCount(userId);
		long myRequestsCount = communityMemberDao.getMyRequestsCount(userId);
		long totalCount = requestsCount + invitesCount + myRequestsCount;
		if (totalCount > 0) {
			section.getDetails().put("additionalIcon", "fa fa-warning faa-flash animated");
		}
	}

	@Override
	public String getSupportedSectionName() {
		return "communities";
	}

}
