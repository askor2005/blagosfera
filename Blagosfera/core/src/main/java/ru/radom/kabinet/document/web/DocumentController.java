package ru.radom.kabinet.document.web;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.*;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantsDto;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.services.*;
import ru.radom.kabinet.document.web.dto.*;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.json.flowofdocuments.DocumentTypeSerializer;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.pdf.PdfService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.flowofdocuments.dto.DocumentDataDto;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * Created by vgusev on 16.06.2015.
 */
@Controller
public class DocumentController {

    /*private static Gson gson = null;

    static {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        b.setDateFormat("dd.MM.yyy");
        gson = b.create();
    }*/
    
    public static final String BASE_URL_ADMIN_PAGE = "/admin/docs";

    public static final String BASE_URL_USER_PAGE = "/document/service";

    //private static final MediaType PDF_MEDIA_TYPE =  MediaType.valueOf("application/pdf");

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private UserFieldsParserService userFieldsParserService;

    @Autowired
    private DocumentTypeSerializer documentTypeSerializer;

    @Autowired
    @Qualifier("calibreHtmlToPdfServiceImpl")
    private PdfService pdfService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private DocumentParticipantService documentParticipantService;

    @Autowired
    private DocumentTemplateDataService documentTemplateDomainService;

    private static final String FIELD_BACKGROUND_COLOR_SYSTEM_ATTR_NAME = "field.background.color";

    private static final String FIELD_COLOR_SYSTEM_ATTR_NAME = "field.color";

    /**
     * Получить все шаблоны.
     * @return
     */
    /*@RequestMapping(BASE_URL_ADMIN_PAGE + "/process/getTemplates.json")
    public @ResponseBody String getTemplates() {
        String result = null;
        try {
            List<DocumentTemplate> flowOfDocumentTemplates = documentService.getAllDocumentTemplates();
            result = gson.toJson(new OperationResult(true, "", flowOfDocumentTemplates));
        } catch (Exception e) {
            result = gson.toJson(new OperationResult(false, "Произошла ошибка! Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage()));
        }
        return result;
    }*/

    /**
     * Получить отфильтрованные шаблоны.
     * Используется в DocumentTemplateModalEditor.js
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/process/getTemplates", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<DocumentTemplateDto> getFilteredTemplates(
        @RequestParam String query,
        @RequestParam(value = "templateClass", required = false) Long templateClassId,
        @RequestParam(required = false) Integer page,
        @RequestParam(value = "per_page", required = false) Integer perPage
    ) {
        List<DocumentTemplate> documentTemplates = documentTemplateDomainService.getFilteredTemplate(query, templateClassId, page, perPage);
        return DocumentTemplateDto.toDtoList(documentTemplates);
    }

    /**
     * Получить список участников шаблона
     * TODO Переделать. Используется в DocumentTemplateModalEditor
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/getTemplateParticipants", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<DocumentClassDataSourceDto> getTemplateParticipants(@RequestParam Long templateId) {
        List<DocumentClassDataSource> participants = documentService.getUsedInTemplateClassParticipants(templateId);
        return DocumentClassDataSourceDto.toDtoList(participants);
    }

    /**
     * Получить шаблон со списком типов участников и возможных участников по ИД шаблона.
     * @param templateId
     * @return
     */
    // TODO Не используется
    /*@RequestMapping(value = BASE_URL_ADMIN_PAGE + "/process/getTemplate.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    public @ResponseBody OperationResult getTemplate(@RequestParam(value = "template_id", required = true) long templateId) {
        OperationResult result;
        try {
            DocumentTemplate flowOfDocumentTemplate = documentService.getFlowOfDocumentTemplateById(templateId);
            result = new OperationResult(true, "", flowOfDocumentTemplate);
        } catch (Exception e) {
            e.printStackTrace();
            result = new OperationResult(false, "Произошла ошибка! Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage());
        }
        return result;
    }*/

