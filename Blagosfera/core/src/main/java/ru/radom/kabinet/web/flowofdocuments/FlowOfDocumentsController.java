package ru.radom.kabinet.web.flowofdocuments;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.fields.MetaFieldDao;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTemplateDao;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTypeDao;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTypeParticipantDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.services.DocumentTemplateDataService;
import ru.radom.kabinet.hibernate.HibernateProxyTypeAdapter;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.json.flowofdocuments.DocumentTemplateSerializer;
import ru.radom.kabinet.json.flowofdocuments.DocumentTypeSerializer;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.FlowOfDocumentsService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.flowofdocuments.dto.*;
import ru.radom.kabinet.web.utils.Breadcrumb;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Контроллер для работы с документооборотом
 */

@Controller
public class FlowOfDocumentsController {
	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private DocumentTypeDao documentTypeDao;

	@Autowired
	private DocumentTypeParticipantDao documentTypeParticipantDao;

	@Autowired
	private DocumentTypeSerializer documentTypeSerializer;

	@Autowired
	@Qualifier("flowOfDocumentsService")
	private FlowOfDocumentsService flowOfDocumentsService;

	@Autowired
	private FieldsGroupDao fieldsGroupDao;

	@Autowired
	private FieldsService fieldsService;

	@Autowired
	private DocumentTemplateDao documentTemplateDao;

	@Autowired
	private DocumentTemplateSerializer documentTemplateSerializer;

	@Autowired
	private MetaFieldDao metaFieldDao;

	@Autowired
	private RequestContext radomRequestContext;

	@Autowired
	private RameraListEditorItemDAO rameraListEditorItemDAO;

	@Autowired
	private DocumentTemplateDataService documentTemplateDataService;

	private static Gson gson = null;

