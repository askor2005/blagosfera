package ru.radom.kabinet.web.communities.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.communities.organization.RegisterOrganizationService;
import ru.radom.kabinet.services.communities.organization.dto.CreateOrganizationTempDataDto;
import ru.radom.kabinet.utils.CommonConstants;

import java.util.Map;

/**
 * Контроллер для формы создания организации.
 * Created by vgusev on 01.02.2016.
 */
@Controller
public class RegisterOrganizationController {

    @Autowired
    private RegisterOrganizationService registerOrganizationService;

    /**
     * Получить данные несохранённой формы
     * @return json
     */
    @RequestMapping(value = "/organization/get_temp_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Map<String, Object> getOrganizationTempData() {
        return registerOrganizationService.getOrganizationTempData(SecurityUtils.getUser().getId());
    }

    /**
     * Сохранить данные формы
     * @param createOrganizationTempDataDto данные формы
     * @return json
     */
    @RequestMapping(value = "/organization/save_temp_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto saveOrganizationTempData(@RequestBody CreateOrganizationTempDataDto createOrganizationTempDataDto) {
        registerOrganizationService.saveOrganizationTempData(createOrganizationTempDataDto, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * Очистить данные формы
     * @return json
     */
    @RequestMapping(value = "/organization/clear_temp_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto clearOrganizationTempData() {
        registerOrganizationService.clearOrganizationTempData(SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

}