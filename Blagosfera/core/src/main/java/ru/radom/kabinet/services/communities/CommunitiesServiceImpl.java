package ru.radom.kabinet.services.communities;

import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsSubscribeRepository;
import ru.askor.blagosfera.data.jpa.services.settings.SystemSettingService;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.events.community.CommunityEvent;
import ru.askor.blagosfera.domain.events.community.CommunityMemberAppointEvent;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.community.CommunityOtherEvent;
import ru.askor.blagosfera.domain.events.file.ImagesEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.dao.DisallowedWordDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.communities.CommunityPermissionDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.DisallowedType;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.dto.CommunityFillingDto;
import ru.radom.kabinet.model.communities.postappointbehavior.IPostAppointBehavior;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.DefaultPostAppointBehavior;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.PostAppointData;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.SharebookService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.utils.BreadcrumbItem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Maxim Nikitin on 01.03.2016.
 */
@Transactional
@Service("communitiesService")
public class CommunitiesServiceImpl implements CommunitiesService {
    @Autowired
    private InvitationDataService invitationDataService;
    @Autowired
    private SystemSettingService systemSettingService;
    @Autowired
    private NewsSubscribeRepository newsSubscribeRepository;
    @Autowired
    private ProfileService profileService;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityPostDomainService communityPostDomainService;

    @Autowired
    private CommunityPermissionDao communityPermissionDao;

    @Autowired
    private CommunityPermissionDomainService communityPermissionService;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private DisallowedWordDao disallowedWordDao;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @Autowired
    private CommunityFillingService communityFillingService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SharerService sharerService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SharebookService sharebookService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestService;
    
    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Autowired
    private UserRepository userRepository;

    private ApplicationContext applicationContext;

    // ?TODO Переделать на BPMHandler
    /*@RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "core.community.appoint.request.sharer.to.post", durable = "true"),
            exchange = @Exchange(value = "task-exchange", durable = "true"),
            key = "core.community.appoint.request.sharer.to.post"
    ))
    @Override
    public void requestAppointWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Long memberId = MapUtils.getLong(data, "memberId");
            Community community = tryGetCommunity(data.get("community"));
            CommunityMember member;
            if (memberId == null) {
                User user = sharerService.tryGetUser(data.get("sharer"));
                member = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId());
                memberId = member.getId();
            } else {
                member = communityMemberDomainService.getByIdFullData(memberId);
            }
            User appointer = sharerService.tryGetUser(data.get("appointer"));
            CommunityPost post = tryGetCommunityPost(community, data.get("post"));
            requestToAppoint(appointer, member, post);
            return "";
        });
    }*/

    // ?TODO Переделать на BPMHandler
    /*@RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "core.community.appoint.sharer.to.post", durable = "true"),
            exchange = @Exchange(value = "task-exchange", durable = "true"),
            key = "core.community.appoint.sharer.to.post"
    ))
    @Override
    public void appointWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Long memberId = MapUtils.getLong(data, "memberId");
            Community community = tryGetCommunity(data.get("community"));
            CommunityMember member;
            if (memberId == null) {
                User user = sharerService.tryGetUser(data.get("sharer"));
                member = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId());
                memberId = member.getId();
            } else {
                member = communityMemberDomainService.getByIdFullData(memberId);
            }
            User appointer = sharerService.tryGetUser(data.get("appointer"));
            CommunityPost post = tryGetCommunityPost(community, data.get("post"));
            appoint(appointer, member, post);
            return "";
        });
    }*/

    // ?TODO Переделать на BPMHandler
    /*@RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "core.community.disappoint.sharer.from.post", durable = "true"),
            exchange = @Exchange(value = "task-exchange", durable = "true"),
            key = "core.community.disappoint.sharer.from.post"
    ))
    @Override
    public void diappointWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Long memberId = MapUtils.getLong(data, "memberId");
            Community community = tryGetCommunity(data.get("community"));
            CommunityMember member;
            if (memberId == null) {
                User user = sharerService.tryGetUser(data.get("sharer"));
                member = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId());
                memberId = member.getId();
            } else {
                member = communityMemberDomainService.getByIdFullData(memberId);
            }
            User disappointer = sharerService.tryGetUser(data.get("disappointer"));
            CommunityPost post = tryGetCommunityPost(community, data.get("post"));
            disapoint(disappointer, member, post);
            return "";
        });
    }*/

    @Override
    public Community tryGetCommunity(Object communityObj) {
        Community community;
        if (communityObj instanceof String) {
            community = communityDataService.getBySeoLinkOrIdMediumData((String) communityObj);
        } else if (communityObj instanceof Long) {
            community = communityDataService.getByIdMediumData(((Long) communityObj));
        } else if (communityObj instanceof Map) {
            Map map = (Map) communityObj;
            Long id = MapUtils.getLong(map, "id");
            if (id != null) {
                community = communityDataService.getByIdMediumData(id);
            } else {
                community = null;
            }
        } else {
            community = null;
        }
        return community;
    }

    @Override
    public CommunityPost tryGetCommunityPost(Community community, Object communityPostObj) {
        CommunityPost communityPost;
        if (communityPostObj instanceof String) {
            communityPost = communityPostDomainService.getByName(community, (String) communityPostObj);
            if (communityPost == null) {
                try {
                    Long id = Long.parseLong((String) communityPostObj);
                    communityPost = communityPostDomainService.getByIdFullData(id);
                } catch (NumberFormatException e) {
                    communityPost = null;
                }
            }
        } else if (communityPostObj instanceof Long) {
            communityPost = communityPostDomainService.getByIdFullData(((Long) communityPostObj));
        } else if (communityPostObj instanceof Map) {
            Map map = (Map) communityPostObj;
            Long id = MapUtils.getLong(map, "id");
            if (id != null) {
                communityPost = communityPostDomainService.getByIdFullData(id);
            } else {
                communityPost = null;
            }
        } else {
            communityPost = null;
        }
        return communityPost;
    }

