package ru.radom.kabinet.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.cyberbrain.*;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.*;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.CyberbrainService;
import ru.radom.kabinet.tools.cyberbrain.ExcelProcessor;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Controller
@MultipartConfig
@RequestMapping(CyberbrainController.Pages.ROOT_PAGE_URL)
public class CyberbrainController {
	public static class Pages {
		public static final String ROOT_PAGE_URL = "/cyberbrain";
		public static final String CURRENT_PAGE_TITLE = "currentPageTitle";

        public static class Sections {
            public static class Url {
                public static final String MAIN = "/sections";
                public static final String GET_COUNTS_RECORDS_AND_SCORE = MAIN + "/get_counts_records_and_score.json";
                public static final String GET_QUESTION_BY_PRIORITY = MAIN + "/get_question_by_priority.json";
                public static final String GET_QUESTION_BY_PRIORITY_INFO = MAIN + "/get_question_by_priority_info.json";
                public static final String ANSWER_THE_QUESTION_BY_PRIORITY = MAIN + "/answerTheQuestionByPriority";
                public static final String GET_USER_COMMUNITIES = MAIN + "/get_user_communities.json";
            }
        }

		public static class JournalAttentionPage {
			public static final String TITLE = "Мое внимание";
			public static final String VIEW = "journalAttentionView";

			public static class Url {
				public static final String MAIN = "/journalAttention";
				public static final String ADD_ATTENTION = MAIN + "/addAttention";
				public static final String SEARCH_BY_TAG_KVANT = MAIN + "/search_by_tag_kvant.json";
				public static final String SEARCH_BY_TEXT_KVANT = MAIN + "/search_by_text_kvant.json";
				public static final String GET_JOURNAL_ATTENTION = MAIN + "/get_journal_attention.json";
			}
		}

		public static class KnowledgeRepositoryPage {
			public static final String TITLE = "Вопросы";
			public static final String VIEW = "knowledgeRepositoryView";

			public static class Url {
				public static final String MAIN = "/knowledgeRepository";
                public static final String GET_QUESTIONS_TRACKS = MAIN + "/get_questions_tracks.json";
				public static final String ANSWER_THE_QUESTION_PROPERTY = MAIN + "/answerTheQuestionProperty";
				public static final String GET_QUESTIONS_PROPERTIES = MAIN + "/get_questions_properties.json";
				public static final String ANSWER_THE_QUESTION_MANY = MAIN + "/answerTheQuestionMany";
				public static final String GET_QUESTIONS_MANY = MAIN + "/get_questions_many.json";
				public static final String ANSWER_THE_QUESTION = MAIN + "/answerTheQuestion";
				public static final String GET_QUESTIONS = MAIN + "/get_questions.json";
				public static final String ADD_NEW_MANY = MAIN + "/addNewMany";
				public static final String ADD_NEW_PROPERTY = MAIN + "/addNewProperty";
				public static final String ADD_NEW_TRACK = MAIN + "/addNewTrack";
                public static final String SAVE_TRACK = MAIN + "/saveTrack";
				public static final String EDIT_TRACKS_FINISH = MAIN + "/editTracksFinish";
			}
		}

        public static class MyKnowledgePage {
            public static final String TITLE = "Мои знания";
            public static final String VIEW = "myKnowledgeView";

            public static class Url {
                public static final String MAIN = "/myKnowledge";
                public static final String GET_THESAURUS = MAIN + "/get_thesaurus.json";
                public static final String GET_JOURNAL_ATTENTION = MAIN + "/get_journal_attention.json";
                public static final String GET_MY_KNOWLEDGE = MAIN + "/get_my_knowledge.json";
                public static final String START_REPLICATION = MAIN + "/startReplication";
            }
        }

		public static class ThesaurusPage {
			public static final String TITLE = "Мои термины";
			public static final String VIEW = "thesaurusView";

			public static class Url {
				public static final String MAIN = "/thesaurus";
				public static final String GET_THESAURUS = MAIN + "/get_thesaurus.json";
				public static final String UPDATE_THESAURUS = MAIN + "/updateThesaurus";
				public static final String SEARCH_THESAURUS = MAIN + "/search.json";
				public static final String ADD_NEW_TAG = MAIN + "/addNewTag";
			}
		}

		public static class TaskManagementPage {
			public static final String TITLE = "Цели и дела";
			public static final String VIEW = "taskManagementView";

