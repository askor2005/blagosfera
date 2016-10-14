package ru.radom.kabinet.web.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.dto.notification.NotificationDto;
import ru.radom.kabinet.dto.notification.NotificationPageDataDto;
import ru.radom.kabinet.dto.notification.NotificationsDto;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.NotificationService;
import ru.radom.kabinet.services.notification.NotificationDomainService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Controller
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationDomainService notificationDomainService;

    @RequestMapping(value = "/notify")
    public String showNotificationsPage(Model model) {
        return "notifications";
    }

    @RequestMapping(value = "/notifications/page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public NotificationPageDataDto notificationPageData() {
        return new NotificationPageDataDto(notificationDomainService.getUnreadBlockingCount(SecurityUtils.getUser().getId()) > 0);
    }

    @RequestMapping(value = "/notifications/list.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<NotificationDto> listJson(
            @RequestParam(value = "start_date", required = false) String startDateString,
            @RequestParam(value = "end_date", required = false) String endDateString,
            @RequestParam(value = "include_read", defaultValue = "true") boolean includeRead,
            @RequestParam(value = "include_unread", defaultValue = "true") boolean includeUnread,
            @RequestParam(value = "priority", required = false) NotificationPriority priority,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
        Date startDate = DateUtils.parseDate(startDateString, null);
        Date endDate = DateUtils.parseDate(endDateString, null);
        page = page - 1;
        List<Notification> notifications = notificationDomainService.getList(SecurityUtils.getUser().getId(), startDate, DateUtils.getDayEnd(endDate), includeRead, includeUnread, priority, page, perPage);
        return NotificationDto.toDtoList(notifications);
    }

    @RequestMapping(value = "/notifications/unread.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public NotificationsDto unreadJson(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        return new NotificationsDto(
                notificationDomainService.getLastUnreadNotifications(SecurityUtils.getUser().getId(), count),
                notificationDomainService.getUnreadCount(SecurityUtils.getUser().getId())
        );
    }

    @RequestMapping(value = "/notifications/markasread.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto markAsRead(@RequestParam(value = "notification_id") Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
        } catch (Throwable ignored) {
            // TODO this should be fixed
        }

        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/notifications/mark_all_as_read.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto markAllAsRead() {
        notificationService.markAllAsRead(SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

}