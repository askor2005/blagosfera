package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.radom.kabinet.dao.communities.CommunityActivityScopeDao;
import ru.radom.kabinet.model.communities.CommunityActivityScope;
import ru.radom.kabinet.utils.StringUtils;

@Service
public class CommunitiesAdminService {

	@Autowired
	private CommunityActivityScopeDao communityActivityScopeDao;
	
	public CommunityActivityScope saveActivityScope(CommunityActivityScope scope) {
		if (StringUtils.isEmpty(scope.getName())) {
			throw new CommunityException("Название не задано");
		}
		if (!communityActivityScopeDao.checkName(scope)) {
			throw new CommunityException("Данное название уже используется");
		}
		communityActivityScopeDao.saveOrUpdate(scope);
		return scope;
	}

	public CommunityActivityScope deleteActivityScope(CommunityActivityScope scope) {
		communityActivityScopeDao.delete(scope);
		return scope;
	}
	
}
