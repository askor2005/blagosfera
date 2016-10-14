package ru.radom.kabinet.services.sharer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.account.AccountDao;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.payment.PaymentSystemDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.NotificationService;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.sharer.SharerModelService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service("sharerModelService")
@Deprecated
public class SharerModelServiceImpl implements SharerModelService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PaymentSystemDao paymentSystemDao;

    @Autowired
    private AccountTypeDao accountTypeDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private RequestContext radomRequestContext;

    @Override
    public Map<String, Object> fillMapForStandardModel(User user, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
/*
        List<ContactEntity> contacts = contactDao.getOnline(radomRequestContext.getCurrentSharer());
        Set<ContactEntity> contactSet = new HashSet<>();
        contactSet.addAll(contacts);

        result.put("sharer", user);

        // Не исползьзуется
        //result.put("sharerBalance", transactionDao.getSharerBallance(sharer));
        // TODO Сделать запрос с клиента
        result.put("onlineContacts", contactSet);
        // TODO Сделать запрос с клиента
        result.put("unreadNotificationsCount", notificationService.getUnreadNotificationsCount());
        // TODO Сделать запрос с клиента
        result.put("unreadChatMessagesCount", chatService.getUnreadDialogsCount(radomRequestContext.getCurrentSharer()));
        result.put("newContactRequestsCount", contactDao.getNewRequestsCount(user));
        result.put("requestsCount", communityMemberDao.getRequestsCount(user));
        result.put("invitesCount", communityMemberDao.getInvitesCount(user));
        result.put("myRequestsCount", communityMemberDao.getMyRequestsCount(user));

        result.put("profileFilling", profileService.getProfileFilling(user));

        result.put("paymentSystems", paymentSystemDao.getAccounts());

        result.put("accountTypes", accountTypeDao.getAccounts(Sharer.class));
        result.put("accountsMap", accountDao.getAccountMap(user));

        result.put("isAdmin", SecurityUtils.hasRole("ROLE_ADMIN"));

        // Если пользователю необходимо поменять пароль - выдаём оповещение,
        // предварительно перенаправив на единственную доступную страницу
        if (SharerStatus.NEED_CHANGE_PASSWORD.equals(user.getStatus())) {

            String showDialogToChangePassword = sharerSettingDao.get(sharer, ProfileService.SHOW_DIALOG_TO_CHANGE_PASSWORD_SETTINGS_KEY, "true");
            if ("true".equals(showDialogToChangePassword)) {
                result.put("showDialogToChangePassword", true);
            }
        }

        //При новой авторизации необходимо обновлять регистрацию service-worker'а для gcm
        result.put("needGcmSwUpdate", radomRequestContext.isJustLogged());


        long end = System.currentTimeMillis();
        long duration = end - (Long)request.getAttribute("start");
        result.put("requestProcessingDuration", duration);*/

        return result;
    }

}
