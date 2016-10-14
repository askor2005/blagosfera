package ru.radom.kabinet.services.field;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldPossibleValueRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldsGroupRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.field.IFieldOwner;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldFileDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.*;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.GeoService;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.VarUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

@Service("fieldsService")
@Transactional
public class FieldsService {

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;

    @Autowired
    private SharerDao sharerDao;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private GeoService geoService;

    @Autowired
    private FieldFileDao fieldFileDao;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private FieldsGroupRepository fieldsGroupRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private FieldPossibleValueRepository fieldPossibleValueRepository;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    public FieldsService() {
    }

    private void check(boolean condition, String message) {
        if (condition) {
            throw new CommunityException(message);
        }
    }

    @Deprecated
    public void setFieldValueHidden(LongIdentifiable object, FieldEntity field, boolean hidden) {
        if (field.isHideable()) {
            FieldValueEntity fieldValue = fieldValueDao.get(object, field);
            if (fieldValue == null) {
                fieldValue = new FieldValueEntity();
                fieldValue.setField(field);
                fieldValue.setObject(object);
            }
            fieldValue.setHidden(hidden);
            fieldValueDao.saveOrUpdate(fieldValue);
        }
    }

    public String makeSearchString(User user) {
        //List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("PERSON_");

        List<FieldValueEntity> fields = fieldValueDao.getByObject(user.getId(), "SHARER");
        StringJoiner values = new StringJoiner(" ");
        values.add(user.getFullName().equals(user.getEmail()) ? " яяя### " : user.getFullName());
        values.add(user.getEmail());

        for (FieldValueEntity field : fields) {
            /*if ((!field.getField().isHideable() || !field.isHidden()) && !field.getStringValue().isEmpty()) {
                values.add(field.getStringValue());
            }*/

            if (!field.isHidden() && (field.getStringValue() != null) && !field.getStringValue().isEmpty()) {
                values.add(field.getStringValue());
            } else if (field.getField().getInternalName().equals("FCOUNTRY_CL")) {
                ListEditorItem universalListItem = listEditorItemDomainService.getById(VarUtils.getLong(field.getStringValue(), -1l));

                if (universalListItem != null) {
                    values.add(universalListItem.getText());
                }
            }
        }

        return " " + values.toString() + " ";
    }

    /**
     * Изменить видимость поля
     * @param object
     * @param fieldId
     * @param hidden
     */
    public void setFieldValueHidden(IFieldOwner object, Long fieldId, boolean hidden) {
        LongIdentifiable owner = getFieldObject(object);
        FieldEntity field = fieldDao.loadById(fieldId);
        setFieldValueHidden(owner, field, hidden);
    }

    @Deprecated
    public void setFieldValuesGroupHidden(LongIdentifiable object, FieldsGroupEntity fieldsGroup, boolean hidden) {
        for (FieldEntity field : fieldsGroup.getFields()) {
            setFieldValueHidden(object, field, hidden);
        }
    }

    /**
     * Изменить видимость группы полей
     * @param object
     * @param fieldsGroupId
     * @param hidden
     */
    public void setFieldValuesGroupHidden(IFieldOwner object, Long  fieldsGroupId, boolean hidden) {
        FieldsGroupEntity fieldsGroup = fieldsGroupDao.getById(fieldsGroupId);
        LongIdentifiable owner = getFieldObject(object);
        for (FieldEntity field : fieldsGroup.getFields()) {
            setFieldValueHidden(owner, field, hidden);
        }
    }

    @Deprecated
    private FieldValueEntity getFieldValue(LongIdentifiable object, FieldEntity field) {
        FieldValueEntity fieldValue = fieldValueDao.get(object, field);
        if (fieldValue == null) {
            fieldValue = new FieldValueEntity();
            fieldValue.setField(field);
            fieldValue.setHidden(field.isHiddenByDefault());
            fieldValue.setObject(object);
        }
        return fieldValue;
    }

    private LongIdentifiable getFieldObject(IFieldOwner object) {
        LongIdentifiable owner = null;
        if (object instanceof Community) {
            owner = communityRepository.findOne(object.getId());
        } else if (object instanceof User) {
            owner = sharerDao.loadById(object.getId());
        }
        return owner;
    }

