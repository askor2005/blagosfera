package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.applications.SharerApplication;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ApplicationCollectionSerializer extends AbstractCollectionSerializer<Application>{

	@Autowired
	private SharerApplicationDao sharerApplicationDao;
	
	@Autowired
	private ApplicationSerializer applicationSerializer;
	
	@Autowired
	private SectionDao sectionDao;

	@Override
	public JSONArray serializeInternal(Collection<Application> collection) {
		List<Application> applications = new ArrayList<>(collection);
		Map<Application, SharerApplication> applicationsSharerApplicationsMap = sharerApplicationDao.get(SecurityUtils.getUser().getId(), applications);
		Map<Application, Section> applicationsSectionsMap = sectionDao.getApplicationsSectionsMap(applications);
		JSONArray jsonArray = new JSONArray();
		for (Application application : collection) {
			jsonArray.put(applicationSerializer.serializeSingle(application, applicationsSharerApplicationsMap.get(application), applicationsSectionsMap.get(application) != null, applicationsSectionsMap.get(application)));
		}
		return jsonArray;
	}

}