	static {
		GsonBuilder b = new GsonBuilder();
		b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
		gson = b.create();
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTypes", method = RequestMethod.GET)
	public String showDocumentTypesPage(Model model) {
		model.addAttribute("currentPageTitle", "Классы документов");

		DocumentClassEntity documentType = new DocumentClassEntity();
		ArrayList<DocumentClassDataSourceEntity> participants = new ArrayList<>();
		documentType.setParticipants(participants);

		model.addAttribute("documentTypeForm", documentType);
		return "documentTypesPage";
	}


	/**
	 * Загрузить дерево классов документов с поиском.
	 * @param body
	 * @return
	 */

	@RequestMapping(value = "/admin/flowOfDocuments/documentTypes.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public DocumentClassGridDto getDocumentTypesList(@RequestBody String body) {
		DocumentClassGridDto result = null;
		try {
			HashMap<String, String> filters = new HashMap<>();
			HashMap<String, String> searchFilter = new HashMap<>();

			String parentIdStr = getValueOfParameter(body, "node");
			Long parentId = -1l;
			try {
				parentId = Long.valueOf(parentIdStr);
			} catch (Exception e) {
				// do nothing
			}

			String nameFieldFilterValue = getValueOfParameter(body, "name");
			if (nameFieldFilterValue != null && !nameFieldFilterValue.equals("")) {
				nameFieldFilterValue = getValueOfParameter(body, "name");
				searchFilter.put("name", nameFieldFilterValue);
			}

			if (parentId == null || parentId == -1l) {
				filters.put("parent", null);
			} else {
				filters.put("parent", parentIdStr);
			}

			String sort = getValueOfParameter(body, "sort");
			List<DocumentClassEntity> list = documentTypeDao.getList(filters, sort);

			List<DocumentClassEntity> searchList = null;
			Set<Long> elementIdsForExpand = new HashSet<>();
			if (searchFilter.size() > 0) {
				searchList = documentTypeDao.getList(searchFilter, sort);
				for (DocumentClassEntity foundDocType : searchList) {
					while (foundDocType.getParent() != null) {
						foundDocType = foundDocType.getParent();
						if (list.contains(foundDocType)) {
							elementIdsForExpand.add(list.get(list.indexOf(foundDocType)).getId());
						}
					}
				}
			}
			result = new DocumentClassGridDto(list, searchList, elementIdsForExpand);
		} catch (Exception e) {
			result = DocumentClassGridDto.toError();
		}
		return result;
	}

	private String getPathNameDocumentType(DocumentClassEntity documentType){
		List<String> list = new LinkedList<>();
		list.add(documentType.getName());
		while(documentType.getParent() != null) {
			documentType = documentType.getParent();
			list.add(documentType.getName());
		}
		list = Lists.reverse(list);
		return StringUtils.join(list, " / ");
	}

	@RequestMapping(value = "/admin/flowOfDocuments/parentDocumentTypes.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String getParentDocumentTypesList(@RequestBody String body) {
		String result = null;
		try {
			HashMap<String, String> filters = new HashMap<>();


			String parentIdStr = getValueOfParameter(body, "node");
			Long parentId = -1l;
			try {
				parentId = Long.valueOf(parentIdStr);
			} catch (Exception e) {
				// do nothing
			}

			String fullPathParentIdStr = getValueOfParameter(body, "full_path_parent_id");
			Long fullPathParentId = -1l;
			try {
				fullPathParentId = Long.valueOf(fullPathParentIdStr);
			} catch (Exception e) {
				// do nothing
			}

			DocumentClassEntity rootParentNode = null;
			DocumentClassEntity foundNode = null;
			String sort = getValueOfParameter(body, "sort");

			foundNode = documentTypeDao.getById(fullPathParentId);

			// Если раскрываем ноду, то искать толь по ИД родителя
			if (parentId > -1) {
				filters.put("parent", parentIdStr);
				rootParentNode = foundNode;
				if (rootParentNode != null) {
					while (rootParentNode.getParent() != null && rootParentNode.getParent().getId().longValue() != parentId) {
						rootParentNode = rootParentNode.getParent();
					}
				}
			} else {
				filters.put("parent", null);
				rootParentNode = foundNode;
				if (rootParentNode != null) {
					while (rootParentNode.getParent() != null) {
						rootParentNode = rootParentNode.getParent();
					}
				}
			}



			List<DocumentClassEntity> list = documentTypeDao.getList(filters, sort);

			Map<String, Object> resultMap = new HashMap<>();

			resultMap.put("text", ".");
			List<Map<String, Object>> items = new ArrayList<>();
			for (DocumentClassEntity documentType : list) {
				Map<String, Object> item = new HashMap<>();

				int countChildren = documentTypeDao.getChildrenCount(documentType);
				if (countChildren > 0 && rootParentNode != null && foundNode != null &&
						foundNode.getId().longValue() != documentType.getId().longValue() &&
						rootParentNode.getId().longValue() == documentType.getId().longValue()) {
					item.put("expanded", true);
				} else if (countChildren > 0) {
					item.put("expanded", false);
				} else {
					item.put("children", new ArrayList<String>());
				}
				item.put("id", documentType.getId());
				item.put("key", documentType.getKey());
				item.put("name", documentType.getName());
				item.put("pathName", getPathNameDocumentType(documentType));
				if (documentType.getParent() != null) {
					item.put("parentId", documentType.getParent().getId());
					item.put("parentName", documentType.getParent().getName());
				}
				//item.put("children", new ArrayList<String>());
				items.add(item);
			}
			resultMap.put("children", items);

			result = gson.toJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Получить отфильтрованные классы документа <br/>
	 * Параметр <code><b>query</b></code> задает фильтр. <br/>
	 * Например: <br/>
	 * <code>'Док // уча // кас'</code><br/>
	 * будет искать классы у которых в названии есть 'кас', в предке - 'уча', а в предке предка 'док'.
	 * Таким образом можно делать поиск по дереву
	 */
	@ResponseBody
	@RequestMapping(
		value = "/admin/flowOfDocuments/getDocumentTypes",
		produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE,
		method = RequestMethod.POST
	)
	public List<Map<String, Object>> getDocumentTypeByQuery(
		@RequestParam String query,
		@RequestParam(required = false) Integer page,
		@RequestParam(value = "per_page", required = false) Integer perPage
	) {
		List<DocumentClassEntity> types = documentTypeDao.searchByQuery(query, true, page, perPage);
		return types.stream().map(documentType -> {
			Map<String, Object> item = new HashMap<>();
			item.put("id", documentType.getId());
			item.put("key", documentType.getKey());
			item.put("name", documentType.getName());
			item.put("pathName", getPathNameDocumentType(documentType));
			if (documentType.getParent() != null) {
				item.put("parentId", documentType.getParent().getId());
				item.put("parentName", documentType.getParent().getName());
			}
			return item;
		}).collect(Collectors.toList());
	}

	@RequestMapping(value = "/admin/flowOfDocuments/saveDocumentType", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String saveDocumentType(@ModelAttribute("documentTypeForm") DocumentClassEntity documentTypeForm) {
		try {
			flowOfDocumentsService.saveDocumentType(documentTypeForm);
			return JsonUtils.getSuccessJson().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/admin/flowOfDocuments/deleteDocumentType", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String deleteDocumentType(@RequestParam("id") DocumentClassEntity documentType) {
		try {
			flowOfDocumentsService.deleteDocumentType(documentType);
			return JsonUtils.getSuccessJson().toString();
		} catch (Exception e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentType/changeParent", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String changeParentDocumentType(@RequestParam("id") Long id, @RequestParam("parentId") Long parentId) {
		try {
			DocumentClassEntity documentType = documentTypeDao.getById(id);
			documentType.setParent(documentTypeDao.getById(parentId));
			documentTypeDao.update(documentType);
			return JsonUtils.getSuccessJson().toString();
		} catch (Exception e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentType/participants.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public List<DocumentClassDataSourceDto> getParticipantsList(
			@RequestParam("id") Long id, @RequestParam(value = "participantType", required = false) String participantType) {
		List<DocumentClassDataSourceEntity> list;
		if (participantType == null) {
			list = documentTypeDao.getById(id).getParticipants();
		} else {
			DocumentClassEntity documentType = documentTypeDao.getById(id);
			list = documentTypeParticipantDao.getListByDocumentType(documentType, participantType);
		}
		return DocumentClassDataSourceDto.toListDto(list);
	}


	/**
	 * Получить массив системных полей
	 * @return
	 */
	@RequestMapping(value = "/admin/flowOfDocuments/documentType/systemFields.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String getDocumentSystemFieldsList() {
		String result = null;
		try {
			List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("DOCUMENT_SYSTEM_", null);
			sortFieldsGroups(fieldsGroups);
			for(FieldsGroupEntity fieldsGroup : fieldsGroups) {
				sortFields(fieldsGroup.getFields());
				for (FieldEntity field : fieldsGroup.getFields()) {
					field.setFieldsGroup(null);
				}
			}
			result = gson.toJson(fieldsGroups);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("errorMessage", "Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage());
			result = gson.toJson(errorMap);
		}
		return result;
	}


	/**
	 * Сортировка групп полей.
	 * @param fieldsGroups
	 */
	private static void sortFieldsGroups(List<FieldsGroupEntity> fieldsGroups) {
		//сортируем группы полей по их позиции
		Collections.sort(fieldsGroups, new Comparator<FieldsGroupEntity>() {
			public int compare(FieldsGroupEntity obj1, FieldsGroupEntity obj2) {
				return ComparisonChain.start()
						.compare(obj1.getPosition(), obj2.getPosition())
						.result();
			}
		});
	}


	/**
	 * Сортировка полей.
	 * @param fields
	 */
	private static void sortFields(List<FieldEntity> fields) {
		Collections.sort(fields, (obj1, obj2) -> ComparisonChain.start()
                .compare(obj1.getPosition(), obj2.getPosition())
                .result());
	}

	/**
	 * Список групп полей и полей по типу участника и по ИД формы объединения
	 * @param participantType
	 * @param associationFormId
	 * @return
	 */
	@RequestMapping(value = "/admin/flowOfDocuments/documentType/filters.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public List<DocumentClassFilterDto> getDocumentTypeFiltersList(
			@RequestParam("participantType") String participantType,
			@RequestParam(value = "associationForm", required = false, defaultValue = "-1") Long associationFormId) {
		List<DocumentClassFilterDto> result = new ArrayList<>();
		List<FieldsGroupEntity> fieldsGroups = new ArrayList<>();

		//поля определяем по списку актуальных групп
		//группы с префиксом "ORGANIZATION_" не актуальны

		if (participantType.equals(ParticipantsTypes.INDIVIDUAL.getName()) || participantType.equals(ParticipantsTypes.INDIVIDUAL_LIST.getName())) {
			fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("PERSON_", null);
		} else if (participantType.equals(ParticipantsTypes.REGISTRATOR.getName())) {
			fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("PERSON_", null);
			fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix("REGISTRATOR_", null));
		} else if (participantType.equals(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName()) ||
				   participantType.equals(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName())) {
			fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("COMMUNITY_COMMON", null);
			fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix("COMMUNITY_WITH_ORGANIZATION_"));
		} else if (participantType.equals(ParticipantsTypes.COMMUNITY_WITHOUT_ORGANIZATION.getName())) {
			fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("COMMUNITY_COMMON", null);
			fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix("COMMUNITY_WITHOUT_ORGANIZATION_", null));
		} else if (participantType.equals(ParticipantsTypes.COMMUNITY_IP.getName())) {
			fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("COMMUNITY_COMMON", null);
		}

		RameraListEditorItem associationForm = null;
		if (associationFormId != null) {
			associationForm = rameraListEditorItemDAO.getById(associationFormId);
		}

		// добавляем все дополнительные группы полей
		if (associationForm == null) {
			fieldsGroups.addAll(fieldsGroupDao.getAllAdditionalFieldsGroup());
		} else {
			fieldsGroups.addAll(fieldsGroupDao.getByRameraListEditorItem(associationForm));
		}
		//сортируем группы полей по их позиции
		sortFieldsGroups(fieldsGroups);

		for (FieldsGroupEntity fieldGroup : fieldsGroups) {
			// TODO Переделать
			//Map<FieldEntity, FieldStates> map = fieldsService.getFieldsStatesMap(Collections.singletonList(fieldGroup), radomRequestContext.getCurrentSharer(), radomRequestContext.getCurrentSharer());

			List<FieldEntity> fields = fieldGroup.getFields();
			/*for (Map.Entry<FieldEntity, FieldStates> obj : map.entrySet()) {
				FieldEntity field = obj.getKey();
				FieldStates fieldStates = obj.getValue();
				if (fieldStates.isVisible()) {
					fields.add(field);
				}
			}*/


			//JSONArray allFields = null;
			List<FieldDto> fieldDtos;
			if (fields.size() > 0) {
				//сортируем набор полей по их позиции
				sortFields(fields);
				fieldDtos = FieldDto.toListDto(fields);
				//allFields = serializationManager.serializeCollection(fields);
			} else {
				fieldDtos = Collections.emptyList();
			}

			//добавляем мета поля
			/*List<MetaField> metaFields = metaFieldDao.getList(participantType, Collections.singletonList(fieldGroup));
			if (metaFields.size() > 0) {
				//сортируем набор полей по их позиции
				Collections.sort(metaFields, new Comparator<MetaField>() {
					public int compare(MetaField obj1, MetaField obj2) {
						return ComparisonChain.start()
								.compare(obj1.getPosition(), obj2.getPosition())
								.result();
					}
				});
				if (allFields != null) {
					JSONArray metaFieldsArray = serializationManager.serializeCollection(metaFields);
					for (int i = 0; i < metaFieldsArray.length(); i++) {
						allFields.put(metaFieldsArray.get(i));
					}
				} else {
					allFields = serializationManager.serializeCollection(metaFields);
				}
			}*/

			if (fieldDtos != null) {
				List<Long> associationForms = new ArrayList<>();
				if (fieldGroup.getAssociationForms() != null && fieldGroup.getAssociationForms().size() > 0) {
					for (RameraListEditorItem listEditorItem : fieldGroup.getAssociationForms()) {
						associationForms.add(listEditorItem.getId());
					}
				}

				DocumentClassFilterDto documentClassFilterDto = new DocumentClassFilterDto();
				documentClassFilterDto.setGroup(fieldGroup.getName());
				documentClassFilterDto.setAssociationForms(associationForms);
				documentClassFilterDto.setFilters(fieldDtos);
				result.add(documentClassFilterDto);
			}
		}

		return result;
	}


	/**
	 * Получить мапу с участками и полями, котороые добавлены в классе документов
	 * @return
	 */
	@RequestMapping(value = "/admin/flowOfDocuments/participantsFilteredFields.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String getParticipantsFilteredFields(@RequestParam(value = "templateId", defaultValue = "-1") long templateId){
		String result = null;
		try {
			Map<Long, List<FieldEntity>> resultMap = new HashMap<>();
			if (templateId > 0) {
				DocumentTemplateEntity documentTemplate = documentTemplateDao.getById(templateId);
				if (documentTemplate != null && documentTemplate.getDocumentType() != null && documentTemplate.getDocumentType().getParticipants() != null) {
					for (DocumentClassDataSourceEntity documentTypeParticipant : documentTemplate.getDocumentType().getParticipants()) {
						for (FieldEntity field : documentTypeParticipant.getFieldsFilters()) {
							field.setFieldsGroup(null);
						}
						resultMap.put(documentTypeParticipant.getId(), documentTypeParticipant.getFieldsFilters());
					}
				}
			}
			result = gson.toJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("errorMessage", "Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage());
			result = gson.toJson(errorMap);
		}
		return result;
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplates", method = RequestMethod.GET)
	public String showDocumentTemplatesPage(Model model) {
		model.addAttribute("currentPageTitle", "Шаблоны документов");
		model.addAttribute("documentTemplateForm", new DocumentTemplateEntity());
		return "documentTemplatesPage";
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTypesForTemplates.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public DocumentClassTreeForTemplatesDto getDocumentTypeList(@RequestBody String body) {
		DocumentClassTreeForTemplatesDto result = null;
		try {
			// Найти все шаблоны документов, по фильтру
			// Найти все родитеслькие классы найденных шаблонов документов

			String parentIdStr = getValueOfParameter(body, "node");
			String searchName = getValueOfParameter(body, "name");
			searchName = searchName.equals("") ? null : searchName;

			// Загружаем все шаблоны и ищем классы
			List<DocumentTemplateEntity> templates = documentTemplateDao.findAll();

			Set<Long> classOfDocumentsIds = new HashSet<>();
			Map<Long, Integer> countDocumentsMap = new HashMap<>();
			for (DocumentTemplateEntity template : templates) {
				if (template.getDocumentType() != null) {
					classOfDocumentsIds.add(template.getDocumentType().getId());
					if (!countDocumentsMap.containsKey(template.getDocumentType().getId())) {
						countDocumentsMap.put(template.getDocumentType().getId(), 1);
					} else {
						countDocumentsMap.put(template.getDocumentType().getId(), countDocumentsMap.get(template.getDocumentType().getId()) + 1);
					}
				}
			}

			Long parentId = VarUtils.getLong(parentIdStr, null);
			//Map<String, Object> resultMap = documentTypeSerializer.serializeDocumentTypeFullTree(parentId, classOfDocumentsIds, searchName, countDocumentsMap);
			//parentClass, Set<Long> classOfDocumentsIds, Map<Long, DocumentClassEntity> documentClasses, String searchName, Map<Long, Integer> countDocumentsMap
			DocumentClassEntity parentClass = null;
			if (parentId != null) {
				parentClass = documentTypeDao.getById(parentId);
			}
			Map<Long, DocumentClassEntity> documentClassMap = new HashMap<>();
			if (classOfDocumentsIds.size() > 0) {
				List<DocumentClassEntity> documentClasses = documentTypeDao.getByIds(new ArrayList<>(classOfDocumentsIds));
				for (DocumentClassEntity documentClass : documentClasses) {
					documentClassMap.put(documentClass.getId(), documentClass);
				}

			}
			return new DocumentClassTreeForTemplatesDto(parentClass, classOfDocumentsIds, documentClassMap, searchName, countDocumentsMap);
		} catch (Exception e) {
			result = DocumentClassTreeForTemplatesDto.toError();
		}
		return result;
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplates.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public DocumentTemplatesGridDto getDocumentTemplatesList(
			@RequestBody String body,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "limit", defaultValue = "15") int limit) {
		HashMap<String, String> filters = new HashMap<>();
		filters.put("name", getValueOfParameter(body, "name"));
		filters.put("classId", getValueOfParameter(body, "classId"));

		String sort = getValueOfParameter(body, "sort");

		List<DocumentTemplateEntity> list = documentTemplateDao.getList(filters, start, limit, sort);
		int count = documentTemplateDao.getCount(filters);

		return new DocumentTemplatesGridDto(list, count);
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplate/edit", method = RequestMethod.GET)
	public String showDocumentTemplateCreatePage(Model model, @RequestParam(value = "documentTemplateId", required = false) Long documentTemplateId) {
		/*model.addAttribute("currentPageTitle", "Создание шаблона документа");

		String params;
		if (documentTemplateId != null && documentTemplateId > 0) {
			DocumentTemplateEntity documentTemplate = documentTemplateDao.getById(documentTemplateId);
			params = "?documentTemplateId=" + documentTemplate.getId().toString();
			model.addAttribute("documentTemplateForm", documentTemplate);
			// Ищем весь путь до класса документов
			model.addAttribute("classDocumentPath", getPathNameDocumentType(documentTemplate.getDocumentType()));
		} else {
			return "documentTemplatesPage";
		}

		Breadcrumb breadcrumb = new Breadcrumb()
				.add("Документооборот", "#")
				.add("Шаблоны документов", "/admin/flowOfDocuments/documentTemplates")
				.add("Создание шаблона документа", "/admin/flowOfDocuments/documentTemplate/edit" + params);

		model.addAttribute("breadcrumb", breadcrumb);*/
		return "documentTemplateEditPage";
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplate/save.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public DocumentTemplateDto saveDocumentTemplate(@RequestBody DocumentTemplateSaveRequestDto documentTemplateForm) {
		DocumentTemplateEntity documentTemplate = flowOfDocumentsService.saveDocumentTemplate(documentTemplateForm);
		return DocumentTemplateDto.toDto(documentTemplate);
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplate/delete", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	public @ResponseBody String deleteDocumentTemplate(@RequestParam("id") DocumentTemplateEntity documentTemplate) {
		try {
			flowOfDocumentsService.deleteDocumentTemplate(documentTemplate);
			return JsonUtils.getSuccessJson().toString();
		} catch (Exception e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}


	/**
	 * Обновить позиции классов документов
	 * @param positionsData
	 * @return
	 */
	@RequestMapping(value = "/admin/flowOfDocuments/updatePositionsDocTypes.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public String updatePositionsDocTypes(@RequestBody Map<Long, Integer> positionsData) {
		flowOfDocumentsService.changePositionsDocumentTypes(positionsData);
		return JsonUtils.getSuccessJson().toString();
	}


	/**
	 * Обновить позиции шаблонов документов
	 * @param positionsData
	 * @return
	 */
	@RequestMapping(value = "/admin/flowOfDocuments/updatePositionsDocTemplates.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public String updatePositionsDocTemplates(@RequestBody Map<Long, Integer> positionsData) {
		flowOfDocumentsService.changePositionsDocumentTemplates(positionsData);
		return JsonUtils.getSuccessJson().toString();
	}

	@RequestMapping(value = "/admin/flowOfDocuments/documentTemplate/pageData.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public DocumentTemplatePageDataDto getTemplatePageData(@RequestParam("templateId") Long templateId) {
		DocumentTemplate documentTemplate = documentTemplateDataService.getById(templateId);
		return new DocumentTemplatePageDataDto(documentTemplate);
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

	@RequestMapping(value = "/flowOfDocuments/filterTemplates.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public List<DocumentTemplateDto> filterTemplates(@RequestParam("query") String query) {
		return DocumentTemplateDto.toDtoList(documentTemplateDataService.getFilteredTemplate(query, null, 0, 10), false);
	}
}