    private FieldValueEntity getFieldValue(IFieldOwner object, FieldEntity field) {
        LongIdentifiable owner = getFieldObject(object);
        FieldValueEntity fieldValue = fieldValueDao.get(owner, field);
        if (fieldValue == null) {
            fieldValue = new FieldValueEntity();
            fieldValue.setField(field);
            fieldValue.setHidden(field.isHiddenByDefault());
            fieldValue.setObject(owner);
        }
        return fieldValue;
    }

    private String prepareFieldValue(FieldEntity field, String stringValue) {
        String result = stringValue;

        // по типу филда
        switch (field.getType()) {
            case NUMBER:
                try {
                    if (org.apache.commons.lang3.StringUtils.isBlank(result)) {
                        result = "";
                    } else {
                        Long value = VarUtils.getLong(result, null);
                        ExceptionUtils.check(value == null, "Поле \"" + field.getName() + "\" должно быть числом");
                        result = value.toString();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Поле \"" + field.getName() + "\" должно быть числом");
                }
                break;
            case CURRENCY:
                try {
                    if (org.apache.commons.lang3.StringUtils.isBlank(result)) {
                        result = "0.00";
                    }
                    result = result.replace(",", "."); //result = StringUtils.prepareNumber(result);
                    BigDecimal bd = new BigDecimal(result);
                    bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
                    result = bd.toString();
                } catch (Exception e) {
                    throw new RuntimeException("Поле \"" + field.getName() + "\" должно быть числом");
                }
                break;

            case HTML_TEXT: // Тип поля - хтмл
                result = StringUtils.removeUnsafeTags(result);
                break;

            default:
                result = StringUtils.prepareString(result);
                break;
        }

        // по самому филду
        switch (field.getInternalName()) {
            case "ENTRANCE_SHARE_FEES":
            case "MIN_SHARE_FEES":
            case "MEMBERSHIP_FEES":
            case "COMMUNITY_ENTRANCE_SHARE_FEES":
            case "COMMUNITY_MIN_SHARE_FEES":
            case "COMMUNITY_MEMBERSHIP_FEES":
                if (!StringUtils.isEmpty(result)) {
                    BigDecimal bd = new BigDecimal(result);
                    if (bd.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("Поле \"" + field.getName() + "\" не может быть отрицательным числом");
                    }
                }
                break;
        }
        return result;
    }

    /**
     * Сохранить значения для полей с проверкой значений
     * @param owner
     * @param fields
     * @param fieldValidatorBundle
     */
    public void saveFields(IFieldOwner owner, List<Field> fields, IFieldValidatorBundle fieldValidatorBundle) {
        if (fields == null) {
            throw new FieldException("Не переданы значения полей");
        }
        List<FieldValidateResult> fieldValidateResults = fieldValidatorBundle.validate(fields, owner);
        if (fieldValidateResults != null) {
            List<FieldValidateResult> errorsFieldValidates = new ArrayList<>();
            for (FieldValidateResult fieldValidateResult : fieldValidateResults) {
                if (!fieldValidateResult.isSuccess()) {
                    errorsFieldValidates.add(fieldValidateResult);
                }
            }
            if (errorsFieldValidates.size() > 0) {
                throw new FieldException("Произошли ошибки во время проверки значений полей", errorsFieldValidates);
            }
        }
        for (Field field : fields) {
            if (field != null) {
                FieldEntity fieldEntity = fieldDao.getById(field.getId());
                FieldValueEntity fieldValue = getFieldValue(owner, fieldEntity);
                String value = field.getValue() == null ? "" : field.getValue();
                //System.err.println(fieldEntity.getInternalName() + " : " + value);
                fieldValue.setStringValue(prepareFieldValue(fieldEntity, value));
                fieldValueDao.saveOrUpdate(fieldValue);
            }
        }
    }

    public FieldValueEntity setFieldValue(LongIdentifiable owner, FieldEntity field, String stringValue) {
        FieldValueEntity fieldValue = getFieldValue(owner, field);
        fieldValue.setStringValue(prepareFieldValue(field, stringValue));
        checkFieldValue(fieldValue);
        fieldValueDao.saveOrUpdate(fieldValue);
        return fieldValue;
    }