    // Пришлось вынести отдельную функцию. т.к. проверка на уникальность происходит методом communityDao.getBySeoLink,
    // и возвращается то же самое объединение, то их надо замержить...
    // TODO (внезапно): т.к. communityDao.getBySeoLink(seoLink) и communityDao.getByIds(ids) вызывают нежелательное сохранение создаваемой группы,
    // то меняем его на проверку полями, тянем из результата каждое объединение по id и проверяем удалено ли оно.
    private String prepareSeoLink(String seoLink, String parentSeoLink, Long communityId, Long parentCommunityId) {
        //CommunityEntity result = community;

        String hashedSeoLink = IkpUtils.longToIkpHash(MurmurHash.hash64("GROUP" + communityId));
        if (org.apache.commons.lang3.StringUtils.isBlank(seoLink) || seoLink.equals(communityId.toString()) || seoLink.equals(hashedSeoLink)) {
            seoLink = hashedSeoLink;
        } else {
            if (seoLink.length() < 4) {
                throw new CommunityException("Имя ссылки должно состоять минимум из четырёх символов");
            }

            Pattern p = Pattern.compile("^[0-9]+$");
            Matcher m = p.matcher(seoLink);
            if (m.matches()) {
                throw new CommunityException("В имени ссылки запрещается использовать только цифры");
            }

            p = Pattern.compile("[a-zа-я0-9]+");
            m = p.matcher(seoLink);
            if (!m.matches()) {
                throw new CommunityException("В имени ссылки допускаются только русские и латинские строчные символы и цифры без пробелов");
            }

            List<String> disallowedWords = disallowedWordDao.getStringsByType(DisallowedType.COMMUNITY_SHORT_LINK_NAME);
            if (disallowedWords.contains(seoLink)) {
                throw new CommunityException("Недопустимое имя ссылки");
            }
        }

        if (parentCommunityId != null) {
            if ((parentSeoLink == null) || parentSeoLink.equals(""))
                parentSeoLink = String.valueOf(parentCommunityId);
            seoLink = parentSeoLink + "/sg/" + seoLink;
        }

        //-------------------------------------------------------------------
        // сложная и неочевидная проверка на уникальность seoLink (подробности в комментарии над методом)
        FieldEntity field = fieldDao.getByInternalName("COMMUNITY_SHORT_LINK_NAME");
        List<FieldValueEntity> fieldValues = fieldValueDao.getList(field, seoLink);
        if (fieldValues.size() > 0) {
            List<Long> ids = new ArrayList<>();
            for (FieldValueEntity fieldValue : fieldValues) {
                ids.add(fieldValue.getObject().getId());
            }

            for (Long id : ids) {
                Community checkedCommunity = communityDataService.getByIdMinData(id);
                if (checkedCommunity.getId().equals(communityId)) {
                    // значит объединение не меняет свой seoLink
                    // надо замержить, т.к. community и checkedCommunity имеют один и тот же id, но community не приатачен к сессии
                    // TODO Изза мержа не проходят параметры из UI
                    //result = (CommunityEntity) communityDao.getCurrentSession().merge(checkedCommunity);
                    // TODO Не мержим, так удаляем из сессии, а то DuplicateKeyException
                    // DuplicateKeyException возникает, т.к.
                    // communityDao.getById(id) возвращает(и помещает в кеш сессии) объект с таким же id как и у редакируемого объединения
                    // (условие checkedCommunity.equals(community), по сути и возвращается редактируемое объединение)
                    // TODO Переделать
                    /*Session s = communityDao.getCurrentSession();
                    if (s.contains(checkedCommunity)) {
                        s.evict(checkedCommunity);
                    }*/
                    break;
                }
                if (!checkedCommunity.isDeleted()) {
                    // значит что существует не удалённое объединение с таким же seoLink
                    throw new CommunityException("Такое имя ссылки уже есть");
                }
            }
        }
        //-------------------------------------------------------------------

        return seoLink;
    }

    /*@Override
    public CommunityEntity createCommunity(CommunityEntity community, Map<FieldEntity, String> fieldsMap, Map<Long, List<FieldFileEntity>> fieldsFiles, Sharer creator, Sharer ceo, List<Sharer> members, Set<Sharer> receiversNotification) {
        community = createCommunity(community, fieldsMap, fieldsFiles, creator, ceo, members);
        // Создаём событие - создано объединение
        blagosferaEventPublisher.publishEvent(new CommunityOtherEvent(this, CommunityEventType.CREATED, community, receiversNotification));
        return community;
    }

    @Override
    public CommunityEntity createCommunity(CommunityEntity community, Map<FieldEntity, String> fieldsMap, Map<Long, List<FieldFileEntity>> fieldsFiles, Sharer creator, Sharer ceo, List<Sharer> members) {
        if (!community.isRoot()) {
            checkPermission(community.getParent(), creator, "SUBGROUP_CREATE", "У Вас нет прав на создание подгрупп");
        }

        check(community.isRoot() && ceo != null, "В объединении только создатель может быть руководителем");
        community.setCreator(creator);
        if (community.getCreatedAt() == null) {
            community.setCreatedAt(new Date());
        }
        if (community.getAvatarUrl() == null) {
            community.setAvatarUrl(CommunityEntity.DEFAULT_AVATAR_URL);
        }

        if (community.getParent() != null) {
            if (community.getParent().getRoot() != null) {
                community.setRoot(community.getParent().getRoot());
            } else {
                community.setRoot(community.getParent());
            }
        }

        communityDao.save(community);

        if ((community.getSeoLink() == null) || community.getSeoLink().equals("")) {
            community.setSeoLink(String.valueOf(community.getId()));
        }

        community = checkAndUpdateSeoLink(community);
        communityDao.update(community);

        if (community.getParent() == null) {
            addMember(community, creator, true, true);
        } else {
            if (ceo != null) {
                addMember(community, ceo, false, true);
            }
        }
        if (members != null) {
            members.remove(ceo);
            for (Sharer member : members) {
                addMember(community, member, false, false);
            }
        }

        // Сохраняем значения полей в объединении
        if (fieldsMap != null) {
            community = fieldsService.saveFields(fieldsMap, community);
        }
        // Сохраняем файлы полей объединения
        if (fieldsFiles != null) {
            for (Long fieldId : fieldsFiles.keySet()) {
                List<FieldFileEntity> fieldFiles = fieldsFiles.get(fieldId);
                FieldEntity field = fieldDao.loadById(fieldId);
                fieldsService.saveFieldFiles(field, fieldFiles, community);
            }
        }

        return community;
    }*/

