package ru.radom.kabinet.web.communities.organization.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.ErrorResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.communities.organization.po.RegisterPOException;
import ru.radom.kabinet.services.communities.organization.po.RegisterPOService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.communities.organization.po.dto.CreatePOPageDataDto;
import ru.radom.kabinet.web.communities.organization.po.dto.RegisterPODto;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для запуска процесса, который создаёт документы для регистрации ПО
 * Created by vgusev on 11.12.2015.
 */
@Controller
public class RegisterPOController {

    @Autowired
    private RegisterPOService createPOService;

    private ErrorResponseDto getErrorJson(RegisterPOException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorField", e.getErrorField());
        return new ErrorResponseDto(e.getMessage(), errorMap);
    }

    /**
     * Создать запрос на запуск процесса ренистрации ПО
     * @param registerPODto параметры формы
     * @return json
     */
    @RequestMapping(value = "/organization/register_po.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto startRegisterOrganization(@RequestBody RegisterPODto registerPODto) {
        CommonResponseDto result;
        try {
            ExceptionUtils.check(SecurityUtils.getUserDetails() == null, "Не установлен текущий пользователь");
            registerPODto.setOwnerId(SecurityUtils.getUser().getId());
            createPOService.registerPO(registerPODto);
            result = SuccessResponseDto.get();
        } catch (RegisterPOException e) {
            result = getErrorJson(e);
        }
        return result;
    }

    /**
     * Сгенерировать устав ПО на основе параметров формы
     * @param registerPODto параметры формы
     * @return обёртка документа
     */
    @RequestMapping(value = "/organization/generate_po_regulations.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Object generateOrganizationRegulations(@RequestBody RegisterPODto registerPODto) {
        Object result;
        try {
            result = createPOService.generateOrganizationRegulations(registerPODto);
        } catch (RegisterPOException e) {
            result = getErrorJson(e);
        }
        return result;
    }

    @RequestMapping(value = "/organization/get_create_po_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CreatePOPageDataDto getCreatePOPageDataDto() {
        return createPOService.getCreatePOPageData();
    }
}
