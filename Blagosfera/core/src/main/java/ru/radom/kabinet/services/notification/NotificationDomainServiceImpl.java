package ru.radom.kabinet.services.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.SystemAccountRepository;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.notification.NotificationLinkRepository;
import ru.askor.blagosfera.data.jpa.repositories.notification.NotificationRepository;
import ru.askor.blagosfera.data.jpa.specifications.NotificationSpecifications;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.notification.NotificationLink;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.askor.blagosfera.domain.notification.NotificationSender;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.notifications.NotificationEntity;
import ru.radom.kabinet.model.notifications.NotificationLinkEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
@Service
@Transactional
public class NotificationDomainServiceImpl implements NotificationDomainService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationLinkRepository notificationLinkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    public Notification getById(Long id) {
        return NotificationEntity.toDomainSafe(notificationRepository.findOne(id));
    }

    @Override
    public Notification save(Notification notification) {
        ExceptionUtils.check(notification == null, "Не передан объект уведомления");
        ExceptionUtils.check(notification.getUser() == null, "Не передан получатель уведомления");
        ExceptionUtils.check(notification.getUser().getId() == null, "Не передан получатель уведомления");
        Object sender = getSender(notification.getSender());
        ExceptionUtils.check(sender == null, "Не определён отправитель уведомления");

        NotificationEntity entity;
        if (notification.getId() == null) {
            entity = new NotificationEntity();
        } else {
            entity = notificationRepository.getOne(notification.getId());
        }
        entity.setSender(sender);
        entity.setShortText(notification.getShortText());
        entity.setDate(notification.getDate());

        //entity.setObject();
        entity.setPriority(notification.getPriority());
        entity.setRead(notification.isRead());
        entity.setSubject(notification.getSubject());
        entity.setText(notification.getText());
        entity.setPriority(notification.getPriority());
        entity.setUser(userRepository.getOne(notification.getUser().getId()));
        entity = notificationRepository.save(entity);
        entity.setLinks(saveLinks(notification.getLinks(), entity));

        return NotificationEntity.toDomainSafe(entity);
    }

    @Override
    public Notification delete(Long id) {
        Notification result = NotificationEntity.toDomainSafe(notificationRepository.findOne(id));
        notificationRepository.delete(id);
        return result;
    }

    @Override
    public List<Notification> getList(Long userId, Date startDate, Date endDate, boolean isIncludeRead, boolean isIncludeUnread, NotificationPriority priority, int page, int perPage) {
        Pageable pageable = new PageRequest(page, perPage, new Sort(Sort.Direction.DESC, "date"));

        Specifications<NotificationEntity> specifications = Specifications.where(NotificationSpecifications.getByUser(userId));
        if (startDate != null && endDate != null) {
            specifications = specifications.and(NotificationSpecifications.betweenDate(startDate, endDate));
        }
        if (priority != null) {
            specifications = specifications.and(NotificationSpecifications.getByPriority(priority));
        }
        if (isIncludeRead && isIncludeUnread) {
            // do nothing
        } else if (isIncludeRead) {
            specifications = specifications.and(NotificationSpecifications.onlyRead());
        } else if (isIncludeUnread) {
            specifications = specifications.and(NotificationSpecifications.onlyUnRead());
        }


        return NotificationEntity.toDomainList(
                notificationRepository.findAll(specifications, pageable).getContent()
        );
    }

    public List<Notification> getLastUnreadNotifications(Long userId, int count) {
        Pageable pageable = new PageRequest(0, count, new Sort(Sort.Direction.DESC, "date"));

        Specifications<NotificationEntity> specifications = Specifications.where(NotificationSpecifications.getByUser(userId));
        specifications = specifications.and(NotificationSpecifications.onlyUnRead());

        return NotificationEntity.toDomainList(
                notificationRepository.findAll(specifications, pageable).getContent()
        );
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.markAsRead(id);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationRepository.getUnreadCount(userId);
    }

    @Override
    public int getUnreadBlockingCount(Long userId) {
        return notificationRepository.getUnreadCount(userId, NotificationPriority.BLOCKING);
    }

    private Object getSender(NotificationSender notificationSender) {
        Object result = null;
        if (notificationSender instanceof User) {
            result = userRepository.getOne(notificationSender.getId());
        } else if (notificationSender instanceof SystemAccount) {
            result = systemAccountRepository.getOne(notificationSender.getId());
        } else if (notificationSender instanceof Community) {
            result = communityRepository.getOne(notificationSender.getId());
        }
        return result;
    }

    private List<NotificationLinkEntity> saveLinks(List<NotificationLink> notificationLinks, NotificationEntity notificationEntity) {
        List<NotificationLinkEntity> result = null;
        if (notificationLinks != null) {
            result = new ArrayList<>();
            for (NotificationLink notificationLink : notificationLinks) {
                NotificationLinkEntity entity = new NotificationLinkEntity();
                entity.setPosition(notificationLink.getPosition());
                entity.setAjax(notificationLink.isAjax());
                entity.setMakrAsRead(notificationLink.isMarkAsRead());
                entity.setTitle(notificationLink.getTitle());
                entity.setType(notificationLink.getType());
                entity.setUrl(notificationLink.getUrl());
                entity.setNotification(notificationEntity);
                entity = notificationLinkRepository.save(entity);
                result.add(entity);
            }
        }
        return result;
    }
}
