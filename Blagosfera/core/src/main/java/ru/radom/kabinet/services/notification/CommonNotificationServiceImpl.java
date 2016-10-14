package ru.radom.kabinet.services.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.core.services.notification.SmsService;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.repositories.RameraTextsRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.account.transaction.TransactionEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.events.chat.ChatEvent;
import ru.askor.blagosfera.domain.events.community.*;
import ru.askor.blagosfera.domain.events.document.LetterOfAuthorityEvent;
import ru.askor.blagosfera.domain.events.document.RameraFlowOfDocumentEvent;
import ru.askor.blagosfera.domain.events.news.CommentEvent;
import ru.askor.blagosfera.domain.events.user.*;
import ru.askor.blagosfera.domain.events.voting.VoterErrorEvent;
import ru.askor.blagosfera.domain.events.voting.VoterVotingEvent;
import ru.askor.blagosfera.domain.notification.*;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;
import ru.askor.blagosfera.domain.notification.sms.SmsNotificationType;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.concurrency.OrderingExecutor;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.exception.FlowOfDocumentException;
import ru.radom.kabinet.document.exception.FlowOfDocumentExceptionType;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.DocumentParameterBuilder;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;
import ru.radom.kabinet.services.*;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.behavior.SharerCommunityMemberBehaviorResolver;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.systemAccount.SystemAccountService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.PadegConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.web.communities.CommunitiesCooperativeSocietyController;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис оповещений по системе и на почту
 * Created by vgusev on 15.09.2015.
 */
@Transactional
@Service("commonNotificationService")
public class CommonNotificationServiceImpl implements CommonNotificationService {

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private GcmService gcmService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SystemAccountService systemAccountService;

    @Autowired
    private SharerCommunityMemberBehaviorResolver sharerCommunityMemberBehaviorResolver;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SharerService sharerService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private RosterService rosterService;

    // Executor тасков для выполнения в рамках одного пользователя последовательно
    @Autowired
    private OrderingExecutor orderingExecutor;

    @Autowired
    private SmsService smsService;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private RameraTextsRepository rameraTextsRepository;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    // Приставка к шаблонам по почте от шаблонов в системе
    private static final String EMAIL_TEMPLATE_PREFIX = "email.";

    //------------------------------------------------------------------------------------------------------------------
    // Наименование участников шаблона
    //------------------------------------------------------------------------------------------------------------------
    private static final String SENDER_PARTICIPANT_NAME = "Отправитель"; // Отправитель оповещения

    private static final String RECEIVER_PARTICIPANT_NAME = "Получатель"; // Получатель оповещения

    private static final String COMMUNITY_PARTICIPANT_NAME = "Объединение"; // Объединение в оповещении как участник документа

    private static final String OTHER_SHARER_PARTICIPANT_NAME = "Инициатор"; // Некий вспомогательный участник
    //------------------------------------------------------------------------------------------------------------------

    @Override
    @PostConstruct
    public void init() throws Exception {
        // orderingExecutor = new OrderingExecutor(taskExecutor);
    }

    /**
     * Отправить сообщение в системе и на почту
     *
     * @param templateCode             Код шаблона
     * @param receiver                 Получатель
     * @param sender                   Отправитель
     * @param createDocumentParameters Параметры шаблона
     */
    private void sendNotification(String templateCode, User receiver, NotificationSender sender,
                                  List<CreateDocumentParameter> createDocumentParameters,
                                  NotificationPriority priority, List<NotifyLink> notificationLinks,
                                  Map<String, Object> scriptVars) {
        sendNotification(templateCode, receiver, sender, createDocumentParameters,
                priority, notificationLinks, scriptVars, new Date());
    }

    private void sendSms(User sender, User receiver, Transaction transaction) {
        if (receiver == null) return;
        if (transaction == null) return;

        String tel = fieldsService.getFieldValue(receiver, "MOB_TEL");
        if (tel == null || tel.isEmpty()) return;

        tel = tel.trim().replaceAll(" ", "").replaceAll("-", "");

        String messageCode;

        String senderNameGenitive = "";
        String senderIkp = "";
        String senderName = fieldsService.getFieldValue(sender, "LASTNAME") + " " +
                fieldsService.getFieldValue(sender, "FIRSTNAME") + " " +
                fieldsService.getFieldValue(sender, "SECONDNAME");

        if (sender == null) {
            messageCode = "SMS_TRANSACTION_SUCCESS_PAYMENT_SYSTEM";
        } else {
            messageCode = "SMS_TRANSACTION_SUCCESS_USER";

            senderNameGenitive = Padeg.getFIOPadegAS(fieldsService.getFieldValue(sender, "LASTNAME"),
                    fieldsService.getFieldValue(sender, "FIRSTNAME"),
                    fieldsService.getFieldValue(sender, "SECONDNAME"),
                    PadegConstants.PADEG_R);

            senderIkp = sender.getIkp();
        }

        RameraTextEntity messageEntity = rameraTextsRepository.findOneByCode(messageCode);

        if (messageEntity == null) return;

        for (TransactionDetail detail : transaction.getDetails()) {
            AccountEntity account = accountRepository.findOne(detail.getAccountId());
            if (!((UserEntity) account.getOwner()).getId().equals(receiver.getId())) continue;

            String text = messageEntity.getText()
                    .replaceAll("%amount%", new DecimalFormat("#0.##").format(detail.getAmount()))
                    .replaceAll("%balance%", new DecimalFormat("#0.##").format(account.getBalance()))
                    .replaceAll("%accountType%", account.getType().getName())
                    .replaceAll("%senderName%", senderName)
                    .replaceAll("%senderNameGenitive%", senderNameGenitive)
                    .replaceAll("%senderIkp%", senderIkp)
                    .replaceAll("%transactionComment%", transaction.getDescription() != null ? transaction.getDescription() : "");

            try {
                text = smsService.send(new SmsNotification(SmsNotificationType.SMS, tel, text));
            } catch (JsonProcessingException | NoSuchAlgorithmException | HttpException | UnsupportedEncodingException ignored) {
            }

            break;
        }
    }

    private void sendSms(User sender, User receiver, Contact contact) {
        if (sender == null) return;
        if (receiver == null) return;
        if (contact == null) return;
        if (rosterService.isUserAuthenticated(receiver.getEmail())) return;

        String tel = fieldsService.getFieldValue(receiver, "MOB_TEL");
        if (tel == null || tel.isEmpty()) return;

        tel = tel.trim().replaceAll(" ", "").replaceAll("-", "");

        String messageCode = "SMS_CONTACT_REQUEST";
        String senderIkp;
        String senderName = fieldsService.getFieldValue(sender, "LASTNAME") + " " +
                fieldsService.getFieldValue(sender, "FIRSTNAME") + " " +
                fieldsService.getFieldValue(sender, "SECONDNAME");

        senderIkp = sender.getIkp();

        RameraTextEntity messageEntity = rameraTextsRepository.findOneByCode(messageCode);

        if (messageEntity == null) return;

        String text = messageEntity.getText()
                .replaceAll("%senderName%", senderName)
                .replaceAll("%senderIkp%", senderIkp);

        try {
            text = smsService.send(new SmsNotification(SmsNotificationType.SMS, tel, text));
        } catch (JsonProcessingException | NoSuchAlgorithmException | HttpException | UnsupportedEncodingException ignored) {
        }
    }

    /**
     * Отправить сообщение в системе и на почту
     *
     * @param templateCode             Код шаблона
     * @param receiver                 Получатель
     * @param sender                   Отправитель
     * @param createDocumentParameters Параметры шаблона
     * @param eventDate                Дата события
     */
    private void sendNotification(String templateCode, User receiver, NotificationSender sender,
                                  List<CreateDocumentParameter> createDocumentParameters,
                                  NotificationPriority priority, List<NotifyLink> notificationLinks,
                                  Map<String, Object> scriptVars, Date eventDate) {
        sendNotification(templateCode, receiver, sender, createDocumentParameters, priority,
                notificationLinks, scriptVars, eventDate, null, new HashMap<>());
    }

    private void sendNotification(String templateCode, User receiver, NotificationSender sender,
                                  List<CreateDocumentParameter> createDocumentParameters,
                                  NotificationPriority priority, List<NotifyLink> notificationLinks,
                                  Map<String, Object> scriptVars, Date eventDate,
                                  NotificationType notificationType, Map<String, String> notificationData) {
        orderingExecutor.execute(
                new NotificationTask(
                        templateCode,
                        receiver,
                        sender,
                        createDocumentParameters,
                        priority,
                        notificationLinks,
                        scriptVars,
                        eventDate,
                        notificationType, notificationData),
                receiver.getId());
    }

    private List<CreateDocumentParameter> createStandardTemplateParameters(User receiver, Object sender) {
        return createStandardTemplateParameters(receiver, sender, null, null, null, null, null, null);
    }

    private List<CreateDocumentParameter> createStandardTemplateParameters(User receiver, Object sender, Community community) {
        return createStandardTemplateParameters(receiver, sender, community, null, null, null, null, null);
    }

