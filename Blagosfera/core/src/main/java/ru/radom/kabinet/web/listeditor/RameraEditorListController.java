package ru.radom.kabinet.web.listeditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorDAO;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.hibernate.HibernateProxyTypeAdapter;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.RameraListEditorAction;
import ru.radom.kabinet.module.rameralisteditor.RameraListEditorData;
import ru.radom.kabinet.module.rameralisteditor.RameraListEditorExceptionWrapper;
import ru.radom.kabinet.module.rameralisteditor.RameraListEditorService;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.listeditor.dto.RequestListEditorDto;
import ru.radom.kabinet.web.listeditor.dto.ResponseListEditorDto;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vgusev on 02.06.2015.
 */
@Controller
public class RameraEditorListController {

    @Autowired
    private RameraListEditorService rameraListEditorService;

    @Autowired
    private RameraListEditorDAO rameraListEditorDAO;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private ListEditorDomainService listEditorDomainService;

    /**
     * Основной метод обработки
     * @param action
     * @return
     */
    public RameraListEditorData handleAction(RameraListEditorAction action) {
        RameraListEditorData result = new RameraListEditorData();
        ListEditor listEditor = null;
        boolean isHasSuperAdminRole = SecurityUtils.getUserDetails().hasRole(Roles.ROLE_SUPERADMIN);
        result.setIsUserSuperAdmin(isHasSuperAdminRole);
        try {
            switch (action.getRameraListEditorActionType()) {
                case ADD_RAMERA_LIST_EDITOR: // Добавление виджета
                case UPDATE_RAMERA_LIST_EDITOR: // Обновление виджета
                    if (!isHasSuperAdminRole) {
                        throw new RuntimeException ("У Вас нет прав на создание и редактирование компонента!");
                    }
                    listEditor = action.getRameraListEditor().toDomain();
                    /*rameraListEditor = action.getRameraListEditor();*/
                    listEditorDomainService.save(listEditor);
                    // Привязываем объекты элементы 1го уровня с объектов - контейнером списка
                    /*rameraListEditor.attachItems();
                    recursiveAddListEditorItems(rameraListEditor.getItems(), null);*/
                    break;
                case DELETE_RAMERA_LIST_EDITOR: // Удаление виджета
                    if (!isHasSuperAdminRole) {
                        throw new RuntimeException ("У Вас нет прав на удаление компонента!");
                    }
                    //rameraListEditor = rameraListEditorDAO.getById(action.getId());
                    //recursiveDeleteListEditorItems(rameraListEditor.getItems());
                    rameraListEditorDAO.delete(action.getId());
                    break;
                /*case GET_RAMERA_LIST_EDITOR_BY_NAME: // Получение виджета по имени (имя уникально)
                    rameraListEditor = rameraListEditorDAO.getByName(action.getListEditorName());
                    result.setResult(rameraListEditor);
                    deleteLoopLinksInComponent(rameraListEditor);
                    break;*/
                case GET_RAMERA_LIST_EDITOR_WITH_CHILDS_BY_NAME: // Получение данных виджета с дочерними элементами
                    listEditor = listEditorDomainService.getByName(action.getListEditorName());
                    /*rameraListEditor = rameraListEditorDAO.getByName(action.getListEditorName());
                    if (rameraListEditor == null) {
                        rameraListEditor = new RameraListEditor();
                    }*/
                    //recursiveLazyLoadItems(rameraListEditor.getItems());
                    result.setResult(ResponseListEditorDto.toDtoSafe(listEditor));
                    //deleteLoopLinksInComponent(rameraListEditor);
                    break;
                /*case GET_ALL_RAMERA_LIST_EDITORS: // Получение данных всех виджетов
                    List<RameraListEditor> listVC = rameraListEditorDAO.findAll();
                    result.setResult(listVC);
                    deleteLoopLinksInComponents(listVC);
                    break;*/

                case ADD_RAMERA_LIST_EDITOR_ITEM: // Добавить элемент виджета
                case UPDATE_RAMERA_LIST_EDITOR_ITEM: // Обновить элемент виджета
                    if (!isHasSuperAdminRole) {
                        throw new RuntimeException ("У Вас нет прав на создание и редактирование элемента компонента!");
                    }
                    rameraListEditorItemDAO.saveOrUpdate(action.getRameraListEditorItem());
                    break;
                case DELETE_RAMERA_LIST_EDITOR_ITEM: // Удалить элемент виджета
                    if (!isHasSuperAdminRole) {
                        throw new RuntimeException ("У Вас нет прав на удаление элемента компонента!");
                    }
                    try {
                        rameraListEditorItemDAO.delete(action.getId());
                    } catch (Exception exception) {
                        if (exception.getMessage().contains("ConstraintViolationException")) {
                            // Есть ссылки на данный элемент
                            throw new RuntimeException("Элемент нельзя удалить, потому как есть внешние ссылки на него.");
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Оборачиваем исключение для того, чтобы на клиенте обработать его
            result.setResult(new RameraListEditorExceptionWrapper(e.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return result;
    }

    /**
     * Разрыв циклических ссылок.
     * @param rameraListEditorList
     */
    private void deleteLoopLinksInComponents(List<RameraListEditor> rameraListEditorList) {
        for (RameraListEditor rameraListEditor : rameraListEditorList) {
            deleteLoopLinksInComponent(rameraListEditor);
        }
    }

    /**
     * Разрыв циклических ссылок.
     * @param rameraListEditor
     */
    private void deleteLoopLinksInComponent(RameraListEditor rameraListEditor) {
        for (RameraListEditorItem item : rameraListEditor.getItems()) {
            item.setListEditor(null);
            if (item.getChildren() != null) {
                deleteLoopLinksInComponentItems(item.getChildren());
            }
        }
    }

    /**
     * Разрыв циклических ссылок.
     * @param items
     */
    private void deleteLoopLinksInComponentItems(List<RameraListEditorItem> items) {
        for (RameraListEditorItem item : items) {

            item.setParent(null);
            if (item.getChildren() != null && item.getChildren().size() > 0) {
                deleteLoopLinksInComponentItems(item.getChildren());
            }
        }
    }

    /**
     * Рекурсивное добавление вложенных элементов.
     * @param items
     * @param parentItem
     */
    private void recursiveAddListEditorItems(List<RameraListEditorItem> items, RameraListEditorItem parentItem) {
        for (RameraListEditorItem item : items) {
            if (parentItem != null) {
                item.setParent(parentItem);
            }
            rameraListEditorItemDAO.saveOrUpdate(item);
            if (item.getChildren() != null && item.getChildren().size() > 0) {
                recursiveAddListEditorItems(item.getChildren(), item);
            }
        }
    }

    /**
     * Ленивая загрузка дочерних элементов.
     * @param items
     */
    private static void recursiveLazyLoadItems(List<RameraListEditorItem> items) {
        for (RameraListEditorItem item : items) {
            if (item.getChildren() != null && item.getChildren().size() > 0) {
                recursiveLazyLoadItems(item.getChildren());
            }
        }
    }


    @RequestMapping(value = "/ramera_list_editor/test_page", method = RequestMethod.GET)
    public String showTestComponentPage(Model model) {
        return "rameraListEditorTest";
    }

    @RequestMapping(value="/ramera_list_editor/handle.json", method= RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public RameraListEditorData handleRequest(@RequestBody RameraListEditorAction rameraListEditorAction) {
        return handleAction(rameraListEditorAction);
    }

    /**
     * Загрузка данных из scv файла
     * @param request
     * @param listId
     * @return
     */
    @PreAuthorize("hasRole(SUPERADMIN)")
    @RequestMapping(value="/ramera_list_editor/uploadCsv", method=RequestMethod.POST)
    public @ResponseBody String handleCsvUpload(HttpServletRequest request, @RequestParam("listId") long listId){
        Map<String, String> resultMap = new HashMap<>();
        try {
            List<FileItem> items = WebUtils.parseMultipartRequest(request);
            FileItem fileItem = null;
            for (FileItem item : items) {
                if (item.getFieldName().equals("file")) {
                    fileItem = item;
                }
            }
            if (fileItem == null) {
                throw new RuntimeException("Вы не выбрали файл!");
            }
            rameraListEditorService.saveItemsFromCSV(listId, fileItem.getInputStream());
            resultMap.put("result", "true");
        } catch (Exception e) {
            resultMap.put("result", "false");
            resultMap.put("trace", ExceptionUtils.getStackTrace(e));
            resultMap.put("error", e.getMessage());
        }
        Gson gson = new Gson();
        return gson.toJson(resultMap);
    }
}
