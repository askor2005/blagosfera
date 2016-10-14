package ru.askor.blagosfera.core.services.user;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldValueRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldsGroupRepository;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;

import java.util.*;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.createLogger(UserService.class);
    @Autowired
    private RegistratorDataService registratorDataService;
    @Autowired
    private SharerService sharerService;
    @Autowired
    private FieldDao fieldDao;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FieldsGroupRepository fieldsGroupRepository;

    @Autowired
    private FieldValueRepository fieldValueRepository;

    public UserServiceImpl() {
    }

    @Override
    public User getUserById(Long userId) {
        UserEntity userEntity = userRepository.findOne(userId);
        if (userEntity == null) return null;
        User user = userEntity.toDomain();
        user.getFields().addAll(getUserFields(user.getId()));
        return user;
    }

    @Override
    public User getUserByIkp(String ikp) {
        UserEntity userEntity = userRepository.findOneByIkp(ikp);
        if (userEntity == null) return null;
        User user = userEntity.toDomain();
        user.getFields().addAll(getUserFields(user.getId()));
        return user;
    }

    @Override
    public List<Field> getUserFields(Long userId) {
        List<FieldEntity> allFields = fieldRepository.findAllByFieldsGroup_ObjectType(Discriminators.SHARER);
        List<FieldValueEntity> userFields = fieldValueRepository.findAllByObjectIdAndObjectType(userId, Discriminators.SHARER);
        List<Field> fields = new ArrayList<>();

        for (FieldEntity fieldEntity : allFields) {
            Field field = fieldEntity.toDomain();
            FieldValueEntity foundFieldValue = null;

            for (FieldValueEntity userField : userFields) {
                if (fieldEntity.getId().equals(userField.getField().getId())) {
                    foundFieldValue = userField;
                    break;
                }
            }
            field.setHideable(fieldEntity.isHideable());
            field.setValue(foundFieldValue != null ? foundFieldValue.getStringValue() : null);
            field.setHidden(foundFieldValue != null ? foundFieldValue.isHidden() : false);
            field.setValue(foundFieldValue != null ? foundFieldValue.getStringValue() : null);
            if (foundFieldValue != null) {
                field.setFieldFiles(FieldFileEntity.toDomainList(foundFieldValue.getFieldFiles()));
            }
            fields.add(field);
        }

        return fields;
    }
    @Override
    public User createFakeUser(String email, String firstName, String secondName, String lastName) {
        User user = new User();

        //Почта
        user.setEmail(email);

        //Фейковые ФИО
        FieldEntity firstNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_FIRSTNAME);
        FieldValueEntity firstNameFieldValue = new FieldValueEntity();
        firstNameFieldValue.setField(firstNameFieldEntity);
        firstNameFieldValue.setStringValue(firstName);

        FieldEntity secondNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_SECONDNAME);
        FieldValueEntity secondNameFieldValue = new FieldValueEntity();
        secondNameFieldValue.setField(secondNameFieldEntity);
        secondNameFieldValue.setStringValue(secondName);

        FieldEntity lastNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_LASTNAME);
        FieldValueEntity lastNameFieldValue = new FieldValueEntity();
        lastNameFieldValue.setField(lastNameFieldEntity);
        lastNameFieldValue.setStringValue(lastName);

        Field firstNameField = new Field(
                firstNameFieldEntity.getId(), firstNameFieldValue.getStringValue(),  firstNameFieldEntity.getInternalName(),
                firstNameFieldEntity.getName(), firstNameFieldValue.isHidden(), firstNameFieldEntity.isHideable(),
                firstNameFieldEntity.getComment(), firstNameFieldEntity.getType(), firstNameFieldEntity.getExample(),
                firstNameFieldEntity.getPoints(), firstNameFieldEntity.isRequired(), firstNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(firstNameFieldEntity.getAttachedFile(), false), null,
                firstNameFieldEntity.getFieldsGroup().getInternalName(),
                firstNameFieldEntity.getMask(), firstNameFieldEntity.getPlaceholder());
        Field secondNameField = new Field(
                secondNameFieldEntity.getId(), secondNameFieldValue.getStringValue(),  secondNameFieldEntity.getInternalName(),
                secondNameFieldEntity.getName(), secondNameFieldValue.isHidden(), secondNameFieldEntity.isHideable(),
                secondNameFieldEntity.getComment(), secondNameFieldEntity.getType(), secondNameFieldEntity.getExample(),
                secondNameFieldEntity.getPoints(), secondNameFieldEntity.isRequired(), secondNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(secondNameFieldEntity.getAttachedFile(), false), null,
                secondNameFieldEntity.getFieldsGroup().getInternalName(),
                secondNameFieldEntity.getMask(), secondNameFieldEntity.getPlaceholder());
        Field lastNameField = new Field(
                lastNameFieldEntity.getId(), lastNameFieldValue.getStringValue(), lastNameFieldEntity.getInternalName(),
                lastNameFieldEntity.getName(), lastNameFieldValue.isHidden(), lastNameFieldEntity.isHideable(),
                lastNameFieldEntity.getComment(), lastNameFieldEntity.getType(), lastNameFieldEntity.getExample(),
                lastNameFieldEntity.getPoints(), lastNameFieldEntity.isRequired(), lastNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(lastNameFieldEntity.getAttachedFile(), false), null,
                lastNameFieldEntity.getFieldsGroup().getInternalName(),
                lastNameFieldEntity.getMask(), lastNameFieldEntity.getPlaceholder());

        user.getFields().addAll(Arrays.asList(firstNameField, secondNameField, lastNameField));
        return user;
    }

    @Override
    public void saveBasicInfo(Long id, Map<String, String> basicInformation) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapBasicInformation(basicInformation);
        sharerService.saveUserData(id, fieldsMap, SecurityUtils.getUser().getId());
    }
    public Map<FieldEntity, String> getBasicInfoMapFieldEntity(Long id, Map<String, String> basicInformation) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapBasicInformation(basicInformation);
        return fieldsMap;
    }
    private Map<FieldEntity, String> getFieldsMapBasicInformation(Map<String, String> basicInformation){
        Map<FieldEntity,String> result = new HashMap<>();
        for (String key : basicInformation.keySet()){
            String internalName = basicInformationToFieldsInternalNameMapping.get(key);
            if (internalName != null) {
                result.put(fieldRepository.findOneByInternalName(internalName),basicInformation.get(key));
            }
        }
        return result;
    }
    private Map<FieldEntity, String> getFieldsMapRegAddress(Map<String, String> regAddress){
        Map<FieldEntity,String> result = new HashMap<>();
        for (String key : regAddress.keySet()){
            String internalName = regAddressToFieldsInternalNameMapping.get(key);
            if (internalName != null) {
                result.put(fieldRepository.findOneByInternalName(internalName),regAddress.get(key));
            }
        }
        return result;
    }
    private Map<FieldEntity, String> getFieldsMapFactAddress(Map<String, String> factAddress){
        Map<FieldEntity,String> result = new HashMap<>();
        for (String key : factAddress.keySet()){
            String internalName = factAddressToFieldsInternalNameMapping.get(key);
            if (internalName != null) {
                result.put(fieldRepository.findOneByInternalName(internalName), factAddress.get(key));
            }
        }
        return result;
    }
    @Override
    public void saveRegAddress(Long id, Map<String, String> regAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapRegAddress(regAddress);
        sharerService.saveUserData(id, fieldsMap, SecurityUtils.getUser().getId());

    }
    public  Map<FieldEntity, String> getRegAddressFieldsMapEntities(Long id, Map<String, String> regAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapRegAddress(regAddress);
        return fieldsMap;

    }
    @Override
    public void saveRegistratorData(Long id, Map<String, String> registratorData) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapRegistratorData(registratorData);
        sharerService.saveUserData(id, fieldsMap, SecurityUtils.getUser().getId());

    }
    public Map<FieldEntity, String> getRegistratorDataFieldsEntityMap(Long id, Map<String, String> registratorData) {
         return getFieldsMapRegistratorData(registratorData);

    }

    private Map<FieldEntity, String> getFieldsMapRegistratorData(Map<String, String> registratorData) {
        Map<FieldEntity,String> result = new HashMap<>();
        for (String key : registratorData.keySet()){
            String internalName = registratorDataToFieldsInternalNameMapping.get(key);
            if (internalName != null) {
                result.put(fieldRepository.findOneByInternalName(internalName),registratorData.get(key));
            }
        }
        return result;
    }

    @Override
    public void saveRegistratorOfficeAddress(Long id, Map<String, String> registratorOfficeAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapRegOfficeAddress(registratorOfficeAddress);
        sharerService.saveUserData(id, fieldsMap, SecurityUtils.getUser().getId());

    }
    public  Map<FieldEntity, String> getRegistratorOfficeAddressFieldEntityMap(Long id, Map<String, String> registratorOfficeAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapRegOfficeAddress(registratorOfficeAddress);
        return fieldsMap;

    }

    private Map<FieldEntity,String> getFieldsMapRegOfficeAddress(Map<String, String> registratorOfficeAddress) {
        Map<FieldEntity,String> result = new HashMap<>();
        for (String key : registratorOfficeAddress.keySet()){
            String internalName = registratorOfficeAddressToFieldsInternalNameMapping.get(key);
            if (internalName != null) {
                result.put(fieldRepository.findOneByInternalName(internalName),registratorOfficeAddress.get(key));
            }
        }
        return result;
    }

    @Override
    public void saveFactAddress(Long id, Map<String, String> factAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapFactAddress(factAddress);
        sharerService.saveUserData(id, fieldsMap, SecurityUtils.getUser().getId());
    }
    public Map<FieldEntity, String> getFactAddressFieldsEntityMap(Long id, Map<String, String> factAddress) {
        Map<FieldEntity, String> fieldsMap = getFieldsMapFactAddress(factAddress);
        return fieldsMap;
    }
    private static final Map<String,String> basicInformationToFieldsInternalNameMapping = new HashMap<>();
    private static final Map<String,String> basicInformationNameToFieldsMappingMapping = new HashMap<>();
    static {
        basicInformationToFieldsInternalNameMapping.put("lastName",FieldConstants.SHARER_LASTNAME);
        basicInformationToFieldsInternalNameMapping.put("firstName",FieldConstants.SHARER_FIRSTNAME);
        basicInformationToFieldsInternalNameMapping.put("middleName",FieldConstants.SHARER_SECONDNAME);
        basicInformationToFieldsInternalNameMapping.put("dateOfBirth",FieldConstants.SHARER_BIRTHDAY);
        basicInformationToFieldsInternalNameMapping.put("gender",FieldConstants.SHARER_GENDER);
        basicInformationToFieldsInternalNameMapping.put("placeOfBirth",FieldConstants.SHARER_BIRTHPLACE);
        basicInformationToFieldsInternalNameMapping.put("nationality",FieldConstants.SHARER_NATIONALITY);
        basicInformationToFieldsInternalNameMapping.put("nativeLanguage",FieldConstants.SHARER_LANGUAGE);
        basicInformationToFieldsInternalNameMapping.put("homePhone",FieldConstants.SHARER_HOME_TEL);
        basicInformationToFieldsInternalNameMapping.put("mobilePhone",FieldConstants.SHARER_MOB_TEL);
        basicInformationToFieldsInternalNameMapping.put("skype",FieldConstants.SHARER_SKYPE);
        basicInformationToFieldsInternalNameMapping.put("www",FieldConstants.SHARER_WWW);
        basicInformationToFieldsInternalNameMapping.put("citizenship",FieldConstants.SHARER_CITIZENSHIP);
        basicInformationToFieldsInternalNameMapping.put("inn",FieldConstants.SHARER_INN);
        basicInformationToFieldsInternalNameMapping.put("snils",FieldConstants.SHARER_SNILS);
        basicInformationToFieldsInternalNameMapping.put("passportSeries",FieldConstants.SHARER_PASSPORT_SERIAL);
        basicInformationToFieldsInternalNameMapping.put("passportNumber",FieldConstants.SHARER_PASSPORT_NUMBER);
        basicInformationToFieldsInternalNameMapping.put("passportIssueDate",FieldConstants.SHARER_PASSPORT_DATE);
        basicInformationToFieldsInternalNameMapping.put("passportIssuedBy",FieldConstants.SHARER_PASSPORT_DEALER);
        basicInformationToFieldsInternalNameMapping.put("passportIssuerCode",FieldConstants.SHARER_PASSPORT_DIVISION);
        basicInformationToFieldsInternalNameMapping.put("passportExpirationDate","PASSPORT_EXPIRATION_DATE");
        basicInformationToFieldsInternalNameMapping.put("identicalNumber","BY_IDENTIFICATION_NUMBER");
        basicInformationToFieldsInternalNameMapping.put("kzIdenticalNumber","KZ_INDIVIDUAL_IDENTIFICATION_NUMBER");
        basicInformationToFieldsInternalNameMapping.put("signature", FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME);
        for (String key : basicInformationToFieldsInternalNameMapping.keySet()){
            basicInformationNameToFieldsMappingMapping.put(basicInformationToFieldsInternalNameMapping.get(key),key);
        }
    }
    private static final Map<String,String> factAddressToFieldsInternalNameMapping = new HashMap<>();
    private static final Map<String,String> factAddressNameToFieldsMapping = new HashMap<>();
    static {
        factAddressToFieldsInternalNameMapping.put("country",FieldConstants.FACT_COUNTRY_SHARER);
        factAddressToFieldsInternalNameMapping.put("zipCode", FieldConstants.FPOSTAL_CODE);
        factAddressToFieldsInternalNameMapping.put("region",FieldConstants.FACT_REGION_SHARER);
        factAddressToFieldsInternalNameMapping.put("regionLabel",FieldConstants.FACT_REGION_DESCRIPTION_SHARER);
        factAddressToFieldsInternalNameMapping.put("city",FieldConstants.FACT_CITY_SHARER);
        factAddressToFieldsInternalNameMapping.put("street",FieldConstants.FACT_STREET_SHARER);
        factAddressToFieldsInternalNameMapping.put("building",FieldConstants.FACT_BUILDING_SHARER);
        factAddressToFieldsInternalNameMapping.put("district",FieldConstants.FACT_DISTRICT_SHARER);
        factAddressToFieldsInternalNameMapping.put("districtLabel",FieldConstants.FAREA_AL_DESCRIPTION);
        factAddressToFieldsInternalNameMapping.put("districtLabelShort",FieldConstants.FDISTRICT_DESCRIPTION_SHORT);
        factAddressToFieldsInternalNameMapping.put("cityLabel",FieldConstants.FACT_CITY_DESCRIPTION_SHARER);
        factAddressToFieldsInternalNameMapping.put("cityLabelShort",FieldConstants.FACT_CITY_DESCRIPTION_SHORT_SHARER);
        factAddressToFieldsInternalNameMapping.put("streetLabel","FSTREET_DESCRIPTION");
        factAddressToFieldsInternalNameMapping.put("streetLabelShort","FSTREET_DESCRIPTION_SHORT");
        factAddressToFieldsInternalNameMapping.put("houseLabel","FHOUSE_DESCRIPTION");
        factAddressToFieldsInternalNameMapping.put("room", FieldConstants.FACT_ROOM_SHARER);
        factAddressToFieldsInternalNameMapping.put("roomLabel","FROOM_DESCRIPTION");
        factAddressToFieldsInternalNameMapping.put("geoPosition",FieldConstants.FACT_GEO_POSITION_SHARER);
        for (String key : factAddressToFieldsInternalNameMapping.keySet()) {
            factAddressNameToFieldsMapping.put(factAddressToFieldsInternalNameMapping.get(key),key);
        }

    }
    private static final Map<String,String> regAddressToFieldsInternalNameMapping = new HashMap<>();
    private static final Map<String,String> regAddressFieldsToNameMapping = new HashMap<>();
    static {
        regAddressToFieldsInternalNameMapping.put("country",FieldConstants.REGISTRATION_COUNTRY_SHARER);
        regAddressToFieldsInternalNameMapping.put("zipCode", FieldConstants.RPOSTAL_CODE);
        regAddressToFieldsInternalNameMapping.put("region", FieldConstants.REGISTRATION_REGION_SHARER);
        regAddressToFieldsInternalNameMapping.put("regionLabel",FieldConstants.REGISTRATION_REGION_DESCRIPTION_SHARER);
        regAddressToFieldsInternalNameMapping.put("city", FieldConstants.REGISTRATION_CITY_SHARER);
        regAddressToFieldsInternalNameMapping.put("street", FieldConstants.REGISTRATION_STREET_SHARER);
        regAddressToFieldsInternalNameMapping.put("building", FieldConstants.REGISTRATION_BUILDING_SHARER);
        regAddressToFieldsInternalNameMapping.put("district", FieldConstants.REGISTRATION_DISTRICT_SHARER);
        regAddressToFieldsInternalNameMapping.put("districtLabel",FieldConstants.AREA_AL_DESCRIPTION);
        regAddressToFieldsInternalNameMapping.put("districtLabelShort",FieldConstants.DISTRICT_DESCRIPTION_SHORT);
        regAddressToFieldsInternalNameMapping.put("cityLabel",FieldConstants.REGISTRATION_CITY_DESCRIPTION_SHARER);
        regAddressToFieldsInternalNameMapping.put("cityLabelShort",FieldConstants.REGISTRATION_CITY_DESCRIPTION_SHORT_SHARER);
        regAddressToFieldsInternalNameMapping.put("streetLabel","STREET_DESCRIPTION");
        regAddressToFieldsInternalNameMapping.put("streetLabelShort","STREET_DESCRIPTION_SHORT");
        regAddressToFieldsInternalNameMapping.put("houseLabel","HOUSE_DESCRIPTION");
        regAddressToFieldsInternalNameMapping.put("room",FieldConstants.REGISTRATION_ROOM_SHARER);
        regAddressToFieldsInternalNameMapping.put("roomLabel","ROOM_DESCRIPTION");
        regAddressToFieldsInternalNameMapping.put("geoPosition",FieldConstants.REGISTRATION_GEO_POSITION_SHARER);
        for (String key : regAddressToFieldsInternalNameMapping.keySet()) {
            regAddressFieldsToNameMapping.put(regAddressToFieldsInternalNameMapping.get(key),key);
        }

    }
    private static final Map<String,String> registratorOfficeAddressToFieldsInternalNameMapping = new HashMap<>();
    private static final Map<String,String> registratorOfficeAddressFieldsToNameMapping = new HashMap<>();
    static {
        registratorOfficeAddressToFieldsInternalNameMapping.put("country", FieldConstants.REGISTRATOR_OFFICE_COUNTRY);
        registratorOfficeAddressToFieldsInternalNameMapping.put("zipCode", FieldConstants.REGISTRATOR_OFFICE_POSTAL_CODE);
        registratorOfficeAddressToFieldsInternalNameMapping.put("region", FieldConstants.REGISTRATOR_OFFICE_REGION);
        registratorOfficeAddressToFieldsInternalNameMapping.put("regionLabel", FieldConstants.REGISTRATOR_OFFICE_REGION_DESCRIPTION);
        registratorOfficeAddressToFieldsInternalNameMapping.put("city", FieldConstants.REGISTRATOR_OFFICE_CITY);
        registratorOfficeAddressToFieldsInternalNameMapping.put("street", FieldConstants.REGISTRATOR_OFFICE_STREET);
        registratorOfficeAddressToFieldsInternalNameMapping.put("building", FieldConstants.REGISTRATOR_OFFICE_BUILDING);
        registratorOfficeAddressToFieldsInternalNameMapping.put("district", FieldConstants.REGISTRATOR_OFFICE_DISTRICT);
        registratorOfficeAddressToFieldsInternalNameMapping.put("districtLabel",FieldConstants.REGISTRATOR_OFFICE_DISTRICT);
        registratorOfficeAddressToFieldsInternalNameMapping.put("districtLabelShort", "REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION_SHORT");
        registratorOfficeAddressToFieldsInternalNameMapping.put("cityLabel", FieldConstants.REGISTRATOR_OFFICE_CITY_DESCRIPTION);
        registratorOfficeAddressToFieldsInternalNameMapping.put("cityLabelShort", FieldConstants.REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT);
        registratorOfficeAddressToFieldsInternalNameMapping.put("streetLabel", "REGISTRATOR_OFFICE_STREET_DESCRIPTION");
        registratorOfficeAddressToFieldsInternalNameMapping.put("streetLabelShort", "REGISTRATOR_OFFICE_STREET_DESCRIPTION_SHORT");
        registratorOfficeAddressToFieldsInternalNameMapping.put("houseLabel", "REGISTRATOR_OFFICE_BUILDING_DESCRIPTION");
        registratorOfficeAddressToFieldsInternalNameMapping.put("room", FieldConstants.REGISTRATOR_OFFICE_ROOM);
        registratorOfficeAddressToFieldsInternalNameMapping.put("roomLabel", "REGISTRATOR_OFFICE_ROOM_DESCRIPTION");
        registratorOfficeAddressToFieldsInternalNameMapping.put("geoPosition", FieldConstants.REGISTRATOR_OFFICE_GEO_POSITION);
        for (String key : registratorOfficeAddressToFieldsInternalNameMapping.keySet()) {
            registratorOfficeAddressFieldsToNameMapping.put(registratorOfficeAddressToFieldsInternalNameMapping.get(key),key);
        }

    }
    private static final Map<String,String> registratorDataToFieldsInternalNameMapping = new HashMap<>();
    private static final Map<String,String> registratorDataFieldsToNameMapping = new HashMap<>();
    static {
        registratorDataToFieldsInternalNameMapping.put("timetable", FieldConstants.SHARER_REGISTRATOR_OFFICE_TIMETABLE);
        registratorDataToFieldsInternalNameMapping.put("officePhone", FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE);
        registratorDataToFieldsInternalNameMapping.put("mobilePhone", FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE);
        registratorDataToFieldsInternalNameMapping.put("skype", FieldConstants.REGISTRATOR_SKYPE);
        registratorDataToFieldsInternalNameMapping.put("www", "REGISTRATOR_WEBSITE");
        registratorDataToFieldsInternalNameMapping.put("additionalService", "REGISTRATOR_ADDITIONAL_SERVICES_DESCRIPTION");
        for (String key : registratorDataToFieldsInternalNameMapping.keySet()){
            registratorDataFieldsToNameMapping.put(registratorDataToFieldsInternalNameMapping.get(key),key);
        }
    }
    @Override
    public String saveUserSignature(String signature, Long userId){
        String url = imagesService.uploadFromBase64(signature, ImageType.PHOTO);
        FieldFileEntity fieldFileEntity = new FieldFileEntity();
        fieldFileEntity.setName("signature");
        fieldFileEntity.setUrl(url);
        List<FieldFileEntity> fieldFileEntityList = new ArrayList<>();
        fieldFileEntityList.add(fieldFileEntity);
        fieldsService.saveFieldFiles(fieldRepository.findOneByInternalName(FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME),fieldFileEntityList,userRepository.findOne(userId));
        return url;
    }

    @Override
    public User saveUserAvatar(String base64avatar, String base64CroppedAvatar, Long userId) {
        User user = userDataService.getByIdMinData(userId);
        user.setAvatar(imagesService.uploadFromBase64(base64CroppedAvatar, ImageType.PHOTO));
        user.setAvatarSrc(imagesService.uploadFromBase64(base64avatar, ImageType.PHOTO));
        userDataService.saveAvatar(user);
        return user;
    }
    @Override
    public void saveUserProfile(Map<String, String> basicInformation, Map<String, String> regAddress, Map<String, String> factAddress,
                                Map<String, String> registratorOfficeAddress, Map<String, String> registratorData){
        User user = getUserById(SecurityUtils.getUser().getId());
        Map<FieldEntity, String> fieldEntityStringMap = new HashMap<>();
        fieldEntityStringMap.putAll(getBasicInfoMapFieldEntity(SecurityUtils.getUser().getId(), basicInformation));
        fieldEntityStringMap.putAll(getRegAddressFieldsMapEntities(SecurityUtils.getUser().getId(), regAddress));
        fieldEntityStringMap.putAll(getFactAddressFieldsEntityMap(SecurityUtils.getUser().getId(), factAddress));
        if (registratorDataService.getRegistratorLevelById(user.getId()) != null) {
            fieldEntityStringMap.putAll(getRegistratorOfficeAddressFieldEntityMap(SecurityUtils.getUser().getId(), registratorOfficeAddress));
            fieldEntityStringMap.putAll(getRegistratorDataFieldsEntityMap(SecurityUtils.getUser().getId(), registratorData));
        }
        sharerService.saveUserData(SecurityUtils.getUser().getId(), fieldEntityStringMap, SecurityUtils.getUser().getId());
    }
    @Override
    public Map<String,Boolean> getBasicInfoVisibilityMap(User user){
        Map<String,Boolean> result = new HashMap<>();
        for (Field field : user.getFields()){
            //logger.logError("lol "+field.getInternalName());
            if (field.isHideable()){
                String foundName = basicInformationNameToFieldsMappingMapping.get(field.getInternalName());
               // logger.logError("lol1 "+foundName);
                if (foundName != null) {
                    result.put(foundName,field.isHidden());
                }

            }
        }
        return result;
    }
    @Override
    public Map<String,Boolean> getFactAddressVisibilityMap(User user){
        Map<String,Boolean> result = new HashMap<>();
        for (Field field : user.getFields()){
            if (field.isHideable()){
                String foundName = factAddressNameToFieldsMapping.get(field.getInternalName());
                if (foundName != null) {
                    result.put(foundName,field.isHidden());
                }

            }
        }
        return result;
    }
    @Override
    public Map<String,Boolean> getRegAddressVisibilityMap(User user){
        Map<String,Boolean> result = new HashMap<>();
        logger.logError("lol "+user.getFields().size());
        for (Field field : user.getFields()){
            logger.logError("lol "+field.isHideable());
            if (field.isHideable()){
                String foundName = regAddressFieldsToNameMapping.get(field.getInternalName());
                if (foundName != null) {
                    result.put(foundName,field.isHidden());
                }

            }
        }
        return result;
    }
    @Override
    public Map<String,Boolean> getRegistratorOfficeAddressVisibilityMap(User user){
        Map<String,Boolean> result = new HashMap<>();
        for (Field field : user.getFields()){
            if (field.isHideable()){
                String foundName = registratorOfficeAddressFieldsToNameMapping.get(field.getInternalName());
                if (foundName != null) {
                    result.put(foundName,field.isHidden());
                }

            }
        }
        return result;
    }
    @Override
    public Map<String,Boolean> getRegistratorDataAddressVisibilityMap(User user){
        Map<String,Boolean> result = new HashMap<>();
        for (Field field : user.getFields()){
            if (field.isHideable()){
                String foundName = registratorDataFieldsToNameMapping.get(field.getInternalName());
                if (foundName != null) {
                    result.put(foundName,field.isHidden());
                }

            }
        }
        return result;
    }

    @Override
    public void setFieldVisibilityBasicInformation(String name, boolean hidden) {
        String internalName = basicInformationToFieldsInternalNameMapping.get(name);
        if (internalName != null) {
            fieldsService.setFieldValueHidden(internalName,hidden);
        }
        else {
            throw new RuntimeException("поле не найдено");
        }
    }

    @Override
    public void setFieldVisibilityRegistratrationAddress(String name, boolean hidden) {
        String internalName = regAddressToFieldsInternalNameMapping.get(name);
        if (internalName != null) {
            fieldsService.setFieldValueHidden(internalName,hidden);
        }
        else {
            throw new RuntimeException("поле не найдено");
        }
    }

    @Override
    public void setFieldVisibilityFactAddress(String name, boolean hidden) {
        String internalName = factAddressToFieldsInternalNameMapping.get(name);
        if (internalName != null) {
            fieldsService.setFieldValueHidden(internalName,hidden);
        }
        else {
            throw new RuntimeException("поле не найдено");
        }
    }

    @Override
    public void setFieldVisibilityRegistratorOfficeAddress(String name, boolean hidden) {
        String internalName = registratorOfficeAddressToFieldsInternalNameMapping.get(name);
        if (internalName != null) {
            fieldsService.setFieldValueHidden(internalName,hidden);
        }
        else {
            throw new RuntimeException("поле не найдено");
        }
    }

    @Override
    public void setFieldVisibilityRegistratorInfo(String name, boolean hidden) {
        String internalName = registratorDataToFieldsInternalNameMapping.get(name);
        if (internalName != null) {
            fieldsService.setFieldValueHidden(internalName,hidden);
        }
        else {
            throw new RuntimeException("поле не найдено");
        }
    }
}
