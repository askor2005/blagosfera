package ru.radom.kabinet.document.services.participants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantDto;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.SystemSettingsConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.utils.thread.ThreadParameters;

import java.util.*;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Service
@Transactional
public class UserParticipantSourceService implements DocumentParticipantSourceService {

    // Имя поля ИКП участника
    private static final String PERSON_ID_FIELD_NAME = "PERSON_LIK";

    // Имя поля Email участника
    private static final String PERSON_EMAIL_FIELD_NAME = "PERSON_EMAIL";

    // Фамилия ИО
    private static final String PERSON_LAST_NAME_WITH_INITIALS_FIELD_NAME = "PERSON_LAST_NAME_WITH_INITIALS";

    // ФИО
    private static final String PERSON_FULL_NAME_FIELD_NAME = "PERSON_FULL_NAME";

    // Пасспортные данные
    private static final String PERSON_PASSPORT_FULL_INFO_FIELD_NAME = "PERSON_PASSPORT_FULL_INFO";

    // Адрес прописки
    /*private static final String PERSON_REGISTRATION_ADDRESS_FULL_INFO_FIELD_NAME = "PERSON_REGISTRATION_ADDRESS_FULL_INFO";

    // Фактический адрес прописки
    private static final String PERSON_ACTUAL_ADDRESS_FULL_INFO_FIELD_NAME = "PERSON_ACTUAL_ADDRESS_FULL_INFO";*/

