package ru.radom.kabinet.document.services;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.events.document.*;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.notifications.SystemAccountDao;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantsDto;
import ru.radom.kabinet.document.exception.FlowOfDocumentException;
import ru.radom.kabinet.document.exception.FlowOfDocumentExceptionType;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.parser.ParticipantFieldParser;
import ru.radom.kabinet.document.web.dto.DocumentPageDto;
import ru.radom.kabinet.document.web.dto.DocumentUserFieldDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.module.blagosfera.bp.util.BPMRabbitSignals;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.script.ScriptEngineService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.util.MapsUtils;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.ramera.signer.common.exception.CommonSignerException;
import ru.ramera.signer.common.service.CommonSignerService;
import ru.ramera.signer.common.service.parameters.SignMessageUsersByDefaultKeyParameters;
import ru.ramera.signer.common.service.parameters.VerifyMessageParameters;
import ru.ramera.signer.common.service.response.SignMessageUsersByDefaultKeyResponseDto;
import ru.ramera.signer.common.service.response.SignOperationResult;
import ru.ramera.signer.common.service.response.VerifyMessageResponseDto;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by vgusev on 16.06.2015.
 * Абстрактный класс для работы с шаблонами, участниками документов и обработка шаблонов.
 */
@Transactional
@Service("documentService")
public class DocumentServiceImpl implements NotifyUnsignDocumentCallback, DocumentService {
    private static final Logger logger = LoggerFactory.createLogger(DocumentServiceImpl.class);
    // Имя поля - дата создания документа
    private static final String DATE_CREATE_DOCUMENT_FIELD_NAME = "DATE_CREATE_DOCUMENT";

    // Шаблон даты
    private static final String DATE_TEMPLATE = "dd.MM.yyyy";

    // Наименование участника документа - директора организации
    private static final String DERECTOR_PARTICIPANT_NAME = "Руководство";

    // Наименование поля - ИД должнонсти руководства
    private static final String DIRECTOR_POST_FIELD_NAME = "COMMUNITY_DIRECTOR_POSITION";

    // Роль просмотра документов в организации
    private static final String COMMUNITY_ROLE_DOCUMENT_VIEW = "ROLE_DOCUMENT_VIEW";

    // Роль заполнения пользовательских полей в документах организации
    private static final String COMMUNITY_ROLE_DOCUMENT_FIELDS_WRITE = "ROLE_DOCUMENT_FIELDS_WRITE";

    // Форма объедиения
    public static final String COMMUNITY_ASSOCIATION_FORM_FIELD_NAME = "COMMUNITY_ASSOCIATION_FORM";

    // Код универсального списка - форма объединений
    public static final String LIST_EDITOR_ASSOCIATION_FORM_NAME = "community_association_forms_groups";

    private static final String COMMON_SCRIPT =
            "var ParticipantCreateDocumentParameter = Packages.ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter; " +
                    "var CreateDocumentParameter = Packages.ru.radom.kabinet.document.generator.CreateDocumentParameter; " +
                    "var ArrayList = Packages.java.util.ArrayList; " +
                    "var UserFieldValueBuilder = Packages.ru.radom.kabinet.document.generator.UserFieldValueBuilder; ";

    private static final List<String> DOCUMENT_NUMBER_CHARS = new ArrayList<>();

    protected static final String DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME = "документ";

    // Настройка - включить сервис подписей
    private static final String ENABLE_SIGN_SERVICE = "enable.sign.service";

    static {
        DOCUMENT_NUMBER_CHARS.add("А");
        DOCUMENT_NUMBER_CHARS.add("Б");
        DOCUMENT_NUMBER_CHARS.add("В");
        DOCUMENT_NUMBER_CHARS.add("Г");
        DOCUMENT_NUMBER_CHARS.add("Д");
        DOCUMENT_NUMBER_CHARS.add("Е");
        DOCUMENT_NUMBER_CHARS.add("Ж");
        DOCUMENT_NUMBER_CHARS.add("З");
        DOCUMENT_NUMBER_CHARS.add("И");
        DOCUMENT_NUMBER_CHARS.add("К");
        DOCUMENT_NUMBER_CHARS.add("Л");
        DOCUMENT_NUMBER_CHARS.add("М");
        DOCUMENT_NUMBER_CHARS.add("Н");
        DOCUMENT_NUMBER_CHARS.add("О");
        DOCUMENT_NUMBER_CHARS.add("П");
        DOCUMENT_NUMBER_CHARS.add("Р");
        DOCUMENT_NUMBER_CHARS.add("С");
        DOCUMENT_NUMBER_CHARS.add("Т");
        DOCUMENT_NUMBER_CHARS.add("У");
        DOCUMENT_NUMBER_CHARS.add("Ф");
        DOCUMENT_NUMBER_CHARS.add("Х");
        DOCUMENT_NUMBER_CHARS.add("Ц");
        DOCUMENT_NUMBER_CHARS.add("Ч");
        DOCUMENT_NUMBER_CHARS.add("Ш");
        DOCUMENT_NUMBER_CHARS.add("Щ");
        DOCUMENT_NUMBER_CHARS.add("Э");
        DOCUMENT_NUMBER_CHARS.add("Ю");
        DOCUMENT_NUMBER_CHARS.add("Я");
    }

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private UserFieldsParserService userFieldsParserService;

    @Autowired
    private DocumentParticipantService documentParticipantService;

    @Autowired
    private SystemAccountDao systemAccountDao;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommonSignerService commonSignerService;

    @Autowired
    private ScriptEngineService scriptEngineService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private DocumentTemplateDataService documentTemplateDomainService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private DocumentParticipantDomainService documentParticipantDomainService;

    @Autowired
    private DocumentClassDomainService documentClassDomainService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private DocumentFolderDataService documentFolderDataService;

    /**
     * Создать участника документа с заполненными полями по типу и ИД.
     *
     * @param participantType
     * @param participantName
     * @param participantId
     * @param index
     * @return
     */
    private DocumentParticipantSourceDto createFlowOfDocumentParticipant(String participantType, String participantName, Long participantId, int index) {
        return documentParticipantService.createFlowOfDocumentParticipant(participantType, participantId, participantName, null, null, null, true, index);
    }

    private DocumentParticipantSourceDto createFlowOfDocumentParticipant(String participantType, String participantName, IDocumentParticipant documentParticipant, int index) {
        return documentParticipantService.createFlowOfDocumentParticipant(participantType, documentParticipant, participantName, null, null, null, true, index);
    }

    @Override
    public List<DocumentClassDataSource> getUsedInTemplateClassParticipants(Long id) {
        List<DocumentClassDataSource> result = new ArrayList<>();
        DocumentTemplate documentTemplate = documentTemplateDomainService.getById(id);
        if (documentTemplate != null && documentTemplate.getDocumentClass() != null && documentTemplate.getDocumentClass().getDataSources() != null) {
            List<DocumentClassDataSource> documentClassDataSources = documentTemplate.getDocumentClass().getDataSources();
            for (DocumentClassDataSource documentClassDataSource : documentClassDataSources) {
                //if (documentTemplate.getContent().contains("[" + documentTypeParticipant.getParticipantName() + ":")) {
                // Ишем участника в списке подписантов
                boolean foundParticipant = false;
                for (DocumentTemplateParticipant templateParticipant : documentTemplate.getDocumentTemplateParticipants()) {
                    boolean equalsParticipantName = documentClassDataSource.getName().equalsIgnoreCase(templateParticipant.getParticipantName());
                    boolean equalsParentParticipantName = templateParticipant.getParentParticipantName() != null && documentClassDataSource.getName().equalsIgnoreCase(templateParticipant.getParentParticipantName());
                    if (equalsParticipantName || equalsParentParticipantName) {
                        foundParticipant = true;
                    }
                }
                ExceptionUtils.check(documentTemplate.getContent() == null, "Не установлен текст шаблона");
                ExceptionUtils.check(documentTemplate.getDocumentName() == null, "Не установлено название шаблона");
                ExceptionUtils.check(documentTemplate.getDocumentShortName() == null, "Н установлено короткое название шаблона");
                // Ищем участника в шаблоне на основе использованных полей
                if (!foundParticipant) {
                    foundParticipant = foundParticipant || documentTemplate.getContent().contains("[" + documentClassDataSource.getName() + ":");
                }
                if (documentTemplate.getDocumentName() != null && !foundParticipant) {
                    foundParticipant = foundParticipant || documentTemplate.getDocumentName().contains("[" + documentClassDataSource.getName() + ":");
                }
                if (documentTemplate.getDocumentShortName() != null && !foundParticipant) {
                    foundParticipant = foundParticipant || documentTemplate.getDocumentShortName().contains("[" + documentClassDataSource.getName() + ":");
                }

                if (foundParticipant) {
                    result.add(documentClassDataSource);
                }
            }
        }
        return result;
    }