    /**
     * Список возможных участников шаблона документа
     * @param templateId
     * @return
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/process/getPossibleParticipants.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<PossibleSourceParticipantsDto> getPossibleSourceParticipants(
            @RequestParam(value = "template_id", required = true) Long templateId) {
        return documentService.getPossibleParticipants(templateId);
    }

    /**
     * Создать контент документа на основе шаблона и участников.
     * @param createDocumentContentRequestDto
     * TODO Переделать documentTestAdminPage
     * @return
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/process/createDocumentContent.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CreateDocumentContentResponseDto createDocumentContent(@RequestBody CreateDocumentContentRequestDto createDocumentContentRequestDto) {
        Long templateId = createDocumentContentRequestDto.getTemplateId();
        List<CreateDocumentParameter> createDocumentParameters = createDocumentContentRequestDto.getCreateDocumentParameters();
        FlowOfDocumentDTO documentDto = documentService.generateDocumentDTO(templateId, createDocumentParameters);
        return new CreateDocumentContentResponseDto(prepareDocumentName(documentDto.getName()), prepareDocumentName(documentDto.getShortName()), documentDto.getContent());
    }

    private String prepareDocumentName(String name) {
        name = Jsoup.parse(name).text();
        name = name.replaceAll("&nbsp;", " ");
        name = StringUtils.join(name.split("[\\s]"), " ");
        return name;
    }

    /*@RequestMapping(value = BASE_URL_ADMIN_PAGE + "/adminpage.html", method = RequestMethod.GET)
    public String getAdminPage(Model model) {
        return "documentTestAdminPage";
    }*/

    /**
     * Получить список участников по типу участников
     * @param participantType
     * @return
     */
    // TODO Вероятнее всего использоваться не будет
    /*@RequestMapping(value = BASE_URL_USER_PAGE + "/getParticipants.json", method = RequestMethod.POST)
    public @ResponseBody String getParticipants(@RequestParam(value = "participant_type", required = true) String participantType) {
        String result = null;
        try {
            List<DocumentParticipantEntity> participants = documentService.getFlowOfDocumentParticipantsByTypeName(participantType);
            result = gson.toJson(new OperationResult(true, "", participants));
        } catch (Exception e) {
            result = gson.toJson(new OperationResult(false, "Произошла ошибка! Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage()));
        }
        return result;
    }*/

    /**
     * Получить список полей участника
     * @param participantType
     * @param participantId
     * @return
     */
    // TODO На клиенте не используется
    /*@RequestMapping(value = BASE_URL_USER_PAGE + "/getParticipantFields.json", method = RequestMethod.POST)
    public @ResponseBody String getParticipantFields(@RequestParam(value = "participant_type", required = true) String participantType, @RequestParam(value = "participant_id", required = true) long participantId) {
        String result;
        try {
            DocumentParticipantEntity participant = documentService.createFlowOfDocumentParticipant(participantType, participantId, 1);
            result = gson.toJson(new OperationResult(true, "", participant.getParticipantFields()));
        } catch (Exception e) {
            result = gson.toJson(new OperationResult(false, "Произошла ошибка! Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage()));
        }
        return result;
    }*/

