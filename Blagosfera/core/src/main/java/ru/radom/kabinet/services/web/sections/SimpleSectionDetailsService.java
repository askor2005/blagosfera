package ru.radom.kabinet.services.web.sections;

import org.springframework.beans.factory.annotation.Autowired;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;

import java.util.HashMap;
import java.util.Map;

//@Service
public class SimpleSectionDetailsService implements SectionsDetailService {

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Override
	public void setDetails(SectionDomain section, Long userId) {
		assert section != null;

		if ("contacts".equals(section.getName())) {
			long newRequestsCount = contactDao.getNewRequestsCount(userId);
			if (newRequestsCount > 0) {
				section.getDetails().put("additionalIcon", "fa fa-warning faa-flash animated");
			}
		}

		if ("contactsNewRequests".equals(section.getName())) {
			long count = contactDao.getNewRequestsCount(userId);
			section.getDetails().put("visible", count > 0);
			section.getDetails().put("titleSuffix", "(" + count + ")");
		}

		if ("communities".equals(section.getName())) {
			long requestsCount = communityMemberDao.getRequestsCount(userId);
			long invitesCount = communityMemberDao.getInvitesCount(userId);
			long totalCount = requestsCount + invitesCount;
			if (totalCount > 0) {
				section.getDetails().put("additionalIcon", "fa fa-warning faa-flash animated");
			}
		}

		if ("communitiesRequests".equals(section.getName())) {
			long count = communityMemberDao.getRequestsCount(userId);
			section.getDetails().put("visible", count > 0);
			section.getDetails().put("titleSuffix", "(" + count + ")");
			Map<String, String> liAttrs = new HashMap<String, String>();
			liAttrs.put("data-requests-count", Long.toString(count));
			section.getDetails().put("liAttrs", liAttrs);
		}
		
		if ("communitiesInvites".equals(section.getName())) {
			long count = communityMemberDao.getInvitesCount(userId);
			section.getDetails().put("visible", count > 0);
			section.getDetails().put("titleSuffix", "(" + count + ")");
			Map<String, String> liAttrs = new HashMap<String, String>();
			liAttrs.put("data-invites-count", Long.toString(count));
			section.getDetails().put("liAttrs", liAttrs);
		}
		
		if ("communitiesMyRequests".equals(section.getName())) {
			long count = communityMemberDao.getMyRequestsCount(userId);
			section.getDetails().put("visible", count > 0);
			section.getDetails().put("titleSuffix", "(" + count + ")");
			Map<String, String> liAttrs = new HashMap<String, String>();
			liAttrs.put("data-my-requests-count", Long.toString(count));
			section.getDetails().put("liAttrs", liAttrs);
		}
		
	}

}