    public String getFieldValue(User user, String fieldCode) {
        FieldEntity entity = fieldRepository.findOneByInternalName(fieldCode);
        FieldValueEntity fieldValue = getFieldValue(user, entity);
        return fieldValue.getStringValue();
    }

    /*
    @Transactional(rollbackFor = Exception.class)
    public Sharer saveFields(Map<FieldEntity, String> fieldsMap, Sharer owner, Sharer editor) {
        try {
            for (Map.Entry<FieldEntity, String> entry : fieldsMap.entrySet()) {
                if (checkFieldValueChangeAllowed(entry.getKey(), owner, editor)) {
                    setFieldValue(owner, entry.getKey(), entry.getValue());
                }
            }

            sharerDao.refresh(owner);
            sharerDao.update(owner);

            profileService.updateProfileUnfilledAt(owner);
            return owner;
        } catch (Exception e) {
            throw new FieldsException(e.getMessage());
        }
    }*/

    // продублированно из CommunitiesController.create, так как по идее тут тоже должа присутствовать валидация такого типа,
    // но в CommunitiesController.create сообщество создаётся до проверки полей, а если изменить порядок проверки полей и создания объединения,
    // то при сохранении полей у объединения не будет id, а это ошибка
    private void checkCommunityFieldValue(CommunityEntity community, FieldEntity field, String fieldValue) {
        if(org.apache.commons.lang3.StringUtils.equals(field.getInternalName(),"COMMUNITY_TYPE")) {
            if(org.apache.commons.lang3.StringUtils.equals(fieldValue, "COMMUNITY_WITH_ORGANIZATION")) {
                if (!community.getAccessType().equals(CommunityAccessType.CLOSE)) {
                    throw new CommunityException("Объединение в рамках юр.лица может быть только закрытым");
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CommunityEntity saveFields(Map<FieldEntity, String> fieldsMap, CommunityEntity community) {
        try {
            check(community.getVerified() != null && community.getVerified(), "Нельзя изменять данные у сертифицированной организации");
            for (FieldEntity field : fieldsMap.keySet()) {
                String fieldValue = fieldsMap.get(field);
                checkCommunityFieldValue(community, field, fieldValue);
                setFieldValue(community, field, fieldValue);
            }
            return community;
        } catch (Exception e) {
            throw new CommunityException(e.getMessage());
        }
    }

    /*
    @Transactional(rollbackFor = Exception.class)
    public Sharer saveFields(Map<FieldEntity, String> fieldsMap, Sharer owner) {
        for (Map.Entry<FieldEntity, String> entry : fieldsMap.entrySet()) {
            if (checkFieldValueChangeAllowed(entry.getKey(), owner, owner)) {
                setFieldValue(owner, entry.getKey(), entry.getValue());
            }
        }
        return owner;
    }

    // аналогичен saveFields(Map<FieldEntity, String> fieldsMap, Sharer owner), только прокидывает исключение в случае неудачи при сохранении
    @Transactional(rollbackFor = Exception.class)
    public Sharer saveFieldsWithException(Map<FieldEntity, String> fieldsMap, Sharer owner) {
        for (Map.Entry<FieldEntity, String> entry : fieldsMap.entrySet()) {
            checkFieldValueChangeAllowedWithException(entry.getKey(), owner, owner);
            setFieldValue(owner, entry.getKey(), entry.getValue());
        }
        return owner;
    }*/

    /*public void updateGeoData(Sharer owner) {
        Address registrationAddress = geoService.updateGeoData(owner.getRegistrationAddress());
        setFieldValue(owner, fieldDao.getByInternalName("GEO_POSITION"), registrationAddress.getGeoPosition());
        setFieldValue(owner, fieldDao.getByInternalName("GEO_LOCATION"), registrationAddress.getGeoLocation());

        Address actualAddress = geoService.updateGeoData(owner.getActualAddress());
        setFieldValue(owner, fieldDao.getByInternalName("F_GEO_POSITION"), actualAddress.getGeoPosition());
        setFieldValue(owner, fieldDao.getByInternalName("F_GEO_LOCATION"), actualAddress.getGeoLocation());

        Address registratorOfficeAddress = geoService.updateGeoData(owner.getRegistratorOfficeAddress());
        setFieldValue(owner, fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_POSITION"), registratorOfficeAddress.getGeoPosition());
        setFieldValue(owner, fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_LOCATION"), registratorOfficeAddress.getGeoLocation());
    }*/


    private void checkFieldValue(FieldValueEntity fieldValue) {
        // TODO Отключил проверку уникальности полей. По замыслу она теперь полностью возлагается на регистратора. http://projects.ramera.ru/browse/RAMERA-482
        //if (fieldValue.getField().isUnique() && StringUtils.hasLength(fieldValue.getStringValue())) {
        //    FieldValueEntity otherFieldValue = fieldValueDao.getByValue(fieldValue.getStringValue());
        //    if (otherFieldValue != null && !otherFieldValue.getId().equals(fieldValue.getId())) {
        //        throw new FieldsException("Такое значение " + fieldValue.getField().getName() + " уже существует");
        //    }
        //}
    }

    // аналогичен checkFieldValueChangeAllowed(FieldEntity field, Sharer owner, Sharer viewer), но прокидывает исключение
    /*public void checkFieldValueChangeAllowedWithException(FieldEntity field, Sharer owner, Sharer viewer) {
        // Супер админ не имеет прав на редактирование полей суперадмина
        if (!viewer.equals(owner) && profileService.hasRole(viewer, Roles.ROLE_SUPERADMIN) && profileService.hasRole(owner, Roles.ROLE_SUPERADMIN)) {
            throw new FieldsException("");
            //return false;
        } else if (!viewer.equals(owner) && profileService.hasRole(viewer, Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR) && profileService.hasAnyRole(owner, Roles.REGISTRATOR_ROLES)) {
            //return true;
        } else if (!viewer.equals(owner) && profileService.hasRole(viewer, Roles.ROLE_REGISTRATOR_SHARERS_EDITOR) && !profileService.hasAnyRole(owner, Roles.REGISTRATOR_ROLES)) {
            //return true;
        } else if (viewer.equals(owner) && profileService.hasRole(owner, Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR)) {
            //return true;
        } else if(viewer.equals(owner) && owner.isVerified() && field.getInternalName().equals("SHARER_SHORT_LINK_NAME")) { // это значит что любой сертифицированный участник сам себе может менять ссылку в любое время и на любом этапе, а не сертифицированный не может
            //return true;
        }else if(viewer.equals(owner) && !owner.isVerified() && field.getInternalName().equals("SHARER_SHORT_LINK_NAME")) { // это значит что любой сертифицированный участник сам себе может менять ссылку в любое время и на любом этапе, а не сертифицированный не может
            throw new FieldsException("Только сертифицированный пользователь может изменить ссылку на страницу профиля");
            //return false;
        }else if (viewer.equals(owner) && !owner.isVerified()) {
            //return true;
        } else if (viewer.equals(owner) && field.isVerifiedEditable()) {
            //return true;
        } else if (profileService.hasRole(viewer, Roles.ROLE_SUPERADMIN)) {
            //return true;
        }   else {
            throw new FieldsException("");
            //return false;
        }
    }*/

    public boolean checkFieldValueChangeAllowed(FieldEntity field, UserDetailsImpl owner, UserDetailsImpl viewer) {
        //boolean isOwnerActiveRegistrator = registratorService.isActiveRegistrator(viewer);
        RegistrationRequest registrationRequest = registrationRequestService.getByObject(owner.getUser());
        boolean isActiveRegistrationRequest = registrationRequest != null && RegistrationRequestStatus.NEW.equals(registrationRequest.getStatus());
        boolean isViewerActiveRegistrator = registratorService.isActiveRegistrator(viewer);
        if (!viewer.equals(owner) && viewer.hasRole(Roles.ROLE_SUPERADMIN) && owner.hasRole(Roles.ROLE_SUPERADMIN)) {
            // Супер админ не имеет прав на редактирование полей суперадмина
            return false;
        } else if (viewer.hasRole(Roles.ROLE_SUPERADMIN)) {
            // Супер админ всегда может редактировать поля
            return true;
        } else if ( // Регистратор всегда может редактировать поля регистратора
                viewer.getUsername().equals(owner.getUsername()) && owner.hasAnyRole(Roles.REGISTRATOR_ROLES) &&
                field.getFieldsGroup() != null &&
                (
                        field.getFieldsGroup().getInternalName().equals("REGISTRATOR_OFFICE_ADDRESS") ||
                        field.getFieldsGroup().getInternalName().equals("REGISTRATOR_CONTACTS")
                )
        ) {
            return true;
        } else if (!viewer.getUsername().equals(owner.getUsername()) && isViewerActiveRegistrator && viewer.hasRole(Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR) && owner.hasAnyRole(Roles.REGISTRATOR_ROLES)) {
            // Редактор регистраторов может редактировать поля других регистраторов
            return true;
        } else if (!viewer.equals(owner) && isViewerActiveRegistrator && viewer.hasRole(Roles.ROLE_REGISTRATOR_SHARERS_EDITOR) && !owner.hasAnyRole(Roles.REGISTRATOR_ROLES) && isActiveRegistrationRequest) {
            // Редактор пользователей может редактировать поля пользователей
            return true;
        } else if (viewer.equals(owner) && owner.hasRole(Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR)) {
            // Редактор регистраторов может редактировать свои поля
            return true;
        } else if(viewer.equals(owner) && owner.getUser().isVerified() && field.getInternalName().equals("SHARER_SHORT_LINK_NAME")) {
            // это значит что любой сертифицированный участник сам себе может менять ссылку в любое время и на любом этапе, а не сертифицированный не может
            return true;
        } else if(viewer.equals(owner) && !owner.getUser().isVerified() && field.getInternalName().equals("SHARER_SHORT_LINK_NAME")) {
            // это значит что любой сертифицированный участник сам себе может менять ссылку в любое время и на любом этапе, а не сертифицированный не может
            return false;
        } else if (viewer.equals(owner) && !owner.getUser().isVerified()) {
            // Не сертифицированный участник может редактировать свои поля
            return true;
        } else if (viewer.equals(owner) && field.isVerifiedEditable()) {
            // Сертифицированный участник с возможностью редактирования полей
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFieldVisible(FieldEntity field, UserDetailsImpl owner, UserDetailsImpl viewer) {
        if (owner.getUsername().equals(viewer.getUsername()) || checkFieldValueChangeAllowed(field, owner, viewer) || !field.isHideable()) {
            return true;
        } else {
            FieldValueEntity fieldValue = fieldValueDao.get(owner.getUser().getId(), "SHARER", field);
            return fieldValue != null && !fieldValue.isHidden();
        }
    }

    public Map<FieldEntity, FieldStates> getFieldsStatesMap(List<FieldsGroupEntity> fieldsGroups, UserDetailsImpl owner, UserDetailsImpl viewer) {
        Map<FieldEntity, FieldStates> map = new HashMap<>();
        List<FieldEntity> fields = fieldDao.getByGroups(fieldsGroups);
        for (FieldEntity field : fields) {
            map.put(field, new FieldStates(field, checkFieldValueChangeAllowed(field, owner, viewer), checkFieldVisible(field, owner, viewer)));
        }
        return map;
    }

    /*public Map<FieldEntity, String> requestToFieldsFileUrlMap(HttpServletRequest request) {
        Map<FieldEntity, String> resultMap = new HashMap<>();
        // Устанавливаем урлы файлов полей
        Enumeration<String> parameterNameEnumeration = request.getParameterNames();
        while (parameterNameEnumeration.hasMoreElements()) {
            String parameterName = parameterNameEnumeration.nextElement();
            if (parameterName.startsWith("f:fileUrl")) {
                Long fieldId = Long.parseLong(parameterName.substring(10));
                String fileUrl = request.getParameter(parameterName);
                FieldEntity field = fieldDao.getById(fieldId);
                resultMap.put(field, fileUrl);
            }
        }
        return resultMap;
    }*/

    public Map<FieldEntity, String> requestToFieldsMap(HttpServletRequest request) {
        Map<FieldEntity, String> fieldValueMap = new HashMap<>();
        Enumeration<String> parameterNameEnumeration = request.getParameterNames();

        while (parameterNameEnumeration.hasMoreElements()) {
            String parameterName = parameterNameEnumeration.nextElement();

            if (parameterName.startsWith("f:fileUrl")) {
                // do nothing
            } else if (parameterName.startsWith("f:")) {
                Long fieldId = Long.parseLong(parameterName.substring(2));
                String fieldValueString = request.getParameter(parameterName);
                FieldEntity field = fieldDao.getById(fieldId);
                fieldValueMap.put(field, fieldValueString);
            }
        }

        return fieldValueMap;
    }

    /**
     * Получаем из запроса файлы полей в виде мапы с ключём - ИД поля
     * @param request
     * @return
     */
    public Map<Long, List<FieldFileEntity>> requestToFieldsFilesMap(HttpServletRequest request, String parameterName) {
        String fieldsFilesJson = request.getParameter(parameterName);
        Type mapType = new TypeToken<HashMap<Long, List<FieldFileEntity>>>() {}.getType();
        Map<Long, List<FieldFileEntity>> fieldFilesMap = new Gson().fromJson(fieldsFilesJson, mapType);
        return fieldFilesMap;
    }

    public static String getFieldStringValue(Field field) {
        String result = null;
        if (field != null && !org.apache.commons.lang3.StringUtils.isEmpty(field.getValue())) {
            switch(field.getType()) {
                case HTML_TEXT:
                case NUMBER:
                    result = field.getValue();
                    break;
                default: // Декодим спецсимволы html
                    result = StringEscapeUtils.unescapeHtml4(field.getValue());
                    break;
            }
        }
        return result;
    }

    /**
     * Получить значение поля.
     * @param fieldValue
     * @return
     */
    public static String getFieldStringValue(FieldValueEntity fieldValue) {
        String result = null;
        if (fieldValue != null && !org.apache.commons.lang3.StringUtils.isEmpty(fieldValue.getStringValue())) {
            switch(fieldValue.getField().getType()) {
                case HTML_TEXT:
                case NUMBER:
                    result = fieldValue.getStringValue();
                    break;
                default: // Декодим спецсимволы html
                    result = StringEscapeUtils.unescapeHtml4(fieldValue.getStringValue());
                    break;
            }
        }
        return result;
    }

    /**
     * Получить список участников системы из поля типа PARTICIPANTS_LIST
     * @param fieldValue
     * @return
     */
    /*public List<Sharer> getSharersFromParticipantsListField(FieldValueEntity fieldValue) {
        if (fieldValue != null && fieldValue.getField() != null && fieldValue.getField().getType() != FieldType.PARTICIPANTS_LIST) {
            throw new RuntimeException("Поле " + fieldValue.getField().getInternalName() + " не явлеятеся полем с типом PARTICIPANTS_LIST");
        }
        List<Sharer> result = new ArrayList<>();
        String fieldStringValue = getFieldStringValue(fieldValue);
        if (fieldStringValue != null && !fieldStringValue.equals("")) {
            String[] idsStr = fieldStringValue.split(";");
            for (String idStr : idsStr) {
                long sharerId = -1;
                try {
                    sharerId = Long.valueOf(idStr).longValue();
                } catch (Exception e) {
                    // do nothing
                }
                if (sharerId > -1) {
                    Sharer sharer = sharerDao.getById(sharerId);
                    if (sharer != null) {
                        result.add(sharer);
                    }
                }
            }
        }
        return result;
    }*/

    /**
     * Получить участника системы из поля с типом SHARER
     * @param fieldValue
     * @return
     */
    /*public Sharer getSharerFromSharerField(FieldValueEntity fieldValue) {
        *//*if (fieldValue != null && fieldValue.getField() != null && fieldValue.getField().getType() != FieldType.SHARER) {
            throw new RuntimeException("Поле " + fieldValue.getField().getInternalName() + " не явлеятеся полем с типом SHARER");
        }*//*
        Sharer result = null;
        String fieldStringValue = getFieldStringValue(fieldValue);
        long sharerId = VarUtils.getLong(fieldStringValue, -1l);
        if (sharerId > -1) {
            result = sharerDao.getById(sharerId);
        }
        return result;
    }*/

    /**
     * Сохранить файлы полей у объединения
     * @param field
     * @param fieldFilesForm
     * @param community
     */
    private void saveFieldFiles(FieldEntity field, List<FieldFileEntity> fieldFilesForm, CommunityEntity community) {
        FieldValueEntity fieldValue = getFieldValue(community, field);
        if (fieldValue.getId() == null) {
            fieldValueDao.save(fieldValue);
        }
        saveFieldFiles(fieldValue, fieldFilesForm);
    }

    public void saveFieldFiles(Long fieldId, Long communityId, List<FieldFile> fieldFiles) {
        FieldEntity fieldEntity = fieldRepository.findOne(fieldId);
        Community community = communityDomainService.getByIdMinData(communityId);
        FieldValueEntity fieldValue = getFieldValue(community, fieldEntity);
        if (fieldValue.getId() == null) {
            fieldValueDao.save(fieldValue);
        }
        List<FieldFileEntity> fieldFileEntities = new ArrayList<>();
        if (fieldFiles != null) {
            for (FieldFile fieldFile : fieldFiles) {
                FieldFileEntity fieldFileEntity = new FieldFileEntity();
                fieldFileEntity.setName(fieldFile.getName());
                fieldFileEntity.setUrl(fieldFile.getUrl());
                fieldFileEntity.setId(fieldFile.getId());
                fieldFileEntities.add(fieldFileEntity);
            }
        }

        saveFieldFiles(fieldValue, fieldFileEntities);
    }

    /**
     * Сохранить файлы полей у пользователя
     * @param field
     * @param fieldFilesForm
     * @param userEntity
     */
    public void saveFieldFiles(FieldEntity field, List<FieldFileEntity> fieldFilesForm, UserEntity userEntity) {
        FieldValueEntity fieldValue = getFieldValue(userEntity, field);
        if (fieldValue.getId() == null) {
            fieldValueDao.save(fieldValue);
        }
        saveFieldFiles(fieldValue, fieldFilesForm);
    }

    private void saveFieldFiles(FieldValueEntity fieldValue, List<FieldFileEntity> fieldFilesForm) {
        // TODO СДелать проверки прав на сохранение данных
        List<FieldFileEntity> allOldFieldValues = fieldFileDao.getByFieldValue(fieldValue);
        List<FieldFileEntity> oldFieldValues = new ArrayList<>();
        for (FieldFileEntity fieldFileForm : fieldFilesForm) {
            if (fieldFileForm.getId() == null) {
                fieldFileForm.setFieldValue(fieldValue);
                fieldFileDao.saveOrUpdate(fieldFileForm);
            } else {
                FieldFileEntity fieldFile = fieldFileDao.getById(fieldFileForm.getId());
                fieldFile.setName(fieldFileForm.getName());
                fieldFile.setUrl(fieldFileForm.getUrl());
                fieldFileDao.saveOrUpdate(fieldFile);
                oldFieldValues.add(fieldFile);
            }
        }
        Iterator<FieldFileEntity> fileIterator = allOldFieldValues.iterator();
        while(fileIterator.hasNext()) {
            FieldFileEntity fieldFile = fileIterator.next();
            if (!oldFieldValues.contains(fieldFile)) {
                fieldFileDao.delete(fieldFile);
            }
        }
    }

    private void fixCommunityField(FieldValueEntity fieldValue, String stringValue) {
        if (stringValue != null && !stringValue.equals(getFieldStringValue(fieldValue))) {
            // По какой то хер здесь падает при сохранении объединения что в сессии 2 объекта
            if (fieldValue.getId() != null) {
                fieldValue = fieldValueDao.getById(fieldValue.getId());
            }
            fieldValue.setStringValue(stringValue);
            fieldValueDao.saveOrUpdate(fieldValue);
        }
    }

    // TODO Падала ошибка Illegal attempt to associate a collection with two open sessions
    /*@Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Transactional
    public void updateCommunityFields(CommunityEntity community) {
        fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_FULL_RU_NAME), community.getName());
        fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_DESCRIPTION), community.getDescription());
        fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_BRIEF_DESCRIPTION), community.getAnnouncement());
        fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_SHORT_LINK_NAME), community.getSeoLink());
        if (community.getInn() == null) {
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_GEO_LOCATION), community.getGeoLocation());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_GEO_POSITION), community.getGeoPosition());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_REGION), community.getRegion());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_AREA), community.getDistrict());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_LOCALITY), community.getCity());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_STREET), community.getStreet());
            fixCommunityField(getFieldValue(community, FieldConstants.COMMUNITY_HOUSE), community.getBuilding());
        }
    }

    private FieldValueEntity getFieldValue(CommunityEntity community, String name) {
        FieldValueEntity fieldValue = community.getFieldValue(name);
        if (fieldValue == null) {
            FieldEntity field = fieldDao.getByInternalName(name);
            fieldValue = new FieldValueEntity();
            fieldValue.setHidden(false);
            fieldValue.setField(field);
            fieldValue.setObject(community);
            fieldValueDao.save(fieldValue);

            community.putFieldValue(fieldValue.getField(), fieldValue);
            community.putFieldValue(fieldValue.getField().getInternalName(), fieldValue);
        }
        return fieldValue;
    }*/

    /**
     * Коллекция групп полей для объединения по типу
     * @param withOrganization тип объединения
     * @return
     */
    public List<FieldsGroup> getFieldGroupsCommunity(boolean withOrganization) {
        List<FieldsGroupEntity> fieldsGroupEntities = fieldsGroupRepository.findAllByObjectType(Discriminators.COMMUNITY);
        List<FieldsGroupEntity> foundGroupEntities = new ArrayList<>();
        for (FieldsGroupEntity fieldsGroupEntity : fieldsGroupEntities) {
            if (fieldsGroupEntity.getInternalName() != null) {
                if (withOrganization &&
                        (
                            fieldsGroupEntity.getInternalName().startsWith("COMMUNITY_WITH_ORGANIZATION_") ||
                            fieldsGroupEntity.getInternalName().startsWith("COMMUNITY_ADDITIONAL_GROUP_") ||
                            fieldsGroupEntity.getInternalName().equals("COMMUNITY_COMMON")
                        )){
                    foundGroupEntities.add(fieldsGroupEntity);
                } else if (!withOrganization &&
                        (
                                fieldsGroupEntity.getInternalName().startsWith("COMMUNITY_WITHOUT_ORGANIZATION_") ||
                                fieldsGroupEntity.getInternalName().equals("COMMUNITY_COMMON")
                        )) {
                    foundGroupEntities.add(fieldsGroupEntity);
                }
            }
        }
        return FieldsGroupEntity.toDomainList(foundGroupEntities, true, true);
    }

    /**
     * Существует значение поля
     * @param internalName
     * @param stringValue
     * @param objectIdExclude
     * @return
     */
    public boolean fieldValueExists(String internalName, String stringValue, Long objectIdExclude) {
        boolean result = false;
        List<FieldValueEntity> fieldValueEntities = fieldValueDao.getByValue(internalName, stringValue);
        if (fieldValueEntities != null && fieldValueEntities.size() == 1) {
            FieldValueEntity fieldValueEntity = fieldValueEntities.get(0);
            if (!fieldValueEntity.getObject().getId().equals(objectIdExclude)) {
                result = true;
            }
        } else if (fieldValueEntities != null && fieldValueEntities.size() > 1) {
            result = true;
        }
        return result;
    }
    public void setFieldValueHidden(String internalName,boolean hidden){
        FieldEntity field = fieldDao.getByInternalName(internalName);
        if (field != null) {
            User user = SecurityUtils.getUser();

            setFieldValueHidden(sharerDao.getById(user.getId()), field, hidden);

            String searchString = makeSearchString(user);
            userDataService.saveSearchString(user.getId(), searchString);
        }
    }
}