    private static final String CACHE_PARTICIPANT_LIST_KEY = "possibleParticipantForParse";

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Override
    public ParticipantsTypes getType() {
        return ParticipantsTypes.INDIVIDUAL;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(
            IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        User sharer = (User)documentParticipant;
        //fieldsGroups = fieldsGroupDao.getByInternalNamePrefix(PERSON_COMMON_FIELDS_PREFIX, null);
        DocumentParticipantSourceDto result = getFlowOfDocumentParticipantFromSharer(sharer, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes);
        if (needFillSystemFields) {
            addSystemFieldsToSharer(sharer, result, filteredFieldIds, index);
        }
        result.setIndex(index);
        return result;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(Long participantId, String participantName, List<Long> filteredFieldIds, List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        User user = userDataService.getByIdFullData(participantId);
        return getParticipantSource(
                user, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index
        );
    }

    @Override
    public IDocumentParticipant getSourceParticipantById(Long id) {
        return userDataService.getByIdMinData(id);
    }

    @Override
    public DocumentParticipant convertSourceParticipantToDocumentParticipant(DocumentTemplate documentTemplate, DocumentParticipantSourceDto sourceParticipant) {
        DocumentParticipant result = new DocumentParticipant();
        result.setSourceParticipantId(sourceParticipant.getId());
        result.setParticipantTypeName(sourceParticipant.getType().getName());
        result.setParticipantTemplateTypeName(sourceParticipant.getName());

        // Ищем участника среди подписантов документа
        for (DocumentTemplateParticipant templateParticipant : documentTemplate.getDocumentTemplateParticipants()) {
            boolean equalsParticipantName =
                    sourceParticipant.getName().equalsIgnoreCase(templateParticipant.getParticipantName());
            if (equalsParticipantName) {
                result.setNeedSignDocument(true);
                break;
            }
        }

        // Если участник документа - физ лицо и не сертифицированн, то кидаем ислюкчение
        boolean isSigned = result.isNeedSignDocument();
        boolean isSignerNeedBeVerified = settingsManager.getSystemSettingAsBool(SystemSettingsConstants.DOCUMENT_SIGNER_NEED_BE_VERIFIED, false);
        if (isSigned && isSignerNeedBeVerified) {
            User user = userDataService.getByIdMinData(result.getSourceParticipantId());
            ExceptionUtils.check(!user.isVerified(), "Участники подписывающие документ должны быть идентифицированы");
        }
        return result;
    }

    @Override
    public List<User> getUsersFromParticipantForFillUserFields(DocumentParticipant documentParticipant) {
        return Collections.singletonList(userDataService.getByIdFullData(documentParticipant.getSourceParticipantId()));
    }

    @Override
    public List<User> getUsersFromParticipantForSignDocument(DocumentParticipant documentParticipant) {
        List<User> result;
        if (documentParticipant.isNeedSignDocument()) {
            result = Collections.singletonList(userDataService.getByIdMinData(documentParticipant.getSourceParticipantId()));
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<PossibleSourceParticipantDto> getPossibleSourceParticipants(DocumentClassDataSource dataSource) {
        List<PossibleSourceParticipantDto> result;
        if (ThreadParameters.exists(CACHE_PARTICIPANT_LIST_KEY)) {
            result = ThreadParameters.getParameter(CACHE_PARTICIPANT_LIST_KEY);
        } else {
            result = new ArrayList<>();
            List<User> users = userDataService.getNotDeletedMinData();
            if (users != null) {
                for (User user : users) {
                    PossibleSourceParticipantDto possibleSourceParticipant = new PossibleSourceParticipantDto();
                    possibleSourceParticipant.setId(user.getId());
                    possibleSourceParticipant.setName(user.getName());
                    result.add(possibleSourceParticipant);
                }
            }
            ThreadParameters.setParameter(CACHE_PARTICIPANT_LIST_KEY, result);
        }
        return result;
    }

    @Override
    public boolean isListDataSource() {
        return false;
    }

    @Override
    public List<DocumentParticipant> getParticipantsOfUser(DocumentParticipant documentParticipant, Long userId) {
        List<DocumentParticipant> foundParticipants = new ArrayList<>();

        if (documentParticipant.getSourceParticipantId().equals(userId)) {
            foundParticipants.add(documentParticipant);
        }

        return foundParticipants;
    }

    @Override
    public String getSourceName(Long sourceParticipantId) {
        User user = userDataService.getByIdMinData(sourceParticipantId);
        return user.getName();
    }

    /**
     * Получить объект участника документа - физ лицо с установленными полями
     *
     * @param user
     * @param participantName          - имя группы для всех полей (наименование поля участника объединения. Например гд. бухгалтер)
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @return
     */
    public DocumentParticipantSourceDto getFlowOfDocumentParticipantFromSharer(User user, String participantName, List<Long> filteredFieldIds,
                                                                            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes) {
        DocumentParticipantSourceDto documentParticipantSource = new DocumentParticipantSourceDto();
        // Для случаев, когда нужны пустые значения полей
        if (user != null) {
            documentParticipantSource.setName(participantName);
            documentParticipantSource.setId(user.getId());
        }

        // Если переданы группы полей, то заполнить поля участника
        if (user.getFields().size() > 0) {
            List<ParticipantField> participantFields = new ArrayList<>();
            List<Field> fields = user.getFields();

            //
            //Map<String, String> addressFieldDescriptions = new HashMap<>();
            //
            //Map<ParticipantField, Boolean> addressFields = new HashMap<>();

            if (fields != null) {
                for (Field field : fields) {
                    if (field != null) {
                        String stringValue = "";
                        boolean needLoadedValue = true;
                        if (filteredFieldIds != null && filteredFieldIds.size() > 0 && !filteredFieldIds.contains(field.getId())) {
                            needLoadedValue = false;
                        }
                        if (needLoadedValue) {

                            switch (field.getType()) {
                                case IMAGE:
                                    if (field.getFieldFiles() != null && field.getFieldFiles().size() > 0) {
                                        stringValue = field.getFieldFiles().get(0).getUrl();
                                    }
                                    if (stringValue == null || stringValue.equals("")) {
                                        stringValue = field.getExample();
                                    }
                                    break;
                                case COUNTRY:
                                    ListEditorItem universalListItem = listEditorItemDomainService.getById(VarUtils.getLong(field.getValue(), -1l));
                                    if (universalListItem != null) {
                                        stringValue = universalListItem.getText();
                                    }
                                    break;
                                default:
                                    stringValue = getFieldValue(field, fields);
                                    break;
                            }

                            /*if (field.getType() == FieldType.IMAGE) {
                                if (field.getFieldFiles() != null && field.getFieldFiles().size() > 0) {
                                    stringValue = field.getFieldFiles().get(0).getUrl();
                                }
                                if (stringValue == null || stringValue.equals("")) {
                                    stringValue = field.getExample();
                                }
                            } else {
                                stringValue = FieldsService.getFieldStringValue(field);
                            }*/
                        }

                        /*if (field.getType() == FieldType.ADDRESS_FIELD_DESCRIPTION) { // Описание адресного поля
                            addressFieldDescriptions.put(field.getInternalName(), stringValue);
                        }*/

                        // Если есть типы полей, которые не нужно заменять на документе
                        boolean needAddField = false;
                        if (excludedFieldTypes != null) {
                            if (!excludedFieldTypes.contains(field.getType())) {
                                needAddField = true;
                            }
                        } else {
                            // Если есть типы полей которые можно заменять в документе
                            if (includedFieldTypes != null) {
                                if (includedFieldTypes.contains(field.getType())) {
                                    needAddField = true;
                                }
                            } else {
                                needAddField = true;
                            }
                        }

                        if (needAddField) {
                            ParticipantField participantField = new ParticipantField(field.getId(), participantName, field.getName(), field.getInternalName(), stringValue, field.getType());
                            participantFields.add(participantField);

                            /*switch (field.getType()) {
                                case REGION:
                                case DISTRICT:
                                    addressFields.put(participantField, true);
                                    break;
                                case CITY:
                                case STREET:
                                case BUILDING:
                                    addressFields.put(participantField, false);
                                    break;
                            }

                            // Квартира
                            if (field.getInternalName().equals(FieldConstants.REGISTRATOR_OFFICE_ROOM) ||
                                    field.getInternalName().equals(FieldConstants.REGISTRATION_ROOM_SHARER) ||
                                    field.getInternalName().equals(FieldConstants.FACT_ROOM_SHARER)) {
                                addressFields.put(participantField, false);
                            }*/
                        }
                    }
                }

                documentParticipantSource.setParticipantFields(participantFields);
            }
        }
        return documentParticipantSource;
    }

    private String getFieldValue(String internalName, List<Field> fields) {
        String result = null;
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                if (internalName.equals(field.getInternalName())) {
                    result = FieldsService.getFieldStringValue(field);
                    break;
                }
            }
        }
        return result == null ? "" : result;
    }

    private String getFieldValue(Field field, List<Field> fields) {
        String result = FieldsService.getFieldStringValue(field);
        if (StringUtils.isBlank(result)) {
            if (FieldConstants.FACT_CITY_SHARER.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.FACT_REGION_SHARER, fields);
            } else if (FieldConstants.REGISTRATION_CITY_SHARER.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.REGISTRATION_REGION_SHARER, fields);
            } else if (FieldConstants.REGISTRATOR_OFFICE_CITY.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.REGISTRATOR_OFFICE_REGION, fields);
            } else if (FieldConstants.FACT_CITY_DESCRIPTION_SHARER.equals(field.getInternalName()) ||
                    FieldConstants.FACT_CITY_DESCRIPTION_SHORT_SHARER.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.FACT_REGION_DESCRIPTION_SHARER, fields);
            } else if (FieldConstants.REGISTRATION_CITY_DESCRIPTION_SHARER.equals(field.getInternalName()) ||
                    FieldConstants.REGISTRATION_CITY_DESCRIPTION_SHORT_SHARER.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.REGISTRATION_REGION_DESCRIPTION_SHARER, fields);
            } else if (FieldConstants.REGISTRATOR_OFFICE_CITY_DESCRIPTION.equals(field.getInternalName()) ||
                    FieldConstants.REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.REGISTRATOR_OFFICE_REGION_DESCRIPTION, fields);
            }
        }
        return result;
    }

    /**
     * Добавить значения системных полей пользователю.
     *
     * @param user - пользователь системы
     * @param documentParticipantSource участник документа
     * @param filteredFieldIds - поля участника.
     * @param index - порядковый номер участника
     */
    private void addSystemFieldsToSharer(User user, DocumentParticipantSourceDto documentParticipantSource, List<Long> filteredFieldIds, int index) {
        documentParticipantSource.setIndex(index);
        // Загружаем системные поля по группам полей
        // TODO
        /*List<FieldEntity> systemFields = fieldDao.getListByGroupsAndTypes(fieldsGroups, Arrays.asList(FieldType.SYSTEM, FieldType.SYSTEM_IMAGE));
        for (FieldEntity field : systemFields) {
            boolean foundSystemField = false;
            for (ParticipantField participantField : documentParticipant.getParticipantFields()) {
                if (participantField.getInternalName().equals(field.getInternalName())) {
                    foundSystemField = true;
                    break;
                }
            }
            if (!foundSystemField) {
                documentParticipant.getParticipantFields().add(new ParticipantField(field.getId(), "", field.getName(), field.getInternalName(), "", field.getType()));
            }
        }*/

        List<ParticipantField> participantFields = documentParticipantSource.getParticipantFields();

        for (ParticipantField participantField : participantFields) {
            boolean needLoadValue = true;
            if (filteredFieldIds != null && filteredFieldIds.size() > 0) {
                needLoadValue = filteredFieldIds.contains(participantField.getId());
            }

            if (participantField.getInternalName().equalsIgnoreCase(PERSON_ID_FIELD_NAME)) { // IKP участника
                if (user != null && needLoadValue) {
                    participantField.setValue(user.getIkp());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(PERSON_EMAIL_FIELD_NAME)) { // Email участника
                if (user != null && needLoadValue) {
                    participantField.setValue(user.getEmail());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(PERSON_LAST_NAME_WITH_INITIALS_FIELD_NAME)) { // Фамилия ИО
                if (user != null && needLoadValue) {
                    participantField.setValue(user.getShortName());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(PERSON_FULL_NAME_FIELD_NAME)) { // ФИО
                if (user != null && needLoadValue) {
                    participantField.setValue(user.getFullName());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(PERSON_PASSPORT_FULL_INFO_FIELD_NAME)) { // Пасспортные данные
                if (user != null && needLoadValue) {
                    //серия 56 76 № 276487, выдан ОВД РАМЕНКИ гор. Москвы, дата выдачи 06.08.2001г., код подразделения 772-765
                    participantField.setValue(generatePassportData(participantFields));
                } else {
                    participantField.setValue("");
                }
            } /*else if (participantField.getInternalName().equalsIgnoreCase(PERSON_REGISTRATION_ADDRESS_FULL_INFO_FIELD_NAME)) { // Адрес регистрации
                if (sharer != null && needLoadValue) {
                    participantField.setValue(sharer.getRegistrationAddress().getFullAddress());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(PERSON_ACTUAL_ADDRESS_FULL_INFO_FIELD_NAME)) { // Фактический адрес
                if (sharer != null && needLoadValue) {// TODO Надо удалить данное поле из БД
                    participantField.setValue(sharer.getActualAddress().getFullAddress());
                } else {
                    participantField.setValue("");
                }
            }*/ else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME)) { // Подпись участника
                // Поле на фильтры проверять не надо
                if (user != null) {
                    // Вставляем картинку с подписью участника
                    participantField.setValue(documentParticipantSource.getId() + "");
                    //participantField.setValue(String.format(SHARER_SIGNATURE_FIELD_HTML_TEMPLATE, documentParticipant.getSourceParticipantId()));
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.PERSON_DATE_SYSTEM_SIGNATURE_FIELD_NAME)) { // Дата подписи участника
                // Поле на фильтры проверять не надо
                if (user != null) {
                    participantField.setValue(documentParticipantSource.getId() + "");
                } else {
                    participantField.setValue("");
                }
            } else if (FieldConstants.SHARER_INDEX.equals(participantField.getInternalName())) {
                if (user != null && needLoadValue) {
                    participantField.setValue(index + "");
                } else {
                    participantField.setValue("");
                }
            } else if (FieldConstants.SHARER_AVATAR.equals(participantField.getInternalName())) {
                participantField.setValue(user.getAvatar());
            }
        }
    }

    /**
     * Сформировать на основе полей пасспортные данные
     * @return
     */
    private String generatePassportData(List<ParticipantField> participantFields) {
        //серия 56 76 № 276487, выдан ОВД РАМЕНКИ гор. Москвы, дата выдачи 06.08.2001г., код подразделения 772-765
        StringBuilder fieldValue = new StringBuilder();

        String passportSeries = getFieldValue(participantFields, FieldConstants.SHARER_PASSPORT_SERIAL);
        String passportNumber = getFieldValue(participantFields, FieldConstants.SHARER_PASSPORT_NUMBER);
        String passportDealer = getFieldValue(participantFields, FieldConstants.SHARER_PASSPORT_DEALER);
        String passportDate = getFieldValue(participantFields, FieldConstants.SHARER_PASSPORT_DATE);
        String passportDivision = getFieldValue(participantFields, FieldConstants.SHARER_PASSPORT_DIVISION);

        if (!StringUtils.isBlank(passportSeries)) {
            fieldValue.append("серия ").append(passportSeries);
        }
        if (!StringUtils.isBlank(passportNumber)) {
            if (fieldValue.length() != 0) {
                fieldValue.append(" ");
            }
            fieldValue.append("№ ").append(passportNumber);
        }
        if (!StringUtils.isBlank(passportDealer)) {
            if (fieldValue.length() != 0) {
                fieldValue.append(", ");
            }
            fieldValue.append("выдан ").append(passportDealer);
        }
        if (!StringUtils.isBlank(passportDate)) {
            if (fieldValue.length() != 0) {
                fieldValue.append(", ");
            }
            fieldValue.append("дата выдачи ").append(passportDate).append("г.");
        }
        if (!StringUtils.isBlank(passportDivision)) {
            if (fieldValue.length() != 0) {
                fieldValue.append(", ");
            }
            fieldValue.append("код подразделения ").append(passportDivision);
        }
        return fieldValue.toString();
    }

    /**
     *
     * @param participantFields
     * @param internalName
     * @return
     */
    private String getFieldValue(List<ParticipantField> participantFields, String internalName) {
        String value = null;
        for (ParticipantField field : participantFields) {
            if (field.getInternalName().equals(internalName)) {
                value = field.getValue();
                break;
            }
        }
        return value;
    }
}