    /**
     * Метод сохранения пользовательских полей в документ
     * @param saveUserFieldsDto
     * @return
     */
    // TODO Переделать обработку на клиенте
    @RequestMapping(value = BASE_URL_USER_PAGE + "/saveUserFieldsInDocument.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto saveUserFieldsInDocument(@RequestBody SaveUserFieldsDto saveUserFieldsDto) {
        documentService.saveUserFieldsInDocument(saveUserFieldsDto.getDocumentId(), saveUserFieldsDto.getDocumentUserFields(), SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * Метод подписания документа текущим пользователем
     * @param documentId ИД документа
     * @return json
     */
    @TokenProtected(systemOption = "document.sign.protected")
    @RequestMapping(value = BASE_URL_USER_PAGE + "/signDocument.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto signDocument(@RequestParam(value = "document_id") Long documentId) {
        documentService.signDocument(documentId, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * Отказаться от подписи документа
     * @param documentId ИД документа
     * @return json
     */
    @TokenProtected(systemOption = "document.sign.protected")
    @RequestMapping(value = BASE_URL_USER_PAGE + "/unSignDocument.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto unSignDocument(@RequestParam(value = "document_id") long documentId) {
        documentService.unSignDocument(documentId, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * Метод пописания списка документов текущим пользователем
     * @param documentIds
     * @return
     */
    @TokenProtected(systemOption = "document.sign.protected")
    @RequestMapping(value = BASE_URL_USER_PAGE + "/signDocuments.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto signDocuments(@RequestBody List<Long> documentIds) {
        documentService.signDocuments(documentIds, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    @RequestMapping(
        value = {
            BASE_URL_ADMIN_PAGE + "/getUserFieldsByTemplate.json",
            BASE_URL_ADMIN_PAGE + "/getTemplateUserFields"
        },
        produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE,
        method = {RequestMethod.POST, RequestMethod.GET}
    )
    @ResponseBody
    public List<DocumentUserFieldDto> getUserFieldsByTemplate(@RequestParam(value = "template_id", required = true) Long templateId) {
        return DocumentUserFieldDto.toDtoList(documentService.getUserFieldsByTemplateId(templateId));
    }


    /**
     * Получить пользовательские поля документа
     * @param documentId
     * @return
     */
    // TODO Переделать на DTO и переписать клиент
    // TODO Скорее всего нужно удалить
    /*@RequestMapping(value = BASE_URL_USER_PAGE + "/getUserFields.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<DocumentUserField> getUserFields(@RequestParam(value = "document_id", required = true) Long documentId) {
        List<DocumentUserField> result = null;
        Long userId = SecurityUtils.getUser().getId();

        // Получить права на документ у текущего пользователя
        // Если нет прав ни на что, то - 403
        Map<DocumentParticipant, List<ParticipantRight>> participantRights = documentService.getRightToDocument(documentId, userId);
        boolean userHasViewRight = false;
        boolean userHasFillUserFieldsRight = false;
        boolean userHasSignDocumentRight = false;
        if (participantRights != null) {
            for (DocumentParticipant part : participantRights.keySet()) {
                List<ParticipantRight> participantRightList = participantRights.get(part);
                userHasViewRight = userHasViewRight || participantRightList.contains(ParticipantRight.VIEW);
                userHasFillUserFieldsRight = userHasFillUserFieldsRight || participantRightList.contains(ParticipantRight.FILL_USER_FIELDS);
                userHasSignDocumentRight = userHasSignDocumentRight || participantRightList.contains(ParticipantRight.SIGN);
            }
        }
        if (!userHasFillUserFieldsRight) {
            //throw new RuntimeException("Нет прав на заполненеие пользовательских полей");
        } else {
            // Определить есть ли незаполненные пользовательские поля
            Map<DocumentParticipant, List<DocumentUserField>> userFields = documentService.getDocumentUserFields(documentId, userId);
            List<DocumentUserField> allUserFields = new ArrayList<>();
            for (DocumentParticipant participant : userFields.keySet()) {
                allUserFields.addAll(userFields.get(participant));
            }
            // Сортируем пользовательские поля в группе полей
            // TODO Переделать
            for (DocumentUserField userField : allUserFields) {
                if (userField.getType().equals(DocumentUserFieldType.FIELDS_GROUPS.getType())) {
                    List<Map<String, Object>> fieldsGroups = (List<Map<String, Object>>) userField.getParameters().get("fieldsGroupsList");
                    for (Map<String, Object> fieldsGroup : fieldsGroups) {
                        if (fieldsGroup.get("userFields") != null) {
                            List<DocumentUserField> childUserFields = (List<DocumentUserField>) fieldsGroup.get("userFields");
                            Collections.sort(childUserFields, new UserFieldsComparator());
                        }
                    }
                }
            }
            result = allUserFields;
        }
        return result;
    }*/

    // TODO Перенести в отдельный контроллер
    @RequestMapping(value = BASE_URL_USER_PAGE + "/documentPage", method = RequestMethod.GET)
    public String documentPage(Model model, @RequestParam(value = "document_id", required = true) String documentId) {
        model.addAttribute("documentHashCode", documentId);
        return "documentPage";
    }

    /**
     * Данные для страницы документа
     * @param documentHashCode
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/document_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public DocumentPageDto getDocumentPageData(@RequestParam(value = "document_hash_code", required = true) String documentHashCode) {
        return documentService.getDocumentPageDto(documentHashCode, SecurityUtils.getUser().getId());

            // Получить права на документ у текущего пользователя
            // Если нет прав ни на что, то - 403
            /*Map<DocumentParticipantEntity, List<ParticipantRight>> participantRights = documentService.getRightToDocument(document);
            boolean userHasViewRight = false;
            boolean userHasFillUserFieldsRight = false;
            boolean userHasSignDocumentRight = false;
            if (participantRights != null) {
                for (DocumentParticipantEntity part : participantRights.keySet()) {
                    List<ParticipantRight> participantRightList = participantRights.get(part);
                    userHasViewRight = userHasViewRight || participantRightList.contains(ParticipantRight.VIEW);
                    userHasFillUserFieldsRight = userHasFillUserFieldsRight || participantRightList.contains(ParticipantRight.FILL_USER_FIELDS);
                    userHasSignDocumentRight = userHasSignDocumentRight || participantRightList.contains(ParticipantRight.SIGN);
                }
            }
            if (!userHasViewRight && !userHasFillUserFieldsRight && !userHasSignDocumentRight) {
                result = "error403";
            } else {
                // Определить есть ли незаполненные пользовательские поля
                // Если есть права на заполнение пользовательских полей и есть поля - добавить в модель список полей
                // Иначе, если есть права на подписание документа и нет пользовательских полей в документе - то добавить в модель список участников от кого подписывает документ пользователь
                // Иначе, если есть права на просмотр документа
                boolean setParticipantsName = false;
                boolean documentHasUserFields = documentService.isDocumentHasUserFields(document);
                List<DocumentParticipantEntity> participantsOfUser = documentService.getParticipantsOfUser(document);
                Map<DocumentParticipantEntity, List<DocumentUserField>> userFields = documentService.getDocumentUserFields(document);
                List<DocumentUserField> allUserFields = new ArrayList<>();
                for (DocumentParticipantEntity participant : userFields.keySet()) {
                    allUserFields.addAll(userFields.get(participant));
                }
                if (documentHasUserFields && userHasFillUserFieldsRight && allUserFields.size() > 0) {
                    Collections.sort(allUserFields, new UserFieldsComparator());
                    model.addAttribute("userFields", allUserFields);
                    // Сортируем пользовательские поля в группе полей
                    // TODO Переделать
                    *//*for (DocumentUserField userField : allUserFields) {
                        if (userField.getType().equals(DocumentUserFieldType.FIELDS_GROUPS.getType())) {
                            List<Map<String, Object>> fieldsGroups = (List<Map<String, Object>>)userField.getParameters().get("fieldsGroupsList");
                            for (Map<String, Object> fieldsGroup : fieldsGroups) {
                                if (fieldsGroup.get("userFields") != null) {
                                    List<DocumentUserField> childUserFields = (List<DocumentUserField>)fieldsGroup.get("userFields");
                                    Collections.sort(childUserFields, new UserFieldsComparator());
                                }
                            }
                        }
                    }*//*

                    userHasDocumentRight = true;
                } else if (!documentHasUserFields && userHasSignDocumentRight) {
                    List<DocumentParticipantEntity> signParticipants = new ArrayList<>();
                    for (DocumentParticipantEntity participant : participantsOfUser) {
                        if (participant.getParent() != null) {
                            setParticipantsName(Arrays.asList(participant.getParent()));
                        }
                        if (participant.getIsNeedSignDocument() == null || participant.getIsNeedSignDocument()) {
                            signParticipants.add(participant);
                        }
                    }
                    model.addAttribute("signParticipants", signParticipants);


                    boolean isSigned = false;
                    for (DocumentParticipantEntity participant : participantsOfUser) {
                        isSigned = isSigned || participant.getIsSigned();
                    }
                    model.addAttribute("isSigned", isSigned);
                    userHasDocumentRight = true;
                } else if (userHasViewRight) {
                    userHasDocumentRight = true;
                }
                model.addAttribute("userHasDocumentRight", userHasDocumentRight);
                if (userHasDocumentRight) {
                    // Установить наименования реальных участников
                    setParticipantsName(document.getParticipants());
                    // Установить создателя документа
                    Object creator = documentService.getDocumentCreator(document);

                    model.addAttribute("document", document);
                    model.addAttribute("creator", creator);

                    long communityId = -1l;
                    if (request.getAttribute("community_id") != null) {
                        try {
                            communityId = ((Long)request.getAttribute("community_id")).longValue();
                        } catch (Exception e) {
                            // do nothing
                        }
                    }
                    boolean isFindedParentParticipantByCommunityId = false;

                    // Если пользователь не является не дочерней стороной документа (не физ лицо и не регистратор), то ищем родительского участника пользователя
                    DocumentParticipantEntity parentParticipant = null;
                    DocumentParticipantEntity userParticipant = null;
                    for (DocumentParticipantEntity participant : participantRights.keySet()) {
                        List<ParticipantRight> participantRightList = participantRights.get(participant);
                        if ((participant.getChildren() == null || participant.getChildren().size() == 0) && participantRightList != null && participantRightList.size() > 0) { // Пользователь как непосредственный участник документа
                            userParticipant = participant;
                        } else if (participant.getChildren() != null && participant.getChildren().size() > 0 && participantRightList != null && participantRightList.size() > 0) { // Пользователь имеет права от имени сообщества
                            // Если у участника есть пользотельские поля, то выбираем его родительским участником
                            // Иначе ищем права на подпись или просмотр

                            for (DocumentParticipantEntity userFieldParticipant : userFields.keySet()) {
                                List<DocumentUserField> partcipantUserFields = userFields.get(userFieldParticipant);
                                if (userFieldParticipant.getSourceParticipantId().longValue() == participant.getSourceParticipantId().longValue() &&
                                        userFieldParticipant.getParticipantTypeName().equals(participant.getParticipantTypeName()) &&
                                        partcipantUserFields.size() > 0) {
                                    parentParticipant = participant;
                                    isFindedParentParticipantByCommunityId = isFindedParentParticipantByCommunityId || communityId == parentParticipant.getSourceParticipantId().longValue();
                                }
                            }
                            if (parentParticipant == null && (participantRightList.contains(ParticipantRight.VIEW) || participantRightList.contains(ParticipantRight.SIGN))) {
                                parentParticipant = participant;
                                isFindedParentParticipantByCommunityId = isFindedParentParticipantByCommunityId || communityId == parentParticipant.getSourceParticipantId().longValue();
                            }
                        }
                    }

                    // Если не найден непосредственный участник документа по текущему пользователю, то редиректим на страницу документа сообщества
                    boolean isNeedRedirect = false;
                    if (userParticipant == null && communityId == -1) {
                        communityId = parentParticipant.getSourceParticipantId();
                        isNeedRedirect = true;
                    } else if (userParticipant == null && communityId > -1 && !isFindedParentParticipantByCommunityId) { // Не правильный ИД сообщества
                        communityId = parentParticipant.getSourceParticipantId();
                        isNeedRedirect = true;
                    }
                    // Если нужно перекинуть на страницу сообщества
                    if (isNeedRedirect) {
                        model.asMap().clear();
                        result = "redirect:/group/" + communityId + "/document/" + documentId;
                    } else {
                        *//*if (communityId > -1 && parentParticipant == null) {
                            result = "error403";
                        } else *//*if (communityId > -1) { // Если страница сообщества


                        } else { // Если страница пользователя

                        }
                    }
                }
            }*/
    }

    /**
     * Получить дерево классов документов с количеством документов
     * @return
     */
    // TODO Переделать на DTO DocumentTreeDto
    @RequestMapping(value = BASE_URL_USER_PAGE + "/documentTree.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Map<String, Object> documentTree(
            @RequestParam(value = "communityId", required = false) Long communityId,
            @RequestParam(value = "node", required = false) String node,
            @RequestParam(value = "name", required = false) String searchName
    ) {
        Long parentId = VarUtils.getLong(node, -1l);
        Long participantId = communityId;

        List<String> participantTypes;
        if (participantId == null) {
            participantId = SecurityUtils.getUser().getId();
            participantTypes = Arrays.asList(
                    ParticipantsTypes.INDIVIDUAL.getName(),
                    ParticipantsTypes.INDIVIDUAL_LIST.getName(),
                    ParticipantsTypes.REGISTRATOR.getName()
            );
        } else {
            participantTypes = Arrays.asList(
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName()
            );
        }
        Map<Long, List<String>> participantFilter = new HashMap<>();
        participantFilter.put(participantId, participantTypes);
        List<Document> documents = documentDomainService.filter(null, null, null, null, participantFilter, null);

        Set<Long> classOfDocumentsIds = new HashSet<>();
        Map<Long, Integer> countDocumentsMap = new HashMap<>();
        for (Document document : documents) {
            if (document.getDocumentClassId() != null) {
                classOfDocumentsIds.add(document.getDocumentClassId());
                if (!countDocumentsMap.containsKey(document.getDocumentClassId())) {
                    countDocumentsMap.put(document.getDocumentClassId(), 1);
                } else {
                    countDocumentsMap.put(document.getDocumentClassId(), countDocumentsMap.get(document.getDocumentClassId()) + 1);
                }
            }
        }
        return documentTypeSerializer.serializeDocumentTypeFullTree(parentId, classOfDocumentsIds, searchName, countDocumentsMap);
    }

    /**
     * Страница с документами пользователя.
     * @param model
     * @param response
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/documentListPage", method = RequestMethod.GET)
    public String documentListPage(Model model) {
        return "documentListPage";
    }

    /**
     * Экспорт документа в PDF
     * @param documentId
     * @param response
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/exportDocumentToPdf", method = RequestMethod.GET)
    public void exportDocumentToPdf(
            @RequestParam(value = "document_id", required = true) long documentId,
            HttpServletResponse response) {
        Document document = documentDomainService.getById(documentId);
        Long userId = SecurityUtils.getUser().getId();
        if (document == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            Map<DocumentParticipant, List<ParticipantRight>> participantRights = documentService.getRightToDocument(documentId, userId);
            boolean isHasRightToDocument = false;
            for (DocumentParticipant participant : participantRights.keySet()) {
                List<ParticipantRight> participantRightsList = participantRights.get(participant);
                if (participantRightsList != null && (participantRightsList.contains(ParticipantRight.VIEW) || participantRightsList.contains(ParticipantRight.SIGN))) {
                    isHasRightToDocument = true;
                    break;
                }
            }
            if (isHasRightToDocument) {
                try {
                    String pdfFileName = Transliterator.transliterate(document.getName());
                    pdfFileName = pdfFileName.replaceAll(" ", "_");
                    pdfFileName = document.getId() + "_" + pdfFileName;

                    response.setContentType(CommonConstants.RESPONSE_PDF_MEDIA_TYPE);
                    response.setHeader("Content-Disposition", "inline; filename=" + pdfFileName + ".pdf");

                    pdfService.writeStreamPdfByHtml(document.getContent(), response.getOutputStream(), document.getPdfExportArguments());
                    response.setStatus(HttpStatus.CREATED.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        }
    }

    /**
     * Страница печати документа
     * @param documentId
     * @return
     */
    // TODO Создать метод для данных страницы печати документа
    @RequestMapping(value = BASE_URL_USER_PAGE + "/documentPrintPage", method = RequestMethod.GET)
    public String documentPrintPage(Model model, @RequestParam(value = "document_id", required = true) Long documentId) {
        String result = "error403";
        Document document = documentDomainService.getById(documentId);
        Long userId = SecurityUtils.getUser().getId();
        Map<DocumentParticipant, List<ParticipantRight>> participantRights = documentService.getRightToDocument(documentId, userId);
        boolean isHasRightToDocument = false;
        for (DocumentParticipant participant : participantRights.keySet()) {
            List<ParticipantRight> participantRightsList = participantRights.get(participant);
            if (participantRightsList != null && (participantRightsList.contains(ParticipantRight.VIEW) || participantRightsList.contains(ParticipantRight.SIGN))) {
                isHasRightToDocument = true;
                break;
            }
        }
        if (isHasRightToDocument) {
            model.addAttribute("document", document);
            result = "documentPrintPage";
        }
        return result;
    }

    /**
     * Фильтрация документов по параметрам
     * @param documentClassId
     * @param dateStart
     * @param dateEnd
     * @param name
     * @param participantType
     * @param participantId
     * @param content
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/filterDocuments.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<DocumentFilterDto> filterDocuments(
            @RequestParam(value = "documentClassId", required = false) Long documentClassId,
            @RequestParam(value = "dateStart", required = false) String dateStart,
            @RequestParam(value = "dateEnd", required = false) String dateEnd,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "participantType", required = false) String participantType,
            @RequestParam(value = "participantId", required = false) Long participantId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "communityId", required = false) Long communityId) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        Date createDateStart = null;
        Date createDateEnd = null;
        if (dateStart != null && !dateStart.equals("")) {
            try {
                Date startDate = dateFormatter.parse(dateStart);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                createDateStart = calendar.getTime();

                if (dateEnd != null && !dateEnd.equals("")) {
                    Date endDate = dateFormatter.parse(dateEnd);
                    calendar.setTime(endDate);
                    calendar.set(Calendar.HOUR, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    createDateEnd = calendar.getTime();
                } else {
                    calendar.set(Calendar.YEAR, 2050); // :D
                    calendar.set(Calendar.HOUR, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    createDateEnd = calendar.getTime();
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        // Фильтр по участникам документа
        Map<Long, List<String>> participantsFilters = new HashMap<>();

        List<String> participantTypes = null;
        Long currentParticipantId = -1l;

        // Если передана группа, то передаём её как участника
        if (communityId != null) {
            // TODO у текущего пользователя должны быть права на просмотр документов в группе
            participantTypes = Arrays.asList(
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName()
            );
            currentParticipantId = communityId;
        } else { // Добавляем себя как участника
            participantTypes = Arrays.asList(
                    ParticipantsTypes.INDIVIDUAL.getName(),
                    ParticipantsTypes.INDIVIDUAL_LIST.getName(),
                    ParticipantsTypes.REGISTRATOR.getName()
            );
            currentParticipantId = SecurityUtils.getUser().getId();
        }

        participantsFilters.put(currentParticipantId, participantTypes);

        if (participantId != null && participantType != null && participantId > 0) {
            participantsFilters.put(participantId, Arrays.asList(new String[]{participantType}));
        }

        List<Document> documents = documentDomainService.filter(documentClassId, createDateStart, createDateEnd, name, participantsFilters, content);
        //List<DocumentEntity> resultDocuments = new ArrayList<>();
        for (Document document : documents) {
            // Фильтруем по участнику
           /*boolean found = true;
            if (participantId != null && participantType != null && participantId > 0) {
                found = false;
                List<DocumentParticipantEntity> participants = document.getParticipants();
                for (DocumentParticipantEntity foundParticipant : participants) {
                    if (foundParticipant.getParticipantTypeName().equalsIgnoreCase(participantType) &&
                        foundParticipant.getSourceParticipantId().longValue() == participantId.longValue()) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                resultDocuments.add(document);
            }*/
        }
        return DocumentFilterDto.toDtoList(documents);
    }

    /**
     * Получить всех участников документов по классу документа
     * @param documentClassId
     * @return
     */
    // TODO используется в documentListPage.jsp
    @RequestMapping(value = BASE_URL_USER_PAGE + "/getParticipantsOfDocuments.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<DocumentParticipantFilterResponseDto> getParticipantsOfDocuments(
            @RequestParam(value = "documentClassId", required = false) Long documentClassId,
            @RequestParam(value = "communityId", required = false) Long communityId) {

        Map<String, Map<Long, DocumentParticipant>> participantsMap = new HashMap<>();

        // Фильтр по участникам документа
        Map<Long, List<String>> participantsFilters = new HashMap<>();
        List<String> participantTypes;
        Long currentParticipantId;

        // Если передана группа, то передаём её как участника
        if (communityId != null) {
            // TODO у текущего пользователя должны быть права на просмотр документов в группе
            participantTypes = Arrays.asList(
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName()
            );
            currentParticipantId = communityId;
            //filterParticipant.setSourceParticipantId(communityId);
            //filterParticipant.setParticipantTypeName(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName());
        } else { // Добавляем себя как участника
            participantTypes = Arrays.asList(
                    ParticipantsTypes.INDIVIDUAL.getName(),
                    ParticipantsTypes.INDIVIDUAL_LIST.getName(), ParticipantsTypes.REGISTRATOR.getName()
            );
            currentParticipantId = SecurityUtils.getUser().getId();
        }

        participantsFilters.put(currentParticipantId, participantTypes);

        List<Document> documents = documentDomainService.filter(documentClassId, null, null, null, participantsFilters, null);
        for (Document document : documents) {
            for (DocumentParticipant participant : document.getParticipants()) {
                if (!participantsMap.containsKey(participant.getParticipantTypeName())) {
                    participantsMap.put(participant.getParticipantTypeName(), new HashMap<>());
                }
                if (!participantsMap.get(participant.getParticipantTypeName()).containsKey(participant.getSourceParticipantId())) {
                    participantsMap.get(participant.getParticipantTypeName()).put(participant.getSourceParticipantId(), participant);
                }
            }
        }

        Long userId = SecurityUtils.getUser().getId();

        List<DocumentParticipant> resultParticipants = new ArrayList<>();
        for (String participantType : participantsMap.keySet()) {
            Map<Long, DocumentParticipant> flowOfDocumentParticipantMap = participantsMap.get(participantType);
            for (DocumentParticipant participant : flowOfDocumentParticipantMap.values()) {
                DocumentParticipant foundParticipant = new DocumentParticipant();
                foundParticipant.setSourceParticipantId(participant.getSourceParticipantId());
                foundParticipant.setParticipantTypeName(participant.getParticipantTypeName());

                if (communityId != null) {
                    // Если участник - текущая организация
                    if ((foundParticipant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName()) ||
                         foundParticipant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName())) &&
                            foundParticipant.getSourceParticipantId().equals(communityId)) {
                        // do nothig
                    } else {
                        resultParticipants.add(foundParticipant);
                    }
                } else {
                    // Если участник - текущий пользователь, то не добавлять его
                    if ((foundParticipant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.INDIVIDUAL.getName()) ||
                            foundParticipant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.INDIVIDUAL_LIST.getName()) ||
                            foundParticipant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.REGISTRATOR.getName())) &&
                            foundParticipant.getSourceParticipantId().equals(userId)) {
                        // do nothig
                    } else {
                        resultParticipants.add(foundParticipant);
                    }
                }
            }
        }
        List<DocumentParticipantFilterResponseDto> result = new ArrayList<>();
        for (DocumentParticipant participant : resultParticipants) {
            IDocumentParticipant sourceParticipant = documentParticipantService.getDocumentParticipantById(
                    ParticipantsTypes.valueOf(participant.getParticipantTypeName()), participant.getSourceParticipantId()
            );
            result.add(new DocumentParticipantFilterResponseDto(participant, sourceParticipant));
        }
        return result;
    }