    @Override
    public Community createCommunity(Community community, User creator, List<User> users) {
        if (!community.isRoot()) {
            checkPermission(community.getParent().getId(), creator, CommunityPermissions.SUBGROUP_CREATE, "У Вас нет прав на создание подгрупп");
        }
        community = communityDataService.save(community, creator);

        if (community.getParent() == null) {
            addMember(community, creator, true, true);
        } else {
            addMember(community, creator, false, true);
        }
        if (users != null) {
            for (User user : users) {
                addMember(community, user, false, false);
            }
        }
        return community;
    }

    @Override
    public Community createCommunity(Community community, User creator, List<User> users, List<User> receiversNotification) {
        community = createCommunity(community, creator, users);
        blagosferaEventPublisher.publishEvent(new CommunityOtherEvent(this, CommunityEventType.CREATED, community, receiversNotification));
        return community;
    }

    @Override
    public Community editCommunity(Community community, User editor) {
        check(community.isVerified(), "Нельзя изменять данные у сертифицированной организации");
        checkPermission(community.getId(), editor, CommunityPermissions.SETTINGS_COMMON, "У Вас нет прав на редактирование объединения");
        community = communityDataService.save(community, editor);
/*
        community = checkAndUpdateSeoLink(community);
        updateChildrenSeoLinks(community);*/

        return community;
    }