    private List<CreateDocumentParameter> createStandardTemplateParameters(User receiver, Object sender, Community community, User otherSharer) {
        return createStandardTemplateParameters(receiver, sender, community, otherSharer, null, null, null, null);
    }

    // Создать параметры для шаблона документа со стандартным набором участников
    private List<CreateDocumentParameter> createStandardTemplateParameters(User receiver, Object sender, Community community, User otherSharer,
                                                                           List<UserFieldValue> receiverUserFieldValues, List<UserFieldValue> senderUserFieldValues,
                                                                           List<UserFieldValue> communityUserFieldValues, List<UserFieldValue> otherSharerUserFieldValues) {
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        DocumentParameterBuilder builder = DocumentParameterBuilder.create(ParticipantsTypes.INDIVIDUAL, receiver, RECEIVER_PARTICIPANT_NAME);
        if (receiverUserFieldValues != null) {
            builder.addAll(receiverUserFieldValues);
        }
        createDocumentParameters.add(builder.get());
        if (sender != null && sender instanceof User) {
            builder = DocumentParameterBuilder.create(ParticipantsTypes.INDIVIDUAL, (User) sender, SENDER_PARTICIPANT_NAME);
            if (senderUserFieldValues != null) {
                builder.addAll(senderUserFieldValues);
            }
            createDocumentParameters.add(builder.get());
        }
        if (community != null) {
            builder = DocumentParameterBuilder.create(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION, community, COMMUNITY_PARTICIPANT_NAME);
            if (communityUserFieldValues != null) {
                builder.addAll(communityUserFieldValues);
            }
            createDocumentParameters.add(builder.get());
        }
        if (otherSharer != null) {
            builder = DocumentParameterBuilder.create(ParticipantsTypes.INDIVIDUAL, otherSharer, OTHER_SHARER_PARTICIPANT_NAME);
            if (otherSharerUserFieldValues != null) {
                builder.addAll(otherSharerUserFieldValues);
            }
            createDocumentParameters.add(builder.get());
        }

        return createDocumentParameters;
    }

    @Override
    @EventListener
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public void onBlagosferaEvent(BlagosferaEvent event) {
        if (event instanceof FeedbackAcceptedEvent) {
            final FeedbackAcceptedEvent feedbackAcceptedEvent = (FeedbackAcceptedEvent) event;
            onFeedbackAcceptedEvent(feedbackAcceptedEvent);
        } else if (event instanceof InviteEvent) {
            final InviteEvent inviteEvent = (InviteEvent) event;
            onInviteEvent(inviteEvent);
        } else if (event instanceof ChatEvent) {
            final ChatEvent chatEvent = (ChatEvent) event;
            onChatEvent(chatEvent);
        } else if (event instanceof ContactEvent) {
            final ContactEvent сontactEvent = (ContactEvent) event;
            onContactEvent(сontactEvent);
        } else if (event instanceof CommunityCreateFileEvent) {
            final CommunityCreateFileEvent communityCreateFileEvent = (CommunityCreateFileEvent) event;
            onCommunityCreateFileEvent(communityCreateFileEvent);
        } else if (event instanceof CommunityMemberAppointRequestEvent) {
            final CommunityMemberAppointRequestEvent communityMemberAppointRequestEvent = (CommunityMemberAppointRequestEvent) event;
            onCommunityMemberAppointRequestEvent(communityMemberAppointRequestEvent);
        } else if (event instanceof CommunityOtherEvent) {
            final CommunityOtherEvent communityCreateEvent = (CommunityOtherEvent) event;
            onCommunityCreateEvent(communityCreateEvent);
        } else if (event instanceof CommunityMemberEvent) {
            final CommunityMemberEvent communityMemberEvent = (CommunityMemberEvent) event;
            onCommunityMemberEvent(communityMemberEvent);
        } else if (event instanceof CommunityCooperativeLeaveEvent) {
            final CommunityCooperativeLeaveEvent communityCooperativeLeaveEvent = (CommunityCooperativeLeaveEvent) event;
            onCommunityCooperativeLeaveEvent(communityCooperativeLeaveEvent);
        } else if (event instanceof CommunityEvent) {
            final CommunityEvent communityEvent = (CommunityEvent) event;
            onCommunityEvent(communityEvent);
        } else if (event instanceof CommentEvent) {
            final CommentEvent commentEvent = (CommentEvent) event;
            onCommentEvent(commentEvent);
        } else if (event instanceof RegistrationEvent) {
            final RegistrationEvent registrationEvent = (RegistrationEvent) event;
            onRegistrationEvent(registrationEvent);
        } else if (event instanceof RameraFlowOfDocumentEvent) {
            final RameraFlowOfDocumentEvent flowOfDocumentEvent = (RameraFlowOfDocumentEvent) event;
            onRameraFlowOfDocumentEvent(flowOfDocumentEvent);
        } else if (event instanceof VoterVotingEvent) { // События которые возникают по инициативе участников собрания
            final VoterVotingEvent voterBatchVotingEvent = (VoterVotingEvent) event;
            onVoterBatchVotingEvent(voterBatchVotingEvent);
        } else if (event instanceof VoterErrorEvent) {
            final VoterErrorEvent voterErrorEvent = (VoterErrorEvent) event;
            onVoterErrorEvent(voterErrorEvent);
        } else if (event instanceof SharerEvent) {
            SharerEvent sharerEvent = (SharerEvent) event;
            onSharerEvent(sharerEvent);
        } else if (event instanceof DeletionNotificationSharerEvent) {
            DeletionNotificationSharerEvent deletionNotificationSharerEvent = (DeletionNotificationSharerEvent) event;
            onDeletionNotificationSharerEvent(deletionNotificationSharerEvent);
        } else if (event instanceof LetterOfAuthorityEvent) {
            LetterOfAuthorityEvent letterOfAuthorityEvent = (LetterOfAuthorityEvent) event;
            onLetterOfAuthorityEvent(letterOfAuthorityEvent);
        } else if (event instanceof OrganizationCommunityMemberEvent) {
            OrganizationCommunityMemberEvent organizationCommunityMemberEvent = (OrganizationCommunityMemberEvent) event;
            onOrganizationCommunityMemberEvent(organizationCommunityMemberEvent);
        }
    }

    @Override
    @TransactionalEventListener
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public void onVotingEvent(VotingEvent event) {
        switch (event.getEventType()) {
            case VOTING_STATE_CHANGE:
                handleVoting(event.getVoting(), event.getCreatedDate());
                break;
            case BATCH_VOTING_STATE_CHANGE:
                handleBatchVoting(event.getBatchVoting(), event.getCreatedDate());
                break;
        }
    }