			public static class Url {
				public static final String MAIN = "/taskManagement";
				public static final String SEARCH_USER_TASK = MAIN + "/search.json";
				public static final String GET_TRACK_OBJECT = MAIN + "/get_track_object.json";
				public static final String GET_TRACK_OBJECT_STATUS_FROM_TO_INFO = MAIN + "/get_track_object_status_from_to_info.json";
				public static final String ADD_USER_TASK = MAIN + "/addUserTask";
				public static final String UPDATE_USER_TASK = MAIN + "/updateUserTask";
                public static final String ADD_USER_PROBLEM = MAIN + "/addUserProblem";
                public static final String UPDATE_USER_PROBLEM = MAIN + "/updateUserProblem";
                public static final String COPY_OBJECT = MAIN + "/copyObject";
				public static final String GET_MY_CUSTOMERS = MAIN + "/get_my_customers.json";
				public static final String GET_MY_SUBCONTRACTORS = MAIN + "/get_my_subcontractors.json";
				public static final String GET_MY_GOALS = MAIN + "/get_my_goals.json";
                public static final String GET_USER_PROBLEMS = MAIN + "/get_user_problems.json";
				public static final String GET_SUBTASKS_BY_USER_TASK_ID = MAIN + "/get_subtasks_by_user_task_id.json";
				public static final String GET_PROBLEMS_BY_USER_PROBLEM_ID = MAIN + "/get_problems_by_user_problem_id.json";
				public static final String GET_MANY_LIST = MAIN + "/get_many_list.json";
				public static final String NEW_OBJECT_WIZARD_FORM = MAIN + "/newObjectWizardForm";
				public static final String NEW_OBJECT_WIZARD_FORM_QUESTIONS_TRACKS = MAIN + "/new_object_wizard_form_questions_tracks.json";
				public static final String NEW_OBJECT_WIZARD_FORM_QUESTIONS_PROPERTIES = MAIN + "/new_object_wizard_form_questions_properties.json";
				public static final String NEW_OBJECT_WIZARD_FORM_GET_TRACKS_FOR_OBJECT = MAIN + "/new_object_wizard_form_get_tracks_for_object.json";
			}
		}

        public static class RatingSystemPage {
            public static final String TITLE = "Рейтинги системы";
            public static final String VIEW = "ratingSystemView";

            public static class Url {
                public static final String MAIN = "/ratingSystem";
                public static final String GET_RATING_SYSTEM = MAIN + "/get_rating_system.json";
            }
        }

        public static class ImportExportPage {
            public static final String TITLE = "Импорт / Экспорт данных";
            public static final String VIEW = "cyberbrainImportExportView";

            public static class Url {
                public static final String MAIN = "/importExport";
                public static final String JOURNAL_ATTENTION_IMPORT_DATA = MAIN + "/journalAttentionImportData";
                public static final String JOURNAL_ATTENTION_EXPORT_DATA = MAIN + "/journalAttentionExportData";
                public static final String KNOWLEDGE_REPOSITORY_IMPORT_DATA = MAIN + "/knowledgeRepositoryImportData";
                public static final String KNOWLEDGE_REPOSITORY_IMPORT_DATA_CONDITION = MAIN + "/knowledgeRepositoryImportDataCondition";
                public static final String KNOWLEDGE_REPOSITORY_EXPORT_DATA = MAIN + "/knowledgeRepositoryExportData";
                public static final String THESAURUS_IMPORT_DATA = MAIN + "/thesaurusImportData";
                public static final String THESAURUS_EXPORT_DATA = MAIN + "/thesaurusExportData";
                public static final String TASK_MANAGEMENT_EXPORT_DATA = MAIN + "/taskManagementExportData";
            }
        }
	}

	public static class Extensions {
		public static final String XLS = "xls";
		public static final String XLSX = "xlsx";
	}

	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private KnowledgeRepositoryDao knowledgeRepositoryDao;

	@Autowired
	private JournalAttentionDao journalAttentionDao;

	@Autowired
	private ThesaurusDao thesaurusDao;

	@Autowired
	private UserTaskDao userTaskDao;

	@Autowired
	private UserSubtaskDao userSubtaskDao;

    @Autowired
    private UserProblemDao userProblemDao;

	@Autowired
	private CyberbrainService cyberbrainService;

	@Autowired
	private ExcelProcessor excelProcessor;

    @Autowired
    private ScoreSharerDao scoreSharerDao;

    @Autowired
    private CyberbrainObjectDao cyberbrainObjectDao;

    @Autowired
    private CyberbrainFileDao cyberbrainFileDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SharerDao sharerDao;

