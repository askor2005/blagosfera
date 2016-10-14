package ru.radom.kabinet;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.contacts.ContactsDataService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.services.ProfileException;
import ru.radom.kabinet.services.ProfileFilling;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.field.FieldValidatorBundle;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.Roles;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 09.11.2015.<br/>
 * Сервис для работы с {@link UserEntity}
 */
@Service
@Transactional
public class SharerService {

    @Autowired
    private ContactsDataService contactsDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private FieldValidatorBundle fieldValidatorBundle;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RegistratorService registratorService;

    private List<String> REGISTRATOR_OFFICE_FIELDS = Arrays.asList(
            FieldConstants.REGISTRATOR_OFFICE_COUNTRY,
            FieldConstants.REGISTRATOR_OFFICE_REGION,
            FieldConstants.REGISTRATOR_OFFICE_STREET,
            FieldConstants.REGISTRATOR_OFFICE_BUILDING,
            FieldConstants.REGISTRATOR_OFFICE_ROOM
    );

    /**
     * Получить контакты онлайн для пользователя, ожидаем объект с полями:
     * <ul>
     *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     *     <li><b>onlyOnline</b>, типа {@link Boolean}, если он не предоставлен, то счиается что равен false
     * </ul>
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(queues = "core.social.get.contacts")
    public void getUserContactsWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Object sharerObj = data.get("sharer");
            User user = tryGetUser(sharerObj);
            if (user != null) {
                List<Contact> contacts = contactsDataService.getContacts(user.getId(), ContactStatus.ACCEPTED, ContactStatus.ACCEPTED);
                return map(contacts);
            } else {
                return Collections.emptyList();
            }
        });
    }*/

    /**
     * Получить информацию о пользователе, ожидаем объект с полем sharer, в котором лежит id, ikp или объект у которого есть поле id
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "core.social.get.sharer", durable = "true"),
        exchange = @Exchange(value = "task-exchange", durable = "true"),
        key = "core.social.get.sharer"
    ))
    public void getUserObjectWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Object sharerObj = data.get("sharer");
            User sharer = tryGetUser(sharerObj);
            return sharer == null ? null : convertUserToSend(sharer);
        });
    }*/


    /**
     * Пытаемся получить пользователя из пришедщих данных
     * @param userObj id, ikp или объект у которого есть поле id
     */
    public User tryGetUser(Object userObj) {
        User user;
        if(userObj instanceof String) {
            user = userDataService.getByIkpOrShortLink((String) userObj);
            if(user == null) {
                try {
                    Long id = Long.parseLong((String) userObj);
                    user = userDataService.getByIdMinData(id);
                } catch (NumberFormatException e) {
                    user = null;
                }
            }
        } else if(userObj instanceof Long) {
            user = userDataService.getByIdMinData(((Long) userObj));
        } else if(userObj instanceof Map) {
            Map map = (Map) userObj;
            Long id = MapUtils.getLong(map, "id");
            if(id != null) {
                user = userDataService.getByIdMinData(id);
            } else {
                user = null;
            }
        } else {
            user = null;
        }
        return user;
    }

    /**
     * Преобразовать одного пользователя в нужный для отправки вид<br/>
     * Данные будут включать также информацию о заполненности профиля
     */
    public Map<String, Object> convertUserToSend(User u) {
        Map<String, Object> sharer = serializeService.toPrimitiveObject(u);
        ProfileFilling profileFilling = profileService.getProfileFilling(u);
        sharer.put("profileFilling", serializeService.toPrimitiveObject(profileFilling));
        return sharer;
    }

    /**
     * Преобразовать список контактов в необходимый для отправки вид
     */
    private List<Map<String, Object>> map(List<Contact> contacts) {
        List<Map<String, Object>> result;
        if (contacts == null) {
            result = Collections.emptyList();
        } else {
            result = contacts.stream().map(contact -> convertUserToSend(contact.getOther())).collect(Collectors.toList());
        }
        return result;
    }

    public User getByIdMinData(Long id) {
        return userDataService.getByIdMinData(id);
    }

    public User saveUserData(Long userId, Map<FieldEntity, String> fieldsMap, Long currentUserId) {
        User user = userDataService.getByIdMinData(userId);

        if (!profileService.isAllowSave(user.getEmail())) {
            throw new ProfileException("У Вас нет прав на редактирование данного профиля");
        }

        List<Field> fields = new ArrayList<>();

        String phoneNumber = null;
        //boolean isFillRegistratorFields = true;

        for (Map.Entry<FieldEntity, String> entry : fieldsMap.entrySet()) {
            Field field = entry.getKey().toDomain();
            field.setValue(entry.getValue());
            fields.add(field);
            /*if (REGISTRATOR_OFFICE_FIELDS.contains(field.getInternalName())) {
                isFillRegistratorFields = isFillRegistratorFields && !StringUtils.isBlank(field.getValue());
            }*/

            if (field.getInternalName().equals("MOB_TEL")) phoneNumber = entry.getValue();
        }

        fieldsService.saveFields(user, fields, fieldValidatorBundle);

        String searchString = fieldsService.makeSearchString(user);
        userDataService.saveSearchString(user.getId(), searchString);

        if (phoneNumber != null) {
            String phoneNumberFromSettings = settingsManager.getUserSetting("mobile_phone.number", userId);

            if (!phoneNumber.equals(phoneNumberFromSettings)) {
                settingsManager.setUserSetting("mobile_phone.number", phoneNumber, user);
                settingsManager.setUserSetting("mobile_phone.verified", "false", user);
                settingsManager.setUserSetting("identification_mode", "fingerprint", user);
                settingsManager.deleteUserSetting("mobile_phone.verification_code", userId);
                settingsManager.deleteUserSetting("mobile_phone.verification_started_at", userId);
            }
        }
        if (currentUserId !=null && currentUserId.equals(userId)) {
            BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
            bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "user_" + userId + "_save_data", Collections.emptyMap()));
            blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());
            boolean isRegistrator = userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES);
            /*if (isRegistrator) {
                if (isFillRegistratorFields(user.getId())) {
                    bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
                    bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "registrator_" + userId + "_fill_data", Collections.emptyMap()));
                    blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
                } else {
                    bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
                    bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "registrator_" + userId + "_not_fill_data", Collections.emptyMap()));
                    blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
                }
            }*/

        }
        return user;
    }

    public boolean isFillRegistratorFields(Long registratorId) {
        User user = userDataService.getByIdFullData(registratorId);
        boolean result = true;
        if (user == null) {
            result = false;
        } else {
            for (Field field : user.getFields()) {
                if (REGISTRATOR_OFFICE_FIELDS.contains(field.getInternalName())) {
                    result = result && !StringUtils.isBlank(field.getValue());
                }
            }
        }
        return result;
    }
}
