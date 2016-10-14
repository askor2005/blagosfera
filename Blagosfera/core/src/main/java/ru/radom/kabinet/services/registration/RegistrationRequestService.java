package ru.radom.kabinet.services.registration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.domain.Verifiable;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.RegistrationEvent;
import ru.askor.blagosfera.domain.events.user.RegistrationEventType;
import ru.askor.blagosfera.domain.registration.request.RegistrationRequestDomain;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.registration.RegistrationRequestDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.StringUtils;

import java.util.List;

@Scope("singleton")
@Service("registrationRequestService")
public class RegistrationRequestService {

    @Autowired
    private RegistrationRequestDao registrationRequestDao;

    /*@Autowired
    private RegistratorDao registratorDao;*/

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private RegistratorDataService registratorDataService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private UserRepository userRepository;

    private void check(boolean condition, String message) {
        if (condition) {
            throw new RuntimeException(message);
        }
    }

    private Verifiable getFromObjectRequest(LongIdentifiable object) {
        Verifiable result = null;
        if (object instanceof UserEntity) {
            result = userDataService.getByIdFullData(object.getId());
        } else if (object instanceof CommunityEntity) {
            result = communityDomainService.getByIdFullData(object.getId());
        }
        ExceptionUtils.check(result == null, "Не найден объект сервтификации");
        return result;
    }

    private Long createRequest(Verifiable verifiable, User registratorUser) {
        LongIdentifiable longIdentifiable = null;

        if (verifiable instanceof User) {
            UserEntity user = sharerDao.getById(verifiable.getId());
            user.setStatus(SharerStatus.WAITING_FOR_CERTIFICATION);
            longIdentifiable = userRepository.save(user);
        } else if (verifiable instanceof Community) {
            longIdentifiable = communityDao.getById(verifiable.getId());
        }

        ExceptionUtils.check(longIdentifiable == null, "Не найден объект сервтификации");
        UserEntity registrator = sharerDao.loadById(registratorUser.getId());
        ExceptionUtils.check(registrator == null, "Не найден регистратор");
        return registrationRequestDao.create(longIdentifiable, registrator);
    }

    private RegistrationRequest getRequest(Verifiable verifiable) {
        LongIdentifiable longIdentifiable = null;
        if (verifiable instanceof User) {
            longIdentifiable = sharerDao.getById(verifiable.getId());
        } else if (verifiable instanceof Community) {
            longIdentifiable = communityDao.getById(verifiable.getId());
        }
        ExceptionUtils.check(longIdentifiable == null, "Не найден объект сервтификации");
        return registrationRequestDao.getRequestByObject(longIdentifiable);
    }

    /**
     * Создать запрос на сертификацию
     * @param registratorId
     * @param user
     * @return
     */
    public Long create(final Long registratorId, User user){
        if(registratorId == null) throw new IllegalArgumentException("registrator id must be specified");
        final RegistrationRequest result = getMy();
        if(result != null) throw new IllegalStateException("У Вас уже есть открытая заявка");
        final User registrator = registratorDataService.getByRegistratorId(registratorId);
        if(registrator == null) throw new IllegalArgumentException("Invalid registrator id");
        final Long id = createRequest(user, registrator);
        blagosferaEventPublisher.publishEvent(new RegistrationEvent(this, RegistrationEventType.VERIFICATION_REQUEST, user, registrator));
        return id;
    }

    /**
     * Создать запрос на сертификацию организации
     * @param registratorId
     * @param requester
     * @param community
     * @return
     */
    public Long create(Long registratorId, User requester, Community community){
        check(registratorId == null, "Не выбран регистратор");
        check(community == null, "Не выбрана организация");
        check(requester == null, "Не передан участник создающий запрос");
        check(!requester.getId().equals(communitiesService.getCommunityDirectorId(community)), "Вы не являетесь директором организации");
        RegistrationRequest result = getRequest(community);
        check(result != null, "У организации уже есть открытая заявка");
        final User registrator = registratorDataService.getByRegistratorId(registratorId);
        check(registrator == null, "Не выбран регистратор");
        final Long id = createRequest(community, registrator);
        blagosferaEventPublisher.publishEvent(new RegistrationEvent(this, RegistrationEventType.VERIFICATION_REQUEST, community, registrator));
        return id;
    }

    /**
     * Получить запрос текущего пользователя на сертификацию
     * @return
     */
    @Deprecated
    public RegistrationRequest getMy(){
        return getByObject(SecurityUtils.getUser());
    }
    public RegistrationRequestDomain getMine(){
        RegistrationRequest registrationRequest = getByObject(SecurityUtils.getUser());
        if (registrationRequest != null)
            return registrationRequest.toDomain();
        return null;
    }

