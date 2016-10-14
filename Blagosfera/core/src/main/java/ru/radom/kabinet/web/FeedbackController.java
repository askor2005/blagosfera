package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.services.support.SupportRequestTypeDataService;
import ru.askor.blagosfera.core.services.support.SupportRequestService;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.web.admin.dto.CreateSupportRequestDto;
import ru.radom.kabinet.web.admin.dto.SupportRequestInitDataDto;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 *
 */
@Controller
public class FeedbackController {
    @Autowired
    private SupportRequestTypeDataService supportRequestTypeDataService;
    @Autowired
    private SupportRequestService supportRequestService;

    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    public String getFeedbackPage() {
        return "supportRequest";
    }

    @RequestMapping(value = "/feedback.json", method = RequestMethod.GET)
    public
    @ResponseBody
    SupportRequestInitDataDto getFeedbackPageInfo() {
        SupportRequestInitDataDto supportRequestInitDataDto = new SupportRequestInitDataDto();
        supportRequestInitDataDto.setSupportRequestTypes(supportRequestTypeDataService.findAll().
                stream().map(supportRequestType -> {
                    supportRequestType.setAdminEmailsList(null);//для безопасности
                    return supportRequestType;
                }).collect(Collectors.toList()));
        return supportRequestInitDataDto;
    }

    @RequestMapping(value = "/feedback/save.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto saveFeedback(@RequestParam("email")  String email,
                                    @RequestParam("theme") String theme,
                                            @RequestParam("description") String description,
                                            @RequestParam("supportRequestTypeId") Long supportRequestTypeId,
                                            @RequestParam("captcha") String captcha,
                                            HttpServletRequest request) throws Exception {
        try {
            supportRequestService.createSupportRequest(request, email,
                    theme, description, supportRequestTypeId, captcha);
        } catch (RecaptchaException e) {
           throw new RuntimeException("Неверно указана капча!");
        }
        return SuccessResponseDto.get();
    }
}