	@RequestMapping(value = Pages.JournalAttentionPage.Url.MAIN, method = RequestMethod.GET)
	public String showJournalAttentionPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.JournalAttentionPage.TITLE);
		return Pages.JournalAttentionPage.VIEW;
	}

	@RequestMapping(value = {Pages.JournalAttentionPage.Url.GET_JOURNAL_ATTENTION, Pages.MyKnowledgePage.Url.GET_JOURNAL_ATTENTION}, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getJournalAttention(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("fixTimeKvantBegin", getValueOfParameter(body, "fixTimeKvantBegin"));
		filters.put("fixTimeKvantEnd", getValueOfParameter(body, "fixTimeKvantEnd"));
		filters.put("tagKvant", getValueOfParameter(body, "tagKvant"));
		filters.put("filterField", getValueOfParameter(body, "filterField"));
		filters.put("filterText", getValueOfParameter(body, "filterText"));

		String sort = getValueOfParameter(body, "sort");

		List list = journalAttentionDao.getJournalAttentionList(filters, start, limit, sort);
		JSONArray jsonArray = serializationManager.serializeCollection(list);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", journalAttentionDao.getJournalAttentionCount(filters));
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = Pages.JournalAttentionPage.Url.ADD_ATTENTION, method = RequestMethod.POST)
	public boolean addAttention(@RequestBody String jsonData) {
		return cyberbrainService.addAttention(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

	@RequestMapping(value = Pages.JournalAttentionPage.Url.SEARCH_BY_TEXT_KVANT, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String searchJournalAttentionByTextKvant(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String query = getValueOfParameter(body, "query");

		List<JournalAttention> list = journalAttentionDao.searchTextKvant(query, (page - 1) * perPage, perPage);
		JSONArray jsonData = serializationManager.serializeCollection(list);
		return jsonData.toString();
	}

	@RequestMapping(value = Pages.JournalAttentionPage.Url.SEARCH_BY_TAG_KVANT, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String searchJournalAttentionByTagKvant(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String query = getValueOfParameter(body, "query");

		List<JournalAttention> list = journalAttentionDao.searchTagKvant(query, (page - 1) * perPage, perPage);
		JSONArray jsonData = serializationManager.serializeCollection(list);
		return jsonData.toString();
	}

	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.MAIN, method = RequestMethod.GET)
	public String showKnowledgeRepositoryPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.KnowledgeRepositoryPage.TITLE);
		return Pages.KnowledgeRepositoryPage.VIEW;
	}

	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.GET_QUESTIONS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getQuestions(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("tagFilter", getValueOfParameter(body, "tagFilter"));
		filters.put("communityId", getValueOfParameter(body, "communityId"));

		List list = knowledgeRepositoryDao.getQuestionsList(filters, start, limit);
		JSONArray jsonArray = new JSONArray();

		for(Object obj : list) {
			jsonArray.put(obj);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", knowledgeRepositoryDao.getQuestionsCount(filters));
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

    @RequestMapping(value = Pages.Sections.Url.GET_QUESTION_BY_PRIORITY, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getQuestionByPriority(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        Object obj = knowledgeRepositoryDao.getPriorityQuestion();

        JSONObject jsonObject = new JSONObject();
        if (obj != null) {
            jsonObject.put("type", ((HashMap) obj).get("type"));
            jsonObject.put("knowledge_rep_id", ((HashMap) obj).get("knowledge_rep_id"));
            jsonObject.put("thesaurus_tag_property_id", ((HashMap) obj).get("thesaurus_tag_property_id"));
            jsonObject.put("tag", ((HashMap) obj).get("tag"));
            jsonObject.put("description", ((HashMap) obj).get("description"));
        }

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.Sections.Url.GET_QUESTION_BY_PRIORITY_INFO, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getQuestionByPriorityInfo(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        String tag = getValueOfParameter(body, "tag");
        List<JournalAttention> list = journalAttentionDao.getListByTagKvant(tag, 0, 9);

        String result = "";

        for (JournalAttention str : list) {
            if (result.equals("")) {
                result = str.getTagKvant();
            } else {
                result += "<br/>" + str.getTagKvant();
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);

        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ADD_NEW_MANY, method = RequestMethod.POST)
    public boolean addNewMany(@RequestBody String jsonData) {
        return cyberbrainService.addNewMany(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ADD_NEW_PROPERTY, method = RequestMethod.POST)
    public boolean addNewProperty(@RequestBody String jsonData) {
        return cyberbrainService.addNewProperty(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ADD_NEW_TRACK, method = RequestMethod.POST)
    public boolean addNewTrack(@RequestBody String jsonData) {
        return cyberbrainService.addNewTrack(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.SAVE_TRACK, method = RequestMethod.POST)
    public boolean saveTrack(@RequestBody String jsonData) {
        return cyberbrainService.saveTrack(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.EDIT_TRACKS_FINISH, method = RequestMethod.POST)
    public boolean editTracksFinish(@RequestBody String jsonData) {
        return cyberbrainService.editTracksFinish(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

	@ResponseBody
	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ANSWER_THE_QUESTION, method = RequestMethod.POST)
	public boolean answerTheQuestion(@RequestBody String jsonData) {
		return cyberbrainService.answerTheQuestion(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

    @ResponseBody
    @RequestMapping(value = Pages.Sections.Url.ANSWER_THE_QUESTION_BY_PRIORITY, method = RequestMethod.POST)
    public boolean answerTheQuestionByPriority(@RequestBody String jsonData) {
        return cyberbrainService.answerTheQuestionByPriority(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @RequestMapping(value = Pages.Sections.Url.GET_USER_COMMUNITIES, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getUserCommunities(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        List<CommunityEntity> list = communityDao.getList(SecurityUtils.getUser().getId(), SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN), Arrays.asList(CommunityMemberStatus.MEMBER, CommunityMemberStatus.REQUEST_TO_LEAVE), null, 0, 20, "", null, null, null, null, true, false, "name", true);

        JSONArray jsonArray = serializationManager.serializeCollection(list);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", communityDao.getListCount(SecurityUtils.getUser().getId(), SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN), Arrays.asList(CommunityMemberStatus.MEMBER, CommunityMemberStatus.REQUEST_TO_LEAVE), null, "", null, null, null, null, true, false));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.GET_QUESTIONS_MANY, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getQuestionsMany(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("manyFilter", getValueOfParameter(body, "manyFilter"));
		filters.put("communityId", getValueOfParameter(body, "communityId"));

		List list = knowledgeRepositoryDao.getQuestionsManyList(filters, start, limit);
		JSONArray jsonArray = new JSONArray();

		for (Object obj : list) {
			jsonArray.put(obj);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", knowledgeRepositoryDao.getQuestionsManyCount(filters));
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ANSWER_THE_QUESTION_MANY, method = RequestMethod.POST)
	public boolean answerTheQuestionMany(@RequestBody String jsonData) {
		return cyberbrainService.answerTheQuestionMany(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.GET_QUESTIONS_PROPERTIES, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getQuestionsProperties(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("propertiesMany", getValueOfParameter(body, "propertiesMany"));
		filters.put("propertiesTag", getValueOfParameter(body, "propertiesTag"));
		filters.put("propertiesProperty", getValueOfParameter(body, "propertiesProperty"));
		filters.put("communityId", getValueOfParameter(body, "communityId"));

		List list = knowledgeRepositoryDao.getQuestionsPropertiesList(filters, start, limit);
		JSONArray jsonArray = new JSONArray();

		for (Object obj : list) {
			jsonArray.put(obj);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", knowledgeRepositoryDao.getQuestionsPropertiesCount(filters));
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.ANSWER_THE_QUESTION_PROPERTY, method = RequestMethod.POST)
	public boolean answerTheQuestionProperty(@RequestBody String jsonData) {
		return cyberbrainService.answerTheQuestionProperty(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

    @RequestMapping(value = Pages.KnowledgeRepositoryPage.Url.GET_QUESTIONS_TRACKS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getQuestionsTracks(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        HashMap<String, String> filters = new HashMap<>();
        filters.put("tracksMany", getValueOfParameter(body, "tracksMany"));
        filters.put("tracksTag", getValueOfParameter(body, "tracksTag"));
        filters.put("communityId", getValueOfParameter(body, "communityId"));

        List list = knowledgeRepositoryDao.getQuestionsTracksList(filters, start, limit);
        JSONArray jsonArray = new JSONArray();

        for (Object obj : list) {
            jsonArray.put(obj);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", knowledgeRepositoryDao.getQuestionsTracksCount(filters));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.MyKnowledgePage.Url.MAIN, method = RequestMethod.GET)
    public String showMyKnowledgePage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.MyKnowledgePage.TITLE);
        return Pages.MyKnowledgePage.VIEW;
    }


    @RequestMapping(value = Pages.MyKnowledgePage.Url.GET_MY_KNOWLEDGE, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getMyKnowledge(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        String filterText = getValueOfParameter(body, "filterText");

        List list = knowledgeRepositoryDao.getMyKnowledge(filterText, start, limit);
        JSONArray jsonArray = serializationManager.serializeCollection(list);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", knowledgeRepositoryDao.getMyKnowledgeCount(filterText));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = Pages.MyKnowledgePage.Url.START_REPLICATION, method = RequestMethod.POST)
    public boolean startReplication(@RequestBody String jsonData) {
        return cyberbrainService.startReplication(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

	@RequestMapping(value = Pages.ThesaurusPage.Url.MAIN, method = RequestMethod.GET)
	public String showThesaurusPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.ThesaurusPage.TITLE);
		return Pages.ThesaurusPage.VIEW;
	}

	@RequestMapping(value = {Pages.ThesaurusPage.Url.GET_THESAURUS, Pages.MyKnowledgePage.Url.GET_THESAURUS}, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getThesaurus(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		String filterField = getValueOfParameter(body, "filterField");
		String filterText = getValueOfParameter(body, "filterText");
		String sort = getValueOfParameter(body, "sort");

		List list = thesaurusDao.getThesaurusList(filterField, filterText, start, limit, sort);
		JSONArray jsonArray = serializationManager.serializeCollection(list);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", thesaurusDao.getThesaurusCount(filterField, filterText));
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@RequestMapping(value = Pages.ThesaurusPage.Url.SEARCH_THESAURUS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String searchJsonThesaurus(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String query = getValueOfParameter(body, "query");
        Long communityId = Long.valueOf(getValueOfParameter(body, "communityId"));

		List<Thesaurus> thesaurus = thesaurusDao.search(query, (page - 1) * perPage, perPage, communityId);
		JSONArray jsonThesaurus = serializationManager.serializeCollection(thesaurus);
		return jsonThesaurus.toString();
	}

    @ResponseBody
    @RequestMapping(value = Pages.ThesaurusPage.Url.ADD_NEW_TAG, method = RequestMethod.POST)
    public boolean addNewTag(@RequestBody String jsonData) {
        return cyberbrainService.addNewTag(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

	@RequestMapping(value = Pages.ThesaurusPage.Url.UPDATE_THESAURUS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String updateThesaurus(@RequestBody String jsonData) {
		return cyberbrainService.updateThesaurusSinonim(jsonData, CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

	@RequestMapping(value = Pages.TaskManagementPage.Url.MAIN, method = RequestMethod.GET)
	public String showTaskManagementPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.TaskManagementPage.TITLE);
		return Pages.TaskManagementPage.VIEW;
	}

    @RequestMapping(value = Pages.RatingSystemPage.Url.MAIN, method = RequestMethod.GET)
    public String showRatingSystemPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.RatingSystemPage.TITLE);
        return Pages.RatingSystemPage.VIEW;
    }

    @RequestMapping(value = Pages.RatingSystemPage.Url.GET_RATING_SYSTEM, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getRatingSystem(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        List list = scoreSharerDao.getRatingSystem(start, limit);
        JSONArray jsonArray = new JSONArray();

        for(Object obj : list) {
            jsonArray.put(obj);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", scoreSharerDao.getRatingSystemCount());
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

	@RequestMapping(value = Pages.TaskManagementPage.Url.GET_MY_SUBCONTRACTORS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getMySubcontractors(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		List list = userTaskDao.getMySubcontractors(start, limit);
		JSONArray jsonArray = serializationManager.serializeCollection(list);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", userTaskDao.getMySubcontractorsCount());
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@RequestMapping(value = Pages.TaskManagementPage.Url.GET_MY_GOALS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getMyGoals(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		List list = userTaskDao.getMyGoals(start, limit);
		JSONArray jsonArray = serializationManager.serializeCollection(list);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", userTaskDao.getMyGoalsCount());
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

    @RequestMapping(value = Pages.TaskManagementPage.Url.GET_USER_PROBLEMS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getUserProblems(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("communityId", getValueOfParameter(body, "communityId"));

        List list = userProblemDao.getProblemsList(filters, start, limit);
        JSONArray jsonArray = serializationManager.serializeCollection(list);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", userProblemDao.getProblemsCount(filters));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

	@RequestMapping(value = Pages.TaskManagementPage.Url.GET_MY_CUSTOMERS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getMyCustomers(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
		List list = userTaskDao.getMyCustomers(start, limit);
		JSONArray jsonArray = serializationManager.serializeCollection(list);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("total", userTaskDao.getMyCustomersCount());
		jsonObject.put("items", jsonArray);

		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = Pages.TaskManagementPage.Url.ADD_USER_TASK, method = RequestMethod.POST)
	public boolean addUserTask(@RequestBody String jsonData) {
		return cyberbrainService.addUserTask(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
	}

    @ResponseBody
    @RequestMapping(value = Pages.TaskManagementPage.Url.UPDATE_USER_TASK, method = RequestMethod.POST)
    public boolean updateUserTask(@RequestBody String jsonData) {
        return cyberbrainService.updateUserTask(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.TaskManagementPage.Url.ADD_USER_PROBLEM, method = RequestMethod.POST)
    public boolean addUserProblem(@RequestBody String jsonData) {
        return cyberbrainService.addUserProblem(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.TaskManagementPage.Url.UPDATE_USER_PROBLEM, method = RequestMethod.POST)
    public boolean updateUserProblem(@RequestBody String jsonData) {
        return cyberbrainService.updateUserProblem(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @ResponseBody
    @RequestMapping(value = Pages.TaskManagementPage.Url.COPY_OBJECT, method = RequestMethod.POST)
    public boolean copyObject(@RequestBody String jsonData) {
        return cyberbrainService.copyObject(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

	@RequestMapping(value = Pages.TaskManagementPage.Url.SEARCH_USER_TASK, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String searchUserTask(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String query = getValueOfParameter(body, "query");
		Long communityId = Long.valueOf(getValueOfParameter(body, "communityId"));

		List<UserTask> list = userTaskDao.search(query, (page - 1) * perPage, perPage, communityId);
		JSONArray jsonArray = serializationManager.serializeCollection(list);
		return jsonArray.toString();
	}

	@RequestMapping(value = Pages.TaskManagementPage.Url.GET_TRACK_OBJECT, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getTrackObject(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String query = getValueOfParameter(body, "query");
		Long communityId = Long.valueOf(getValueOfParameter(body, "communityId"));

		List<KnowledgeRepository> list = knowledgeRepositoryDao.getUserTaskObjects(query, (page - 1) * perPage, perPage, communityId);
		JSONArray jsonArray = serializationManager.serializeCollection(list);
		return jsonArray.toString();
	}

    @RequestMapping(value = Pages.TaskManagementPage.Url.GET_TRACK_OBJECT_STATUS_FROM_TO_INFO, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getTrackObjectStatusFromToInfo(@RequestBody String jsonData) {
        JSONObject jsonObject = new JSONObject(WebUtils.urlDecode(jsonData));
        Long knowledgeId = Long.valueOf(jsonObject.get("knowledgeId").toString());

        KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeId);
        jsonObject.put("status_from_id", knowledge.getTag().getId());
        jsonObject.put("status_from_name", knowledge.getTag().getEssence());

        if (knowledge.getNext() != null) {
            knowledge = knowledgeRepositoryDao.getById(knowledge.getNext());
            jsonObject.put("status_to_id", knowledge.getTag().getId());
            jsonObject.put("status_to_name", knowledge.getTag().getEssence());

            JSONObject conditionsInfo = knowledgeRepositoryDao.getConditionsInfoById(knowledge.getId());
            jsonObject.put("conditions", conditionsInfo.get("conditions"));
        } else {
            jsonObject.put("status_to_id", thesaurusDao.getByEssence("?", null).getId());
            jsonObject.put("status_to_name", "?");
            jsonObject.put("conditions", "");
        }

        JSONObject trackInfo = knowledgeRepositoryDao.getTrackInfoById(knowledgeId);
        jsonObject.put("tag_many_id", trackInfo.get("tag_many_id"));
        jsonObject.put("tag_many_name", trackInfo.get("tag_many_name"));
        jsonObject.put("tag_many_properties", trackInfo.get("tag_many_properties"));

        return jsonObject.toString();
    }

	@RequestMapping(value = Pages.TaskManagementPage.Url.GET_SUBTASKS_BY_USER_TASK_ID, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getSubtasks(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		String taskId = getValueOfParameter(body, "id");

		List<UserTask> list = userSubtaskDao.getSubtasksByUserTask(userTaskDao.getById(Long.valueOf(taskId)));
		JSONArray jsonArray = serializationManager.serializeCollection(list);
		return jsonArray.toString();
	}

    @RequestMapping(value = Pages.TaskManagementPage.Url.GET_PROBLEMS_BY_USER_PROBLEM_ID, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getProblemsByUserProblemId(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
        String problemId = getValueOfParameter(body, "id");

        List<UserProblemPerformer> list = userProblemDao.getById(Long.valueOf(problemId)).getUserProblemPerformer();
        JSONArray jsonArray = serializationManager.serializeCollection(list);
        return jsonArray.toString();
    }

    @RequestMapping(value = Pages.TaskManagementPage.Url.GET_MANY_LIST, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getManyList(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        HashMap<String, String> filters = new HashMap<>();
        filters.put("communityId", getValueOfParameter(body, "communityId"));
        filters.put("filterMany", getValueOfParameter(body, "filterMany"));

        List list = knowledgeRepositoryDao.getManyList(filters, start, limit);
        JSONArray jsonArray = serializationManager.serializeCollection(list);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", knowledgeRepositoryDao.getManyCount(filters));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.TaskManagementPage.Url.NEW_OBJECT_WIZARD_FORM, method = RequestMethod.POST)
    public @ResponseBody String newObjectWizardForm(@RequestBody String jsonData) {
        return cyberbrainService.newObjectWizardForm(WebUtils.urlDecode(jsonData), CyberbrainObjects.USER.getName(), SecurityUtils.getUser().getId());
    }

    @RequestMapping(value = Pages.TaskManagementPage.Url.NEW_OBJECT_WIZARD_FORM_QUESTIONS_TRACKS, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String newObjectWizardFormQuestionsTracks(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        HashMap<String, String> filters = new HashMap<>();
        filters.put("tagOwnerId", getValueOfParameter(body, "tagOwnerId"));

        List list = knowledgeRepositoryDao.newObjectWizardFormQuestionsTracksList(filters, start, limit);

        JSONArray jsonArray = new JSONArray();
        for (Object obj : list) {
            jsonArray.put(obj);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", knowledgeRepositoryDao.newObjectWizardFormQuestionsTracksCount(filters));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.TaskManagementPage.Url.NEW_OBJECT_WIZARD_FORM_QUESTIONS_PROPERTIES, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String newObjectWizardFormQuestionsProperties(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        HashMap<String, String> filters = new HashMap<>();
        filters.put("tagOwnerId", getValueOfParameter(body, "tagOwnerId"));

        List list = knowledgeRepositoryDao.newObjectWizardFormQuestionsPropertiesList(filters, start, limit);
        JSONArray jsonArray = new JSONArray();

        for (Object obj : list) {
            jsonArray.put(obj);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", knowledgeRepositoryDao.newObjectWizardFormQuestionsPropertiesCount(filters));
        jsonObject.put("items", jsonArray);

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.TaskManagementPage.Url.NEW_OBJECT_WIZARD_FORM_GET_TRACKS_FOR_OBJECT, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String newObjectWizardFormGetTracksForObject(@RequestBody String body, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "15") int limit) {
        JSONObject jsonObject = new JSONObject(WebUtils.urlDecode(body));

        if (!jsonObject.isNull("tagOwnerId")) {
            Long tagOwnerId = Long.valueOf(jsonObject.get("tagOwnerId").toString());

            List list = knowledgeRepositoryDao.newObjectWizardFormGetTracksListByObjectId(tagOwnerId);
            JSONArray jsonArray = new JSONArray();

            for (Object obj : list) {
                jsonArray.put(obj);
            }

            jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("total", list.size());
            jsonObject.put("items", jsonArray);
        } else {
            jsonObject.put("success", false);
        }

        return jsonObject.toString();
    }

    @RequestMapping(value = Pages.ImportExportPage.Url.MAIN, method = RequestMethod.GET)
    public String showImportExportPage(Model model) {
        model.addAttribute(Pages.CURRENT_PAGE_TITLE, Pages.ImportExportPage.TITLE);
        return Pages.ImportExportPage.VIEW;
    }

    @RequestMapping(value = Pages.ImportExportPage.Url.JOURNAL_ATTENTION_IMPORT_DATA, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String journalAttentionImportData(HttpServletRequest request, HttpServletResponse response) {
        String result = "";

        try {
            Map<String, Object> dataMap = uploadExcelFile(CyberbrainObjects.JOURNAL_ATTENTION.getName(), request, response, false);

            if (dataMap != null) {
                List<String> errors = (List<String>) dataMap.get("errors");
                CyberbrainFile cyberbrainFile = (CyberbrainFile) dataMap.get("file");

                if (errors.size() == 0) {
                    if (cyberbrainService.importDataJournalAttention(dataMap, CyberbrainObjects.FILE.getName(), cyberbrainFile.getId())) {
                        cyberbrainFile.setSuccessfullyLoaded(true);
                        cyberbrainFileDao.update(cyberbrainFile);

                        result = (String) dataMap.get("success");
                    }
                } else {
                    for (String str : errors) {
                        result = result + str + "<br/>";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);

        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = Pages.ImportExportPage.Url.JOURNAL_ATTENTION_EXPORT_DATA, method = RequestMethod.GET)
    public void journalAttentionExportData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exportData(JournalAttention.class, request, response);
    }

    @RequestMapping(value = Pages.ImportExportPage.Url.KNOWLEDGE_REPOSITORY_IMPORT_DATA, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String knowledgeRepositoryImportData(HttpServletRequest request, HttpServletResponse response) {
        String result = "";

        try {
            Map<String, Object> dataMap = uploadExcelFile(CyberbrainObjects.KNOWLEDGE_REPOSITORY.getName(), request, response, true);

            if (dataMap != null) {
                List<String> errors = (List<String>) dataMap.get("errors");
                CyberbrainFile cyberbrainFile = (CyberbrainFile) dataMap.get("file");

                if (errors.size() == 0) {
                    if (cyberbrainService.importDataKnowledgeRepository(dataMap, CyberbrainObjects.FILE.getName(), cyberbrainFile.getId())) {
                        cyberbrainFile.setSuccessfullyLoaded(true);
                        cyberbrainFileDao.update(cyberbrainFile);

                        result = (String) dataMap.get("success");
                    }
                } else {
                    for (String str : errors) {
                        result = result + str + "<br/>";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);

        return jsonObject.toString();
    }

	@RequestMapping(value = Pages.ImportExportPage.Url.KNOWLEDGE_REPOSITORY_IMPORT_DATA_CONDITION, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String knowledgeRepositoryImportDataCondition(HttpServletRequest request, HttpServletResponse response) {
		String result = "";

		try {
			Map<String, Object> dataMap = uploadExcelFile(CyberbrainObjects.KNOWLEDGE_REPOSITORY_CONDITION.getName(), request, response, true);

			if (dataMap != null) {
				List<String> errors = (List<String>) dataMap.get("errors");
				CyberbrainFile cyberbrainFile = (CyberbrainFile) dataMap.get("file");

				if (errors.size() == 0) {
					if (cyberbrainService.importDataKnowledgeRepositoryCondition(dataMap, CyberbrainObjects.FILE.getName(), cyberbrainFile.getId())) {
						cyberbrainFile.setSuccessfullyLoaded(true);
						cyberbrainFileDao.update(cyberbrainFile);

						result = (String) dataMap.get("success");
					}
				} else {
					for (String str : errors) {
						result = result + str + "<br/>";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", result);

		return jsonObject.toString();
	}

    @ResponseBody
    @RequestMapping(value = Pages.ImportExportPage.Url.KNOWLEDGE_REPOSITORY_EXPORT_DATA, method = RequestMethod.GET)
    public void knowledgeRepositoryExportData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exportData(KnowledgeRepository.class, request, response);
    }

    @RequestMapping(value = Pages.ImportExportPage.Url.THESAURUS_IMPORT_DATA, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String thesaurusImportData(HttpServletRequest request, HttpServletResponse response) {
        String result = "";

        try {
            Map<String, Object> dataMap = uploadExcelFile(CyberbrainObjects.THESAURUS.getName(), request, response, false);

            if (dataMap != null) {
                List<String> errors = (List<String>) dataMap.get("errors");
                CyberbrainFile cyberbrainFile = (CyberbrainFile) dataMap.get("file");

                if (errors.size() == 0) {
                    if (cyberbrainService.importThesaurusServiceTags(dataMap, CyberbrainObjects.FILE.getName(), cyberbrainFile.getId())) {
                        cyberbrainFile.setSuccessfullyLoaded(true);
                        cyberbrainFileDao.update(cyberbrainFile);

                        result = (String) dataMap.get("success");
                    }
                } else {
                    for (String str : errors) {
                        result = result + str + "<br/>";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);

        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = Pages.ImportExportPage.Url.THESAURUS_EXPORT_DATA, method = RequestMethod.GET)
    public void thesaurusExportData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exportData(Thesaurus.class, request, response);
    }

    @ResponseBody
    @RequestMapping(value = Pages.ImportExportPage.Url.TASK_MANAGEMENT_EXPORT_DATA, method = RequestMethod.GET)
    public void taskManagementExportData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exportData(UserTask.class, request, response);
    }

	private Map<String, Object> uploadExcelFile(String object, HttpServletRequest request, HttpServletResponse response, boolean withHeaders) throws IOException, FileUploadException {
		if (!ServletFileUpload.isMultipartContent(request)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		OutputStream data = null;
        String fileName = "";
        Long communityId = -1l;

		for (FileItem item : items) {
			if (!item.isFormField()) {
				fileName = item.getName();
				String fileExtensionName = getFileExtension(fileName);

				if ((!Extensions.XLS.equals(fileExtensionName)) && (!Extensions.XLSX.equals(fileExtensionName))) {
					return null;
				}

				data = item.getOutputStream();
			}

            if (item.getFieldName().equals("communityId")) {
                communityId = Long.valueOf(item.getString());
            }
		}

        Map<String, Object> dataMap = excelProcessor.getDataList(object, data, withHeaders);
        CyberbrainObject cyberbrainObject = cyberbrainObjectDao.getByName(object);

        // Сделаем запись в БД о загрузке файла
        CyberbrainFile cyberbrainFile = new CyberbrainFile();
        cyberbrainFile.setCreationDate(new Date(System.currentTimeMillis()));
        cyberbrainFile.setName(fileName);
        cyberbrainFile.setObject(cyberbrainObject);
        cyberbrainFile.setSuccessfullyLoaded(false); // файл еще не полностью обработан
        cyberbrainFile.setUserEntity(sharerDao.getById(SecurityUtils.getUser().getId()));
        cyberbrainFile.setCommunity(communityDao.getById(communityId));
        cyberbrainFileDao.save(cyberbrainFile);

        dataMap.put("file", cyberbrainFile);
        dataMap.put("communityId", communityId);

        return dataMap;
	}

	private String getFileExtension(String fileName) {
		int dotPos = fileName.lastIndexOf(".") + 1;
		return fileName.substring(dotPos).toLowerCase();
	}

	private String getValueOfParameter(String body, String parameter) {
		String paramValue = "";
		body = WebUtils.urlDecode(body);
		for (String pair : body.split("&")) {
			String[] parts = pair.split("=");
			if (parts.length > 1) {
				String key = parts[0];
				String value = parts[1];
				if (parameter.equals(key)) {
					paramValue = value.replaceAll("'", "''");
				}
			}
		}
		return paramValue;
	}

    private void exportData(Class<?> clazz, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long communityId = Long.valueOf(request.getParameter("communityId").toString());
		String fileToDownload = cyberbrainService.exportDataTable(clazz, true, communityId);

		if (fileToDownload != null) {
			int BUFFER_SIZE = 4096;

			ServletContext context = request.getServletContext();

			File downloadFile = new File(fileToDownload);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fileToDownload);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", downloadFile.getName()));
			response.setHeader("Content-Length", String.valueOf(downloadFile.length()));

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			OutputStream outStream = response.getOutputStream();
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			response.flushBuffer();

			inputStream.close();
			outStream.close();

			// Удаляем временный файл
			File f = new File(fileToDownload);
			f.delete();
		}
	}

    @RequestMapping(value = Pages.Sections.Url.GET_COUNTS_RECORDS_AND_SCORE, produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public @ResponseBody String getCountsRecordsAndScore(@RequestBody String body, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
        int myCustomersCount = userTaskDao.getMyCustomersCount();
        int mySubcontractorsCount = userTaskDao.getMySubcontractorsCount();
        int myGoalsCount = userTaskDao.getMyGoalsCount();
        int taskManagementCount =  myCustomersCount + mySubcontractorsCount + myGoalsCount;

        int journalAttentionCount = journalAttentionDao.getJournalAttentionCount(null);
        int thesaurusCount = thesaurusDao.getThesaurusCount("", "");

        BigInteger questionsCount = knowledgeRepositoryDao.getQuestionsCount(null);
        BigInteger questionsManyCount = knowledgeRepositoryDao.getQuestionsManyCount(null);
        BigInteger questionsPropertiesCount = knowledgeRepositoryDao.getQuestionsPropertiesCount(null);
        BigInteger questionsTracksCount = knowledgeRepositoryDao.getQuestionsTracksCount(null);
        BigInteger myProblemsCount = BigInteger.valueOf(userProblemDao.getProblemsCount(null));
        BigInteger knowledgeRepositoryCount = questionsCount.add(questionsManyCount).add(questionsPropertiesCount).add(questionsTracksCount).add(myProblemsCount);
        BigDecimal userScore = scoreSharerDao.getCurrentUserScore();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskManagementCount", taskManagementCount);
        jsonObject.put("journalAttentionCount", journalAttentionCount);
        jsonObject.put("thesaurusCount", thesaurusCount);
        jsonObject.put("knowledgeRepositoryCount", knowledgeRepositoryCount);
        jsonObject.put("userScore", userScore);

        return jsonObject.toString();
    }
}