    /**
     * Получить запрос на сертификацию по объекту
     * @param object
     * @return
     */
    /*public RegistrationRequest getByObject(LongIdentifiable object) {
        return registrationRequestDao.getRequestByObject(object);
    }*/

    /**
     * Получить запрос на сертификацию по объекту
     * @param verifiable
     * @return
     */
    public RegistrationRequest getByObject(Verifiable verifiable) {
        LongIdentifiable object = null;
        RegistrationRequest result = null;
        if (verifiable instanceof User) {
            object = sharerDao.getById(verifiable.getId());
        } else if (verifiable instanceof Community) {
            object = communityDao.getById(verifiable.getId());
        }
        if (object != null) {
            result = registrationRequestDao.getRequestByObject(object);
        }
        return result;
    }

    public RegistrationRequest getByCommunityId(Long communityId) {
        CommunityEntity community = communityDao.getById(communityId);
        return registrationRequestDao.getRequestByObject(community);
    }

    private List<RegistrationRequest> searchUserRequests(final User registratorUser, final String nameTemplate, final String orderBy, final boolean asc, final int offset, final int limit, final RegistrationRequestStatus status ) {
        UserEntity registrator = sharerDao.getById(registratorUser.getId());
        return registrationRequestDao.searchUserRequests(registrator, nameTemplate, orderBy, asc, offset, limit, status);
    }

    private List<RegistrationRequest> searchCommunityRequests(final User registratorUser, final String nameTemplate, final String orderBy, final boolean asc, final int offset, final int limit, final RegistrationRequestStatus status ) {
        UserEntity registrator = sharerDao.getById(registratorUser.getId());
        return registrationRequestDao.searchUserRequests(registrator, nameTemplate, orderBy, asc, offset, limit, status);
    }

    /**
     * Фильтрация запросов на сертификацию от пользователей системы
     * @param registrator
     * @param offset
     * @param limit
     * @param nameTemplate
     * @param status
     * @param orderBy
     * @param asc
     * @return
     */
    public List<RegistrationRequest> searchRequests(User registrator, final Integer offset, final Integer limit, final String nameTemplate, final RegistrationRequestStatus status,
                                          final String orderBy, final boolean asc, String objectType){
        check(!Discriminators.SHARER.equals(objectType) && !Discriminators.COMMUNITY.equals(objectType), "Не правильный тип запроса");
        List<RegistrationRequest> result = null;
        if (Discriminators.SHARER.equals(objectType)) {
            result = searchUserRequests(registrator, nameTemplate, orderBy, asc, offset, limit, status);
        } else if (Discriminators.COMMUNITY.equals(objectType)) {
            result = searchCommunityRequests(registrator, nameTemplate, orderBy, asc, offset, limit, status);
        }
        return result;
    }

    /**
     * Количество запросов к регистратору по статусу
     * @param registratorUser
     * @param status
     * @param objectType
     * @return
     */
    public Integer count(User registratorUser, final RegistrationRequestStatus status, String objectType){
        check(!Discriminators.SHARER.equals(objectType) && !Discriminators.COMMUNITY.equals(objectType), "Не правильный тип запроса");
        String className = Discriminators.SHARER.equals(objectType) ? UserEntity.class.getName() :
                (Discriminators.COMMUNITY.equals(objectType) ? CommunityEntity.class.getName() : null);
        UserEntity registrator = sharerDao.getById(registratorUser.getId());
        return registrationRequestDao.count(registrator, status, className);
    }

