package ru.radom.kabinet.web.lettersofauthority;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.letterofauthority.LetterOfAuthorityRole;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.LetterOfAuthorityAttributesGridDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityAttributeEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityRoleEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityService;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityServiceImpl;
import ru.radom.kabinet.services.letterOfAuthority.PossibleDelegates;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.web.lettersofauthority.dto.*;
import ru.radom.kabinet.web.lettersofauthority.dto.request.CreateLetterOfAuthorityRequest;
import ru.radom.kabinet.web.lettersofauthority.dto.request.CreateLetterOfAuthorityRoleRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class LetterOfAuthorityController {

    @Autowired
    private LetterOfAuthorityService letterOfAuthorityService;

    private static final String BASE_URL_ADMIN_PAGE = "/admin/letterofauthority";

    /**
     * Страница администрирования доверенностей
     * @param model модель
     * @return представление модели
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/adminPage.html", method = RequestMethod.GET)
    public String getAdminPage(Model model) {
        model.addAttribute("currentPageTitle", "Роли доверенностей");
        //
        model.addAttribute("letterOfAuthorityRoles", letterOfAuthorityService.getAllLetterOfAuthorityRoles());
        // Типы объектов
        model.addAttribute("scopeTypes", LetterOfAuthorityService.SCOPE_TYPES);
        // Типы ролей объектов
        model.addAttribute("scopeRoleTypes", LetterOfAuthorityService.SCOPE_ROLE_TYPES);
        // Права доступа в организации
        model.addAttribute("communityPermissions", letterOfAuthorityService.getAllCommunityPermissions());
        //
        model.addAttribute("sharerCommunityFields", letterOfAuthorityService.getSharerCommunityFields());
        //
        model.addAttribute("sharerListCommunityFields", letterOfAuthorityService.getSharerListCommunityFields());

        return "letterAuthorityAdminPage";
    }

    /**
     * Получить роли доверенностей для админ панели
     * @param nameSearchString строка поиска
     * @param page номер страницы
     * @return json
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/getLetterOfAuthorityRolesByFilter.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public LetterOfAuthorityRoleGridDto getLetterOfAuthorityRolesByFilter(@RequestParam(value = "name", required = true) String nameSearchString,
                                                    @RequestParam(value = "page", defaultValue = "1") int page) {
        LetterOfAuthorityRoleGridDto result;
        try {
            List<LetterOfAuthorityRoleEntity> letterOfAuthorityRoleEntities = letterOfAuthorityService.getLetterOfAuthorityRolesByFilter(nameSearchString, page - 1);
            int count = letterOfAuthorityService.getCountLetterOfAuthorityRolesByFilter(nameSearchString);

            List<LetterOfAuthorityRoleDto> letterOfAuthorityRoleDtos = new ArrayList<>();
            for (LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity : letterOfAuthorityRoleEntities) {
                letterOfAuthorityRoleDtos.add(new LetterOfAuthorityRoleDto(letterOfAuthorityRoleEntity.toDomain()));
            }
            result = LetterOfAuthorityRoleGridDto.toSuccessDto(count, letterOfAuthorityRoleDtos);
        } catch (Exception e) {
            e.printStackTrace();
            result = LetterOfAuthorityRoleGridDto.toErrorDto();
        }

        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("total", count);
        jsonObject.put("items", letterOfAuthorityRoleDtos);*/

        //return jsonObject.toString();
        return result;
    }


    /**
     * Создать роль доверенности
     * @param createLetterOfAuthorityRoleRequest запрос с параметрами создания роли
     * @return json
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/createLetterOfAuthorityRole.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto createLetterOfAuthorityRole(@RequestBody CreateLetterOfAuthorityRoleRequest createLetterOfAuthorityRoleRequest) {
        letterOfAuthorityService.createLetterOfAuthorityRole(createLetterOfAuthorityRoleRequest.letterOfAuthorityRole, createLetterOfAuthorityRoleRequest.listEditorItemId);
        return SuccessResponseDto.get();
    }

    /**
     * Обновить роль доверенности
     * @param createLetterOfAuthorityRoleRequest запрос с параметрами обносления роли
     * @return json
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/updateLetterOfAuthorityRole.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto updateLetterOfAuthorityRole(@RequestBody CreateLetterOfAuthorityRoleRequest createLetterOfAuthorityRoleRequest) {
        letterOfAuthorityService.updateLetterOfAuthorityRole(createLetterOfAuthorityRoleRequest.letterOfAuthorityRole, createLetterOfAuthorityRoleRequest.listEditorItemId);
        return SuccessResponseDto.get();
    }

    /**
     * Удалить роль доверенности
     * @param id ИД роли
     * @return json
     */
    @RequestMapping(value = BASE_URL_ADMIN_PAGE + "/deleteLetterOfAuthorityRole.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto deleteLetterOfAuthorityRole(@RequestParam(value = "id", required = true) Long id) {
        letterOfAuthorityService.deleteLetterOfAuthorityRole(id);
        return SuccessResponseDto.get();
    }

    /**
     * Получить доверенность
     * @param id ИД доверенности
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getLetterOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public LetterOfAuthorityDto getLetterOfAuthority(@RequestParam(value = "id", required = true) Long id) {
        LetterOfAuthorityEntity letterOfAuthorityEntity = letterOfAuthorityService.getLetterOfAuthority(id);
        if (letterOfAuthorityEntity == null) {
            throw new RuntimeException("Доверенность не найдена.");
        }

        if (!letterOfAuthorityEntity.getOwner().getId().equals(SecurityUtils.getUser().getId())
                && !letterOfAuthorityEntity.getDelegate().getId().equals(SecurityUtils.getUser().getId())) {
            throw new RuntimeException("У Вас нет прав на данную доверенность.");
        }
        return letterOfAuthorityEntity.toDto();
    }

    /**
     * Список доверенностей, которые выдал текущий пользователь
     * @param name строка поиска
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getOwnerLettersOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<LetterOfAuthorityDto> getOwnerLettersOfAuthority(@RequestParam(value = "name", required = true) String name,
                                                                 @RequestParam(value = "page", defaultValue = "1") int page) {
        return LetterOfAuthorityEntity.toDto(letterOfAuthorityService.getOwnerLettersOfAuthority(name, page - 1));
    }

    /**
     * Список доверенностей, которые выдали текущему пользователю
     * @param name строка поиска
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getMyLettersOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<LetterOfAuthorityDto> getMyLettersOfAuthority(@RequestParam(value = "name", required = true) String name,
                                                              @RequestParam(value = "page", defaultValue = "1") int page) {
        return LetterOfAuthorityEntity.toDto(letterOfAuthorityService.getMyLettersOfAuthority(name, page - 1));
    }

    /**
     * Получить список ролей доверенностей по типу объекта в рамках которого создаётся доверенность
     * @param scopeType тип объекта в рамках которого создаётся доверенность
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getLetterOfAuthorityRoles.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<LetterOfAuthorityRoleDto> getLetterOfAuthorityRoles(@RequestParam(value = "scope_type", required = true) String scopeType) {
        List<LetterOfAuthorityRoleDto> result = new ArrayList<>();

        for (LetterOfAuthorityRole role : letterOfAuthorityService.getLetterOfAuthorityRoles(scopeType)) {
            result.add(new LetterOfAuthorityRoleDto(role));
        }

        return result;
    }

    /**
     * Получить массив объектов в рамках которых создаётся доверенность
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getPossibleScopes.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<RadomAccountDto> getPossibleScopes(@RequestParam(value = "role_key", required = true) String roleKey) {
        return LetterOfAuthorityServiceImpl.toDto(letterOfAuthorityService.getRadomAccountsByRole(roleKey));
    }

    /**
     * Получить список возможных делегатов для доверенности
     * @param roleKey код роли
     * @param radomAccountId ИД объекта в рамках которого создаётся доверенность
     * @param searchString строка поиска
     * @param page номер страницы
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/getPossibleDelegates.json", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public DelegatesResponseDto getPossibleDelegates(@RequestParam(value = "role_key") String roleKey,
                                                     @RequestParam(value = "radom_account_id") Long radomAccountId,
                                                     @RequestParam(value = "name") String searchString,
                                                     @RequestParam(value = "page", defaultValue = "1") int page) {
        DelegatesResponseDto result = new DelegatesResponseDto();
        PossibleDelegates possibleDelegates = letterOfAuthorityService.getPossibleDelegates(roleKey, radomAccountId, page - 1, searchString);

        if (possibleDelegates != null) {
            result.total = possibleDelegates.getCount();

            for (UserEntity delegate : possibleDelegates.getDelegates()) {
                result.items.add(new DelegateDto(delegate));
            }
        }

        return result;
    }

    /**
     * Создать доверенность
     * @param request
     * @return
     */
    @RequestMapping(value = "/letterofauthority/createLetterOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto createLetterOfAuthority(@RequestBody CreateLetterOfAuthorityRequest request) {
        Date date = DateUtils.parseDate(request.expiredDate, new Date());
        letterOfAuthorityService.createLetterOfAuthority(request.roleKey, date, request.radomAccountId, request.delegateId, request.attributes);
        return SuccessResponseDto.get();
    }


    /**
     * Установить активность доверенности
     * @param letterOfAuthorityDto обёртка с параметрами доверенности
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/updateLetterOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto updateLetterOfAuthority(@RequestBody LetterOfAuthorityDto letterOfAuthorityDto) {
        letterOfAuthorityService.updateLetterOfAuthority(letterOfAuthorityDto);
        return SuccessResponseDto.get();
    }

    /**
     * Удалить доверенность
     * @param id ИД доверенности
     * @return json
     */
    @RequestMapping(value = "/letterofauthority/deleteLetterOfAuthority.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto deleteLetterOfAuthority(@RequestParam(value = "id", required = true) Long id) {
        letterOfAuthorityService.deleteLetterOfAuthority(id);
        return SuccessResponseDto.get();
    }

    /**
     * Страница с доверенностями выданных мне
     * @param model модель
     * @return представление модели
     */
    @RequestMapping(value = "/letterofauthority/myLetterOfAuthority.html", method = RequestMethod.GET)
    public String getMyLetterOfAuthorityPage(Model model) {
        model.addAttribute("currentPageTitle", "Доверенности выданные мне");
        // Типы объектов
        model.addAttribute("scopeTypes", LetterOfAuthorityService.SCOPE_TYPES);
        return "myLetterOfAuthority";
    }

    /**
     * Страница с доверенностями выданных мной
     * @param model
     * @return
     */
    @RequestMapping(value = "/letterofauthority/ownerLetterOfAuthority.html", method = RequestMethod.GET)
    public String getOwnerLetterOfAuthorityPage(Model model) {
        model.addAttribute("currentPageTitle", "Доверенности выданные мной");
        // Типы объектов
        model.addAttribute("scopeTypes", LetterOfAuthorityService.SCOPE_TYPES);
        return "ownerLetterOfAuthority";
    }

    /**
     * Данные для таблицы с аттрибутами
     * @param letterOfAuthorityId
     * @param name
     * @param page
     * @return
     */
    @RequestMapping(value = "/letterofauthority/getLetterOfAuthorityAttributes.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public LetterOfAuthorityAttributesGridDto getLetterOfAuthorityAttributes(@RequestParam(value = "letterOfAuthorityId", required = true) Long letterOfAuthorityId,
                                                                             @RequestParam(value = "name", required = true) String name,
                                                                             @RequestParam(value = "page", defaultValue = "1") int page) {
        LetterOfAuthorityAttributesGridDto result;
        try {
            List<LetterOfAuthorityAttributeEntity> attributeEntities = letterOfAuthorityService.getAttributes(letterOfAuthorityId, name, page - 1);
            int count = letterOfAuthorityService.getAttributesCount(letterOfAuthorityId, name);
            result = LetterOfAuthorityAttributesGridDto.successDtoFromDomain(count, attributeEntities);
        } catch (Exception e) {
            e.printStackTrace();
            result = LetterOfAuthorityAttributesGridDto.failDto();
        }
        return result;
    }

    /**
     * Сохранить атрибут
     * @param letterOfAuthorityId
     * @param name
     * @param value
     * @return
     */
    @RequestMapping(value = "/letterofauthority/saveLetterOfAuthorityAttribute.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto saveLetterOfAuthorityAttribute(@RequestParam(value = "letterOfAuthorityId", required = true) Long letterOfAuthorityId,
                                                 @RequestParam(value = "name", required = true) String name,
                                                 @RequestParam(value = "value", required = true) String value) {
        letterOfAuthorityService.saveAttribute(letterOfAuthorityId, name, value);
        return SuccessResponseDto.get();
    }

    /**
     * Обновить атрибут
     * @param id
     * @param name
     * @param value
     * @return
     */
    @RequestMapping(value = "/letterofauthority/updateLetterOfAuthorityAttribute.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto updateLetterOfAuthorityAttribute(@RequestParam(value = "id", required = true) Long id,
                                                   @RequestParam(value = "name", required = true) String name,
                                                   @RequestParam(value = "value", required = true) String value) {
        letterOfAuthorityService.updateAttribute(id, name, value);
        return SuccessResponseDto.get();
    }

    /**
     * Удалить атрибут
     * @param id
     * @return
     */
    @RequestMapping(value = "/letterofauthority/deleteLetterOfAuthorityAttribute.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto deleteLetterOfAuthorityAttribute(@RequestParam(value = "id", required = true) Long id) {
        letterOfAuthorityService.deleteAttribute(id);
        return SuccessResponseDto.get();
    }
}