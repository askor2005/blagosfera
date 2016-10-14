package ru.askor.blagosfera.core.services.support;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.security.RecaptchaService;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.core.util.cache.WebUtils;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.FeedbackAcceptedEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.sessions.UserSession;
import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;
import ru.askor.blagosfera.domain.support.SupportRequestType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Service
public class SupportRequestServiceImpl implements SupportRequestService {
    @Autowired
    private UserService userService;
    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;
    @Autowired
    private FieldDao fieldDao;
    @Autowired
    private RecaptchaService recaptchaService;
    @Autowired
    private SupportRequestDataService supportRequestDataService;
    @Autowired
    private SupportRequestTypeDataService supportRequestTypeDataService;
    @Override
    public void createSupportRequest(HttpServletRequest httpServletRequest, String email, String theme, String description, Long supportRequestTypeId, String captcha) throws Exception {
       validateCreateRequest(httpServletRequest, email, theme, description, supportRequestTypeId, captcha);
       SupportRequest supportRequest = new SupportRequest(email,theme,description, SupportRequestStatus.NEW,supportRequestTypeDataService.getById(supportRequestTypeId));
        supportRequestDataService.save(supportRequest);
        User user = new User();
        user.setEmail(email);
        sendToEmail(supportRequest);

    }
    @Override
    public void validateCreateRequest(HttpServletRequest httpServletRequest, String email, String theme, String description, Long supportRequestTypeId, String captcha) throws Exception {
        if ((captcha == null) || (StringUtils.isEmpty(captcha))) {
            throw new Exception("Капча не заполнена!");
        }
        if (!recaptchaService.verify(WebUtils.getRemoteIp(httpServletRequest),captcha).isSuccess()) {
            throw new Exception("Неверно указана капча!");
        }
        if ((email == null) || (StringUtils.isEmpty(email))) {
            throw new Exception("E-mail не введен!");
        }
        if (!StringUtils.checkEmail(email)) {
            throw new Exception("Введен некорректный e-mail!");
        }
        if ((supportRequestTypeId == null) || (supportRequestTypeDataService.getById(supportRequestTypeId) == null)) {
            throw new Exception("Не указана категория запроса!");
        }
        if ((theme == null) || (StringUtils.isEmpty(theme))) {
            throw new Exception("Не указана тема запроса!");
        }
        if ((description == null) || (StringUtils.isEmpty(description))) {
            throw new Exception("Не указано описание запроса!");
        }
        if (description.length() > 5000) {
            throw new Exception("Описание не может быть длинее 5000 символов!");
        }
        if (email.length() > 128) {
            throw new Exception("E-mail не может быть длинее 128 символов!");
        }
        if (theme.length() > 128) {
            throw new Exception("Тема не может быть длинее 128 символов!");
        }

    }
    @Override
    public void sendToEmail(SupportRequest supportRequest) {
        User receiver = userService.createFakeUser(supportRequest.getEmail(),"","","");
        List<User> adminReceivers = new ArrayList<>();
        for (String email : supportRequest.getType().getAdminEmailsList().split(",")) {
            adminReceivers.add(userService.createFakeUser(email,"","",""));
        }
        //Публикуем событие
        blagosferaEventPublisher.publishEvent(
                new FeedbackAcceptedEvent(this, receiver, adminReceivers,supportRequest)
        );

    }

    @Override
    public void close(Long id) {
        SupportRequest supportRequest = supportRequestDataService.getById(id);
        assert  supportRequest != null;
        supportRequest.setStatus(SupportRequestStatus.RESOLVED);
        supportRequestDataService.save(supportRequest);
    }
}