    /**
     * Удалить запрос на сертификацию
     * @param id
     * @param deleter
     */
    public void deleteRequest(final Long id, User deleter){
        check(id == null, "Не передан запрос на удаление");
        final RegistrationRequest request = registrationRequestDao.getById(id);
        check(request == null, "Не передан запрос на удаление");
        check(deleter == null, "Не передан участник удаляющий запрос");
        Verifiable verifiable = null;
        if (request.getObject() instanceof UserEntity) {
            check(!request.getObject().getId().equals(deleter.getId()), "Вы не являетесь автором запроса на идентификацию");
            verifiable = userDataService.getByIdMinData(request.getObject().getId());
            UserEntity userEntity = userRepository.findOne(verifiable.getId());
            userEntity.setStatus(SharerStatus.CONFIRM);
            verifiable = userRepository.save(userEntity).toDomain();
        } else if (request.getObject() instanceof CommunityEntity) {
            CommunityEntity communityEntity = (CommunityEntity)request.getObject();
            Community community = communityDomainService.getByIdFullData(communityEntity.getId());
            verifiable = community;
            Long directorId = communitiesService.getCommunityDirectorId(community);
            check(!deleter.getId().equals(directorId), "Вы не являетесь директором организации");
        } else {
            check(true, "Не допустимый объект в запросе на сертификацию");
        }
        if(request.getStatus() == RegistrationRequestStatus.NEW){
            registrationRequestDao.updateStatus(request, RegistrationRequestStatus.DELETED, null);
            User registrator = request.getRegistrator().toDomain();
            blagosferaEventPublisher.publishEvent(new RegistrationEvent(this, RegistrationEventType.DELETE_REQUEST, verifiable, registrator));
        }
    }

    /**
     * Отлонить запрос регистратором
     * @param id
     * @param comment
     * @param registrator
     */
    public void cancelRequest(final Long id, final String comment, User registrator){
        check(StringUtils.isEmpty(comment), "Не указана причина в отказе на сертификацию/идентификацию");
        check(id == null, "Не передан запрос");
        final RegistrationRequest request = registrationRequestDao.getById(id);
        check(request == null, "Не передан запрос");
        check (!request.getRegistrator().getId().equals(registrator.getId()), "Вы не являетесь регистратором данного запроса");
        if(request.getStatus() == RegistrationRequestStatus.NEW) {
            registrationRequestDao.updateStatus(request, RegistrationRequestStatus.CANCELED, comment);
            Verifiable verifiableObject = getFromObjectRequest(request.getObject());
            blagosferaEventPublisher.publishEvent(new RegistrationEvent(this, RegistrationEventType.CANCEL_REQUEST, verifiableObject, registrator, comment));
        }
    }

    /**
     * Метод проверки запроса перед сертификацей
     * @param object
     * @param registrator
     */
    public void checkObjectToVerified(Verifiable object, User registrator) {
        if (object instanceof UserEntity) {
            check(object == null, "Не передан объект идентификации");
        }
        else {
            check(object == null, "Не передан объект сертификации");
        }
        check(registrator == null, "Не передан регистратор");
        RegistrationRequest request = registrationRequestDao.getRequestByObject(getRequestObjectEntity(object));
        if (object instanceof UserEntity) {
            check(request == null, "Не найден запрос идентификации");
        }
        else {
            check(request == null, "Не найден запрос сертификации");
        }
        check(!request.getRegistrator().getId().equals(registrator.getId()), "Вы не являетесь регистратором данного запроса");
        if (object instanceof UserEntity) {
            check(request.getStatus() != RegistrationRequestStatus.NEW, "У запроса на идентификацию не правильный статус");
        }
        else {
            check(request.getStatus() != RegistrationRequestStatus.NEW, "У запроса на сертификацию не правильный статус");
        }
    }

    /**
     * Установить запрос на сертификайию пользователя или организации как выполненный
     * @param object
     */
    public void setVerifiedRequest(Verifiable object) {
        final RegistrationRequest request = registrationRequestDao.getRequestByObject(getRequestObjectEntity(object));
        if(request != null){
            if(request.getStatus() == RegistrationRequestStatus.NEW){
                registrationRequestDao.updateStatus(request, RegistrationRequestStatus.PROCESSED, null);
            }
        }
    }

    private LongIdentifiable getRequestObjectEntity(Verifiable object) {
        LongIdentifiable result = null;
        if (object instanceof User) {
            result = sharerDao.loadById(object.getId());
        } else if (object instanceof Community) {
            result = communityDao.loadById(object.getId());
        }
        ExceptionUtils.check(result == null, "Передан объект который не может быть идентифицирован");
        return result;
    }
    
    /*@Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof RegistrationEvent) {
            final RegistrationEvent registrationEvent = (RegistrationEvent) event;
            switch(registrationEvent.getType()){
                case VERIFIED:
                    final RegistrationRequest request = registrationRequestDao.getUserRequest(registrationEvent.getUser());
                    if(request != null){
                        //if (!request.getRegistrator().equals(radomRequestContext.getCurrentSharer())) throw new AccessDeniedException("you can't process request of other user");
                        if((request.getStatus() == RegistrationRequestStatus.NEW)){
                            registrationRequestDao.updateStatus(request, RegistrationRequestStatus.PROCESSED, null);
                        }
                    }
                    break;
                default:
            }
        }
    }*/
}