    private void onFeedbackAcceptedEvent(FeedbackAcceptedEvent feedbackAcceptedEvent) {
        User receiver = feedbackAcceptedEvent.getReceiver();
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, null);
        Map<String, Object> scriptVars = new HashMap<>();
        scriptVars.put("theme", feedbackAcceptedEvent.getSupportRequest().getTheme());
        sendNotification("notify.support-request-accepted", receiver, receiver, parameters, NotificationPriority.NORMAL, null, scriptVars);
        for (User admin : feedbackAcceptedEvent.getAdminReceivers()) {
            parameters = createStandardTemplateParameters(admin, null);
            scriptVars = new HashMap<>();
            scriptVars.put("theme", feedbackAcceptedEvent.getSupportRequest().getTheme());
            sendNotification("notify.support-request-created", admin, admin, parameters, NotificationPriority.NORMAL, null, scriptVars);
        }
    }

    private void onInviteEvent(InviteEvent inviteEvent) {
        switch (inviteEvent.getType()) {
            case INVITE: { //Пользователя пригласили в систему
                User sender = inviteEvent.getSender();
                User receiver = inviteEvent.getReceiver();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender);

                Map<String, Object> scriptVars = new HashMap<>();
                scriptVars.put("hashUrl", inviteEvent.getInvite().getHashUrl());
                scriptVars.put("inviteDaysExpire", settingsManager.getSystemSetting("invite.days-expire"));

                sendNotification("notify.on-invite", receiver, sender, parameters, NotificationPriority.NORMAL, null, scriptVars);
                break;
            }
            case ACCEPT_INVITE: { // Пользователь принял приглашение в систему
                NotificationSender sender = systemAccountService.getById(SystemAccountEntity.BLAGOSFERA_ID);
                User receiver = inviteEvent.getReceiver();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender);

                Map<String, Object> scriptVars = new HashMap<>();
                scriptVars.put("login", receiver.getEmail());
                scriptVars.put("password", inviteEvent.getPassword());
                //scriptVars.put("inviteLogin", "invite/" + inviteEvent.getInvite().getHashUrl() + "/login?password=" + inviteEvent.getPassword());

                sendNotification("notify.on-accept-invite", receiver, sender, parameters, NotificationPriority.NORMAL, null, scriptVars);
                break;
            }
        }
    }

    private void onChatEvent(ChatEvent chatEvent) {
        switch (chatEvent.getType()) {
            case ADD: { // Пришло сообщение в чат
                ChatMessage message = chatEvent.getMessage();
                DialogEntity dialog = message.getDialog();
                //List<ChatMessage> unreadMessages = chatMessageRepository.findByDialog_IdAndReadOrderByDateDesc(dialog.getId(), false);
                //List<ChatMessageReceiver> chatMessageReceivers = chatMessageReceiverRepository.findByChatMessage_DialogIdAndRead(dialog.getId(), false);
                List<User> receivers = new ArrayList<>();
                List<User> pushReceivers = new ArrayList<>();
                User sender = message.getSender().toDomain();
                String senderAvatarLink = EmailTemplateContextFunctions.resizeImage(sender.getAvatar(), "c100");
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти к чату", "/chat#" + dialog.getId(), true, true, NotificationLinkType.SUCCESS, 1, "dialogLink"),
                        new NotifyLink("Ссылка на аватар отправителя", senderAvatarLink, true, true, NotificationLinkType.SUCCESS, 2, "senderAvatarLink")
                );
                List<UserFieldValue> senderUserFieldValues = Arrays.asList(
                        UserFieldValueBuilder.createStringValue("Сообщение", message.getText()),
                        UserFieldValueBuilder.createDateValue("Дата сообщения", message.getDate()),
                        UserFieldValueBuilder.createStringValue("Название чата", dialog.getName())
                );
                for (ChatMessageReceiver messageReceiver : message.getChatMessageReceivers()) {
                    if (messageReceiver.getReceiver().getId().equals(sender.getId())) continue;

                    for (ChatMessageReceiver chatMessageReceiver : message.getChatMessageReceivers()) {
                        UserEntity user = chatMessageReceiver.getReceiver();

                        //if (!user.isOnline() /*&& (unreadMessages.size() > 0)*/) {
                        pushReceivers.add(user.toDomain());

                        if (!user.isNotifiedAboutUnreadMessages()) {
                            receivers.add(user.toDomain());
                            user.setNotifiedAboutUnreadMessages(true);
                            sharerDao.update(user);
                        }
                        //}
                    }

                    String templateCode = null;

                    if (dialog.getUsers().size() == 2) {
                        templateCode = "notify.on-add-message";
                    } else if (dialog.getUsers().size() > 2) {
                        templateCode = "notify.on-add-chat-message";
                    }

                    Map<String, Object> scriptVars = new HashMap<>();
                    scriptVars.put("message", message);

                    for (User receiver : receivers) {
                        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, senderUserFieldValues, null, null);
                        sendNotification(templateCode, receiver, sender, parameters, NotificationPriority.NORMAL, notificationLinks, scriptVars);
                    }

                    gcmService.sendChatNotificationToSharers(message, pushReceivers);

                    break;
                }
            }
        }
    }

    private void onContactEvent(ContactEvent сontactEvent) {
        Contact contact = сontactEvent.getContact();
        Contact mirror = contactsService.getMirror(contact);

        switch (сontactEvent.getType()) {
            case ADD: { // Новый контакт
                User receiver = сontactEvent.getContact().getOther();
                User sender = сontactEvent.getContact().getUser();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Принять", "/contacts/add.json?other_id=" + sender.getId(), true, true, NotificationLinkType.SUCCESS, 1),
                        new NotifyLink("Отклонить", "/contacts/delete.json?other_id=" + sender.getId(), true, true, NotificationLinkType.DANGER, 2),
                        new NotifyLink("Смотреть профиль", sender.getLink(), false, false, NotificationLinkType.PRIMARY, 3, "senderLink"),
                        new NotifyLink("Скрыть", null, true, true, NotificationLinkType.DEFAULT, 4)
                );
                sendNotification("contact.add", receiver, sender, createStandardTemplateParameters(receiver, sender),
                        NotificationPriority.NORMAL, notificationLinks, null);

                sendSms(sender, receiver, contact);
                break;
            }
            /*case ACCEPTED: {
                if (mirror != null) {
                    Notification notification = notificationDomainService.get(NotificationType.CONTACT_REQUEST, mirror, false);
                    if (notification != null) {
                        notificationService.markAsRead(notification);
                    }
                }
                break;
            }
            case DELETE:
                Notification notification = notificationDomainService.get(NotificationType.CONTACT_REQUEST, contact, false);
                if (notification != null) {
                    notificationDomainService.delete(notification.getId());
                }
                if (mirror != null) {
                    notification = notificationDomainService.get(NotificationType.CONTACT_REQUEST, mirror, false);
                    if (notification != null) {
                        notificationDomainService.delete(notification.getId());
                    }
                }
                break;*/
            default:
                break;
        }
    }

    private void onCommunityMemberAppointRequestEvent(CommunityMemberAppointRequestEvent communityMemberAppointRequestEvent) {
        Community community = communityMemberAppointRequestEvent.getCommunity();
        switch (communityMemberAppointRequestEvent.getType()) {
            case NEED_APPOINT_MEMBER_TO_POST: { // Назначение на должность
                User receiver = communityMemberAppointRequestEvent.getAppointer();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти на страницу назначения на должность", community.getLink() + "/settings/members", false, true, NotificationLinkType.PRIMARY, 1, "membersLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> communityUserFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createStringValue("Наименование должности", communityMemberAppointRequestEvent.getPost().getName())
                );
                List<UserFieldValue> senderUserFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createStringValue("Инструкция", communityMemberAppointRequestEvent.getAppointInstruction())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, community, null, null, senderUserFieldValues, communityUserFieldValues, null);
                sendNotification("community-member.need-appoint-member-to-post", receiver, receiver, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
        }
    }

    private void onCommunityCreateEvent(CommunityOtherEvent communityCreateEvent) {
        Community community = communityCreateEvent.getCommunity();
        Map<String, Object> scriptVars = new HashMap<>();
        scriptVars.put("community", community);
        switch (communityCreateEvent.getType()) {
            case CREATED: {// Было создано новое объединение и в него добавлены участники
                for (User receiver : communityCreateEvent.getReceivers()) {
                    List<NotifyLink> notificationLinks = Arrays.asList(
                            new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityUrl"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    sendNotification("community-member.on-create-community", receiver, receiver,
                            createStandardTemplateParameters(receiver, receiver, community),
                            NotificationPriority.NORMAL, notificationLinks, scriptVars);
                }
                break;
            }
            case MEMBER_VOTING_POST: { // Участник назначен на пост (не стандартный шаблон)
                Long candidateId = VarUtils.getLong(communityCreateEvent.getParameters().get("candidateId"), -1l);
                String postName = communityCreateEvent.getParameters().get("postName");
                User otherSharer = sharerDao.getById(candidateId).toDomain();

                List<UserFieldValue> userFieldValues = Collections.singletonList(UserFieldValueBuilder.createStringValue("Наименование должности", postName));

                for (User receiver : communityCreateEvent.getReceivers()) {
                    if (receiver != null) {
                        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, community, otherSharer, null, null, userFieldValues, null);
                        List<NotifyLink> notificationLinks = Arrays.asList(
                                new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                                new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                        );
                        sendNotification("community-member.on-voting-member-to-post", receiver, receiver, parameters,
                                NotificationPriority.NORMAL, notificationLinks, scriptVars);
                    }
                }
                break;
            }

        }
    }

    private void onCommunityMemberEvent(CommunityMemberEvent communityMemberEvent) {
        CommunityMember member = communityMemberEvent.getMember();
        Community community = communityMemberEvent.getMember().getCommunity();
        if (communityMemberEvent.getCommunity() != null && community.getLink() == null) {
            community = communityDataService.getByIdFullData(communityMemberEvent.getCommunity().getId());
        } else if (community.getLink() == null) {
            community = communityDataService.getByIdFullData(community.getId());
        }
        switch (communityMemberEvent.getType()) {
            case REQUEST: { // Получен запрос на вступление в объединение
                User receiver = community.getCreator();
                User sender = member.getUser();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Принять", "/communities/accept_request.json?member_id=" + member.getId(), true, true, NotificationLinkType.SUCCESS, 1),
                        new NotifyLink("Отклонить", "/communities/reject_request.json?member_id=" + member.getId(), true, true, NotificationLinkType.DANGER, 2),
                        new NotifyLink("К списку запросов", "/groups/requests", false, true, NotificationLinkType.PRIMARY, 3, "requestsLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 4)
                );
                sendNotification("community-member.request", receiver, sender,
                        createStandardTemplateParameters(receiver, sender, community), NotificationPriority.NORMAL,
                        notificationLinks, null);
                break;
            }

            /*case CANCEL_REQUEST: {
                Notification notification = notificationDomainService.get(NotificationType.COMMUNITY_REQUEST, member, false);
                if (notification != null) {
                    notificationDomainService.delete(notification.getId());
                }
                break;
            }*/

            case ACCEPT_REQUEST: { // Подвтерждение вступления
                User receiver = member.getUser();
                User sender = community.getCreator();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                sendNotification("community-member.accept-request", receiver, sender,
                        createStandardTemplateParameters(receiver, sender, community),
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REJECT_REQUEST: { // Запрос отклонён
                User receiver = member.getUser();
                User sender = community.getCreator();
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                sendNotification("community-member.reject-request", receiver, sender,
                        createStandardTemplateParameters(receiver, sender, community),
                        NotificationPriority.NORMAL, notificationLinks, null);
                /*Notification notification = notificationDomainService.get(NotificationType.COMMUNITY_INVITE, member, false);
                if (notification != null) {
                    notificationService.markAsRead(notification);
                }*/
                break;
            }

            case INVITE: { // Приглашение вступить в группу
                User receiver = member.getUser();
                User sender = community.getCreator();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Принять", "javascript:CommunityFunctions.acceptToJoinToCommunity(" + member.getId() + ");", true, true, NotificationLinkType.SUCCESS, 1),
                        new NotifyLink("Отклонить", "/communities/reject_invite.json?member_id=" + member.getId(), true, true, NotificationLinkType.DANGER, 2),
                        new NotifyLink("К списку приглашений", "/groups/invites", false, true, NotificationLinkType.PRIMARY, 3, "inviteLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 4)
                );
                sendNotification("community-member.invite", receiver, sender,
                        createStandardTemplateParameters(receiver, sender, community),
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case EXCLUDE: { // Вы исключены из объединения
                User receiver = member.getUser();
                User sender = community.getCreator();
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                sendNotification("community-member.exclude", receiver, sender,
                        createStandardTemplateParameters(receiver, sender, community),
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            /*case CANCEL_INVITE: {
                Notification notification = notificationDomainService.get(NotificationType.COMMUNITY_INVITE, member, false);
                if (notification != null) {
                    notificationDomainService.delete(notification.getId());
                }
                break;
            }*/
            /*case ACCEPT_INVITE:
            case REJECT_INVITE: {
                Notification notification = notificationDomainService.get(NotificationType.COMMUNITY_INVITE, member, false);
                if (notification != null) {
                    notificationService.markAsRead(notification);
                }
                break;
            }*/

            case REQUEST_TO_APPOINT_POST: {// Приглашение на должность
                CommunityMemberAppointEvent communityMemberAppointEvent = (CommunityMemberAppointEvent) communityMemberEvent;
                User receiver = member.getUser();
                User sender = communityMemberAppointEvent.getAppointer();
                String appointName = Padeg.getAppointmentPadeg(communityMemberAppointEvent.getPost().getName(), PadegConstants.PADEG_R);
                List<UserFieldValue> userFieldValues = Collections.singletonList(UserFieldValueBuilder.createStringValue("Наименование должности", appointName));
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, null, userFieldValues, null);

                String requestPageLink = "/communities/requests/appoint?request_id=" + communityMemberAppointEvent.getPostRequest().getId();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти на страницу приглашения", requestPageLink, false, true, NotificationLinkType.PRIMARY, 1, "requestAppointLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                sendNotification("community-member.requestappoint", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }

            case APPOINT: { // Назначение на должность
                CommunityMemberAppointEvent communityMemberAppointEvent = (CommunityMemberAppointEvent) communityMemberEvent;
                User receiver = member.getUser();
                User sender = communityMemberAppointEvent.getAppointer();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(UserFieldValueBuilder.createStringValue("Наименование должности", communityMemberAppointEvent.getPost().getName()));
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("community-member.appoint", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }

            case APPOINT_BY_VOTING: { // Назначен на должность по итогам голосования
                CommunityMemberAppointEvent communityMemberAppointEvent = (CommunityMemberAppointEvent) communityMemberEvent;
                User receiver = member.getUser();
                // Отправляет уведомление система
                NotificationSender sender = systemAccountService.getById(SystemAccountEntity.BLAGOSFERA_ID);
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Arrays.asList(
                        UserFieldValueBuilder.createStringValue("Наименование должности", communityMemberAppointEvent.getPost().getName()),
                        UserFieldValueBuilder.createStringValue("Наименование собрания", communityMemberAppointEvent.getBatchVoting().getSubject())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, community, null, null, userFieldValues, null, null);
                sendNotification("community-member.appoint-by-voting", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case DISAPPOINT: { // Снятие с должности
                CommunityMemberAppointEvent communityMemberAppointEvent = (CommunityMemberAppointEvent) communityMemberEvent;
                User receiver = member.getUser();
                User sender = communityMemberAppointEvent.getAppointer();
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createStringValue("Наименование должности", communityMemberAppointEvent.getPost().getName())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("community-member.disappoint", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case CONDITION_DONE_REQUEST: { // Условие вступления в объединение выполнено
                User receiver = member.getUser();
                User sender = member.getUser();
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community);
                sendNotification("community-member.condition-done-request", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);


                // Нужно получить участников объединения с правом принятия пайщиков и отправить сообщение
                List<CommunityMemberEntity> approverMembers = communityMemberDao.getByPermissions(community.getId(), Collections.singletonList(SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION));
                for (CommunityMemberEntity approveMember : approverMembers) {
                    receiver = approveMember.getUser().toDomain();
                    sender = member.getUser();
                    notificationLinks = Arrays.asList(
                            new NotifyLink("Перейти на страницу принятия пайщиков", community.getLink() + CommunitiesCooperativeSocietyController.APPROVE_SHARERS_LINK, false, true, NotificationLinkType.PRIMARY, 1, "approveSharersLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    sendNotification("community-member.create-request-from-share", receiver, sender,
                            createStandardTemplateParameters(receiver, sender, community),
                            NotificationPriority.NORMAL, notificationLinks, null);
                }
                break;
            }

            case REQUEST_TO_LEAVE: { // Запрос на выход из объединения отправлен
                User receiver = member.getUser();
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, community);
                sendNotification("community-member.request-to-leave", receiver, receiver, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);

                // Нужно получить участников объединения с правом вывода пайщиков и отправить сообщение
                List<CommunityMemberEntity> leaverMembers = communityMemberDao.getByPermissions(community.getId(), Collections.singletonList(SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION));

                for (CommunityMemberEntity leaverMember : leaverMembers) {
                    receiver = leaverMember.getUser().toDomain();
                    User sender = member.getUser();
                    notificationLinks = Arrays.asList(
                            new NotifyLink("Перейти на страницу вывода пайщиков", community.getLink() + CommunitiesCooperativeSocietyController.LEAVE_SHARERS_LINK, false, true, NotificationLinkType.PRIMARY, 1, "requestToLeaveLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    sendNotification("community-member.create-request-to-leave-from-share", receiver, sender,
                            createStandardTemplateParameters(receiver, sender, community),
                            NotificationPriority.NORMAL, notificationLinks, null);
                }
                break;
            }

            case ACCEPT_REQUEST_TO_COOPERATIVE: { // Участника приняли в ПО или КУч
                User receiver = member.getUser();
                User sender = community.getCreator();
                String linkDescription;
                switch (sharerCommunityMemberBehaviorResolver.getCommunityType(community)) {
                    case PO:
                        linkDescription = "Перейти в Потребительское общество";
                        break;
                    case KUCH:
                        linkDescription = "Перейти в Кооперативный участок";
                        break;
                    default:
                        throw new RuntimeException("Оповещение о том, что принят пайщик поддерживают только ПО и КУч");
                }
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink(linkDescription, community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createDateValue("Дата принятия", new Date())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, null, userFieldValues, null);
                sendNotification("community-member.accept-request-to-cooperative", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }

            default:
                break;
        }
    }

    private void onCommunityCooperativeLeaveEvent(CommunityCooperativeLeaveEvent communityCooperativeLeaveEvent) {
        Community community = communityCooperativeLeaveEvent.getCommunity();

        if (community.getLink() == null) {
            community = communityDataService.getByIdFullData(community.getId());
        }

        switch (communityCooperativeLeaveEvent.getType()) {
            case LEAVE_FROM_COOPERATIVE_IS_DONE: { // Возврат с паевой книжки
                User receiver = communityCooperativeLeaveEvent.getReceiver();
                String linkDescription;
                SharerCommunityMemberBehaviorResolver.CommunityType communityType = sharerCommunityMemberBehaviorResolver.getCommunityType(community);
                switch (communityType) {
                    case PO:
                        linkDescription = "Перейти в Потребительское общество";
                        break;
                    case KUCH:
                        linkDescription = "Перейти в Кооперативный участок";
                        break;
                    default:
                        throw new RuntimeException("Оповещение о том, что принят пайщик поддерживают только ПО и КУч");
                }
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink(linkDescription, community.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "communityLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createDoubleValue("Перевод с паевой книжки", communityCooperativeLeaveEvent.getBookAccountAmount())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, community, null, userFieldValues, null, null, null);
                sendNotification("community-member.on-leave-from-cooperative", receiver, receiver, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
        }
    }

    private void onCommunityEvent(CommunityEvent communityEvent) {
        Community community = communityEvent.getCommunity();
        switch (communityEvent.getType()) {
            case DELETED: { // Удалено объединение
                if (!community.getCreator().equals(community.getDeleter())) {
                    User receiver = community.getCreator();
                    User sender = community.getDeleter();
                    List<NotifyLink> notificationLinks = Collections.singletonList(
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                    );
                    List<UserFieldValue> userFieldValues = Collections.singletonList(
                            UserFieldValueBuilder.createStringValue("Причина удаления", community.getDeleteComment())
                    );
                    List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, null, userFieldValues, null);
                    sendNotification("community.delete", receiver, sender, parameters, NotificationPriority.NORMAL,
                            notificationLinks, null);
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Обработчик создания файла объединения
     *
     * @param communityCreateFileEvent
     */
    private void onCommunityCreateFileEvent(CommunityCreateFileEvent communityCreateFileEvent) {
        User receiver = communityCreateFileEvent.getReceiver();
        NotificationSender sender = systemAccountService.getById(SystemAccountEntity.BLAGOSFERA_ID);
        List<NotifyLink> notificationLinks = Arrays.asList(
                new NotifyLink("Скачать файл", communityCreateFileEvent.getFileLink(), false, true, NotificationLinkType.PRIMARY, 1, "fileLink"),
                new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
        );
        List<UserFieldValue> userFieldValues = Collections.singletonList(
                UserFieldValueBuilder.createStringValue("Добавленный файл", communityCreateFileEvent.getFileName())
        );
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, null, null, userFieldValues, null, null, null);
        sendNotification("community.add.file", receiver, sender, parameters, NotificationPriority.NORMAL, notificationLinks, null);
    }

    private void onCommentEvent(CommentEvent commentEvent) {
        // TODO Нет шаблона для почты. Нужен ли?
        News news = commentEvent.getNews();
        User sender = commentEvent.getCommentEntity().getOwner().toDomain();
        User receiver = ((UserEntity) news.getAuthor()).toDomain();

        // получаем нотификацию о комментах от других пользователей
        if (receiver instanceof User && !sender.equals(receiver)) {
            List<NotifyLink> notificationLinks = Collections.singletonList(
                    new NotifyLink("Перейти к новости", news.getLink(), false, true, NotificationLinkType.DEFAULT, 1)
            );
            List<UserFieldValue> userFieldValues = Collections.singletonList(
                    UserFieldValueBuilder.createStringValue("Наименование новости", news.getTitle())
            );
            List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, userFieldValues, null, null);
            sendNotification("comment.new", receiver, sender, parameters, NotificationPriority.NORMAL,
                    notificationLinks, null);
        }
    }

    // Возвращает список конкретный шареров которые должны подписать документ.
    private List<UserEntity> getSignersFromDocument(DocumentEntity document) {
        List<UserEntity> signers = new ArrayList<>(); // все подписанты
        List<DocumentParticipantEntity> participants = document.getParticipants();
        for (DocumentParticipantEntity participant : participants) {
            List<DocumentParticipantEntity> participantChildren = participant.getChildren();
            if (participantChildren != null && !participantChildren.isEmpty()) {
                for (DocumentParticipantEntity child : participantChildren) {
                    if (child.getIsSigned()) {
                        UserEntity signer = sharerDao.getById(child.getSourceParticipantId());
                        signers.add(signer);
                    }
                }
            } else {
                if (participant.getIsSigned()) {
                    UserEntity signer = sharerDao.getById(participant.getSourceParticipantId());
                    signers.add(signer);
                }
            }
        }
        return signers;
    }

    @Override
    @TransactionalEventListener
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public void onTransactionEvent(TransactionEvent transactionEvent) {
        Transaction transaction = transactionEvent.getTransaction();

        switch (transaction.getState()) {
            case HOLD:
                break;
            case POST: {
                if (transaction.getTransactionType() == TransactionType.USER) {
                    List<NotifyLink> notificationLinks = Collections.singletonList(new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1));
                    User senderUser = null;
                    Community senderCommunity = null;
                    Sharebook senderSharebook = null;
                    AccountType senderAccountType = null;
                    //Set<User> recipients = new HashSet<>();

                    for (TransactionDetail transactionDetail : transaction.getDetails()) {
                        if (transactionDetail.getType() == TransactionDetailType.DEBIT) {
                            AccountEntity accountEntity = accountRepository.findOne(transactionDetail.getAccountId());
                            if (accountEntity.getOwner() instanceof UserEntity) {
                                senderUser = ((UserEntity) accountEntity.getOwner()).toDomain();
                            } else if (accountEntity.getOwner() instanceof CommunityEntity) {
                                senderCommunity = ((CommunityEntity) accountEntity.getOwner()).toDomain();
                            } else if (accountEntity.getOwner() instanceof SharebookEntity) {
                                senderSharebook = ((SharebookEntity) accountEntity.getOwner()).toDomain();
                                if (senderSharebook.getSharebookOwner() instanceof User) {
                                    senderUser = (User) senderSharebook.getSharebookOwner();
                                } else if (senderSharebook.getSharebookOwner() instanceof Community) {
                                    senderCommunity = (Community) senderSharebook.getSharebookOwner();
                                }
                            }
                            senderAccountType = accountEntity.getType().toDomain();

                        }
                    }
                    HashMap<String, Object> scriptVarsSender = new HashMap<>();
                    scriptVarsSender.put("accountType", senderAccountType.getName());
                    scriptVarsSender.put("senderType", "");
                    scriptVarsSender.put("senderName", "");
                    scriptVarsSender.put("senderLink", "");
                    if (senderUser != null) {
                        scriptVarsSender.put("senderType", "пользователя:");
                        scriptVarsSender.put("senderName", senderUser.getName());
                        scriptVarsSender.put("senderLink", senderUser.getLink());
                    } else if (senderCommunity != null) {
                        scriptVarsSender.put("senderType", "сообщества:");
                        scriptVarsSender.put("senderName", senderCommunity.getName());
                        scriptVarsSender.put("senderLink", senderCommunity.getLink());
                    }
                    for (TransactionDetail transactionDetail : transaction.getDetails()) {
                        if (transactionDetail.getType() == TransactionDetailType.CREDIT) {
                            AccountEntity accountEntity = accountRepository.findOne(transactionDetail.getAccountId());
                            if (accountEntity.getOwner() instanceof UserEntity) {
                                HashMap<String, Object> scriptVarsForReceiver = new HashMap<>();
                                scriptVarsForReceiver.putAll(scriptVarsSender);
                                if (senderUser != null) {
                                    if (((UserEntity) accountEntity.getOwner()).getId() == senderUser.getId()) {
                                        scriptVarsForReceiver.put("senderType", "");
                                        scriptVarsForReceiver.put("senderName", "");
                                        scriptVarsForReceiver.put("senderLink", "");
                                    }
                                }
                                scriptVarsForReceiver.put("amount", transactionDetail.getAmount());
                                scriptVarsForReceiver.put("receiverAccountType", accountRepository.findOne(transactionDetail.getAccountId()).toDomain().getType().getName());
                                User recipient = ((UserEntity) accountEntity.getOwner()).toDomain();
                                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(recipient, senderUser);
                                sendNotification("transaction.saved", recipient, senderUser, parameters, NotificationPriority.NORMAL, notificationLinks, scriptVarsForReceiver);
                                sendSms(senderUser, recipient, transaction);
                            }
                        }
                    }

                    /*if (transaction.getDocumentFolder().getDocuments() != null) {
                        for (Document document : transaction.getDocumentFolder().getDocuments()) {
                            List<UserEntity> signers = getSignersFromDocument(documentRepository.findOne(document.getId()));

                            for (UserEntity signer : signers) {
                             //   recipients.add(signer.toDomain());//TODO
                            }
                        }
                    }*/

                    /*for (User recipient : recipients) {
                        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(recipient, sender);
                        sendNotification("transaction.saved", recipient, sender, parameters, NotificationPriority.NORMAL, notificationLinks, null);
                        sendSms(sender, recipient, transaction);
                    }*/
                }

                break;
            }
            case REJECT:
                break;
        }
    }

    private void onRegistrationEvent(RegistrationEvent registrationEvent) {
        switch (registrationEvent.getType()) {
            case VERIFICATION_REQUEST: { // Пришла заявка регистратору
                if (registrationEvent.getObject() instanceof User) { // От участника
                    User receiver = registrationEvent.getRegistrator();
                    User sender = (User) registrationEvent.getObject();
                    List<NotifyLink> notificationLinks = Arrays.asList(
                            new NotifyLink("К списку заявок", "/registrator/requests?object_type=" + Discriminators.SHARER, false, true, NotificationLinkType.PRIMARY, 1, "registratorRequestsLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    sendNotification("registration.verification-request", receiver, sender,
                            createStandardTemplateParameters(receiver, sender),
                            NotificationPriority.NORMAL, notificationLinks, null);
                } else if (registrationEvent.getObject() instanceof CommunityEntity) { // От объединения
                    User receiver = registrationEvent.getRegistrator();
                    Community community = (Community) registrationEvent.getObject();
                    List<NotifyLink> notificationLinks = Arrays.asList(
                            new NotifyLink("К списку заявок", "/registrator/requests?object_type=" + Discriminators.COMMUNITY, false, true, NotificationLinkType.PRIMARY, 1, "registratorRequestsLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    sendNotification("registration-organization.verification-request", receiver, community,
                            createStandardTemplateParameters(receiver, null, community),
                            NotificationPriority.NORMAL, notificationLinks, null);
                }
                break;
            }
            case CANCEL_REQUEST: { // Регистратор отказал в регистрации
                if (registrationEvent.getObject() instanceof UserEntity) { // От участника
                    User receiver = (User) registrationEvent.getObject();
                    User sender = registrationEvent.getRegistrator();
                    List<NotifyLink> notificationLinks = Arrays.asList(
                            new NotifyLink("К выбору регистратора", "/registrator/select", false, true, NotificationLinkType.PRIMARY, 1, "selectRegistratorLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    List<UserFieldValue> userFieldValues = Collections.singletonList(
                            UserFieldValueBuilder.createStringValue("Комментарий", registrationEvent.getComment())
                    );
                    List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, userFieldValues, null, null);
                    sendNotification("registration.cancel-request", receiver, sender, parameters,
                            NotificationPriority.NORMAL, notificationLinks, null);
                } else if (registrationEvent.getObject() instanceof CommunityEntity) { // От объединения
                    Community community = (Community) registrationEvent.getObject();
                    User receiver = communitiesService.getCommunityDirector(community);
                    User sender = registrationEvent.getRegistrator();
                    List<NotifyLink> notificationLinks = Arrays.asList(
                            new NotifyLink("К выбору регистратора", community.getLink() + "/registrator/select", false, true, NotificationLinkType.PRIMARY, 1, "selectRegistratorLink"),
                            new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                    );
                    List<UserFieldValue> userFieldValues = Collections.singletonList(
                            UserFieldValueBuilder.createStringValue("Комментарий", registrationEvent.getComment())
                    );
                    List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                    sendNotification("registration-organization.cancel-request", receiver, sender, parameters,
                            NotificationPriority.NORMAL, notificationLinks, null);
                }
                break;
            }
        }
    }

    private NotificationSender getSenderFromDocumentCreator(DocumentCreator documentCreator) {
        NotificationSender result = null;
        if (documentCreator instanceof User) {
            result = (User) documentCreator;
        } else if (documentCreator instanceof SystemAccount) {
            result = (SystemAccount) documentCreator;
        }
        return result;
    }

    private void onRameraFlowOfDocumentEvent(RameraFlowOfDocumentEvent flowOfDocumentEvent) {
        switch (flowOfDocumentEvent.getDocumentEventType()) {
            case FILL_USER_FIELDS: { // Переход на страницу заполнения пользовательских полей
                User receiver = flowOfDocumentEvent.getUser();
                NotificationSender sender = getSenderFromDocumentCreator(flowOfDocumentEvent.getCreator());
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти к документу для заполнения полей", flowOfDocumentEvent.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "documentLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createDocumentValue("Наименование документа", flowOfDocumentEvent.getDocument().getId())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, userFieldValues, null, null, null);
                sendNotification("document.fill.user.fields", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case SIGN_DOCUMENT: { // Переход на страницу подписания документа
                User receiver = flowOfDocumentEvent.getUser();
                NotificationSender sender = getSenderFromDocumentCreator(flowOfDocumentEvent.getCreator());
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти к документу для подписания", flowOfDocumentEvent.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "documentLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createDocumentValue("Наименование документа", flowOfDocumentEvent.getDocument().getId())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, userFieldValues, null, null, null);
                sendNotification("document.sign", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case UNSIGN_DOCUMENT: { // Документ был отклонён от подписи и стал недействителен
                User receiver = flowOfDocumentEvent.getUser();
                NotificationSender sender = getSenderFromDocumentCreator(flowOfDocumentEvent.getCreator());
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти к документу", flowOfDocumentEvent.getLink(), false, true, NotificationLinkType.PRIMARY, 1, "documentLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                List<UserFieldValue> userFieldValues = Collections.singletonList(
                        UserFieldValueBuilder.createDocumentValue("Наименование документа", flowOfDocumentEvent.getDocument().getId())
                );
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, userFieldValues, null, null, null);
                sendNotification("document.unsign", receiver, sender, parameters,
                        NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
        }
    }

    private void handleVoting(Voting voting, LocalDateTime eventDate) {
        User sender = sharerDao.getById(voting.getOwnerId()).toDomain();
        for (Long voterId : voting.getParameters().getVotersAllowed()) {
            User receiver = sharerDao.getById(voterId).toDomain();
            if (sender == null || receiver == null) continue;
            onVotingEventOneVoter(voting, sender, receiver, eventDate);
        }
    }

    private String getFailReason(Voting voting) {
        String failReason = "";
        switch (voting.getResult().getResultType()) {
            case INVALID_NO_VOTES:
                failReason = "нет голосов";
                break;
            case INVALID_NO_QUORUM:
                failReason = "недостаточно голосов";
                break;
            case INVALID_DEAD_HEAT:
                failReason = "ничья";
                break;
            case INVALID_OUT_OF_DATE_RANGE:
                failReason = "завершено до даты начала или до даты окончания если запрещено досрочное завершение";
                break;
            case INVALID_WRONG_RESULT:
                failReason = "большинство проголосовало против";
                break;
        }
        return failReason;
    }

    private void onVotingEventOneVoter(Voting voting, User sender, User receiver, LocalDateTime eventDate) {
        String link = null;
        String descriptionLink = null;
        String template = null;
        NotificationLinkType linkType = NotificationLinkType.INFO;
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        //Отправитель:Тема голосования
        userFieldValues.add(UserFieldValueBuilder.createStringValue("Тема голосования", voting.getSubject()));
        switch (voting.getState()) {
            case NEW:
                template = "voting-new";
                break;
            case ACTIVE:
                // Голосование было перезапущено
                if (voting.getAdditionalData().containsKey(AdditionalDataConstants.RESTART_COUNT_KEY)) {
                    link = "/votingsystem/votingPage.html?votingId=" + voting.getId();
                    descriptionLink = "Перейти к участию в голосовании";
                    template = "voting-restart";
                    String failReason = getFailReason(voting);
                    userFieldValues.add(UserFieldValueBuilder.createStringValue("Причина", failReason));
                } else {
                    link = "/votingsystem/votingPage.html?votingId=" + voting.getId();
                    descriptionLink = "Перейти к участию в голосовании";
                    template = "voting-active";
                }
                break;
            case PAUSED:
                template = "voting-paused";
                break;
            case FINISHED:
                if (voting.getResult().getResultType() == VotingResultType.VALID) {
                    template = "voting-finished";
                    link = "/votingsystem/votingPage.html?votingId=" + voting.getId();
                    descriptionLink = "Посмотреть результаты голосования";
                    linkType = NotificationLinkType.PRIMARY;
                } else {
                    template = "voting-failed";
                    String failReason = getFailReason(voting);
                    userFieldValues.add(UserFieldValueBuilder.createStringValue("Причина", failReason));
                }

                break;
        }

        if (template == null) return;
        HashMap<String, Object> scriptVars = new HashMap<>();
        try {
            BatchVoting batchVoting = batchVotingService.getBatchVotingByVotingId(voting.getId(), true, true);
            Long communityId = Long.parseLong(batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME));
            Community community = communityDataService.getByIdFullData(communityId);
            scriptVars.put("community", community);
            scriptVars.put("batchVotingSubject", batchVoting.getSubject());
            scriptVars.put("batchVotingLink", "/votingsystem/registrationInVoting.html?batchVotingId=" + batchVoting.getId());
        } catch (Exception e) {
            scriptVars.put("community", new Community());
            scriptVars.put("batchVotingSubject", "");
            scriptVars.put("batchVotingLink", "");
        }
        scriptVars.put("votingLink", link);
        scriptVars.put("votingDescription", voting.getAdditionalData().get(BatchVotingConstants.VOTING_DESCRIPTION));
        List<NotifyLink> notificationLinks = new ArrayList<>();
        if (link != null && descriptionLink != null) {
            notificationLinks.add(new NotifyLink(descriptionLink, link, false, true, linkType, 1, "votingLink"));
            notificationLinks.add(new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2));
        } else {
            notificationLinks.add(new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1));
        }
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, userFieldValues, null, null);

        Map<String, String> notificationData = new HashMap<>();

        try {
            BatchVoting batchVoting = batchVotingService.getBatchVotingByVotingId(voting.getId(), false, false);
            notificationData.put("batchVotingId", String.valueOf(batchVoting.getId()));
        } catch (VotingSystemException e) {
            e.printStackTrace();
        }

        sendNotification(template, receiver, sender, parameters, NotificationPriority.NORMAL, notificationLinks, scriptVars, ru.askor.blagosfera.core.util.DateUtils.toDate(eventDate),
                NotificationType.VOTING, notificationData);
    }

    private void handleBatchVoting(BatchVoting batchVoting, LocalDateTime eventDate) {
        User sender = sharerDao.getById(batchVoting.getOwnerId()).toDomain();
        for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
            User receiver = sharerDao.getById(registeredVoter.getVoterId()).toDomain();
            if (sender == null || receiver == null) continue;
            onBatchVotingEventOneVoter(batchVoting, sender, receiver, eventDate);
        }
    }

    private void onBatchVotingEventOneVoter(BatchVoting batchVoting, User sender, User receiver, LocalDateTime eventDate) {
        Community community = null;
        String link = null;
        String descriptionLink = null;
        String template = null;
        //Отправитель:Тема собрания
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue("Тема собрания", batchVoting.getSubject()));
        switch (batchVoting.getState()) {
            case NEW:
                template = "voting-batch-new";
                break;
            case VOTERS_REGISTRATION:
                link = "/votingsystem/registrationInVoting.html?batchVotingId=" + batchVoting.getId();
                descriptionLink = "Перейти к регистрации в собрании";
                template = "voting-batch-voters-registration";
                break;
            case VOTING:
                template = "voting-batch-voting";
                break;
            case FINISHED:
                Voting failedVoting = batchVotingService.getFirstFailedVoting(batchVoting);

                if (failedVoting == null) {
                    template = "voting-batch-finished";
                } else {
                    template = "voting-batch-failed";
                    userFieldValues.add(UserFieldValueBuilder.createStringValue("Тема голосования", failedVoting.getSubject()));
                }

                break;
        }

        if (template == null) return;
        HashMap<String, Object> scriptVars = new HashMap<>();
        scriptVars.put("batchVotingDescription", batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME));
        try {
            if (batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME) != null) {
                Long communityId = Long.parseLong(batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME));
                community = communityDataService.getByIdFullData(communityId);
                scriptVars.put("community", community);
            } else {
                scriptVars.put("community", new Community());
            }
        } catch (NumberFormatException e) {
            //такого не должно быть
            scriptVars.put("community", new Community());
        }

        List<NotifyLink> notificationLinks = new ArrayList<>();
        if (link != null && descriptionLink != null) {
            notificationLinks.add(new NotifyLink(descriptionLink, link, false, true, NotificationLinkType.INFO, 1, "batchVotingLink"));
            notificationLinks.add(new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2));
        } else {
            notificationLinks.add(new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1));
        }
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, userFieldValues, null, null);

        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("batchVotingId", String.valueOf(batchVoting.getId()));

        sendNotification(template, receiver, sender, parameters, NotificationPriority.NORMAL, notificationLinks, scriptVars, ru.askor.blagosfera.core.util.DateUtils.toDate(eventDate),
                NotificationType.VOTING, notificationData);
    }

    private void onVoterBatchVotingEvent(VoterVotingEvent voterBatchVotingEvent) {
        if (voterBatchVotingEvent.getSender() != null && voterBatchVotingEvent.getReceiver() != null && voterBatchVotingEvent.getBatchVoting() != null) {
            onBatchVotingEventOneVoter(voterBatchVotingEvent.getBatchVoting(), voterBatchVotingEvent.getSender(), voterBatchVotingEvent.getReceiver(), voterBatchVotingEvent.getCreatedDate());
        } else if (voterBatchVotingEvent.getSender() != null && voterBatchVotingEvent.getReceiver() != null && voterBatchVotingEvent.getVoting() != null) {
            onVotingEventOneVoter(voterBatchVotingEvent.getVoting(), voterBatchVotingEvent.getSender(), voterBatchVotingEvent.getReceiver(), voterBatchVotingEvent.getCreatedDate());
        }
    }

    private void onVoterErrorEvent(VoterErrorEvent voterErrorEvent) {
        // TODO Нужен ли шаблон для почты?
        User receiver = voterErrorEvent.getVoter();
        User sender = voterErrorEvent.getVoter();
        List<UserFieldValue> userFieldValues = Collections.singletonList(
                UserFieldValueBuilder.createStringValue("Контент уведомления", voterErrorEvent.getError())
        );
        List<NotifyLink> notificationLinks = Collections.singletonList(
                new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
        );
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, null, null, null, userFieldValues, null, null);
        sendNotification("voting", receiver, sender, parameters, NotificationPriority.CRITICAL, notificationLinks, null);
    }

    private void onSharerEvent(SharerEvent sharerEvent) {
        User receiver = sharerEvent.getUser();
        String typeString = sharerEvent.getType().toString();
        Map<String, Object> receiverObject = sharerService.convertUserToSend(receiver);

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharerEvent",
                "receiver", receiverObject,
                "sender", receiverObject,
                "type", typeString
        ));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer" + typeString + "Event",
                "receiver", receiverObject,
                "sender", receiverObject
        ));

        switch (sharerEvent.getType()) {
            case ACTIVATE: { // Аккаунт активирован
                sendNotification("notify.on-activate", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case REGISTER: { // Аккаунт зарегистрирован
                sendNotification("notify.on-register", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case CHANGE_PASSWORD: { // Смена пароля
                sendNotification("notify.on-change-password", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case CHANGE_EMAIL_INIT: { // Смена e-mail
                sendNotification("notify.on-init-change-email", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case CHANGE_EMAIL_COMPLETE: { // Заверщена смена e-mail
                sendNotification("notify.on-complete-change-email", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case RECOVERY_VERIFICATION_CODE_INIT: { //Восстановление проверочного кода
                sendNotification("notify.on-init-verification-code-recovery", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case RECOVERY_PASSWORD_INIT: { // Восстановление пароля
                sendNotification("notify.on-init-password-recovery", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            case RECOVERY_PASSWORD_COMPLETE: { // Пароль восстановлен
                sendNotification("notify.on-complete-password-recovery", receiver, receiver,
                        createStandardTemplateParameters(receiver, receiver), NotificationPriority.NORMAL, null, null);
                break;
            }
            default:
                break;
        }
    }

    /**
     * Событие удаления \ блокировки профиля
     *
     * @param deletionNotificationSharerEvent
     */
    private void onDeletionNotificationSharerEvent(DeletionNotificationSharerEvent deletionNotificationSharerEvent) {
        User receiver = deletionNotificationSharerEvent.getUser();
        ProfileFilling profileFilling = deletionNotificationSharerEvent.getProfileFilling();

        // Получаем количество часов перед удалением профиля
        String countDaysBeforeDeleteProfile = settingsManager.getSystemSetting("profile.not-filled.deletion-days", "3");
        Integer countHoursBeforeDeleteProfile = VarUtils.getInt(countDaysBeforeDeleteProfile, 3) * 24;

        String deletedOrBlockingForm1 = "";
        String deletedOrBlockingForm2 = "";
        if (profileFilling.getHoursBeforeDeletion() <= countHoursBeforeDeleteProfile) {
            deletedOrBlockingForm1 = "удален";
            deletedOrBlockingForm2 = "удаления";
        } else {
            deletedOrBlockingForm1 = "заблокирован";
            deletedOrBlockingForm2 = "блокировки";
        }

        List<UserFieldValue> userFieldValues = Arrays.asList(
                UserFieldValueBuilder.createIntegerValue("Процент заполнения профиля", profileFilling.getPercent()),
                UserFieldValueBuilder.createIntegerValue("Необходимый процент заполнения профиля", profileFilling.getTreshold()),
                UserFieldValueBuilder.createStringValue("Время до удаления", DateUtils.getHumanReadableDistanceAccusative(profileFilling.getHoursBeforeDeletion())),
                UserFieldValueBuilder.createStringValue("Дата удаления профиля", DateUtils.formatDate(profileFilling.getDeletionDate(), "dd.MM.yyyy HH:mm")),
                UserFieldValueBuilder.createStringValue("Удален или заблокирован", deletedOrBlockingForm1),
                UserFieldValueBuilder.createStringValue("Удаления или блокировки", deletedOrBlockingForm2)

        );
        List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, receiver, null, null, userFieldValues, null, null, null);
        sendNotification("notify.on-deletion-notification", receiver, receiver,
                parameters, NotificationPriority.NORMAL, null, null);
    }

    /**
     * Оповещение о доверенностях
     *
     * @param letterOfAuthorityEvent
     */
    private void onLetterOfAuthorityEvent(LetterOfAuthorityEvent letterOfAuthorityEvent) {
        Date expiredDate = DateUtils.parseDate(letterOfAuthorityEvent.getLetterOfAuthorityDto().getExpiredDate(), new Date());
        List<UserFieldValue> userFieldValues = Arrays.asList(
                UserFieldValueBuilder.createStringValue("доверенность", letterOfAuthorityEvent.getLetterOfAuthorityDto().getAuthorityRole().getName()),
                UserFieldValueBuilder.createDateValue("дата истечения доверенности", expiredDate)
        );
        switch (letterOfAuthorityEvent.getLetterOfAuthorityEventType()) {
            case NOTIFY_OWNER: {
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти на страницу с доверенностями", "/letterofauthority/ownerLetterOfAuthority.html", false, true, NotificationLinkType.INFO, 1, "letterOfAuthoritiesLink"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                UserEntity receiver = sharerDao.getById(letterOfAuthorityEvent.getLetterOfAuthorityDto().getOwner().getId());
                UserEntity sender = sharerDao.getById(letterOfAuthorityEvent.getLetterOfAuthorityDto().getDelegate().getId());
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver.toDomain(), sender.toDomain(), null, null, userFieldValues, null, null, null);
                sendNotification("letterofauthority.on.expired.date.owner", receiver.toDomain(), sender.toDomain(),
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case NOTIFY_DELEGATE: {
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                UserEntity receiver = sharerDao.getById(letterOfAuthorityEvent.getLetterOfAuthorityDto().getDelegate().getId());
                UserEntity sender = sharerDao.getById(letterOfAuthorityEvent.getLetterOfAuthorityDto().getOwner().getId());
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver.toDomain(), sender.toDomain(), null, null, userFieldValues, null, null, null);
                sendNotification("letterofauthority.on.expired.date.delegate", receiver.toDomain(), sender.toDomain(),
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
        }
    }

    private void onOrganizationCommunityMemberEvent(OrganizationCommunityMemberEvent organizationCommunityMemberEvent) {
        List<UserFieldValue> userFieldValues = Collections.singletonList(
                UserFieldValueBuilder.createStringValue("Организация", organizationCommunityMemberEvent.getOrganization().getFullRuName())
        );
        Community community = organizationCommunityMemberEvent.getCommunity();
        OrganizationCommunityMember member = organizationCommunityMemberEvent.getMember();
        switch (organizationCommunityMemberEvent.getEventType()) {
            case REQUEST: {// Событие запроса на вступление в объединение
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Принять", "/communities/accept_organization_request.json?member_id=" + member.getId(), true, true, NotificationLinkType.SUCCESS, 1),
                        new NotifyLink("Отклонить", "/communities/reject_organization_request.json?member_id=" + member.getId(), true, true, NotificationLinkType.DANGER, 2),
                        new NotifyLink("Страница запросов", community.getLink() + "/requests/incoming", false, true, NotificationLinkType.INFO, 3, "requestsPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 4)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.request", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REJECT_REQUEST: { // Событие отказа в запросе на вступление
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.reject_request", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case ACCEPT_TO_JOIN: { // Принятие в объедиение
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в объединение", community.getLink(), false, true, NotificationLinkType.INFO, 1, "communityPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.accept_to_join", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case EXCLUDE: { // Руководство объедиения исключило организацию
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.exclude", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }

            case REQUEST_TO_PO: { // Событие запроса на вступление в ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Страница принятия пайщиков", community.getLink() + "/approve_sharers", false, true, NotificationLinkType.INFO, 1, "approveSharersPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.request_to_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REJECT_REQUEST_TO_PO: {// Событие отказа в запросе на вступление в ПО
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.reject_request_to_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case ACCEPT_TO_JOIN_IN_PO: {// Принятие в ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в ПО", community.getLink(), false, true, NotificationLinkType.INFO, 1, "communityPOPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.accept_to_join_in_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REQUEST_TO_EXCLUDE_FROM_PO: {// Создан запрос на выход из ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Страница вывода пайщиков", community.getLink() + "/leave_sharers", false, true, NotificationLinkType.INFO, 1, "leaveSharersPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.request_to_exclude_from_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case ACCEPT_TO_EXCLUDE_FROM_PO: {// Организацию исключили из ПО
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.accept_to_exclude_from_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }


            case REQUEST_TO_KUCH_PO: { // Событие запроса на вступление в КУч ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Страница принятия пайщиков", community.getLink() + "/approve_sharers", false, true, NotificationLinkType.INFO, 1, "approveSharersPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.request_to_kuch_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REJECT_REQUEST_TO_KUCH_PO: {// Событие отказа в запросе на вступление в КУч ПО
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.reject_request_to_kuch_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case ACCEPT_TO_JOIN_IN_KUCH_PO: {// Принятие в КУч ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Перейти в КУч ПО", community.getLink(), false, true, NotificationLinkType.INFO, 1, "communityPOPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.accept_to_join_in_kuch_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case REQUEST_TO_EXCLUDE_FROM_KUCH_PO: {// Создан запрос на выход из КУч ПО
                List<NotifyLink> notificationLinks = Arrays.asList(
                        new NotifyLink("Страница вывода пайщиков", community.getLink() + "/leave_sharers", false, true, NotificationLinkType.INFO, 1, "leaveSharersPage"),
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 2)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.request_to_exclude_from_kuch_po",
                        receiver, sender, parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
            case ACCEPT_TO_EXCLUDE_FROM_KUCH_PO: {// Организацию исключили из КУч ПО
                List<NotifyLink> notificationLinks = Collections.singletonList(
                        new NotifyLink("Скрыть", null, false, true, NotificationLinkType.DEFAULT, 1)
                );
                User receiver = sharerDao.getById(organizationCommunityMemberEvent.getOrganization().getCreator().getId()).toDomain();
                User sender = sharerDao.getById(organizationCommunityMemberEvent.getCommunity().getCreator().getId()).toDomain();
                List<CreateDocumentParameter> parameters = createStandardTemplateParameters(receiver, sender, community, null, null, userFieldValues, null, null);
                sendNotification("organizationcommunitymember.on.accept_to_exclude_from_kuch_po", receiver, sender,
                        parameters, NotificationPriority.NORMAL, notificationLinks, null);
                break;
            }
        }
    }

    private class NotificationTask implements Runnable {

        private String templateCode;
        private User receiver;
        private NotificationSender sender;
        private final List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        private NotificationPriority priority;
        private final List<NotifyLink> notificationLinks = new ArrayList<>();
        private final Map<String, Object> scriptVars = new HashMap<>();
        private Date eventDate;
        private NotificationType notificationType;
        private final Map<String, String> notificationData = new HashMap<>();

        public NotificationTask(String templateCode, User receiver, NotificationSender sender,
                                List<CreateDocumentParameter> createDocumentParameters,
                                NotificationPriority priority, List<NotifyLink> notificationLinks,
                                Map<String, Object> scriptVars, Date eventDate,
                                NotificationType notificationType, Map<String, String> notificationData) {
            this.templateCode = templateCode;
            this.receiver = receiver;
            this.sender = sender;
            if (createDocumentParameters != null) this.createDocumentParameters.addAll(createDocumentParameters);
            this.priority = priority;
            if (notificationLinks != null) this.notificationLinks.addAll(notificationLinks);
            if (scriptVars != null) this.scriptVars.putAll(scriptVars);
            this.eventDate = eventDate;
            this.notificationType = notificationType;
            if (notificationData != null) this.notificationData.putAll(notificationData);
        }

        @Override
        public void run() {
            Notification notification = new Notification();
            notification.setUser(receiver);
            notification.setSender(sender);
            notification.setPriority(priority);
            notification.setType(notificationType);
            notification.getData().putAll(notificationData);
            notification.setDate(eventDate);

            if (notificationLinks != null) {
                for (NotifyLink notifyLink : notificationLinks) {
                    notification.getLinks().add(notifyLink);

                    if (notifyLink.getCode() != null && notifyLink.getUrl() != null) {
                        scriptVars.put(notifyLink.getCode(), notifyLink.getUrl());
                    }
                }
            }

            //Генерируем push уведомление
            /*try {
                gcmService.sendNotificationToSharer(notification, receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            if (NotificationType.VOTING == notificationType) {
                if (!rosterService.isUserAuthenticated(receiver.getEmail())) {
                    sendEmailNotification();
                }

                sendSystemNotification(notification, scriptVars);
            } else {
                sendSystemNotification(notification, scriptVars);
                sendEmailNotification();
            }
        }

        private void sendSystemNotification(Notification notification, Map<String, Object> scriptVars) {
            try {
                FlowOfDocumentDTO notificationFlowOfDocumentDTO = documentService.generateDocumentDTO(templateCode, createDocumentParameters, scriptVars);

                if (notificationFlowOfDocumentDTO != null) {
                    notification.setSubject(notificationFlowOfDocumentDTO.getHtmlDecodedName());
                    notification.setShortText(notificationFlowOfDocumentDTO.getHtmlDecodedContent());
                    notificationService.addNotification(notification);
                }
            } catch (FlowOfDocumentException e) {
                if (e.getExceptionType() != FlowOfDocumentExceptionType.NOT_FOUND_TEMLATE) {
                    throw e;
                }
            }
        }

        private void sendEmailNotification() {
            try {
                scriptVars.put("receiver", receiver);
                scriptVars.put("sender", sender);
                scriptVars.put("applicationUrl", systemSettingsService.getApplicationUrl());
                FlowOfDocumentDTO emailFlowOfDocumentDTO = documentService.generateDocumentDTO(EMAIL_TEMPLATE_PREFIX + templateCode, createDocumentParameters, scriptVars);

                if (emailFlowOfDocumentDTO != null) {
                    emailService.sendTo(receiver, emailFlowOfDocumentDTO.getHtmlDecodedName(), emailFlowOfDocumentDTO.getContent(), scriptVars);
                }
            } catch (FlowOfDocumentException e) {
                if (e.getExceptionType() != FlowOfDocumentExceptionType.NOT_FOUND_TEMLATE) {
                    throw e;
                }
            }
        }
    }
}
