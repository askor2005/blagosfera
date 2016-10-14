package ru.radom.kabinet.web.communities;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.document.web.DocumentController;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.security.communities.CommunityPermissionRequired;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.communities.dto.CommunityDocumentSearchDto;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by vgusev on 20.07.2015.
 */
@Controller("communitiesDocumentController")
public class CommunitiesDocumentController {

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private DocumentDomainService documentDomainService;

    // Префикс общих полей объединений
    private static final String COMMUNITY_COMMON_FIELDS_PREFIX = "COMMUNITY_COMMON";

    // Префикс полей организаций
    private static final String COMMUNITY_WITH_ORGANIZATION_FIELDS_PREFIX = "COMMUNITY_WITH_ORGANIZATION_";

    // Префикс дополнительных полей юр лица, которые появляются в зависимости от выбранной формы юр лица
    private static final String COMMUNITY_ADDITIONAL_GROUP_FIELDS_PREFIX = "COMMUNITY_ADDITIONAL_GROUP_";

    // Роль в объединении - просмотр документов
    private static final String ROLE_DOCUMENT_VIEW = "ROLE_DOCUMENT_VIEW";

    @Autowired
    private DocumentController documentController;

    @CommunityPermissionRequired(ROLE_DOCUMENT_VIEW)
    @RequestMapping(value = "/group/{seolink}/document/{documentId}", method = RequestMethod.GET)
    public String communityDocumentPage(Model model, @PathVariable("seolink") String seoLink, @PathVariable("documentId") String documentId) {
        model.addAttribute("documentHashCode", documentId);
        return "communityDocumentPage";
    }

    @CommunityPermissionRequired(ROLE_DOCUMENT_VIEW)
    @RequestMapping(value = "/group/{seolink}/documents", method = RequestMethod.GET)
    public String communityDocumentListPage(Model model, @PathVariable("seolink") String seoLink) {
        return "communityDocumentListPage";
    }

    @RequestMapping(value = "/group/{seolink}/createdocument", method = RequestMethod.GET)
    public String communityCreateDocumentPage(Model model, HttpServletResponse response, @PathVariable("seolink") String seoLink) {
        return "communityCreateDocumentPage";
    }

    @RequestMapping(value = "/group/{seolink}/signdocuments", method = RequestMethod.GET)
    public String communitySignDocumentListPage(Model model, HttpServletResponse response, @PathVariable("seolink") String seoLink) {
        // TODO Переделать
        /*CommunityEntity community = radomRequestContext.getCommunity();

        // Права на подпись есть у всех
        boolean userHasRightsToPage = true;

        // Права на подпись документов сообщества есть
        List<DocumentEntity> documents = null;
        if (userHasRightsToPage) {
            List<String> participantTypes = Arrays.asList(
                    ParticipantsTypes.INDIVIDUAL.getName(),
                    ParticipantsTypes.REGISTRATOR.getName(),
                    ParticipantsTypes.INDIVIDUAL_LIST.getName()
            );
            List<String> parentParticipantTypes = Arrays.asList(
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName()
            );

            documents = documentDao.findNotSignedChildParticipants(participantTypes, SecurityUtils.getUser().getId(), parentParticipantTypes, community.getId());
            List<DocumentEntity> findDocuments = new ArrayList<>();
            for (DocumentEntity document : documents) {
                if (!documentService.isDocumentHasUserFields(document)) {
                    findDocuments.add(document);
                }
            }
            documents = findDocuments;
        }

        Section section = sectionDao.getByName("ramera");
        model.addAttribute("currentSection", section);

        model.addAttribute("userHasRightsToPage", userHasRightsToPage);

        model.addAttribute("documents", documents);
        model.addAttribute("communityId", community.getId());
        model.addAttribute("breadcrumb", new Breadcrumb()
                .add(section.getTitle(), section.getLink())
                .add("Мои объединения", "/groups")
                .add(community.getName(), "/group/" + seoLink)
                .add("Список документов на подпись", "/group/" + seoLink + "/signdocuments"));

        model.addAttribute("communityId", community.getId());*/
        return "communitySignDocumentListPage";
    }

    /**
     * Найти документ в объединении
     * @param community
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/communities/{id}/documents.json", method = RequestMethod.POST)
    public @ResponseBody List<CommunityDocumentSearchDto> filterDocuments(@PathVariable("id") CommunityEntity community, @RequestBody String requestBody) {
        String name = WebUtils.getValueOfParameter(requestBody, "query");
        Map<Long, List<String>> participantsFilters = new HashMap<>();
        participantsFilters.put(community.getId(), Arrays.asList(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName()));

        List<Document> documents = documentDomainService.filter(null, null, null, name, participantsFilters, null);
        return CommunityDocumentSearchDto.toListDto(documents);
    }

    //List<DocumentEntity> documents = flowOfDocumentDao.filter(documentClassId, createDateStart, createDateEnd, name, participantsFilters, content);
    /*
    private List<Sharer> getManagersSharersFromCommunity(CommunityEntity community) {
        List<Sharer> result = new ArrayList<>();
        List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByInternalNamePrefix(COMMUNITY_COMMON_FIELDS_PREFIX, null);
        fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix(COMMUNITY_WITH_ORGANIZATION_FIELDS_PREFIX, null));
        fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix(COMMUNITY_ADDITIONAL_GROUP_FIELDS_PREFIX, null));
        List<FieldEntity> fields = fieldDao.getByGroups(fieldsGroups);
        List<String> managersFieldNames = new ArrayList<>();
        for (FieldEntity field : fields) {
            if (field.getType() == FieldType.SHARER) {
                managersFieldNames.add(field.getInternalName() + "_ID");
            }
        }
        for (String fieldName : managersFieldNames) {
            FieldEntity foundField = null;
            for (FieldEntity field : fields) {
                if (field.getInternalName().equalsIgnoreCase(fieldName)) {
                    foundField = field;
                    break;
                }
            }
            String stringFieldValue = null;
            if (foundField != null) {
                FieldValueEntity fieldValue = fieldValueDao.get(community, foundField);
                if (fieldValue != null) {
                    stringFieldValue = FieldsService.getFieldStringValue(fieldValue);
                }
            }
            Sharer sharer = null;
            if (stringFieldValue != null && !stringFieldValue.equals("")) {
                try {
                    sharer = sharerDao.getById(Long.valueOf(stringFieldValue));
                } catch (Exception e) {
                    // do nothing
                }

            }
            if (sharer != null) {
                result.add(sharer);
            }
        }
        return result;
    }*/

    // Страница с настройками оформления документов объединения
}
