package ru.askor.blagosfera.web.controllers.ng.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.services.support.SupportRequestDataService;
import ru.askor.blagosfera.core.services.support.SupportRequestService;
import ru.askor.blagosfera.core.services.support.SupportRequestTypeDataService;
import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;
import ru.askor.blagosfera.domain.support.SupportRequestType;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.web.admin.dto.CreateSupportRequestDto;
import ru.radom.kabinet.web.admin.dto.SaveFeedbackStatusDto;
import ru.radom.kabinet.web.admin.dto.SupportRequestInitDataDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 19.05.2016.
 */
@RestController
@RequestMapping("/api/support/requests")
public class FeedbackControllerNg {
    @Autowired
    private SupportRequestTypeDataService supportRequestTypeDataService;
    @Autowired
    private SupportRequestService supportRequestService;
    @Autowired
    private SupportRequestDataService supportRequestDataService;
    public @ResponseBody
    @PreAuthorize("permitAll")
    @RequestMapping(value = "/info.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    SupportRequestInitDataDto getInitInfo() {
        SupportRequestInitDataDto supportRequestInitDataDto = new SupportRequestInitDataDto();
        supportRequestInitDataDto.setSupportRequestTypes(supportRequestTypeDataService.findAll().
                stream().map(supportRequestType -> {
            supportRequestType.setAdminEmailsList(null);//для безопасности
            return supportRequestType;
        }).collect(Collectors.toList()));
        return supportRequestInitDataDto;
    }
    @PreAuthorize("permitAll")
    @RequestMapping(value = "/save.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    SaveFeedbackStatusDto saveRequest(@RequestBody CreateSupportRequestDto createSupportRequestDto,HttpServletRequest httpServletRequest) {
        SaveFeedbackStatusDto saveFeedbackStatusDto = new SaveFeedbackStatusDto();
        try {
            try {
                supportRequestService.createSupportRequest(httpServletRequest, createSupportRequestDto.getEmail(),
                        createSupportRequestDto.getTheme(), createSupportRequestDto.getDescription(), createSupportRequestDto.getSupportRequestTypeId(), createSupportRequestDto.getCaptcha());
            } catch (RecaptchaException e) {
                throw new Exception("Неверно указана капча!");
            }
        } catch (Throwable e) {
            String message = e.getCause() != null && e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getMessage();
            saveFeedbackStatusDto.setError(message);
            saveFeedbackStatusDto.setStatus("error");
            return saveFeedbackStatusDto;
        }
        saveFeedbackStatusDto.setStatus("ok");
        return saveFeedbackStatusDto;
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/admin/search.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SupportRequest> searchSupportRequests(@RequestParam("page") int page,@RequestParam("perPage") int perPage) {
        return supportRequestDataService.search(SupportRequestStatus.NEW,page - 1,perPage);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/admin/resolve.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponseDto closeSupportRequest(@RequestParam("id") Long id) {
        supportRequestService.close(id);
        return SuccessResponseDto.get();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/admin/info.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    SupportRequestInitDataDto getInitInfoAdmin() {
        SupportRequestInitDataDto supportRequestInitDataDto = new SupportRequestInitDataDto();
        supportRequestInitDataDto.setSupportRequestTypes(supportRequestTypeDataService.findAll());
        supportRequestInitDataDto.setTotalRequestsCount(supportRequestDataService.count(SupportRequestStatus.NEW));
        return supportRequestInitDataDto;
    }
}
