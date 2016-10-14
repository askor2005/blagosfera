package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;

@Controller
public class CertificationController {
	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private FieldsGroupDao fieldsGroupDao;

	@RequestMapping("/certification")
	public String showCertificationPage(@RequestParam(value = "ikp", required = false) String ikp, Model model) {
		UserEntity profile = sharerDao.getByIkp(ikp);
		if (profile == null) {
			throw new ResourceNotFoundException("Участник не найден");
		}
		model.addAttribute("profile", profile);
		model.addAttribute("fieldsGroups", fieldsGroupDao.getByInternalNames("PERSON_COMMON", "PERSON_PASSPORT", "PERSON_REGISTRATION_ADDRESS"));
		model.addAttribute("registrator", SecurityUtils.getUser());
		return "certificationPage";
	}
}