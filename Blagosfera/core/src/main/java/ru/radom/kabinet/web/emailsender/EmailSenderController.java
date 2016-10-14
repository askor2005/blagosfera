package ru.radom.kabinet.web.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.emailsender.EmailSenderService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.web.emailsender.dto.FindTemplateResponseDto;

import java.util.List;

/**
 * Контроллер для отправки писем участникам системы на основе шаблона документа
 * Created by vgusev on 26.12.2015.
 */
@Controller
public class EmailSenderController {

    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Поиск шаблона
     * @param templateName код шаблона
     * @return список шаблонов
     */
    @RequestMapping(value = "/admin/emailsender/findDocumentTemplates.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<FindTemplateResponseDto> findDocumentTemplates(@RequestParam(value = "template_name", required = true) String templateName) {
        return FindTemplateResponseDto.toDtoList(emailSenderService.findTemplates(templateName));
    }

    /**
     * Отправить письмо активным участникам системы
     * @param templateCode код шаблона
     * @param mailSubject название темы письма
     * @param mailFrom от кого письмо
     * @return json с результатом
     */
    @RequestMapping(value = "/admin/emailsender/sendEmailsToActiveSharers.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto sendEmailsToActiveSharers(
            @RequestParam(value = "template_code", required = true) String templateCode,
            @RequestParam(value = "mail_subject", required = true) String mailSubject,
            @RequestParam(value = "mail_from", required = true) String mailFrom) {
        emailSenderService.sendToActiveSharers(SecurityUtils.getUser(), templateCode, mailSubject, mailFrom);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/emailsender/sendEmailsToMan.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto sendEmailsToMan(
            @RequestParam(value = "template_code", required = true) String templateCode,
            @RequestParam(value = "mail_subject", required = true) String mailSubject,
            @RequestParam(value = "mail_from", required = true) String mailFrom) {
        emailSenderService.sendToActiveToMan(SecurityUtils.getUser(), templateCode, mailSubject, mailFrom);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/emailsender/sendEmailsToWomen.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto sendEmailsToWomen(
            @RequestParam(value = "template_code", required = true) String templateCode,
            @RequestParam(value = "mail_subject", required = true) String mailSubject,
            @RequestParam(value = "mail_from", required = true) String mailFrom) {
        emailSenderService.sendToActiveWomen(SecurityUtils.getUser(), templateCode, mailSubject, mailFrom);
        return SuccessResponseDto.get();
    }

    /**
     * Отправить письмо выбранным участникам системы
     * @param templateCode код шаблона
     * @param mailSubject название темы письма
     * @param mailFrom от кого письмо
     * @param sharerIds ИДы пользователей
     * @return json с результатом
     */
    @RequestMapping(value = "/admin/emailsender/sendEmailsToSharers.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto sendEmailsToSharers(
            @RequestParam(value = "template_code", required = true) String templateCode,
            @RequestParam(value = "mail_subject", required = true) String mailSubject,
            @RequestParam(value = "mail_from", required = true) String mailFrom,
            @RequestParam(value = "sharer_ids[]", required = true) List<Long> sharerIds) {
        emailSenderService.sendToUsers(SecurityUtils.getUser(), templateCode, mailSubject, mailFrom, sharerIds);
        return SuccessResponseDto.get();
    }

    /**
     * Страница отправки писем
     * @return отображение страницы
     */
    @RequestMapping(value = "/admin/emailsender/emailSenderPage", method = RequestMethod.GET)
    public String getEmailSenderPage(Model model) {
        /*model.addAttribute("currentPageTitle", "Отправка писем пользователям");
        model.addAttribute("breadcrumb", new Breadcrumb()
                .add("Админ панель", "/admin/systemSettings")
                .add("Отправка писем пользователям", "/admin/emailsender/emailSenderPage"));*/
        return "emailSenderPage";
    }
}