    /**
     * Страница подписи документов.
     * @param model
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/signDocumentsListPage", method = RequestMethod.GET)
    public String signDocumentsListPage(Model model) {
        /*model.addAttribute("userHasRightsToPage", true);
        model.addAttribute("documents", resultDocuments);
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаДОМ", "/radom")
                .add("Список документов", BASE_URL_USER_PAGE + "/documentListPage")
                .add("На подпись", BASE_URL_USER_PAGE + "/signDocumentsListPage"));*/
        return "signDocumentsListPage";
    }

    /**
     * Список документов для подписания пользователю
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/document_sign_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<DocumentSignPageDto> getSignDocumentsListPageData() {
        // Получить все свои документы, у которых пользовательские поля заполнены, но они ещё не подписаны
        List<String> participantTypes = Arrays.asList(
                ParticipantsTypes.INDIVIDUAL.getName(),
                ParticipantsTypes.REGISTRATOR.getName(),
                ParticipantsTypes.INDIVIDUAL_LIST.getName()
        );
        List<Document> documents = documentDomainService.findNotSignedDocuments(participantTypes, SecurityUtils.getUser().getId());
        List<Document> resultDocuments = new ArrayList<>();

        for (Document document : documents) {
            // Ищем пользовательские поля
            if (!documentService.isDocumentHasUserFields(document)) {
                // Если пользовательских полей нет, то определяем есть ли пользовательские поля у других участников
                // Если полей у других участников нет, то добавляем документ на подписание

                boolean isFillUserFields = true;
                // Проверить, что все заполнили пользовательские поля документа
                List<DocumentParticipant> documentParticipants = document.getParticipants();
                for (DocumentParticipant documentParticipant : documentParticipants) {
                    // Поля участника заполнены не все
                    if (documentService.isDocumentHasUserFields(document, documentParticipant)) {
                        isFillUserFields = false;
                        //break;
                    }
                }

                // Если все поля заполнены всеми сторонами
                if (isFillUserFields) {
                    resultDocuments.add(document);
                }
            }
        }
        return DocumentSignPageDto.toDtoList(resultDocuments);
    }

    /**
     * Проверить подпись в документе у участника
     * @param participantId ИД участника
     * @return результат валидна, не валидна
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/checkSignature.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    private CommonResponseDto checkSignature(@RequestParam(value = "participantId", required = true) Long participantId) {
        documentService.checkParticipantSignature(participantId);
        return SuccessResponseDto.get();
    }

    /**
     * Стили полей в редакторе
     * @param model
     * @return
     */
    @RequestMapping(value = BASE_URL_USER_PAGE + "/fieldsStyles", method = RequestMethod.GET)
    public String getFieldsStyles(Model model) {
        String backgroundColor = settingsManager.getSystemSetting(FIELD_BACKGROUND_COLOR_SYSTEM_ATTR_NAME, "#FFFF00");
        String color = settingsManager.getSystemSetting(FIELD_COLOR_SYSTEM_ATTR_NAME, "red");
        model.addAttribute("backgroundColor", backgroundColor);
        model.addAttribute("color", color);
        return "fieldsStyles";
    }

    @RequestMapping(value = BASE_URL_USER_PAGE + "/getDocumentData.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public DocumentDataDto getDocumentData(@RequestParam(value = "document_id", required = true) Long documentId) {
        Document document = documentDomainService.getById(documentId);
        checkAccessToDocument(document);
        return DocumentDataDto.toDto(document, documentService.isSignedDocument(document));
    }

    private void checkAccessToDocument(Document document) {
        ExceptionUtils.check(document == null, "Документ не найден");
        ExceptionUtils.check(!checkAccessToDocument(document.getParticipants()), "Вы не являетесь участником документа");

    }

    private boolean checkAccessToDocument(List<DocumentParticipant> documentParticipants) {
        boolean result = false;
        if (documentParticipants != null && !documentParticipants.isEmpty()) {
            for (DocumentParticipant documentParticipant : documentParticipants) {
                if ((ParticipantsTypes.INDIVIDUAL.getName().equals(documentParticipant.getParticipantTypeName()) || ParticipantsTypes.INDIVIDUAL_LIST.getName().equals(documentParticipant.getParticipantTypeName())) &&
                        SecurityUtils.getUser().getId().equals(documentParticipant.getSourceParticipantId()) ) {
                    result = true;
                    break;
                }
                result = checkAccessToDocument(documentParticipant.getChildren());
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

}
