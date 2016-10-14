package ru.radom.kabinet.web;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.voting.business.services.BatchVotingService;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.account.AccountDao;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.SystemSettingsService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.jcr.JcrFilesService;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityService;
import ru.radom.kabinet.services.test.TestService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.voting.protocol.VotingProtocolManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@EnableRabbit
@Controller
public class TestController {

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private SystemSettingsService systemSettingsService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private SharerService sharerService;

	@Autowired
	private BatchVotingService batchVotingService;

	@Autowired
	private SerializeService serializeService;

	@Autowired
	private VotingProtocolManager votingProtocolManager;

	@Autowired
	private CommunityDataService communityDataService;

	@Autowired
	private BlagosferaEventPublisher blagosferaEventPublisher;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String showTestPage(Model model, HttpServletRequest request) {
		model.addAttribute("availableProcessors", Runtime.getRuntime().availableProcessors());
		model.addAttribute("freeMemory", StringUtils.formatBytes(Runtime.getRuntime().freeMemory()));
		long maxMemory = Runtime.getRuntime().maxMemory();
		model.addAttribute("maxMemory", maxMemory == Long.MAX_VALUE ? "no limit" : StringUtils.formatBytes(maxMemory));
		model.addAttribute("totalMemory", StringUtils.formatBytes(Runtime.getRuntime().totalMemory()));
		Enumeration<String> headerNames = request.getHeaderNames();
		Map<String, String> headers = new LinkedHashMap<String, String>();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			headers.put(headerName, headerValue);
		}
		model.addAttribute("headers", headers);
		
		String remoteAddress = request.getRemoteAddr();
		model.addAttribute("remoteAddress", remoteAddress);
		return "test";
	}

	@RequestMapping(value = "/finger_test.json", method = RequestMethod.POST)
	@TokenProtected
	public @ResponseBody String fingerTest() {
		return JsonUtils.getSuccessJson().toString();
	}

	@RequestMapping(value = "/finger_test", method = RequestMethod.GET)
	public String showFingerTestPage(Model model) {
		model.addAttribute("applicationUrl", systemSettingsService.getApplicationUrl());
		return "fingerTest";
	}

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private AccountTypeDao accountTypeDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private LetterOfAuthorityService letterOfAuthorityService;

	@Autowired
	private CommunityDao communityDao;

	@RequestMapping(value = "/test/qwerty.json", method = RequestMethod.GET)
	@ResponseBody
	public String qwertyTest() {
		// Проверка доверенностей
		UserEntity userEntity = sharerDao.getById(543l);
		CommunityEntity community = communityDao.getById(270l);
		Map<String, String> attrMap = new HashMap<String, String>(){{
			put("88888", "88888");
		}};
		if (!letterOfAuthorityService.checkLetterOfAuthority("test", userEntity, community, attrMap)) {
			throw new RuntimeException("Доверенность не валидна");
		}

		return JsonUtils.getSuccessJson().toString();
	}

	@Autowired
	private JcrFilesService jcrSharerFilesService;

	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private FieldDao fieldDao;

	@RequestMapping(value = "/test/testProcess", method = RequestMethod.GET)
	@ResponseBody
	public String testProcess() {
		testService.testTransactionalEvent();
		return "asd";
	}

	@Autowired
	private TestService testService;

	@RequestMapping(value = "/test/signDocuments.json", method = RequestMethod.GET)
	@ResponseBody
	public String signDocuments() {
		System.err.println(Padeg.getOfficePadeg("Потребительское Общество \"Москва\"", 2));
		//testService.signDocuments();
		return "asd";
	}
}