    private void addMember(Community community, User user, boolean isCreator, boolean isCeo) {
        boolean existsMember = communityMemberDomainService.exists(community.getId(), user.getId());
        if (!existsMember) {
            check(communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId()) != null, "Данный участник уже состоит в объединении");
            CommunityMember communityMember = new CommunityMember();
            communityMember.setCommunity(community);
            communityMember.setCreator(isCreator);
            communityMember.setUser(user);
            communityMember.setStatus(CommunityMemberStatus.MEMBER);


            if (isCeo) {
                CommunityPost ceoPost = getCeoPost(community);
                check(ceoPost.getMembers().size() >= ceoPost.getVacanciesCount(), "Все места для должности [" + ceoPost.getName() + "] заняты. Назначение невозможно.");
                communityMember.setPosts(new ArrayList<>());
                communityMember.getPosts().add(ceoPost);
            }
            communityMemberDomainService.save(communityMember);
            blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.ADD_MEMBER, communityMember));
        }
    }

    private String getCommunitySeoLinkName(String seoLink, Long id) {
        if (seoLink == null) {
            seoLink = String.valueOf(id);
        }
        int lastSgIndex = seoLink.lastIndexOf("/sg/");
        if (lastSgIndex == -1) {
            return seoLink;
        }
        return seoLink.substring(lastSgIndex + 4, seoLink.length());
    }

    /*@Override
    public void updateChildrenSeoLinks(CommunityEntity community) {
        List<CommunityEntity> children = community.getChildren();
        if (children != null) {
            for (CommunityEntity child : children) {
                String childSeoLink = community.getSeoLink() + "/sg/" + getCommunitySeoLinkName(child.getSeoLink(), child.getId());
                child.setSeoLink(childSeoLink);

                // TODO вообще правильнее делать всё через communityDao.update(child);, но возникают разные ошибки(например “Illegal attempt to associate a collection with two open sessions”), или RadomEntityListener попросту не срабатывает
                FieldValueEntity fieldValue = child.getFieldValue("COMMUNITY_SHORT_LINK_NAME");
                fieldValue.setStringValue(childSeoLink);
                fieldValueDao.saveOrUpdate(fieldValue);

                updateChildrenSeoLinks(child);
            }
        }
    }*/

    /*@Override
    public CommunityEntity editCommunity(CommunityEntity community, Map<FieldEntity, String> fieldsMap, Sharer editor) {
        check(community.getVerified() != null && community.getVerified(), "Нельзя изменять данные у сертифицированной организации");
        checkPermission(community, editor, "SETTINGS_COMMON", "У Вас нет прав на редактирование объединения");

        community = checkAndUpdateSeoLink(community);
        updateChildrenSeoLinks(community);
        communityDao.merge(community);

        community = fieldsService.saveFields(fieldsMap, community);

        return community;
    }*/

    @Override
    public List<CommunityMember> getPossibleMembers(Long communityId, int firstResult, int maxResults, String query, boolean onlyVerifiedUsers) {
        Community community = communityDataService.getByIdFullData(communityId);
        List<User> users = onlyVerifiedUsers ?  userDataService.searchMinDataVerified(query, firstResult, maxResults):  userDataService.searchMinData(query, firstResult, maxResults);
        List<CommunityMember> members = new ArrayList<>();
        for (User user : users) {
            if (!communityMemberDomainService.exists(community.getId(), user.getId())) {
                if (community.isRoot()) {
                    CommunityMember member = new CommunityMember();
                    member.setUser(user);
                    member.setCommunity(community);
                    members.add(member);
                } else {
                    if (communityMemberDomainService.exists(community.getParent().getId(), user.getId())) {
                        CommunityMember member = new CommunityMember();
                        member.setUser(user);
                        member.setCommunity(community);
                        members.add(member);
                    }
                }
            }
        }
        return members;
    }

    private void check(boolean condition, String message) {
        if (condition) {
            throw new CommunityException(message);
        }
    }

    @Override
    public boolean hasPermission(CommunityEntity community, Long userId, String permission) {
        return communityPermissionDao.getPermissions(community).contains(permission) &&
                (
                        community.getCreator().getId().equals(userId) ||
                        community.getRoot() != null && community.getRoot().getCreator().getId().equals(userId) ||
                        communityPermissionDao.getPermissions(communityMemberDao.get(community, userId)).contains(permission)
                );
    }

    @Override
    public boolean isMember(Long communityId, Long userId) {
        CommunityMemberEntity member = communityMemberDao.get(communityId, userId);
        return member != null ? member.getStatus() == CommunityMemberStatus.MEMBER : false;
    }

    @Override
    public boolean hasPermission(Long communityId, Long userId, String permission) {
        return hasPermission(communityDao.loadById(communityId), userId, permission);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ImagesEvent) {
            ImagesEvent imagesEvent = (ImagesEvent) event;
            if ("community".equals(((ImagesEvent) event).getObjectType())) {
                CommunityEntity community = communityDao.getById(imagesEvent.getObjectId());
                check(!community.getCreator().getId().equals(SecurityUtils.getUser().getId()), "У Вас нет прав на управление этим объединением");
                community.setAvatarUrl(imagesEvent.getUrl());
                communityDao.update(community);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void checkPermission(Long communityId, User user, String permission, String errorMessage) {
        if (!hasPermission(communityId, user.getId(), permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    @Override
    public CommunityPost savePost(User user, CommunityPost post, Long communityId) {
        check(post.getCommunity() == null || post.getCommunity().getId() == null, "Не загружен объект объединения");
        check(!post.getCommunity().getId().equals(communityId), "Должность не принаджежит объединению");

        Community community = communityDataService.getByIdMediumData(post.getCommunity().getId());
        check(!communityPostDomainService.checkPost(post), "Должность с таким названием уже существует в объединении");
        check(post.getPermissions() == null || post.getPermissions().size() == 0, "Не выбрана ни одна роль для должности");
        check(post.getAppointBehavior() == null, "Не выбран способ принятия на должность. С документами или без.");
        if (post.isCeo()) {
            post.setPermissions(null);
        }

        Long associationFormId = community.getAssociationForm() != null ? community.getAssociationForm().getId() : -1l;
        for (CommunityPermission permission : post.getPermissions()) {
            if (permission.getAssociationForms() != null && permission.getAssociationForms().size() > 0) {
                ListEditorItem listEditorItem = listEditorItemDomainService.getById(associationFormId);
                String listEditorName = listEditorItem == null ? "" : listEditorItem.getText();
                boolean findAssociationForm = false;
                for (ListEditorItem permissionAssociationForm : permission.getAssociationForms()) {
                    findAssociationForm = permissionAssociationForm.getId().equals(associationFormId);
                    if (findAssociationForm) {
                        break;
                    }
                }
                check(!findAssociationForm, "Право [" + permission.getTitle() + "] недопустимо в объединении с формой [" + listEditorName + "]. Сохранение невозможно.");
            }
        }

        if (post.getMembers() != null) {
            check(post.getMembers().size() > post.getVacanciesCount(),
                    "В настоящее время на данную должность " +
                            StringUtils.getDeclension(post.getMembers().size(), "назначен ", "назначено ", "назначено ") +
                            post.getMembers().size() + StringUtils.getDeclension(post.getMembers().size(),
                            " участник", " участника", " участников") + ". Нельзя установить количестиво мест меньше " +
                            post.getMembers().size() + ".");
        }

        check(!post.isCeo() && post.getSchemaUnit() == null, "Должно быть задано подразделение из структуры объединения");
        return communityPostDomainService.save(post);
    }

    @Override
    public CommunityPost deletePost(User user, Long postId, Long communityId) {
        CommunityPost post = communityPostDomainService.getById(postId, true, true, true, true);
        check(post.getCommunity() == null || post.getCommunity().getId() == null, "Не загружен объект объединения");
        check(!post.getCommunity().getId().equals(communityId), "Должность не принаджежит объединению");
        check(post.isCeo(), "Должность руководителя не может быть удалена");

        communityPostDomainService.delete(post);

        return post;
    }

    @Override
    public CommunityPost copyPost(User user, Long postId, Long communityId) {
        CommunityPost post = communityPostDomainService.getById(postId, true, true, true, true);
        check(post.isCeo(), "Невозможно создать копию должности руководителя");
        check(post.getCommunity() == null || post.getCommunity().getId() == null, "Не загружен объект объединения");
        check(!post.getCommunity().getId().equals(communityId), "Должность не принаджежит объединению");
        post.setId(null);
        post.setPosition(post.getPosition());
        post.setVacanciesCount(0);
        post.setName(getPostCopyName(post));
        return communityPostDomainService.save(post);
    }

    private String getPostCopyName(CommunityPost post) {
        String name = post.getName();
        do {
            name = StringUtils.getNextCopyName(name);
        } while (!communityPostDomainService.checkPost(post.getCommunity(), name));
        return name;
    }

    private CommunityPost createCeoPost(Community community) {
        check(communityPostDomainService.getCeo(community) != null, "Нельзя создать больше одной должности руководителя в одном объединении");
        CommunityPost post = new CommunityPost();
        post.setCommunity(community);
        post.setCeo(true);
        List<CommunityMember> members = new ArrayList<>();
        if (community.isRoot()) {
            CommunityMember creatorMember = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), community.getCreator().getId());
            if (creatorMember != null) {
                members.add(creatorMember);
            }
        }
        post.setMembers(members);
        post.setName(community.isRoot() ? "Генеральный директор" : "Руководитель");
        post.setPermissions(communityPermissionService.getByCommunityId(community.getId()));
        post.setPosition(-1);
        post.setVacanciesCount(1);
        post = communityPostDomainService.save(post);
        return post;
    }

    @Override
    public CommunityPost getCeoPost(Community community) {
        CommunityPost post = communityPostDomainService.getCeo(community);
        if (post == null) {
            post = createCeoPost(community);
        }
        return post;
    }

    /**
     * Проверка возможности назначения на должность
     *
     * @param appointer
     * @param member
     * @param post
     */
    private void checkAppoint(User appointer, CommunityMember member, CommunityPost post) {
        if (member != null && member.getCommunity() == null) {
            CommunityMember loadedMember = communityMemberDomainService.getByIdFullData(member.getId());
            member.setPosts(loadedMember.getPosts());
            member.setCommunity(loadedMember.getCommunity());
        }
        if (post != null && post.getCommunity() == null) {
            CommunityPost loadedPost = communityPostDomainService.getById(post.getId(), true, true, false, false);
            post.setCommunity(loadedPost.getCommunity());
            post.setMembers(loadedPost.getMembers());
        }

        checkPermission(member.getCommunity().getId(), appointer, "MEMBERS_APPOINTS", "У Вас нет прав на назначение должностей");
        ExceptionUtils.check(!post.getCommunity().getId().equals(member.getCommunity().getId()), "Должность не принадлежит к объединению");
        int membersCount = 0;
        for (CommunityMember communityMember : post.getMembers()) {
            if (!communityMember.getId().equals(member.getId())) {
                membersCount++;
            }
        }
        ExceptionUtils.check(membersCount >= post.getVacanciesCount(), "Все места для должности [" + post.getName() + "] заняты. Назначение невозможно.");
        for (CommunityPost communityPost : member.getPosts()) {
            ExceptionUtils.check(communityPost.getId().equals(post.getId()), "Участник объединения уже занимает эту должность");
        }
    }

    private void checkDisapoint(User appointer, CommunityMember member, CommunityPost post) {
        if (member != null && (member.getCommunity() == null || member.getPosts() == null)) {
            CommunityMember loadedMember = communityMemberDomainService.getByIdFullData(member.getId());
            member.setPosts(loadedMember.getPosts());
            member.setCommunity(loadedMember.getCommunity());
        }
        if (post != null && post.getCommunity() == null) {
            CommunityPost loadedPost = communityPostDomainService.getById(post.getId(), true, true, false, false);
            post.setCommunity(loadedPost.getCommunity());
            post.setMembers(loadedPost.getMembers());
        }
        Community community = communityDataService.getByIdFullData(member.getCommunity().getId());

        checkPermission(member.getCommunity().getId(), appointer, "MEMBERS_APPOINTS", "У Вас нет прав на назначение должностей");
        ExceptionUtils.check(!post.getCommunity().getId().equals(member.getCommunity().getId()), "Должность не принадлежит к объединению");
        if (member.getUser().getId().equals(community.getCreator().getId())) {
            CommunityPost ceoPost = getCeoPost(member.getCommunity());
            ExceptionUtils.check(post.getId().equals(ceoPost.getId()), "Нельзя снять создателя объединения с должности руководителя");
        }
    }

    /**
     * Получить и проверить запрос на должность
     * @param postRequestId
     * @return
     * @throws Exception
     */
    private CommunityPostRequest getPostRequest(Long postRequestId, Long userId) {
        CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(postRequestId);
        ExceptionUtils.check(communityPostRequest == null, "Запрос на должность с ИД: " + postRequestId + " не найден!");

        CommunityMember member = communityMemberDomainService.getByIdFullData(communityPostRequest.getReceiver().getId());

        ExceptionUtils.check(
                !member.getUser().getId().equals(userId),
                "Запрос на должность Вам не принадлежит!"
        );

        ExceptionUtils.check(
                !CommunityPostRequestStatus.NEW.equals(communityPostRequest.getStatus()),
                "Принятие на должность в процессе!"
        );
        return communityPostRequest;
    }

    @Override
    public PostAppointData approveAppointRequest(Long postRequestId, Long userId) {
        CommunityPostRequest communityPostRequest = getPostRequest(postRequestId, userId);
        // Проверка назначения
        checkAppoint(communityPostRequest.getSender().getUser(), communityPostRequest.getReceiver(), communityPostRequest.getCommunityPost());

        // Изменяем у запроса статус - в обработке
        /*communityPostRequest.setStatus(CommunityPostRequestStatus.IN_PROCESS);
        communityPostRequest = communityPostRequestService.save(communityPostRequest);*/
        // Запускаем механизм принятия участника на должность
        String behaviorName = communityPostRequest.getCommunityPost().getAppointBehavior();
        /*if (communityPostRequest.getCommunityPost().getAppointBehavior() == null && communityPostRequest.getCommunityPost().isWithTemplates()) {
            behaviorName = DocumentPostAppointBehavior.NAME;
        } else if (communityPostRequest.getCommunityPost().getAppointBehavior() == null) {
            behaviorName = DefaultPostAppointBehavior.NAME;
        } else {
            behaviorName = communityPostRequest.getCommunityPost().getAppointBehavior();
        }*/
        behaviorName = behaviorName == null ? DefaultPostAppointBehavior.NAME : behaviorName;
        IPostAppointBehavior postAppointBehavior = (IPostAppointBehavior) applicationContext.getBean(behaviorName);
        PostAppointData postAppointData = postAppointBehavior.start(communityPostRequest);

        Long memberId = communityPostRequest.getReceiver().getId();

        Map<String, Object> payload = new HashMap<>();
        payload.put("postRequest", serializeService.toPrimitiveObject(communityPostRequest));
        payload.put("postAppointData", serializeService.toPrimitiveObject(postAppointData));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + memberId + "_approve_appoint_request", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);

        return postAppointData;
    }

    @Override
    public void cancelPostAppoint(Long postRequestId, Long userId) {
        CommunityPostRequest communityPostRequest = getPostRequest(postRequestId, userId);
        // Удалить запрос на должность
        communityPostRequestDomainService.delete(communityPostRequest.getId());

        Long memberId = communityPostRequest.getReceiver().getId();

        Map<String, Object> payload = new HashMap<>();

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + memberId + "_cancel_appoint_request", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
    }

    @Override
    public void requestToAppoint(User appointer, CommunityMember member, CommunityPost post) {
        // Проверка назначения
        checkAppoint(appointer, member, post);

        // Создаём запрос на назначение
        CommunityMember appointerMember = communityMemberDomainService.getByCommunityIdAndUserId(member.getCommunity().getId(), appointer.getId());
        CommunityPostRequest communityPostRequest = new CommunityPostRequest();
        communityPostRequest.setSender(appointerMember);
        communityPostRequest.setReceiver(member);
        communityPostRequest.setCommunity(member.getCommunity());
        communityPostRequest.setCommunityPost(post);
        communityPostRequest.setStatus(CommunityPostRequestStatus.NEW);

        communityPostRequest = communityPostRequestService.save(communityPostRequest);

        // Запрос на назначение
        blagosferaEventPublisher.publishEvent(new CommunityMemberAppointEvent(this, CommunityEventType.REQUEST_TO_APPOINT_POST, member, appointer, post, communityPostRequest));

        Map<String, Object> payload = new HashMap<>();
        payload.put("member", serializeService.toPrimitiveObject(member));
        payload.put("sender", serializeService.toPrimitiveObject(appointerMember));
        payload.put("post", serializeService.toPrimitiveObject(post));
        payload.put("user", serializeService.toPrimitiveObject(member.getUser()));
        payload.put("community", serializeService.toPrimitiveObject(member.getCommunity()));
        payload.put("communityPostRequestId", communityPostRequest.getId());

        blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(
                this,
                "createRequestToAppoint",
                payload
        ));

    }

    @Override
    public CommunityMember appoint(CommunityPostRequest communityPostRequest) {
        User appointer = communityPostRequest.getSender().getUser();
        CommunityMember member = communityPostRequest.getReceiver();
        CommunityPost post = communityPostRequest.getCommunityPost();

        // Удаляем запрос на назначение в должности
        communityPostRequestService.delete(communityPostRequest.getId());

        return appoint(appointer, member, post);
    }

    @Override
    public CommunityMember appoint(BatchVoting batchVoting, CommunityMember member, CommunityPost post) {
        List<CommunityPost> oldPosts = new ArrayList<>(member.getPosts());
        member.getPosts().add(post);
        communityMemberDomainService.save(member);

        // Назначение по итогам голосования
        if (batchVoting != null) {
            if (!oldPosts.contains(post)) {
                blagosferaEventPublisher.publishEvent(new CommunityMemberAppointEvent(this, CommunityEventType.APPOINT_BY_VOTING, member, batchVoting, post));
            }
        }
        signalAboutAppoint(member, post);
        return member;
    }

    @Override
    public CommunityMember appoint(User appointer, CommunityMember member, CommunityPost post) {
        checkAppoint(appointer, member, post);

        List<CommunityPost> oldPosts = new ArrayList<>(member.getPosts());
        member.getPosts().add(post);

        communityMemberDomainService.save(member);

        if (appointer == null || !appointer.getId().equals(member.getUser().getId())) {
            if (!oldPosts.contains(post)) {
                blagosferaEventPublisher.publishEvent(new CommunityMemberAppointEvent(this, CommunityEventType.APPOINT, member, appointer, post));
            }
        }
        signalAboutAppoint(member, post);
        return member;
    }

    /**
     * Просигналить в BPM о назначении на должность
     */
    private void signalAboutAppoint(final CommunityMember member, final CommunityPost post) {
        User sharer = member.getUser();
        Map<String, Object> payload = new HashMap<>();
        payload.put("appointedMember", serializeService.toPrimitiveObject(member));
        payload.put("appointedToPost", serializeService.toPrimitiveObject(post));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_appointed_to_post", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_appointed_to_post_" + post.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + sharer.getId() + "_appointed_to_post_" + post.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + sharer.getId() + "_appointed_to_post", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + member.getId() + "_appointed_to_post", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
    }

    @Override
    public void disapoint(User appointer, CommunityMember member, CommunityPost post) {
        checkDisapoint(appointer, member, post);
        Iterator<CommunityPost> communityPostIterator = member.getPosts().iterator();
        while (communityPostIterator.hasNext()) {
            CommunityPost communityPost = communityPostIterator.next();
            if (communityPost.getId().equals(post.getId())) {
                communityPostIterator.remove();
            }
        }

        communityMemberDomainService.save(member);
        if (!appointer.getId().equals(member.getUser().getId())) {
            blagosferaEventPublisher.publishEvent(new CommunityMemberAppointEvent(this, CommunityEventType.DISAPPOINT, member, appointer, post));
        }

        User user = member.getUser();
        Map<String, Object> payload = new HashMap<>();
        payload.put("disappointedMember", serializeService.toPrimitiveObject(member));
        payload.put("disappointedFromPost", serializeService.toPrimitiveObject(post));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_disappointed_from_post", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_disappointed_from_post_" + post.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + user.getId() + "_disappointed_from_post_" + post.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + user.getId() + "_disappointed_from_post", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + member.getId() + "_disappointed_from_post", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
    }

    private void putChildrenToMap(Community community, Map<Community, Integer> map, int level) {
        for (Community child : community.getChildren()) {
            map.put(child, level);
            putChildrenToMap(child, map, level + 1);
        }
    }

    @Override
    public Map<Community, Integer> getChildMap(Community community) {
        Map<Community, Integer> map = new LinkedHashMap<>();
        putChildrenToMap(community, map, 0);
        return map;
    }

    private void checkTransferMoneyPermission(Community community, User transferer) {
        if (community.isDeleted()) {
            throw new CommunityException(community.isRoot() ? "Объединение уже удалено" : "Группа уже удалена");
        }
        if (!hasPermission(community.getId(), transferer.getId(), CommunityPermissionUtils.TRANSFER_MONEY_PERMISSION)) {
            throw new CommunityException(community.isRoot() ? "У Вас нет прав для перевода денег с баланса этого объединения" : "У Вас нет прав для перевода денег с баланса этой группы");
        }
    }

    private void checkDeletePermission(CommunityEntity community, User deleter) {
        if (community.isDeleted()) {
            throw new CommunityException(community.isRoot() ? "Объединение уже удалено" : "Группа уже удалена");
        }
        if (!community.getCreator().getId().equals(deleter.getId())) {
            if (community.isRoot()) {
                if (!SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)) {
                    throw new CommunityException("У Вас нет прав на удаление этого объединения");
                }
            } else {
                if (!SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN) && !community.getRoot().getCreator().equals(deleter)) {
                    throw new CommunityException("У Вас нет прав на удаление этой подгруппы");
                }
            }
        }
    }

    private void checkRestorePermission(Community community) {
        if (!community.isDeleted()) {
            throw new CommunityException(community.isRoot() ? "Объединение не удалено" : "Группа не удалена");
        }
        if (!SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)) {
            throw new CommunityException("У Вас нет прав на восстановление этого объединения");
        }
    }

    @Override
    public Community deleteCommunity(Long communityId, String comment, User user) {
        CommunityEntity entity = communityDao.getById(communityId);
        Community community = entity.toDomain();

        checkDeletePermission(entity, user);

        if (!entity.getCreator().getId().equals(user.getId())) {
            if (StringUtils.isEmpty(comment)) {
                throw new CommunityException("Необходимо указать причину удаления");
            }
        }

        entity.setDeleted(true);
        entity.setDeleteComment(comment);
        entity.setDeleter(userRepository.findOne(user.getId()));
        communityDao.update(entity);

        community = communityDataService.getByIdFullData(entity.getId());
        newsSubscribeRepository.deleteNewsSubscribesToCommunity(communityId);
        blagosferaEventPublisher.publishEvent(new CommunityEvent(this, CommunityEventType.DELETED, community));
        return community;
    }

    @Override
    public Community restoreCommunity(Long communityId) {
        CommunityEntity entity = communityDao.getById(communityId);
        Community community = entity.toDomain();

        checkRestorePermission(community);
        entity.setDeleter(null);
        entity.setDeleted(false);
        entity.setDeleteComment(null);
        communityDao.update(entity);

        return communityDataService.getByIdFullData(entity.getId());
    }

    @Override
    public boolean canTransferMoneyCommunity(Community community, User transferer) {
        try {
            checkTransferMoneyPermission(community, transferer);
            return true;
        } catch (CommunityException e) {
            return false;
        }
    }

    @Override
    public boolean canDeleteCommunity(Community community, UserDetailsImpl userDetails) {
        boolean result = true;
        if (community.isDeleted()) {
            result = false;
        }
        if (!community.getCreator().getId().equals(userDetails.getUser().getId())) {
            if (community.isRoot()) {
                if (!userDetails.hasRole(Roles.ROLE_ADMIN)) {
                    result = false;
                }
            } else {
                Community rootCommunity = communityDataService.getByIdFullData(community.getRoot().getId());
                if (!userDetails.hasRole(Roles.ROLE_ADMIN) && !rootCommunity.getCreator().equals(userDetails.getUser())) {
                    result = false;
                }
            }
        }
        return result;
    }

    @Override
    public boolean canRestoreCommunity(Community community, UserDetailsImpl userDetails) {
        boolean result = true;
        if (!community.isDeleted()) {
            result = false;
        }
        if (!userDetails.hasRole(Roles.ROLE_ADMIN)) {
            result = false;
        }
        return result;
    }

    @Override
    public void verifiedCommunity(Long communityId, User registrator) {
        Community community = communityDataService.getByIdFullData(communityId);
        check(communityId == null, "Не передан ИД организации для сертификации");
        check(community == null, "Не найдена организация для сертификации");
        check(registrator == null, "Не передан регистратор");
        CommunityFillingDto communityFillingDto = communityFillingService.getCommunityFilling(community);
        check(communityFillingDto.getRequiredFields() != null && communityFillingDto.getRequiredFields().size() > 0,
                "У организации не заполнены обязательные поля.");

        check(communityFillingDto.getPercent() < communityFillingDto.getThreshold(),
                "Поля организации заполнены на " + communityFillingDto.getPercent() + "%. " +
                "Необходимый уровень заполнения " + communityFillingDto.getThreshold() + "%.");

        // Проверка запроса на сертификацию
        registrationRequestService.checkObjectToVerified(community, registrator);

        community.setVerifier(registrator);
        community.setVerificationDate(new Date());
        community.setVerified(true);
        communityDataService.save(community, community.getCreator());
        //communityDao.update(community);
        registrationRequestService.setVerifiedRequest(community);
    }

    /*@Override
    public List<BreadcrumbItem> getBreadcrumbCommonItems() {
        boolean isMember = radomRequestContext.isCommunityMember();
        boolean isCreator = radomRequestContext.isCommunityCreator();
        return getBreadcrumbCommonItems(isMember, isCreator, radomRequestContext.getCommunity());
    }*/

    @Override
    public List<BreadcrumbItem> getBreadcrumbCommonItems(boolean isMember, boolean isCreator, Community community) {
        List<BreadcrumbItem> items = new ArrayList<>();
        String link = "/groups/all";
        String title = "Объединения";
        if (isMember) {
            link = "/groups";
            title = "Объединения с моим участием";
        }
        if (isCreator) {
            link = "/groups/creator";
            title = "Мои Объединения";
        }
        items.add(new BreadcrumbItem(title, link));
        List<Community> parents = communityDataService.getParents(community);
        for (Community parent : parents) {
            items.add(new BreadcrumbItem(parent.getName(), parent.getLink()));
        }
        return items;
    }

    @Override
    public Long getCommunityDirectorId(Community community) {
        Long userId = null;
        Field field = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_DIRECTOR_SHARER_ID);
        if (field != null && field.getValue() != null) {
            userId = VarUtils.getLong(field.getValue(), null);
        }
        return userId;
    }

    @Override
    public User getCommunityDirector(Community community) {
        User director = null;
        Long userId = getCommunityDirectorId(community);
        if (userId != null) {
            director = userDataService.getByIdFullData(userId);
        }
        return director;
    }

    private Field getFieldOfCommunity(Community community, String fieldName) {
        if (community.getCommunityData() == null) {
            community = communityDataService.getByIdFullData(community.getId());
        }
        return community.getCommunityData().getFieldByInternalName(fieldName);
    }

    private User getUserFromField(Field field) {
        User result = null;
        String fieldStringValue = FieldsService.getFieldStringValue(field);
        long sharerId = VarUtils.getLong(fieldStringValue, -1l);
        if (sharerId > -1) {
            result = userDataService.getByIdFullData(sharerId);
        }
        return result;
    }

    /**
     * Получить список участников системы из поля типа PARTICIPANTS_LIST
     * @param field
     * @return
     */
    public List<User> getUsersFromParticipantsListField(Field field) {
        ExceptionUtils.check(field == null, "Не передано значение поля");
        ExceptionUtils.check(field != null && field.getType() != FieldType.PARTICIPANTS_LIST,
                "Поле " + field.getInternalName() + " не явлеятеся полем с типом PARTICIPANTS_LIST"
        );

        List<User> result = new ArrayList<>();
        String fieldStringValue = FieldsService.getFieldStringValue(field);
        if (fieldStringValue != null && !fieldStringValue.equals("")) {
            String[] idsStr = fieldStringValue.split(";");
            for (String idStr : idsStr) {
                Long userId = VarUtils.getLong(idStr, null);
                if (userId != null) {
                    User user = userDataService.getByIdFullData(userId);
                    if (user != null) {
                        result.add(user);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<User> getMembersSovietOfCooperative(Community community) {
        Field field = getFieldOfCommunity(community, FieldConstants.MEMBERES_OF_SOVIET_ID_FIELD_NAME);
        return getUsersFromParticipantsListField(field);
    }

    @Override
    public User getPresidentSovietOfCooperative(Community community) {
        Field field = getFieldOfCommunity(community, FieldConstants.PRESIDENT_OF_SOVIET_ID_FIELD_NAME);
        return getUserFromField(field);
    }

    @Override
    //@Cacheable("newsPageData")
    public CommunityNewsPageDomain getCommunityNewsPageDomain(Long communityId, UserDetailsImpl userDetails) {
        Community community = communityDataService.getByIdMediumData(communityId);
        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, userDetails.getUser().getId());

        boolean isOpen = CommunityAccessType.OPEN.equals(community.getAccessType());

        boolean canDelete = canDeleteCommunity(community, userDetails);
        boolean canJoin = selfMember == null && isOpen;
        boolean canRequest = selfMember == null && !isOpen;
        boolean canCancelRequest = selfMember != null && (selfMember.getStatus() == CommunityMemberStatus.REQUEST ||
                selfMember.getStatus() == CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST ||
                selfMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST ||
                selfMember.getStatus() == CommunityMemberStatus.JOIN_IN_PROCESS);
        boolean canAcceptInvite = selfMember != null && selfMember.getStatus() == CommunityMemberStatus.INVITE;
        boolean canRejectInvite = selfMember != null && selfMember.getStatus() == CommunityMemberStatus.INVITE;
        boolean canLeave = selfMember != null && !selfMember.isCreator() && selfMember.getStatus() == CommunityMemberStatus.MEMBER;

        //
        boolean canCancelRequestToLeave = selfMember != null && !selfMember.isCreator() &&
                (
                        selfMember.getStatus() == CommunityMemberStatus.REQUEST_TO_LEAVE ||
                                selfMember.getStatus() == CommunityMemberStatus.LEAVE_IN_PROCESS
                );

        String maxFieldValueHeight = settingsManager.getSystemSetting("max-field-value-height", "400");

        boolean canTransferMoney = canTransferMoneyCommunity(community, userDetails.getUser());

        // Если объединение в рамках юр лица, то установить флаг - возможность стать участником объединения от объединения
        boolean canJoinInCommunityAsOrganization = ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getCommunityType());

        boolean isConsumerSociety = false;
        RameraListEditorItem poAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        RameraListEditorItem kuchAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE);
        Long associationFormId = community.getAssociationForm() != null ? community.getAssociationForm().getId() : null;
        if (associationFormId != null && (associationFormId.equals(poAssociationForm.getId()) || associationFormId.equals(kuchAssociationForm.getId()))) {
            isConsumerSociety = true;
        }

        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("NEWS_CREATE", hasPermission(community.getId(), userDetails.getUser().getId(), "NEWS_CREATE"));

        return new CommunityNewsPageDomain(
                canDelete, canJoin, canRequest, canCancelRequest,
                canAcceptInvite, canRejectInvite, canLeave, canCancelRequestToLeave,
                maxFieldValueHeight, canTransferMoney,
                canJoinInCommunityAsOrganization, isConsumerSociety, selfMember, permissions
        );
    }

    public CommunityMenuDomain communityDomainService(Long communityId, User currentUser) {
        CommunityMenuDomain result = new CommunityMenuDomain();
        SharebookEntity shareBook = accountService.getSharebook(currentUser, communityId);
        result.setCommunitySections(communityDataService.getAllCommunitySections());
        result.setSharerBookAccount(shareBook != null ? shareBook.toDomain() : null);
        result.setCommunityBookAccountsBalance(sharebookService.getCommunitySharebooksTotalBalance(communityId));
        result.setAccounts(accountService.getCommunityAccounts(communityId));
        return result;
    }

    private CommunitySectionDomain searchKuchSection(List<CommunitySectionDomain> communitySections) {
        CommunitySectionDomain result = null;
        for(CommunitySectionDomain communitySection : communitySections) {
            if (communitySection.getName().equals("CREATE_MEETING")) {
                result = communitySection;
            } else if (communitySection.getChildren() != null){
                result = searchKuchSection(communitySection.getChildren());
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public List<Long> getVisibleSectionsForUser(Long communityId, Long currentUserId, List<CommunitySectionDomain> communitySections) {
        Community community = communityDataService.getByIdFullData(communityId);

        Community rootCommunity = null;
        if (community.getRoot() != null) {
            rootCommunity = communityDataService.getByIdFullData(community.getRoot().getId());
        }
        boolean userHasAllRights = community.getCreator().getId().equals(currentUserId) || rootCommunity != null && rootCommunity.getCreator().getId().equals(currentUserId);
        Set<String> permissions = null;
        if (!userHasAllRights) {
            permissions = communityPermissionService.getPermissions(communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), currentUserId));
        }
        List<Long> visibleSectionIds = new ArrayList<>();
        for (CommunitySectionDomain communitySection : communitySections) {
            boolean visibleChild = false;
            if (communitySection.getChildren() != null && communitySection.getChildren().size() > 0) {
                List<CommunitySectionDomain> children = communitySection.getChildren();
                for (CommunitySectionDomain child : children) {
                    if (userHasAllRights || (permissions != null && permissions.contains(child.getPermission())) || child.getPermission() == null) {
                        visibleSectionIds.add(child.getId());
                        visibleChild = true;
                    }
                }
            }
            if (userHasAllRights || (permissions != null && permissions.contains(communitySection.getPermission())) || communitySection.getPermission() == null) {
                if (visibleChild) {
                    visibleSectionIds.add(communitySection.getId());
                }
            }
        }

        // TODO Надо как то сделать проверку формы объединения нормально
        CommunitySectionDomain kuchSection = searchKuchSection(communitySections);
        RameraListEditorItem poAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        Long associationFormId = community.getAssociationForm() != null ? community.getAssociationForm().getId() : null;

        if (associationFormId == null || !associationFormId.equals(poAssociationForm.getId())) {
            if (kuchSection != null) {
                visibleSectionIds.remove(kuchSection.getId());
            }
        }

        //accountService.getSharebook(radomRequestContext.getCurrentSharer(), radomRequestContext.getCommunity())
        return visibleSectionIds;
    }

    @Override
    public List<Account> getCommunityAccounts(Long communityId) {
        return accountService.getCommunityAccounts(communityId);
    }
}
