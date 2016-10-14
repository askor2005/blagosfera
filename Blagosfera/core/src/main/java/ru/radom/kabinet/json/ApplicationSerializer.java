package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.applications.SharerApplication;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.StringUtils;

@Component
public class ApplicationSerializer extends AbstractSerializer<Application> {

	@Autowired
	private SharerApplicationDao sharerApplicationDao;

	@Autowired
	private SectionDao sectionDao;

	public JSONObject serializeSingle(Application object, SharerApplication sharerApplication, boolean hasSection, Section section) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", object.getId());
		jsonObject.put("name", object.getName());
		jsonObject.put("logoUrl", object.getLogoUrl());
		jsonObject.put("cost", StringUtils.formatMoney(object.getCost()));
		jsonObject.put("free", object.isFree());

		jsonObject.put("hasSection", hasSection);
		if (hasSection) {
			JSONObject sectionObject = new JSONObject();
			sectionObject.put("id", section.getId());
			jsonObject.put("section", sectionObject);
		}
		
		jsonObject.put("startLink", object.getStartLink());
		jsonObject.put("infoLink", object.getInfoLink());
		jsonObject.put("editLink", object.getEditLink());
		jsonObject.put("sharerApplication", serializationManager.serialize(sharerApplication));
		return jsonObject;
	}

	@Override
	public JSONObject serializeInternal(Application object) {
		Section section = sectionDao.getByApplication(object);
		return serializeSingle(object, sharerApplicationDao.get(SecurityUtils.getUser().getId(), object), section != null, section);
	}

}