    /**
     * Получить список системых полей документа для завершения документа.
     *
     * @return
     */
    private List<ParticipantField> getDocumentSystemFieldsForFinishDocument(Document document) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TEMPLATE);
        List<ParticipantField> result = new ArrayList<>();
        FieldEntity field = fieldDao.getByInternalName(FieldConstants.DATE_LAST_SIGN_DOCUMENT_FIELD_NAME);// Дата последнего подписания документа
        if (field != null) {
            ParticipantField participantField = new ParticipantField(field.getType());
            participantField.setId(field.getId());
            participantField.setInternalName(field.getInternalName());
            participantField.setName(field.getName());
            participantField.setValue(dateFormat.format(new Date()));
            result.add(participantField);
        }
        field = fieldDao.getByInternalName(FieldConstants.DOCUMENT_CODE_FIELD_NAME);// Код документа
        if (field != null) {
            ParticipantField participantField = new ParticipantField(field.getType());
            participantField.setId(field.getId());
            participantField.setInternalName(field.getInternalName());
            participantField.setName(field.getName());
            participantField.setValue(document.getCode());
            result.add(participantField);
        }
        return result;
    }

    /**
     * Получить список системных полей документа для его создания.
     *
     * @return
     */
    private List<ParticipantField> getDocumentSystemFieldsForCreateDocument() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TEMPLATE);
        List<ParticipantField> result = new ArrayList<>();
        FieldEntity field = fieldDao.getByInternalName(DATE_CREATE_DOCUMENT_FIELD_NAME);// Дата создания документа
        if (field != null) {
            ParticipantField participantField = new ParticipantField(field.getType());
            participantField.setId(field.getId());
            participantField.setInternalName(field.getInternalName());
            participantField.setName(field.getName());
            participantField.setValue(dateFormat.format(new Date()));
            result.add(participantField);
        }
        return result;
    }

    /**
     * Получить список системных полей документа, которые проставляются после его сохранения.
     *
     * @param document
     * @return
     */
    private List<ParticipantField> getDocumentSystemFieldsForInsertAfterSaveDocument(Document document) {
        List<ParticipantField> result = new ArrayList<>();
        // TODO Пока нет полей которые нужно вставлять после создания документа
        return result;
    }

    /**
     * Документ имеет не заполненные пользовательские поля участника
     *
     * @param document
     * @param participant
     * @return
     */
    @Override
    public boolean isDocumentHasUserFields(Document document, DocumentParticipant participant) {
        return userFieldsParserService.isDocumentHasUserFields(document, participant);
    }

    /**
     * Документ имеет незаполненные пользовательские поля
     *
     * @param document
     * @return
     */
    @Override
    public boolean isDocumentHasUserFields(Document document) {
        boolean result = false;
        for (DocumentParticipant participant : document.getParticipants()) {
            result = result || isDocumentHasUserFields(document, participant);
        }
        return result;
    }

    /**
     * Анализ документа и выполнение действий на основе анализа
     */
    private void analyzeAndHandleDocument(Document document) {
        analyzeAndHandleDocument(document, true);
    }

    /**
     * Проанализировать документ и выполнить необходимые действия.
     * Например: отправить участникам сообщения о необходимости заполнить данные
     * или о необходимости подписать документ
     *
     * @param document
     * @param notifySignEvent отправлять или нет уведомления о необходимости подписания документа
     */
    private void analyzeAndHandleDocument(Document document, boolean notifySignEvent) {
        if (document != null) {
            // Список уникальных участников
            Set<User> usersForEvents = new HashSet<>();
            // Создатель документа
            DocumentCreator creator = document.getCreator();

            List<DocumentParticipant> documentParticipants = document.getParticipants();
            for (DocumentParticipant documentParticipant : documentParticipants) {
                // Если есть незаполненные поля участника
                if (isDocumentHasUserFields(document, documentParticipant)) {
                    usersForEvents.addAll(documentParticipantService.getUsersFromParticipantForFillUserFields(documentParticipant));
                }
            }

            // Если есть участники с незаполненными полями
            if (usersForEvents.size() > 0) {
                // Отправляем сообщение о заполнении полей
                for (User targetUser : usersForEvents) {
                    blagosferaEventPublisher.publishEvent(new RameraFlowOfDocumentEvent(this, creator, targetUser, document, RameraFlowOfDocumentEventType.FILL_USER_FIELDS, document.getLink()));
                }
            } else { // Ищем участников для отправки сообщений о подписании документа
                for (DocumentParticipant documentParticipant : documentParticipants) {
                    usersForEvents.addAll(documentParticipantService.getUsersFromParticipantForSignDocument(documentParticipant));
                }
                if (notifySignEvent) {
                    for (User targetUser : usersForEvents) {
                        blagosferaEventPublisher.publishEvent(new RameraFlowOfDocumentEvent(this, creator, targetUser, document, RameraFlowOfDocumentEventType.SIGN_DOCUMENT, document.getLink()));
                    }
                }
            }

        }
    }

    /**
     * Получить список пользовательских полей документа для текущего пользователя по участникам пользователя.
     *
     * @param documentId
     * @param userId
     * @return
     */
    @Override
    public Map<DocumentParticipant, List<DocumentUserField>> getDocumentUserFields(Long documentId, Long userId) {
        Map<DocumentParticipant, List<DocumentUserField>> result = new HashMap<>();
        Document document = documentDomainService.getById(documentId);
        // Ищем участников документа по текущему пользователю
        List<DocumentParticipant> foundParticipants = new ArrayList<>();
        for (DocumentParticipant participant : document.getParticipants()) {
            // Ищем участников - физ лиц и регистраторов
            if ((participant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.INDIVIDUAL.getName()) ||
                    participant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.REGISTRATOR.getName()) ||
                    participant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.INDIVIDUAL_LIST.getName())) &&
                    participant.getSourceParticipantId().equals(userId)) {
                foundParticipants.add(participant);
            }
            // Ищем участников - юр лиц, на которые у текущего пользователя есть права
            if (participant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName()) ||
                    participant.getParticipantTypeName().equalsIgnoreCase(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName())) {
                List<ParticipantRight> participantRights = getRightToCommunityOfUser(participant, userId);
                // Если есть права заполнения пользовательских полей
                if (participantRights != null && participantRights.contains(ParticipantRight.FILL_USER_FIELDS)) {
                    foundParticipants.add(participant);
                }
            }
        }

        // Добавляем поля участников
        for (DocumentParticipant participant : foundParticipants) {
            result.put(participant, userFieldsParserService.getDocumentUserFieldsByParticipant(document.getContent(), participant.getParticipantTemplateTypeName()));
            //result.addAll(getDocumentUserFieldsByParticipant(document.getContent(), participantSource));
        }

        return result;
    }

    /**
     * Получить список всех пользовательских полей в шаблоне документа
     *
     * @param templateId
     * @return
     */
    @Override
    public List<DocumentUserField> getUserFieldsByTemplateId(Long templateId) {
        List<DocumentUserField> result = new ArrayList<>();
        DocumentTemplate documentTemplate = documentTemplateDomainService.getById(templateId);
        List<DocumentClassDataSource> typeParticipants = getUsedInTemplateClassParticipants(templateId);
        for (DocumentClassDataSource typeParticipant : typeParticipants) {
            Map<String, DocumentUserField> resultParticipantUserFields = new HashMap<>();
            List<DocumentUserField> contentUserFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentTemplate.getContent(), typeParticipant.getName());
            List<DocumentUserField> docNameUserFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentTemplate.getDocumentName(), typeParticipant.getName());
            List<DocumentUserField> docShortNameUserFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentTemplate.getDocumentShortName(), typeParticipant.getName());

            for (DocumentUserField contentUserField : contentUserFields) {
                resultParticipantUserFields.put(contentUserField.getName(), contentUserField);
            }
            for (DocumentUserField docNameUserField : docNameUserFields) {
                resultParticipantUserFields.put(docNameUserField.getName(), docNameUserField);
            }
            for (DocumentUserField shortNameUserField : docShortNameUserFields) {
                resultParticipantUserFields.put(shortNameUserField.getName(), shortNameUserField);
            }
            Collection<DocumentUserField> values = resultParticipantUserFields.values();
            for (DocumentUserField value : values) {
                value.setParticipantId(typeParticipant.getId());
            }
            result.addAll(values);
        }
        return result;
    }

    @Override
    public void saveUserFieldsInDocument(Long documentId, List<DocumentUserField> documentUserFields, Long userId) {
        Document document = documentDomainService.getById(documentId);
        String content = parseDocumentByUserFields(document, documentUserFields, userId);
        document.setContent(content);
        documentDomainService.save(document);

        // Проверить, что все заполнили пользовательские поля документа и разослать сообщения о подписании документа
        boolean needSendMessageToSignDocument = !isDocumentHasUserFields(document);

        // Если поля заполнили все, то отправляем сообщение о подписании
        if (needSendMessageToSignDocument) {
            analyzeAndHandleDocument(document);
        }
    }

    /**
     * Парсинг поьзовательских полей в документе участников текущего пользователя
     *
     * @param document
     * @param userFields
     * @return
     */
    private String parseDocumentByUserFields(Document document, List<DocumentUserField> userFields, Long userId) {

        String content = document.getContent();

        List<DocumentParticipantSourceDto> foundParticipants = new ArrayList<>();
        List<Long> foundParticipantIds = new ArrayList<>();

        // Ищем участников документа по текущему пользователю
        for (DocumentParticipant participant : document.getParticipants()) {
            List<User> users = documentParticipantService.getUsersFromParticipantForFillUserFields(participant);
            for (User user : users) {
                // Если текущий пользователь в списке людей, кто может заполнять поля
                if (user.getId().equals(userId) && !foundParticipantIds.contains(participant.getId())) {
                    DocumentParticipantSourceDto participantSource = new DocumentParticipantSourceDto();
                    participantSource.setId(participant.getSourceParticipantId());
                    participantSource.setName(participant.getParticipantTemplateTypeName());
                    foundParticipants.add(participantSource);
                    foundParticipantIds.add(participant.getId());
                }
            }
        }

        if (foundParticipants.size() == 0) {
            throw new RuntimeException("Пользователь не имеет доступ к документу!");
        }

        // Прикрепляем пользовательские поля к участникам TODO надо как то сделать чтобы правильно прикреплять
        for (DocumentParticipantSourceDto participant : foundParticipants) {
            // Перебираем поля и подставляем значения
            content = userFieldsParserService.parseDocumentByUserFields(content, participant, userFields);
        }

        return content;
    }
    /**
     * Права доступа к документу у текущего пользователя
     *
     * @return
     */
    @Override
    public Map<DocumentParticipant, List<ParticipantRight>> getRightToDocument(Long documentId, Long userId) {
        Map<DocumentParticipant, List<ParticipantRight>> result = new HashMap<>();
        Document document = documentDomainService.getById(documentId);
        DocumentParticipant userParticipant = null;
        // Ищем текущего пользователя в списке участников документа (в качестве физ лица)
        for (DocumentParticipant participant : document.getParticipants()) {
            if ((ParticipantsTypes.INDIVIDUAL.getName().equals(participant.getParticipantTypeName()) ||
                    ParticipantsTypes.REGISTRATOR.getName().equals(participant.getParticipantTypeName()) ||
                    ParticipantsTypes.INDIVIDUAL_LIST.getName().equals(participant.getParticipantTypeName())) &&
                    participant.getSourceParticipantId().equals(userId)) {
                userParticipant = participant;
                break;
            }
        }
        // Если пользователь - сторона документа, то у него полные права к документу
        if (userParticipant != null) {
            result.put(userParticipant, new ArrayList<>());
            result.get(userParticipant).add(ParticipantRight.VIEW);
            result.get(userParticipant).add(ParticipantRight.FILL_USER_FIELDS);
            result.get(userParticipant).add(ParticipantRight.SIGN);
        }

        // Ищем объединения - участников документа
        for (DocumentParticipant participant : document.getParticipants()) {
            if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName().equals(participant.getParticipantTypeName()) ||
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName().equals(participant.getParticipantTypeName())) {
                // Если текущий пользователь - член объединения
                // то ищем права доступа к документу на основе штатного расписания объединения
                List<ParticipantRight> rightsToCommunity = getRightToCommunityOfUser(participant, userId);
                result.put(participant, new ArrayList<ParticipantRight>());
                for (ParticipantRight participantRight : rightsToCommunity) {
                    if (!result.get(participant).contains(participantRight)) {
                        result.get(participant).add(participantRight);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Права доступа текущего пользователя к документу участника - юр лица на основе штатного расписания объединения
     *
     * @param participant
     * @param userId
     * @return
     */
    private List<ParticipantRight> getRightToCommunityOfUser(DocumentParticipant participant, Long userId) {
        List<ParticipantRight> result = new ArrayList<>();
        Long communityId = participant.getSourceParticipantId();
        if (communitiesService.hasPermission(communityId, userId, COMMUNITY_ROLE_DOCUMENT_VIEW)) {
            result.add(ParticipantRight.VIEW);
        }
        if (communitiesService.hasPermission(communityId, userId, COMMUNITY_ROLE_DOCUMENT_FIELDS_WRITE)) {
            result.add(ParticipantRight.FILL_USER_FIELDS);
        }
        // Если текущий пользователь - дочерний участник участника сообщества - то у него есть права подписания документа
        if (participant.getChildren() != null && participant.getChildren().size() > 0) {
            List<DocumentParticipant> childrenParticipants = participant.getChildren();
            for (DocumentParticipant childParticipant : childrenParticipants) {
                if (childParticipant.getSourceParticipantId().equals(userId)) {
                    result.add(ParticipantRight.SIGN);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Подписать документы текущим пользователем
     *
     * @param documentIds
     * @param userId
     */
    @Override
    public void signDocuments(List<Long> documentIds, Long userId) {
        for (Long document : documentIds) {
            signDocument(document, userId);
        }
    }

    @Override
    public void signDocument(Long documentId, Long userId) {
        Document document = documentDomainService.getById(documentId);
        signDocument(document, userId);
    }

    /**
     * Подписать документ заданным пользователем
     *
     * @param document
     * @param userId
     */
    @Override
    public void signDocument(Document document, Long userId) {
        ExceptionUtils.check(document == null, "Документ не существует!");

        List<DocumentParticipant> participants = documentParticipantService.getParticipantsOfUser(document, userId);
        ExceptionUtils.check(participants.size() == 0, "Вы не являетесь участником документа \"" + document.getName() + "\"!");

        ExceptionUtils.check(!document.isActive(), "Документ нельзя подписать, потому как он не действителен!");

        boolean documentIsSigned = true;
        for (DocumentParticipant participant : participants) {
            if (!participant.isSigned()) {
                documentIsSigned = false;
                break;
            }
        }
        ExceptionUtils.check(documentIsSigned, "Документ \"" + document.getName() + "\" уже подписан!");
        String documentContent = document.getContent();

        BpmRaiseSignalsEvent userSignDocumentEvents = new BpmRaiseSignalsEvent(this);
        for (DocumentParticipant participant : participants) {
            if (participant.isNeedSignDocument()) {
                // Нужно заменить фейковое поле с подписью участника

                //String signatureFieldHtml = String.format(SHARER_SIGNATURE_FIELD_HTML_TEMPLATE, participantSource.getSourceParticipantId());
                // TODO Потом при необходимсоти надо написать отдельную парсилку картинок

                //String signatureFieldValue = null;
                // Грузим поля участника без спец полей (поля как есть)
                DocumentParticipantSourceDto participantForParseImages =
                        documentParticipantService.createFlowOfDocumentParticipant(
                                participant.getParticipantTypeName(),
                                participant.getSourceParticipantId(),
                                participant.getParticipantTemplateTypeName(),
                                null, null, Collections.singletonList(FieldType.IMAGE), false, 1);
                List<ParticipantField> participantFields = participantForParseImages.getParticipantFields();
                String imageSource = null;
                for (ParticipantField participantField : participantFields) {
                    if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME)) {
                        //signatureFieldValue = String.format(SHARER_SIGNATURE_FIELD_HTML_RESULT_TEMPLATE, participantField.getValue());
                        imageSource = participantField.getValue();
                        break;
                    }
                }

                if (imageSource != null) {
                    DocumentParticipantSourceDto participantSource = createFlowOfDocumentParticipant(participant.getParticipantTypeName(), participant.getParticipantTemplateTypeName(), participant.getSourceParticipantId(), 1);
                    documentContent = new ParticipantFieldParser(participant.getParticipantTemplateTypeName(), Collections.singletonList(participantSource), listEditorItemDomainService, null).parseSignFieldValue(documentContent, participant.getSourceParticipantId(), imageSource);
                    //documentContent = documentContent.replaceAll(signatureFieldHtml, signatureFieldValue);
                }

                documentParticipantDomainService.signDocumentByParticipantId(participant.getId());

                blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(this, BPMRabbitSignals.DOCUMENT_SIGNED_BY_SHARER,
                        MapsUtils.map(Stream.of(
                                MapsUtils.entry("document", convertDocumentToSend(document)),
                                MapsUtils.entry("sharer", serializeService.toPrimitiveObject(userDataService.getByIdMinData(userId))), // TODO Проверить
                                MapsUtils.entry("participantSource", participant)
                        ))
                ));

                Map<String, Object> userSignDocumentParameters = new HashMap<>();
                userSignDocumentParameters.put("signedDocument", serializeService.toPrimitiveObject(document));
                userSignDocumentEvents.getEvents().add(new BpmRaiseSignalEvent(
                        this,
                        "document_" + document.getId() +"_signed_by_" + userId + "_user",
                        userSignDocumentParameters
                ));
                if (document.getDocumentFolder() != null) {
                    userSignDocumentEvents.getEvents().add(new BpmRaiseSignalEvent(
                            this,
                            "document_folder_" + document.getDocumentFolder().getId() + "_document_signed_by_" + userId + "_user",
                            userSignDocumentParameters
                    ));
                }
            }
        }

        List<DocumentParameter> params = document.getParameters();

        document = documentDomainService.getById(document.getId());
        document.setContent(documentContent);

        if (document.getParameters().size() == 0) document.getParameters().addAll(params);

        // Проанализировать остались ли участники, которые должны подписать документ
        // Если нет, то проставить поля, которые устанавливаются после всех подписейd
        boolean isAllSigned = true;
        // ИДы участников, которые подписывают документ
        Map<Long, Long> usersWhoSignDocument = new HashMap<>();
        for (DocumentParticipant participant : document.getParticipants()) {
            if ((participant.getChildren() == null || participant.getChildren().size() == 0)) {
                if (participant.isNeedSignDocument()) {
                    isAllSigned = isAllSigned && participant.isSigned();
                    usersWhoSignDocument.put(participant.getSourceParticipantId(), participant.getId());
                }
            } else {
                for (DocumentParticipant childParticipant : participant.getChildren()) {
                    if (childParticipant.isNeedSignDocument()) {
                        isAllSigned = isAllSigned && childParticipant.isSigned();
                        usersWhoSignDocument.put(childParticipant.getSourceParticipantId(), childParticipant.getId());
                    }
                }
            }
        }

        BpmRaiseSignalEvent allSignedDocumentEvent = null;
        if (isAllSigned) {
            if (document.getParameters() != null && document.getParameters().size() > 0) {
                // Ищем событие когда все подписались в параметрах документа
                boolean isSignedEventFound = false;
                Map<String, String> parameters = new HashMap<>();
                for (DocumentParameter parameter : document.getParameters()) {
                    // Есть тип события и это подписание документа
                    if (parameter.getName().equals(DocumentParameter.EVENT_TYPE) &&
                            parameter.getValue().equals(FlowOfDocumentStateEventType.DOCUMENT_SIGNED.getTypeName())) {
                        // Событие в параметрах найдено
                        isSignedEventFound = true;
                    }
                    parameters.put(parameter.getName(), parameter.getValue());
                }
                if (isSignedEventFound) {
                    blagosferaEventPublisher.publishEvent(new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED, document));
                }
            }
            //TODO а это точно должно быть не за скобками?
            // Генерируем код документа после того как все его подписали
            document.setCode(generateDocumentUniqueCode(document));
            document.setContent(finishDocumentContent(document));
            // Генерируем хеш код, когда уже все подписали документ
            document.setHashCodeForSignature(generateDocumentHashCodeForSignature(document));
            document = documentDomainService.save(document);

            boolean enableSignService = settingsManager.getSystemSettingAsBool(ENABLE_SIGN_SERVICE, false);
            if (enableSignService && document.isNeedSignByEDS()) {
                // Подписываем документ цифровыми подписями пользователей
                setSignaturesToParticipants(document, usersWhoSignDocument);
            }

            allSignedDocumentEvent = new BpmRaiseSignalEvent(
                    this,
                    "document_" + document.getId() + "_signed",
                    Collections.emptyMap());

            blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(this, BPMRabbitSignals.DOCUMENT_SIGNED,
                    MapsUtils.map(Stream.of(MapsUtils.entry("document", convertDocumentToSend(document))))));

        } else {
            documentDomainService.save(document);
        }

        boolean isAllDocsSigned = true;
        if (document.getDocumentFolder() != null && document.getDocumentFolder().getId() != null) {
            DocumentFolder documentFolder = documentFolderDataService.getById(document.getDocumentFolder().getId());
            for (Document doc : documentFolder.getDocuments()) {
                if (!doc.getId().equals(document.getId())) {
                    isAllDocsSigned = isAllDocsSigned && isSignedDocument(doc.getId());
                }
            }
        }

        if (isAllDocsSigned && document.getDocumentFolder() != null && document.getDocumentFolder().getId() != null) {
            blagosferaEventPublisher.publishEvent(
                    new BpmRaiseSignalEvent(this,
                            "document_folder_" + document.getDocumentFolder().getId() + "_signed",
                            Collections.emptyMap()
                    )
            );
        } else if (allSignedDocumentEvent != null) {
            blagosferaEventPublisher.publishEvent(allSignedDocumentEvent);
        } else if (!userSignDocumentEvents.getEvents().isEmpty()) {
            blagosferaEventPublisher.publishEvent(userSignDocumentEvents);
        }
    }

    /**
     * Отказаться от подписи документа
     *
     * @param documentId ИД документа
     * @param userId     ИД пользователя
     */
    @Override
    public void unSignDocument(Long documentId, Long userId) {
        // Помечаем документ как неактивный. В методе подписи должна быть проверка того, что документ
        // активный, чтобы его можно было подписывать
        Document document = documentDomainService.getById(documentId);
        ExceptionUtils.check(!document.isActive(), "Документ уже не действителен!");
        ExceptionUtils.check(!document.isCanUnsignDocument(), "Документ нельзя не подписать!");
        List<DocumentParticipant> participants = documentParticipantService.getParticipantsOfUser(document, userId);
        boolean isSigned = false;
        for (DocumentParticipant participant : participants) {
            if (participant.isNeedSignDocument()) {
                isSigned = participant.isSigned();
                break;
            }
        }
        ExceptionUtils.check(isSigned, "Документ вами уже подписан!");
        document.setActive(false);
        documentDomainService.save(document);

        // Отправить сообщение о том, что документ не действителен
        //blagosferaEventPublisher.publishEvent(new NotifyUnsignDocumentCallbackEvent(this, document, userId, this));
        notifyUnsignDocument(document, userId);
    }

    /**
     * Проверить подпись участника документа
     *
     * @param participantId ИД участника
     */
    @Override
    public void checkParticipantSignature(Long participantId) {
        ExceptionUtils.check(participantId == null, "ИД участника не передан");
        DocumentParticipant participant = documentParticipantDomainService.getById(participantId);
        ExceptionUtils.check(participant == null, "Участник не найден");
        boolean isNeedSign = participant.isNeedSignDocument();
        ExceptionUtils.check(!isNeedSign, "Участник не подписывает документ");

        boolean isSigned = participant.isSigned();
        ExceptionUtils.check(!isSigned, "Участник не подписал документ");
        ExceptionUtils.check(StringUtils.isBlank(participant.getSignature()), "ЭЦП ещё не сформированы, потому как не все подписали документ или документ создан в тестовом режиме");

        Document document = documentDomainService.getByParticipantId(participantId);
        ExceptionUtils.check(document == null, "Документ по участнику не найден");
        VerifyMessageParameters verifyMessageParameters = new VerifyMessageParameters();
        verifyMessageParameters.setMessage(document.getHashCodeForSignature());
        verifyMessageParameters.setUserId(participant.getSourceParticipantId());
        verifyMessageParameters.setSignatureBase64(participant.getSignature());

        SignOperationResult<VerifyMessageResponseDto> operationResult = null;
        try {
            operationResult = commonSignerService.verifyMessage(verifyMessageParameters);
        } catch (CommonSignerException e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        ExceptionUtils.check(!operationResult.isOperationResult(), "Подпись не валидна");
    }

    /**
     * Сформировать контент документа.
     *
     * @param templateContent
     * @param sourceParticipants - изначальные участники документа для его формирования
     * @return
     */
    private String createDocumentContentByTemplateContent(String templateContent, List<DocumentParticipantSourceDto> sourceParticipants, Map<String, Object> scriptVars) {
        // Ключ - имя участника шаблона
        Map<String, List<DocumentParticipantSourceDto>> participantsMap = new HashMap<>();
        if (templateContent != null) {
            // Сгруппируем участников по имени из шаблона

            for (DocumentParticipantSourceDto sourceParticipant : sourceParticipants) {
                if (!participantsMap.containsKey(sourceParticipant.getName())) {
                    participantsMap.put(sourceParticipant.getName(), new ArrayList<>());
                }
                participantsMap.get(sourceParticipant.getName()).add(sourceParticipant);
            }

            for (String participantName : participantsMap.keySet()) {
                List<DocumentParticipantSourceDto> participants = participantsMap.get(participantName);
                templateContent = parseTemplateByParticipant(templateContent, participantName, participants, scriptVars, participantsMap);
            }

            // Создаем ещё одного участника документа - сам документ, потому как держатель данных системых полей является сам документ
            DocumentParticipantSourceDto documentParticipant = new DocumentParticipantSourceDto();
            documentParticipant.setParticipantFields(getDocumentSystemFieldsForCreateDocument());
            documentParticipant.setName(DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME);

            templateContent = parseTemplateByParticipant(templateContent, documentParticipant.getName(), Collections.singletonList(documentParticipant), scriptVars, participantsMap);
        }
        templateContent = deleteErrorFieldsFromDocument(templateContent);
        return templateContent;
    }

    /**
     * Обработать контент документа после его сохранения
     *
     * @param document
     */
    private String handleDocumentContentAfterSave(Document document, String content) {
        DocumentParticipantSourceDto sourceParticipant = new DocumentParticipantSourceDto();
        sourceParticipant.setParticipantFields(getDocumentSystemFieldsForInsertAfterSaveDocument(document));
        sourceParticipant.setName(DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME);
        return parseTemplateByParticipant(content, DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME, Collections.singletonList(sourceParticipant), null, null);
    }

    /**
     * Установить поля докумнта после того, как все подписали документ.
     *
     * @param document
     * @return
     */
    private String finishDocumentContent(Document document) {
        String result = document.getContent();
        DocumentParticipantSourceDto sourceParticipant = new DocumentParticipantSourceDto();
        sourceParticipant.setParticipantFields(getDocumentSystemFieldsForFinishDocument(document));
        sourceParticipant.setName(DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME);
        if (result != null) {
            //participantTypeName - наименование участника типа шаблона
            result = parseTemplateByParticipant(result, DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME, Collections.singletonList(sourceParticipant), null, null);
        }
        result = deleteErrorFieldsFromDocument(result);
        return result;
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @return
     */
    @Override
    @Deprecated
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents) {
        return createDocument(templateCode, createDocumentParameters, documentOwnerId, stateEvents, null);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @return
     */
    @Override
    public Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents) {
        return createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, stateEvents, null, true, null);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @param notifySignEvent          отправлять или нет уведомление о необходимости подписания документа
     * @return
     */
    @Override
    @Deprecated
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, boolean notifySignEvent) {
        return createDocument(templateCode, createDocumentParameters, documentOwnerId, stateEvents, null, notifySignEvent);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @param notifySignEvent          отправлять или нет уведомление о необходимости подписания документа
     * @return
     */
    @Override
    public Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, boolean notifySignEvent) {
        return createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, stateEvents, null, notifySignEvent, null);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     */
    @Override
    @Deprecated
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId) {
        return createDocument(templateCode, createDocumentParameters, documentOwnerId, null, null);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @return
     */
    @Override
    @Deprecated
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate) {
        return createDocument(templateCode, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, true);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @param notifySignEvent          отправлять или нет уведомления о необходимости подписания документа
     * @return
     */
    @Override
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent) {
        return createDocument(templateCode, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, null);
    }

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @param notifySignEvent
     * @param scriptVars
     * @return
     */
    @Override
    public DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars) {
        Document document = createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, scriptVars);
        // TODO Выпилить
        DocumentEntity entity = documentRepository.findOne(document.getId());
        return entity;
    }

    @Override
    public Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars) {
        return createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, scriptVars, true);
    }

    @Override
    public Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument) {
        return createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, scriptVars, canUnsignDocument, true);
    }

    @Override
    public Document createDocumentDomain(Long templateId, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument, boolean needSignByEDS) {
        DocumentTemplate documentTemplate = documentTemplateDomainService.getById(templateId);
        return createDocumentDomain(documentTemplate, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, scriptVars, canUnsignDocument, needSignByEDS);
    }

    @Override
    public Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument, boolean needSignByEDS) {
        DocumentTemplate documentTemplate = documentTemplateDomainService.getByCode(templateCode);
        return createDocumentDomain(documentTemplate, createDocumentParameters, documentOwnerId, stateEvents, expiredDate, notifySignEvent, scriptVars, canUnsignDocument, needSignByEDS);
    }

    private Document createDocumentDomain(DocumentTemplate documentTemplate, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument, boolean needSignByEDS) {
        FlowOfDocumentDTO flowOfDocumentDTO = generateDocumentDTO(documentTemplate, createDocumentParameters, scriptVars);

        // Проверяем документ на наличие полей, которые должны уже быть заполнены
        checkDocumentBeforeSave(flowOfDocumentDTO);

        // Сохраняем документ
        Document document = saveDocument(documentTemplate, flowOfDocumentDTO.getContent(), flowOfDocumentDTO.getName(), flowOfDocumentDTO.getShortName(), documentOwnerId, flowOfDocumentDTO.getParticipants(), expiredDate, canUnsignDocument, needSignByEDS, documentTemplate.getPdfExportArguments());

        // Производим анализ документа
        analyzeAndHandleDocument(document, notifySignEvent);

        List<DocumentParameter> parameters = new ArrayList<>();

        // Сохраняем события в параметры документа
        if (stateEvents != null) {
            for (FlowOfDocumentStateEvent event : stateEvents) {
                DocumentParameter documentParameter = new DocumentParameter();
                documentParameter.setName(DocumentParameter.EVENT_TYPE);
                documentParameter.setValue(event.getStateEventType().getTypeName());
                documentParameter = documentDomainService.saveDocumentParameter(document.getId(), documentParameter);

                parameters.add(documentParameter);

                if (event.getParameters() != null) {
                    for (String parameterName : event.getParameters().keySet()) {
                        documentParameter = new DocumentParameter();
                        documentParameter.setName(parameterName);
                        documentParameter.setValue(event.getParameters().get(parameterName));
                        documentParameter = documentDomainService.saveDocumentParameter(document.getId(), documentParameter);

                        parameters.add(documentParameter);
                    }
                }
            }
        }
        document.setParameters(parameters);

        blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(this, BPMRabbitSignals.DOCUMENT_CREATED, MapsUtils.map(Stream.of(MapsUtils.entry("document", serializeService.toPrimitiveObject(document))))));
        return document;
    }

    /**
     * Установить активность документа
     *
     * @param documentId
     * @param isActive
     */
    @Override
    public void setActiveDocument(Long documentId, boolean isActive) {
        Document document = documentDomainService.getById(documentId);
        document.setActive(isActive);
        documentDomainService.save(document);
    }

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateCode
     * @param createDocumentParameters
     * @return
     */
    @Override
    public FlowOfDocumentDTO generateDocumentDTO(String templateCode, List<CreateDocumentParameter> createDocumentParameters) {
        return generateDocumentDTO(templateCode, createDocumentParameters, null);
    }

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateCode
     * @param createDocumentParameters
     * @return
     * @paran scriptVars
     */
    @Override
    public FlowOfDocumentDTO generateDocumentDTO(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Map<String, Object> scriptVars) {
        DocumentTemplate documentTemplate = documentTemplateDomainService.getByCode(templateCode);
        if (documentTemplate == null) {
            throw new FlowOfDocumentException("Шаблон документа с кодом " + templateCode + " не найден!", FlowOfDocumentExceptionType.NOT_FOUND_TEMLATE);
        }
        return generateDocumentDTO(documentTemplate, createDocumentParameters, scriptVars);
    }

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateId
     * @param createDocumentParameters
     * @return
     */
    @Override
    public FlowOfDocumentDTO generateDocumentDTO(Long templateId, List<CreateDocumentParameter> createDocumentParameters) {
        return generateDocumentDTO(templateId, createDocumentParameters, null);
    }

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateId
     * @param createDocumentParameters
     * @param scriptVars
     * @return
     */
    @Override
    public FlowOfDocumentDTO generateDocumentDTO(Long templateId, List<CreateDocumentParameter> createDocumentParameters, Map<String, Object> scriptVars) {
        DocumentTemplate documentTemplate = documentTemplateDomainService.getById(templateId);
        if (documentTemplate == null) {
            throw new FlowOfDocumentException("Шаблон документа с ИД " + templateId + " не найден!", FlowOfDocumentExceptionType.NOT_FOUND_TEMLATE);
        }
        return generateDocumentDTO(documentTemplate, createDocumentParameters, scriptVars);
    }

    /**
     * Сгенерировать обёртку документа при помощи скрипта
     *
     * @param script
     * @param resultVarName
     * @param scriptVariables
     * @return
     * @throws ScriptException
     */
    @Override
    public FlowOfDocumentDTO generateDocumentDTOByScript(String script, String resultVarName, Map<String, Object> scriptVariables) throws ScriptException {
        scriptVariables.put("flowOfDocumentService", this);
        return scriptEngineService.runScript(COMMON_SCRIPT + script, resultVarName, scriptVariables);
    }


    /**
     * Генерация уникального кода документа.
     *
     * @param document
     * @return
     */
    private String generateDocumentUniqueCode(Document document) {
        String result;
        // Создатель документа
        DocumentCreator creator = document.getCreator();
        List<String> seriesList = new ArrayList<>();
        String fullName = creator.getName().replaceAll("[\\s]{2,}", " ");
        String[] nameParts = fullName.split(" ");
        if (nameParts != null) {
            for (String namePart : nameParts) {
                seriesList.add(namePart.substring(0, 1).toUpperCase());
            }
        } else {
            throw new RuntimeException("У пользователя, создавшего документ не установлено имя!");
        }
        if (nameParts.length < 3) {
            for (int i = 0; i < 3 - nameParts.length; i++) {
                seriesList.add(nameParts[0].substring(0, 1).toUpperCase());
            }
        }
        String series = StringUtils.join(seriesList, "");
        String classCode = document.getDocumentClassId().toString();
        if (classCode.length() < 6) {
            int len = 6 - classCode.length();
            for (int i = 0; i < len; i++) {
                classCode = "0" + classCode;
            }
        }
        result = "Д" + classCode + "-" + series;
        int countDocs = documentDomainService.countByCodePrefix(result);
        String code;
        do {
            countDocs++;
            int charCodeDocNumber = (countDocs / 10) % DOCUMENT_NUMBER_CHARS.size();
            int docNumberSuffix = countDocs % 10;
            int docNumberPrefix = ((countDocs / 10) / DOCUMENT_NUMBER_CHARS.size()) * DOCUMENT_NUMBER_CHARS.size();
            if (docNumberPrefix < 0) {
                docNumberPrefix = 0;
            }
            String strDocNumberPrefix = docNumberPrefix + "";
            if (strDocNumberPrefix.length() < 6) {
                int len = 6 - strDocNumberPrefix.length();
                for (int i = 0; i < len; i++) {
                    strDocNumberPrefix = "0" + strDocNumberPrefix;
                }
            }
            code = result + "-" + strDocNumberPrefix + "-" + DOCUMENT_NUMBER_CHARS.get(charCodeDocNumber) + docNumberSuffix;
        } while (documentDomainService.getByCode(code) != null);
        result = code;
        return result;
    }

    @Override
    public void notifyUnsignDocument(Document document, Long userId) {
        List<DocumentParticipant> documentParticipants = document.getParticipants();
        List<User> users = new ArrayList<>();
        for (DocumentParticipant documentParticipant : documentParticipants) {
            users.addAll(documentParticipantService.getUsersFromParticipantForSignDocument(documentParticipant));
        }
        User sender = userDataService.getByIdMinData(userId);
        for (User targetUser : users) {
            if (!targetUser.getId().equals(sender.getId())) {
                blagosferaEventPublisher.publishEvent(
                        new RameraFlowOfDocumentEvent(
                                this, sender, targetUser, document,
                                RameraFlowOfDocumentEventType.UNSIGN_DOCUMENT, document.getLink()
                        ));
            }
        }

        Map<String, String> parameters = new HashMap<>();
        for (DocumentParameter parameter : document.getParameters()) {
            parameters.put(parameter.getName(), parameter.getValue());
        }
        blagosferaEventPublisher.publishEvent(new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_UNSIGNED, document));

        // TODO Не работает отправка уведомления
        if (document.getDocumentFolder() != null) {
            Map<String, Object> unsignParameters = new HashMap<>();
            unsignParameters.put("unsignDocument", serializeService.toPrimitiveObject(document));
            blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(
                    this,
                    "document_folder_" + document.getDocumentFolder().getId() + "_document_cancel_sign",
                    unsignParameters
            ));
        }
        blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(
                this,
                "document_" + document.getId() + "_cancel_sign",
                Collections.emptyMap()
        ));
    }

    // Создаёт документ перевода денежных средств от физ.лица в объединение(в рамках юр.лица).
    // Тестовый метод. В дальнейшем этот документ должен будет генерироваться другим способом.
    @Override
    public DocumentEntity createSharerToCommunityMoveDocument(UserEntity fromUserEntity, CommunityEntity toCommunity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId) {
        ParticipantCreateDocumentParameter participantParameter1 = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), fromUserEntity.getId(), "part1");
        ParticipantCreateDocumentParameter participantParameter2 = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), toCommunity.getId(), "part2");

        List<UserFieldValue> userFieldValueList1 = new ArrayList<>();
        userFieldValueList1.add(UserFieldValueBuilder.createStringValue("money", amount.toString()));

        List<UserFieldValue> userFieldValueList2 = new ArrayList<>();
        //userFieldValueList2.add(UserFieldValueBuilder.createParticipantValue("participant2", 485l));

        CreateDocumentParameter createDocumentParameter1 = new CreateDocumentParameter(participantParameter1, userFieldValueList1);

        CreateDocumentParameter createDocumentParameter2 = new CreateDocumentParameter(participantParameter2, userFieldValueList2);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        createDocumentParameters.add(createDocumentParameter1);
        createDocumentParameters.add(createDocumentParameter2);

        DocumentEntity document = createDocument("SharerCommunityAccountsMove", createDocumentParameters, userId, stateEvents);
        return document;
    }

    // Создаёт документ перевода денежных средств из объединения(в рамках юр.лица) физ.лицу.
    // Тестовый метод. В дальнейшем этот документ должен будет генерироваться другим способом.
    @Override
    public DocumentEntity createCommunityToSharerMoveDocument(CommunityEntity fromCommunity, UserEntity toUserEntity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId) {
        ParticipantCreateDocumentParameter participantParameter1 = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), toUserEntity.getId(), "part1");
        ParticipantCreateDocumentParameter participantParameter2 = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), fromCommunity.getId(), "part2");

        List<UserFieldValue> userFieldValueList1 = new ArrayList<>();
        //userFieldValueList1.add(UserFieldValueBuilder.createStringValue("money", amount.toString()));

        List<UserFieldValue> userFieldValueList2 = new ArrayList<>();
        userFieldValueList2.add(UserFieldValueBuilder.createStringValue("money", amount.toString()));

        CreateDocumentParameter createDocumentParameter1 = new CreateDocumentParameter(participantParameter1, userFieldValueList1);

        CreateDocumentParameter createDocumentParameter2 = new CreateDocumentParameter(participantParameter2, userFieldValueList2);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        createDocumentParameters.add(createDocumentParameter1);
        createDocumentParameters.add(createDocumentParameter2);

        DocumentEntity document = createDocument("CommunitySharerAccountsMove", createDocumentParameters, userId, stateEvents);
        return document;
    }

    // Создаёт документ перевода денежных средств из объединения(в рамках юр.лица) в объединение(в рамках юр.лица).
    // Тестовый метод. В дальнейшем этот документ должен будет генерироваться другим способом.
    @Override
    public DocumentEntity createCommunityToCommunityMoveDocument(CommunityEntity fromCommunity, CommunityEntity toCommunity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId) {
        ParticipantCreateDocumentParameter participantParameter1 = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), fromCommunity.getId(), "part1");
        ParticipantCreateDocumentParameter participantParameter2 = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), toCommunity.getId(), "part2");

        List<UserFieldValue> userFieldValueList1 = new ArrayList<>();
        userFieldValueList1.add(UserFieldValueBuilder.createStringValue("money", amount.toString()));

        List<UserFieldValue> userFieldValueList2 = new ArrayList<>();
        //userFieldValueList2.add(UserFieldValueBuilder.createStringValue("money", amount.toString()));

        CreateDocumentParameter createDocumentParameter1 = new CreateDocumentParameter(participantParameter1, userFieldValueList1);

        CreateDocumentParameter createDocumentParameter2 = new CreateDocumentParameter(participantParameter2, userFieldValueList2);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        createDocumentParameters.add(createDocumentParameter1);
        createDocumentParameters.add(createDocumentParameter2);

        DocumentEntity document = createDocument("CommunitiesAccountsMove", createDocumentParameters, userId, stateEvents);
        return document;
    }

    /**
     * Конвертируем документ, чтобы послать его через Rabbit
     */
    private Map<String, Object> convertDocumentToSend(Document document) {
        return serializeService.toPrimitiveObject(document);
    }

    // Реализация парсинга шаблона документа.
    private String parseTemplateByParticipant(String template,
                                              String participantTemplateName,
                                              List<DocumentParticipantSourceDto> participants,
                                              Map<String, Object> scriptVars, Map<String, List<DocumentParticipantSourceDto>> allTemplateParticipants) {
        return new ParticipantFieldParser(participantTemplateName, participants, listEditorItemDomainService, allTemplateParticipants)
                .parseTemplate(template, scriptVars);
    }

    // Удалить все ошибочные поля из документа.
    private String deleteErrorFieldsFromDocument(String content) {
        return content.replaceAll("[\\s]*class=\"mceNonEditable\"[^>]*data-field-id=\"[^>]*><", "><");
    }

    // Сохранить документ
    private Document saveDocument(
            DocumentTemplate documentTemplate, String documentContent, String documentName, String documentShortName,
            Long creatorId, List<DocumentParticipantSourceDto> sourceParticipants, Date expiredDate, boolean canUnsignDocument,
            boolean needSignByEDS, String pdfExportArguments) {
        if (documentTemplate == null || documentTemplate.getDocumentClass() == null) {
            throw new RuntimeException("Класс документа не найден!");
        }
        DocumentClass documentType = documentTemplate.getDocumentClass();

        // Производим инициализацию участников документа
        List<DocumentParticipant> documentParticipants = new ArrayList<>();
        for (DocumentParticipantSourceDto sourceParticipant : sourceParticipants) {
            documentParticipants.add(
                    documentParticipantService.convertSourceParticipantToDocumentParticipant(documentTemplate, sourceParticipant)
            );
        }

        DocumentCreator documentCreator = null;
        if (creatorId != null && creatorId > 0) {
            User userCreator = new User();
            userCreator.setId(creatorId);
            documentCreator = userCreator;
        }

        // Создаём документ
        Document document = new Document();
        document.setContent(documentContent);
        document.setName(documentName);
        document.setShortName(documentShortName);
        document.setDocumentClassId(documentType.getId());
        document.setCreateDate(new Date());
        document.setCreator(documentCreator);
        document.setParticipants(documentParticipants);
        document.setActive(true);
        document.setExpiredDate(expiredDate);
        document.setCanUnsignDocument(canUnsignDocument);
        document.setNeedSignByEDS(needSignByEDS);
        document.setPdfExportArguments(pdfExportArguments);
        // Код докмента присваивается после подписания документа всему кому нужно подписать
        //document.setCode(generateDocumentUniqueCode(document));
        document.setHashCode(generateDocumentHashCode(document));
        document = documentDomainService.save(document);

        // Производим внедрение полей документа, которые устанавливаются после его сохранения
        document.setContent(handleDocumentContentAfterSave(document, document.getContent()));
        document.setName(handleDocumentContentAfterSave(document, document.getName()));

        // Удаляем теги из наименования документа
        documentName = Jsoup.parse(document.getName()).text();
        documentName = documentName.replaceAll("&nbsp;", " ");
        documentName = StringUtils.join(documentName.split("[\\s]"), " ");

        document.setName(documentName);

        document = documentDomainService.save(document);

        return document;
    }

    // Парсинг шаблона
    private String parseTemplate(String content, List<DocumentParticipantSourceDto> sourceParticipants, Map<String, List<DocumentUserField>> userFieldsMap, Map<String, Object> scriptVars) {
        String result = content;

        // Инъектим значения обычных полей в документ
        result = createDocumentContentByTemplateContent(result, sourceParticipants, scriptVars);
        // Устанавливаем пользовательские поля участника
        for (DocumentParticipantSourceDto participantSource : sourceParticipants) {
            if (userFieldsMap.containsKey(participantSource.getName())) {
                result = userFieldsParserService.parseDocumentByUserFields(result, participantSource, userFieldsMap.get(participantSource.getName()));
            }
        }
        return result;
    }

    // Проверить документ перед сохранением на наличие полей, которые не были заполнены
    private void checkDocumentBeforeSave(FlowOfDocumentDTO flowOfDocumentDTO) {
        checkDocumentContentBeforeSave(flowOfDocumentDTO.getName());
        checkDocumentContentBeforeSave(flowOfDocumentDTO.getShortName());
        checkDocumentContentBeforeSave(flowOfDocumentDTO.getHtmlDecodedName());
        checkDocumentContentBeforeSave(flowOfDocumentDTO.getContent());
    }

    // Проверить поля в контенте документа
    private void checkDocumentContentBeforeSave(String content) {
        Set<String> excludedParticipants = new HashSet<>(
                Collections.singletonList(
                        DOCUMENT_SYSTEM_FIELDS_PARTICIPANT_NAME
                )
        );
        ExceptionUtils.check(ParticipantFieldParser.findFieldsInContent(content, excludedParticipants), "Были заполнены не все поля");
    }

    // Создать обёртку документа по параметрам
    private FlowOfDocumentDTO generateDocumentDTO(DocumentTemplate documentTemplate, List<CreateDocumentParameter> createDocumentParameters, Map<String, Object> scriptVars) {
        String documentContent = documentTemplate.getContent();

        List<DocumentParticipantSourceDto> participants = new ArrayList<>();
        Map<String, List<DocumentUserField>> participantsUserFields = new HashMap<>();

        for (CreateDocumentParameter createDocumentParameter : createDocumentParameters) {
            ParticipantCreateDocumentParameter participantParameter = createDocumentParameter.getParticipantParameter();

            int index = 1;
            if (participantParameter.getDocumentParticipants() != null) {
                DocumentParticipantSourceDto participantSource = null;
                for (IDocumentParticipant documentParticipant : participantParameter.getDocumentParticipants()) {
                    // Получаем участника из системы по его типу и ИД.
                    participantSource = createFlowOfDocumentParticipant(participantParameter.getType(), participantParameter.getName(), documentParticipant, index++);
                    participants.add(participantSource);
                }
                if (participantSource != null) {
                    // Получить пользовательские поля и добавить к ним значения
                    List<DocumentUserField> userFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentContent, participantSource.getName());
                    // Установить значения полей из параметров
                    userFieldsParserService.setUserFieldsValues(userFields, createDocumentParameter);
                    participantsUserFields.put(participantSource.getName(), userFields);
                }
            } else if (participantParameter.getIds() != null) { // Список участников
                DocumentParticipantSourceDto participant = null;
                for (Long participantSourceId : participantParameter.getIds()) {
                    // Получаем участника из системы по его типу и ИД.
                    participant = createFlowOfDocumentParticipant(participantParameter.getType(), participantParameter.getName(), participantSourceId, index++);
                    participants.add(participant);
                }
                if (participant != null) {
                    // Получить пользовательские поля и добавить к ним значения
                    List<DocumentUserField> userFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentContent, participant.getName());
                    // Установить значения полей из параметров
                    userFieldsParserService.setUserFieldsValues(userFields, createDocumentParameter);
                    participantsUserFields.put(participant.getName(), userFields);
                }
            } else { // Один участник
                // Получаем участника из системы по его типу и ИД.
                DocumentParticipantSourceDto participant = createFlowOfDocumentParticipant(participantParameter.getType(), participantParameter.getName(), participantParameter.getId(), index);
                participants.add(participant);

                // Получить пользовательские поля и добавить к ним значения
                List<DocumentUserField> userFields = userFieldsParserService.getDocumentUserFieldsByParticipant(documentContent, participant.getName());
                // Установить значения полей из параметров
                userFieldsParserService.setUserFieldsValues(userFields, createDocumentParameter);
                participantsUserFields.put(participant.getName(), userFields);
            }
        }

        checkSignedParticipants(participants, documentTemplate);

        // Парсим контент документа
        documentContent = parseTemplate(documentContent, participants, participantsUserFields, scriptVars);
        // Парсим наименоваение документа
        String documentName = parseTemplate(documentTemplate.getDocumentName(), participants, participantsUserFields, scriptVars);
        // Парсим сокращённое наименование документа
        String documentShortName = parseTemplate(documentTemplate.getDocumentShortName(), participants, participantsUserFields, scriptVars);

        return new FlowOfDocumentDTO(documentName, documentShortName, documentContent, participants);
    }

    private void checkSignedParticipants(List<DocumentParticipantSourceDto> participants, DocumentTemplate documentTemplate) {
        List<DocumentTemplateParticipant> documentTemplateParticipants = documentTemplate.getDocumentTemplateParticipants();
        if (documentTemplateParticipants != null && !documentTemplateParticipants.isEmpty()) {
            for (DocumentTemplateParticipant documentTemplateParticipant : documentTemplateParticipants) {
                boolean foundParticipant = searchParticipant(participants, documentTemplateParticipant);
                ExceptionUtils.check(!foundParticipant, "Для создания документа на основе шаблона \"" + documentTemplate.getName() + "\" не установлен обязательный участник документа - \"" + documentTemplateParticipant.getParticipantName() + "\"");
            }
        }
    }

    private boolean searchParticipant(List<DocumentParticipantSourceDto> participants, DocumentTemplateParticipant documentTemplateParticipant) {
        boolean foundParticipant = false;
        for (DocumentParticipantSourceDto documentParticipantSourceDto : participants) {
            if (documentParticipantSourceDto.getName().equals(documentTemplateParticipant.getParticipantName())) {
                foundParticipant = true;
            }
            if (!foundParticipant && documentParticipantSourceDto.getChildMap() != null) {
                for (String key : documentParticipantSourceDto.getChildMap().keySet()) {
                    List<DocumentParticipantSourceDto> child = documentParticipantSourceDto.getChildMap().get(key);
                    if (searchParticipant(child, documentTemplateParticipant)) {
                        foundParticipant = true;
                        break;
                    }
                }
                //
            }
            if (foundParticipant) {
                break;
            }
        }
        return foundParticipant;
    }

    // Подписать документ всеми участниками после того как он в последний раз парвился
    private void setSignaturesToParticipants(Document document, Map<Long, Long> usersWhoSignDocument) {
        // Создаём подписи документа перебрав всех участников которые должны подписать документ
        SignMessageUsersByDefaultKeyParameters signMessageParameters = new SignMessageUsersByDefaultKeyParameters();
        signMessageParameters.setMessage(document.getHashCodeForSignature());
        signMessageParameters.setUserIds(usersWhoSignDocument.keySet());
        try {
            // Подписываем документ цифровой подписью всеми участниками кто должен подписать
            SignOperationResult<SignMessageUsersByDefaultKeyResponseDto> signOperationResult =
                    commonSignerService.signMessageUsersByDefaultKey(signMessageParameters);
            ExceptionUtils.check(
                    signOperationResult == null ||
                            signOperationResult.getData() == null ||
                            signOperationResult.getData().getUsersSignaturesBase64Map() == null,
                    "При подписании документа возникла ошибка"
            );
            // Устанавливаем подписи
            for (Long userId : signOperationResult.getData().getUsersSignaturesBase64Map().keySet()) {
                String base64Signature = signOperationResult.getData().getUsersSignaturesBase64Map().get(userId);
                Long participantId = usersWhoSignDocument.get(userId);
                documentParticipantDomainService.setSignatureByParticipantId(participantId, base64Signature);
            }
        } catch (CommonSignerException e) {
            ExceptionUtils.check(true, e.getMessage());
        }
    }

    // Создать хеш код документа, который будет его идентифицировать в урл
    private String generateDocumentHashCode(Document document) {
        String result = String.valueOf(MurmurHash.hash64(document.getContent() + new Date().toString() + new Random().nextInt()));
        return result.replaceAll("-", "");
    }

    // Создать хеш код документа после того как все подпишут документ и больше изменений в документе не должно быть.
    // Хеш нужен для создения подписей участников документа для того, чтобы не отсылоть контент документа целиком.
    private String generateDocumentHashCodeForSignature(Document document) {
        return IkpUtils.longToIkpHash(MurmurHash.hash64(document.getContent()));
    }

    @Override
    public List<PossibleSourceParticipantsDto> getPossibleParticipants(Long templateId) {
        List<PossibleSourceParticipantsDto> result = null;
        List<DocumentClassDataSource> documentClassDataSources = documentClassDomainService.getDataSourcesByTemplateId(templateId);
        if (documentClassDataSources != null) {
            result = new ArrayList<>();
            List<DocumentUserField> documentUserFields = getUserFieldsByTemplateId(templateId);
            for (DocumentClassDataSource documentClassDataSource : documentClassDataSources) {
                PossibleSourceParticipantsDto possibleSourceParticipantsDto = documentParticipantService.getPossibleSourceParticipants(documentClassDataSource);
                if (documentUserFields != null) {
                    for (DocumentUserField documentUserField : documentUserFields) {
                        if (documentClassDataSource.getId().equals(documentUserField.getParticipantId())) {
                            possibleSourceParticipantsDto.setUserFields(DocumentUserFieldDto.toDtoList(documentUserFields));
                        }
                    }
                }
                result.add(possibleSourceParticipantsDto);
            }

        }
        return result;
    }

    @Override
    public DocumentPageDto getDocumentPageDto(String documentHashCode, Long userId) {
        Document document = documentDomainService.getByHashCode(documentHashCode);
        if (document == null) { // Если по хешу документ не найден, то ищем по ИД
            document = documentDomainService.getById(VarUtils.getLong(documentHashCode, -1l));
        }
        ExceptionUtils.check(document == null, "Документ не найден");

        Map<DocumentParticipant, List<ParticipantRight>> participantRights = getRightToDocument(document.getId(), userId);
        boolean userHasViewRight = false;
        boolean userHasFillUserFieldsRight = false;
        boolean userHasSignDocumentRight = false;
        if (participantRights != null) {
            for (DocumentParticipant part : participantRights.keySet()) {
                List<ParticipantRight> participantRightList = participantRights.get(part);
                userHasViewRight = userHasViewRight || participantRightList.contains(ParticipantRight.VIEW);
                userHasFillUserFieldsRight = userHasFillUserFieldsRight || participantRightList.contains(ParticipantRight.FILL_USER_FIELDS);
                userHasSignDocumentRight = userHasSignDocumentRight || participantRightList.contains(ParticipantRight.SIGN);
            }
        }
        ExceptionUtils.check(!userHasViewRight && !userHasFillUserFieldsRight && !userHasSignDocumentRight, "У Вас нет доступа к документу");


        // Определить есть ли незаполненные пользовательские поля
        // Если есть права на заполнение пользовательских полей и есть поля - добавить в модель список полей
        // Иначе, если есть права на подписание документа и нет пользовательских полей в документе - то добавить в модель список участников от кого подписывает документ пользователь
        // Иначе, если есть права на просмотр документа
        boolean userHasDocumentRight = false;
        boolean documentHasUserFields = isDocumentHasUserFields(document);
        List<DocumentParticipant> signParticipants = null;
        boolean isSigned = true;
        List<DocumentParticipant> participantsOfUser = documentParticipantService.getParticipantsOfUser(document, userId);
        Map<DocumentParticipant, List<DocumentUserField>> userFields = getDocumentUserFields(document.getId(), userId);
        List<DocumentUserField> allUserFields = new ArrayList<>();
        for (DocumentParticipant participant : userFields.keySet()) {
            allUserFields.addAll(userFields.get(participant));
        }
        if (documentHasUserFields && userHasFillUserFieldsRight && allUserFields.size() > 0) {
            Collections.sort(allUserFields, new UserFieldsComparator());
            //model.addAttribute("userFields", allUserFields); 111111
            userHasDocumentRight = true;
        } else if (!documentHasUserFields && userHasSignDocumentRight) {
            signParticipants = new ArrayList<>();
            for (DocumentParticipant participant : participantsOfUser) {
                /*if (participant.getParent() != null) {
                    //setParticipantsName(Arrays.asList(participant.getParent()));
                }*/
                if (participant.isNeedSignDocument()) {
                    signParticipants.add(participant);
                    isSigned = isSigned && participant.isSigned();
                }
            }
            userHasDocumentRight = true;
        } else if (userHasViewRight) {
            userHasDocumentRight = true;
        }
        //model.addAttribute("userHasDocumentRight", userHasDocumentRight); 44444
        if (userHasDocumentRight) {
            // Установить наименования реальных участников
            //setParticipantsName(document.getParticipants());
            // Установить создателя документа
            //Object creator = getDocumentCreator(document);

            //model.addAttribute("document", document); 5555
            //model.addAttribute("creator", creator); 6666

            /*long communityId = -1l;
            if (request.getAttribute("community_id") != null) {
                try {
                    communityId = ((Long)request.getAttribute("community_id")).longValue();
                } catch (Exception e) {
                    // do nothing
                }
            }*/
            /*boolean isFoundParentParticipantByCommunityId = false;

            // Если пользователь не является не дочерней стороной документа (не физ лицо и не регистратор), то ищем родительского участника пользователя
            DocumentParticipant parentParticipant = null;
            DocumentParticipant userParticipant = null;
            for (DocumentParticipant participant : participantRights.keySet()) {
                List<ParticipantRight> participantRightList = participantRights.get(participant);
                if ((participant.getChildren() == null || participant.getChildren().size() == 0) && participantRightList != null && participantRightList.size() > 0) { // Пользователь как непосредственный участник документа
                    userParticipant = participant;
                } else if (participant.getChildren() != null && participant.getChildren().size() > 0 && participantRightList != null && participantRightList.size() > 0) { // Пользователь имеет права от имени сообщества
                    // Если у участника есть пользовательские поля, то выбираем его родительским участником
                    // Иначе ищем права на подпись или просмотр

                    for (DocumentParticipant userFieldParticipant : userFields.keySet()) {
                        List<DocumentUserField> participantUserFields = userFields.get(userFieldParticipant);
                        if (userFieldParticipant.getSourceParticipantId().longValue() == participant.getSourceParticipantId().longValue() &&
                                userFieldParticipant.getParticipantTypeName().equals(participant.getParticipantTypeName()) &&
                                participantUserFields.size() > 0) {
                            parentParticipant = participant;
                            isFoundParentParticipantByCommunityId = isFoundParentParticipantByCommunityId || communityId == parentParticipant.getSourceParticipantId().longValue();
                        }
                    }
                    if (parentParticipant == null && (participantRightList.contains(ParticipantRight.VIEW) || participantRightList.contains(ParticipantRight.SIGN))) {
                        parentParticipant = participant;
                        isFoundParentParticipantByCommunityId = isFoundParentParticipantByCommunityId || communityId == parentParticipant.getSourceParticipantId().longValue();
                    }
                }
            }

            // Если не найден непосредственный участник документа по текущему пользователю, то редиректим на страницу документа сообщества
            boolean isNeedRedirect = false;
            if (userParticipant == null && communityId == -1) {
                communityId = parentParticipant.getSourceParticipantId();
                isNeedRedirect = true;
            } else if (userParticipant == null && communityId > -1 && !isFoundParentParticipantByCommunityId) { // Не правильный ИД сообщества
                communityId = parentParticipant.getSourceParticipantId();
                isNeedRedirect = true;
            }*/
        }



        return new DocumentPageDto(
                document.getId(),
                document.getName(),
                document.isActive(),
                document.getCreateDate(),
                document.getCode(),
                allUserFields,
                signParticipants,
                document.getParticipants(),
                documentParticipantService.getSourceNames(document),
                isSigned,
                userHasDocumentRight,
                document.getCreator(),
                document.isCanUnsignDocument()
        );
    }

    @Override
    public boolean isSignedDocument(Long documentId) {
        Document document = documentDomainService.getById(documentId);
        return isSignedDocument(document);
    }

    @Override
    public boolean isSignedDocument(Document document) {
        return isSignedDocument(document.getParticipants());
    }

    public boolean isSignedDocument(List<DocumentParticipant> participants) {
        boolean result = true;
        if (participants != null && !participants.isEmpty()) {
            for (DocumentParticipant documentParticipant : participants) {
                if (documentParticipant.isNeedSignDocument() && !documentParticipant.isSigned()) {
                    result = false;
                    break;
                }
                result = isSignedDocument(documentParticipant.getChildren());
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }
}
