package ru.radom.kabinet.dao.applications;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.applications.SharerApplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SharerApplicationDao extends Dao<SharerApplication> {

	public SharerApplication get(Long userId, Application application) {
		return findFirst(Restrictions.eq("user.id", userId), Restrictions.eq("application", application));
	}

	public Map<Application, SharerApplication> get(Long userId, List<Application> applications) {

		if (applications.isEmpty()) {
			return Collections.EMPTY_MAP;
		} else {
			Map<Application, SharerApplication> map = new HashMap<>();
			List<SharerApplication> sharerApplications = find(Restrictions.eq("userEntity.id", userId), Restrictions.in("application", applications));
			for (SharerApplication sharerApplication : sharerApplications) {
				map.put(sharerApplication.getApplication(), sharerApplication);
			}
			return map;
		}
	}

}
