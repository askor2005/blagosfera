package ru.radom.kabinet.web.jcr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.jcr.JcrFilesService;

/**
 * Контроллер для загрузки файлов в поля пользователя и группы
 * Created by vgusev on 27.08.2015.
 */
@Controller
public class JcrFieldFileController {

    private final static Logger logger = LoggerFactory.getLogger(JcrFieldFileController.class);

    @Autowired
    private JcrFilesService jcrFilesService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private RequestContext radomRequestContext;

    // TODO Переделать
    /*@RequestMapping(value = "/sharerfiles/fields/upload.json", method = RequestMethod.POST)
    public @ResponseBody String uploadSharerFieldFiles(HttpServletRequest request, @RequestParam("fieldId") FieldEntity field) {
        String result;
        try {
            Sharer sharer = radomRequestContext.getCurrentSharer();
            List<FileItem> items = WebUtils.parseMultipartRequest(request);
            if (items == null || items.size() == 0) {
                throw new Exception("Необходимо выбрать файл");
            }
            for (FileItem fileItem : items) {
                jcrFilesService.saveFieldFile(sharer, field, fileItem, sharer);
            }
            result = JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = JsonUtils.getErrorJson(e.getMessage()).toString();
        }
        return result;
    }*/

    /**
     * Удалить файл
     * @param field
     * @param nodeId
     * @return
     */
    // TODO Переделать
    /*@RequestMapping(value = "/sharerfiles/fields/remove.json", method = RequestMethod.POST)
    public @ResponseBody String removeSharerFieldFile(@RequestParam("fieldId") FieldEntity field, @RequestParam("nodeId") String nodeId)  {
        String result;
        try {
            Sharer sharer = radomRequestContext.getCurrentSharer();
            // Все проверки доступа происходят внутри
            jcrFilesService.removeFieldFileById(sharer, field, nodeId);
            result = JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = JsonUtils.getErrorJson(e.getMessage()).toString();
        }
        return result;
    }*/

    // TODO Переделать
    /*
    @RequestMapping(value = "/groupfiles/fields/upload.json", method = RequestMethod.POST)
    public @ResponseBody String uploadCommunityFieldFiles(
            HttpServletRequest request,
            @RequestParam("communityId") Long communityId,
            @RequestParam("fieldId") FieldEntity field) {
        String result;
        try {
            User sharer = radomRequestContext.getCurrentUser();
            // Проверить, что текущий пользователь имеет права на загрузку файла в поле объединения
            Community community = communityDomainService.getByIdMinData(communityId);
            communitiesService.checkPermission(community, sharer, "SETTINGS_COMMON", "У Вас нет прав на редактирование объединения");

            List<FileItem> items = WebUtils.parseMultipartRequest(request);
            if (items == null || items.size() == 0) {
                throw new Exception("Необходимо выбрать файл");
            }
            for (FileItem fileItem : items) {
                jcrFilesService.saveFieldFile(community, field, fileItem, sharer);
            }
            result = JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = JsonUtils.getErrorJson(e.getMessage()).toString();
        }
        return result;
    }*/

}
