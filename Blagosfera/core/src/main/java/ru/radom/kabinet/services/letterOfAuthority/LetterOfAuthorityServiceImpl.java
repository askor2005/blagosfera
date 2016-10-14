package ru.radom.kabinet.services.letterOfAuthority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.LetterOfAuthorityAttributeRepository;
import ru.askor.blagosfera.data.jpa.repositories.LetterOfAuthorityRepository;
import ru.askor.blagosfera.data.jpa.repositories.LetterOfAuthorityRoleRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.document.LetterOfAuthorityEvent;
import ru.askor.blagosfera.domain.events.document.LetterOfAuthorityEventType;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.letterofauthority.LetterOfAuthorityRole;
import ru.askor.blagosfera.domain.loa.LetterOfAuthorityScope;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.communities.CommunityPermissionDao;
import ru.radom.kabinet.dao.communities.CommunityPostDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.letterofauthority.CommunityRoleTypes;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityAttributeEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityRoleEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.settings.SharerSetting;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.web.lettersofauthority.dto.LetterOfAuthorityDto;
import ru.radom.kabinet.web.lettersofauthority.dto.RadomAccountDto;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

@Transactional
@Service("letterOfAuthorityService")
public class LetterOfAuthorityServiceImpl implements LetterOfAuthorityService {

    @Autowired
    private LetterOfAuthorityRoleRepository letterOfAuthorityRoleRepository;

    @Autowired
    private LetterOfAuthorityRepository letterOfAuthorityRepository;

    @Autowired
    private LetterOfAuthorityAttributeRepository letterOfAuthorityAttributeRepository;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityPostDao communityPostDao;

    @Autowired
    private CommunityPermissionDao communityPermissionDao;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    // Ключ настройки - дни до истеения действия доверенности, когда нужно оповещять участников доверенностей
    private static final String NOTIFY_EXPIRED_DATE_LETTERS_OF_AUTHORITIES_SETTINGS_KEY = "lettersofauthorities.expireddate.notify.days.before";

    // Значение по умолчанию настройки NOTIFY_EXPIRED_DATE_LETTERS_OF_AUTHORITIES_SETTINGS_KEY
    private static final String NOTIFY_EXPIRED_DATE_LETTERS_OF_AUTHORITIES_DEFAULT_VALUE = "90,60,30,14,7";

    // Префикс настройки участника системы - оповещение о доверенности было отправлено
    private static final String SEND_NOTIFY_PREFFIX_SHARER_SETTINGS_KEY = "letterofauthority.send.before.days.";

    // Общая часть скрипта для создания документа
    private static final String COMMON_SCRIPT =
            "var ParticipantCreateDocumentParameter = Packages.ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter; " +
            "var CreateDocumentParameter = Packages.ru.radom.kabinet.document.generator.CreateDocumentParameter; " +
            "var ArrayList = Packages.java.util.ArrayList;";

    /**
     * Получить все права доступа в объединенях
     * @return
     */
    @Override
    public List<CommunityPermissionEntity> getAllCommunityPermissions() {
        return communityPermissionDao.findAll();
    }

    /**
     * Список полей в объединениях с типом SHARER
     * @return
     */
    @Override
    public List<FieldEntity> getSharerCommunityFields() {
        return fieldDao.getByType(FieldType.SHARER);
    }

    /**
     * Список полей в объединениях с типом SHARER_LIST
     * @return
     */
    @Override
    public List<FieldEntity> getSharerListCommunityFields() {
        return fieldDao.getByType(FieldType.PARTICIPANTS_LIST);
    }

    /**
     * Получить все роли доверенностей
     * @return
     */
    @Override
    public List<LetterOfAuthorityRoleEntity> getAllLetterOfAuthorityRoles() {
        return letterOfAuthorityRoleRepository.findAll();
    }

    @Override
    public List<LetterOfAuthorityRoleEntity> getLetterOfAuthorityRolesByFilter(String nameSearchString, int page) {
        // page c 0
        Pageable pageable = new PageRequest(page, COUNT_ELEMENTS_IN_GRID_PAGE/*, Sort.Direction.ASC*/);
        return letterOfAuthorityRoleRepository.findByNameContaining(nameSearchString, pageable);
    }

    @Override
    public int getCountLetterOfAuthorityRolesByFilter(String nameSearchString) {
        return letterOfAuthorityRoleRepository.countByNameContaining(nameSearchString);
    }

    /**
     * Сохранить роль доверенностей
     * @param letterOfAuthorityRoleEntityForm
     * @param listEditorItemId
     */
    @Override
    public void createLetterOfAuthorityRole(LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntityForm, Long listEditorItemId) {
        if (letterOfAuthorityRoleEntityForm.getKey() == null || letterOfAuthorityRoleEntityForm.getKey().equals("")) {
            throw new RuntimeException("Не установлен код роли доверенности.");
        }
        if (letterOfAuthorityRoleEntityForm.getName() == null || letterOfAuthorityRoleEntityForm.getName().equals("")) {
            throw new RuntimeException("Не установлено имя роли доверенности.");
        }
        if (letterOfAuthorityRoleRepository.getByKey(letterOfAuthorityRoleEntityForm.getKey()) != null) {
            throw new RuntimeException("Роль доверенности с таким кодом уже существует.");
        }

        RameraListEditorItem rameraListEditorItem = null;
        if (listEditorItemId != null) {
            rameraListEditorItem = rameraListEditorItemDAO.getById(listEditorItemId);
        }

        if (letterOfAuthorityRoleEntityForm.getScopeType() == null || !SCOPE_TYPES.containsKey(letterOfAuthorityRoleEntityForm.getScopeType())) {
            throw new RuntimeException("Неправильно задан тип объекта в рамках которого создаётся доверенность.");
        }

        if (Discriminators.COMMUNITY.equals(letterOfAuthorityRoleEntityForm.getScopeType())) {
            if (letterOfAuthorityRoleEntityForm.getScopeRoleType() != null && !SCOPE_ROLE_TYPES.containsKey(letterOfAuthorityRoleEntityForm.getScopeRoleType())) {
                throw new RuntimeException("Неправильно задан тип роли в объединении.");
            }
        }

        LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity = new LetterOfAuthorityRoleEntity();
        letterOfAuthorityRoleEntity.setCreateDocumentScript(letterOfAuthorityRoleEntityForm.getCreateDocumentScript());
        letterOfAuthorityRoleEntity.setName(letterOfAuthorityRoleEntityForm.getName());
        letterOfAuthorityRoleEntity.setKey(letterOfAuthorityRoleEntityForm.getKey());
        letterOfAuthorityRoleEntity.setRameraListEditorItem(rameraListEditorItem);
        letterOfAuthorityRoleEntity.setScopeRoleName(letterOfAuthorityRoleEntityForm.getScopeRoleName());
        letterOfAuthorityRoleEntity.setScopeRoleType(letterOfAuthorityRoleEntityForm.getScopeRoleType());
        letterOfAuthorityRoleEntity.setScopeType(letterOfAuthorityRoleEntityForm.getScopeType());

        letterOfAuthorityRoleRepository.save(letterOfAuthorityRoleEntity);
    }

    /**
     * Обновить роль доверенности
     * @param letterOfAuthorityRoleEntityForm
     */
    @Override
    public void updateLetterOfAuthorityRole(LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntityForm, Long listEditorItemId) {
        RameraListEditorItem rameraListEditorItem = null;
        if (listEditorItemId != null) {
            rameraListEditorItem = rameraListEditorItemDAO.getById(listEditorItemId);
        }

        LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity = letterOfAuthorityRoleRepository.getOne(letterOfAuthorityRoleEntityForm.getId());

        letterOfAuthorityRoleEntity.setCreateDocumentScript(letterOfAuthorityRoleEntityForm.getCreateDocumentScript());
        letterOfAuthorityRoleEntity.setName(letterOfAuthorityRoleEntityForm.getName());
        //letterOfAuthorityRoleEntity.setKey(letterOfAuthorityRoleEntityForm.getKey());
        //letterOfAuthorityRoleEntity.setRameraListEditorItem(rameraListEditorItem);
        /*letterOfAuthorityRoleEntity.setScopeRoleName(letterOfAuthorityRoleEntityForm.getScopeRoleName());
        letterOfAuthorityRoleEntity.setScopeRoleType(letterOfAuthorityRoleEntityForm.getScopeRoleType());
        letterOfAuthorityRoleEntity.setScopeType(letterOfAuthorityRoleEntityForm.getScopeType());*/

        letterOfAuthorityRoleRepository.save(letterOfAuthorityRoleEntity);
    }

    /**
     * Удалить роль доверенности
     * @param id
     */
    @Override
    public void deleteLetterOfAuthorityRole(Long id) {
        letterOfAuthorityRoleRepository.delete(id);
    }

    /**
     * Получить роли доверенностей по типу объекта
     * @param scopeType
     * @return
     */
    @Override
    public List<LetterOfAuthorityRole> getLetterOfAuthorityRoles(String scopeType) {
        List<LetterOfAuthorityRole> result = new ArrayList<>();
        List<LetterOfAuthorityRoleEntity> entities = letterOfAuthorityRoleRepository.findByScopeType(scopeType);

        for (LetterOfAuthorityRoleEntity entity : entities) {
            result.add(entity.toDomain());
        }

        return result;
    }

    /**
     * Получить объекты в рамках которых будет создана доверенность
     * @param roleKey
     * @return
     */
    @Override
    public List<RadomAccount> getRadomAccountsByRole(String roleKey) {
        LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity = letterOfAuthorityRoleRepository.getByKey(roleKey);
        UserEntity currentUser = sharerDao.getById(SecurityUtils.getUser().getId());
        List<RadomAccount> result = new ArrayList<>();
        if (letterOfAuthorityRoleEntity != null) {
            switch (letterOfAuthorityRoleEntity.getScopeType()) {
                case Discriminators.SHARER: { // Для пользователя возращаем текущего пользователя
                    result.add(currentUser);
                    break;
                }
                case Discriminators.COMMUNITY: {
                    // Загружаем объединения по параметрам
                    String fieldValue = letterOfAuthorityRoleEntity.getRameraListEditorItem() == null ? null : String.valueOf(letterOfAuthorityRoleEntity.getRameraListEditorItem().getId());
                    if (fieldValue != null && !fieldValue.equals("")) {
                        result.addAll(communityDao.getByField(currentUser, FieldConstants.COMMUNITY_ASSOCIATION_FORM, fieldValue));
                    } else {
                        result.addAll(communityDao.getByMember(currentUser.getId()));
                    }
                    // Фильтруем по настройкам роли доверенности
                    filterCommunities(result, letterOfAuthorityRoleEntity);
                    break;
                }
            }
        }
        return result;
    }

    private void filterCommunities(List<RadomAccount> radomAccounts, LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity) {
        UserEntity currentUser = sharerDao.getById(SecurityUtils.getUser().getId());
        // Фильтруем по настройкам роли доверенности
        if (letterOfAuthorityRoleEntity.getScopeRoleType() != null) {
            switch(letterOfAuthorityRoleEntity.getScopeRoleType()) {
                case CommunityRoleTypes.COMMUNITY_POST: { // Фильтруем объединения по занимаемому посту текущего участника
                    Iterator<RadomAccount> radomAccountIterator = radomAccounts.iterator();
                    while (radomAccountIterator.hasNext()) {
                        CommunityEntity community = (CommunityEntity) radomAccountIterator.next();
                        // Если участник не находится на посту в объединении, то удаляем объединение из списка
                        if (!communityPostDao.checkMemberOnPost(community, letterOfAuthorityRoleEntity.getScopeRoleName(), currentUser)) {
                            radomAccountIterator.remove();
                        }
                    }
                    break;
                }
                case CommunityRoleTypes.COMMUNITY_PERMISSION: { // Фильтруем объединения по правам текущего участника
                    Iterator<RadomAccount> radomAccountIterator = radomAccounts.iterator();
                    while (radomAccountIterator.hasNext()) {
                        CommunityEntity community = (CommunityEntity) radomAccountIterator.next();
                        // Если у участника в объединении нет определённых прав, то удаляем объединение из списка
                        if (!communityPermissionDao.checkPermissionBySharer(community, currentUser, letterOfAuthorityRoleEntity.getScopeRoleName())) {
                            radomAccountIterator.remove();
                        }
                    }
                    break;
                }
                case CommunityRoleTypes.COMMUNITY_FIELD_SHARER: {
                    // ИД участника у поля с типом SHARER находится в поле в именем [поле]_ID
                    String fieldInternalName = letterOfAuthorityRoleEntity.getScopeRoleName() + "_ID";
                    Iterator<RadomAccount> radomAccountIterator = radomAccounts.iterator();
                    while (radomAccountIterator.hasNext()) {
                        CommunityEntity communityEntity = (CommunityEntity) radomAccountIterator.next();
                        Community community = communityDomainService.getByIdFullData(communityEntity.getId());
                        //
                        String fieldValue = community.getCommunityData().getFieldValueByInternalName(fieldInternalName);
                        Long sharerId = VarUtils.getLong(fieldValue, -1l);
                        // Если участник не является назначенным участником в объедиении в поле с типом SHARER, то удаляем объединение из списка
                        if (!currentUser.getId().equals(sharerId)) {
                            radomAccountIterator.remove();
                        }
                    }
                    break;
                }
                case CommunityRoleTypes.COMMUNITY_FIELD_SHARER_LIST: {
                    String fieldInternalName = letterOfAuthorityRoleEntity.getScopeRoleName();
                    Iterator<RadomAccount> radomAccountIterator = radomAccounts.iterator();
                    while (radomAccountIterator.hasNext()) {
                        CommunityEntity communityEntity = (CommunityEntity) radomAccountIterator.next();
                        Community community = communityDomainService.getByIdFullData(communityEntity.getId());
                        //
                        String fieldValue = community.getCommunityData().getFieldValueByInternalName(fieldInternalName);
                        boolean found = false;
                        if (fieldValue != null && !fieldValue.equals("")) {
                            String[] idsStr = fieldValue.split(";");
                            for (String idStr : idsStr) {
                                Long sharerId = VarUtils.getLong(idStr, -1l);
                                if (currentUser.getId().equals(sharerId)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        // Если участник не является назначенным участником в объедиении в поле с типом PARTICIPANTS_LIST, то удаляем объединение из списка
                        if (!found) {
                            radomAccountIterator.remove();
                        }
                    }

                }
            }
        }
    }

    /**
     * Получить возможных делегатов доверенности по ИД оъединения
     * @param radomAccountId
     * @return
     */
    @Override
    public PossibleDelegates getPossibleDelegates(String roleKey, Long radomAccountId, int page, String searchString) {
        PossibleDelegates result = null;
        LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity = letterOfAuthorityRoleRepository.getByKey(roleKey);
        if (letterOfAuthorityRoleEntity != null) {
            switch (letterOfAuthorityRoleEntity.getScopeType()) {
                case Discriminators.SHARER: {
                    int firstResult = page * COUNT_ELEMENTS_IN_GRID_PAGE;
                    int count = sharerDao.getSearchCount(searchString, false);
                    List<UserEntity> delegates = sharerDao.search(searchString, firstResult, COUNT_ELEMENTS_IN_GRID_PAGE);
                    result = new PossibleDelegates(count, delegates);
                    break;
                }
                case Discriminators.COMMUNITY: {
                    CommunityEntity community = communityDao.getById(radomAccountId);
                    // Фильтруем объединение по роли доверенности
                    List<RadomAccount> radomAccounts = Collections.singletonList((RadomAccount)community);
                    filterCommunities(radomAccounts, letterOfAuthorityRoleEntity);
                    if (radomAccounts != null && radomAccounts.size() > 0) {
                        List<CommunityMemberEntity> members = communityMemberDao.getList(community, COUNT_ELEMENTS_IN_GRID_PAGE, page, true, searchString);
                        List<UserEntity> delegates = new ArrayList<>();
                        int count = communityMemberDao.getListCount(community, true, searchString);
                        if (members != null) {
                            for (CommunityMemberEntity member : members) {
                                delegates.add(member.getUser());
                            }
                        }
                        result = new PossibleDelegates(count, delegates);
                    }
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Создать документ на основе скрипта роли
     * @param expiredDate
     * @return
     */
    private DocumentEntity createDocument(LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity, Date expiredDate, RadomAccount scopeObject, UserEntity delegate, UserEntity owner) {
        DocumentEntity documentEntity;

        try {
            UserEntity currentUser = sharerDao.getById(SecurityUtils.getUser().getId());
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            // инъектим переменные в скрипт
            engine.put("flowOfDocumentService", documentService);
            engine.put("expiredDate", expiredDate);
            engine.put("scopeObject", scopeObject);
            engine.put("owner", owner);
            engine.put("delegate", delegate);
            engine.put("currentUser", currentUser);

            // Выполняем скрипт
            engine.eval(COMMON_SCRIPT + letterOfAuthorityRoleEntity.getCreateDocumentScript());

            // Получаем результат
            documentEntity = (DocumentEntity) engine.get("flowOfDocument");

            if (documentEntity == null) throw new RuntimeException("Ссылка на созданную доверенность - null");
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return documentEntity;
    }

    /**
     * Создать доверенность под текущим пользователем
     * @param roleKey
     * @param expiredDate
     * @param radomAccountId
     * @param delegateId
     */
    @Override
    public void createLetterOfAuthority(String roleKey, Date expiredDate, Long radomAccountId, Long delegateId, Map<String, String> attributes) {
        createLetterOfAuthority(roleKey, expiredDate, radomAccountId, delegateId, attributes, null);
    }

    /**
     * Создать доверенность под пользователем с ИД ownerId
     * @param roleKey
     * @param expiredDate
     * @param radomAccountId
     * @param delegateId
     * @param ownerId
     */
    @Override
    public void createLetterOfAuthority(String roleKey, Date expiredDate, Long radomAccountId, Long delegateId, Map<String, String> attributes, Long ownerId) {
        LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntity = letterOfAuthorityRoleRepository.getByKey(roleKey);
        RadomAccount scopeObject = null;
        UserEntity delegate = sharerDao.getById(delegateId);
        switch (letterOfAuthorityRoleEntity.getScopeType()) {
            case Discriminators.SHARER: { // Для пользователя возращаем текущего пользователя
                scopeObject = sharerDao.getById(radomAccountId);
                break;
            }
            case Discriminators.COMMUNITY: {
                scopeObject = communityDao.getById(radomAccountId);
                // Проверяем, кого добавляют в доверенность
                List<RadomAccount> radomAccounts = Collections.singletonList(scopeObject);
                filterCommunities(radomAccounts, letterOfAuthorityRoleEntity);
                if (radomAccounts == null || radomAccounts.size() == 0) {
                    throw new RuntimeException("У вас нет прав создавать доверенность с выбранной ролью доверенности для данной организации.");
                }
                CommunityMemberEntity member = communityMemberDao.get((CommunityEntity) scopeObject, delegate.getId());
                if (member == null) {
                    throw new RuntimeException("Выбранный делегат не является участником выбранного объединения.");
                }
                break;
            }
        }
        UserEntity owner;
        if (ownerId != null) {
            owner = sharerDao.getById(ownerId);
        } else {
            owner = sharerDao.getById(SecurityUtils.getUser().getId());
        }
        DocumentEntity documentEntity = createDocument(letterOfAuthorityRoleEntity, expiredDate, scopeObject, delegate, owner);
        LetterOfAuthorityEntity letterOfAuthorityEntity = new LetterOfAuthorityEntity();
        letterOfAuthorityEntity.setDelegate(delegate);
        letterOfAuthorityEntity.setDocument(documentEntity);
        letterOfAuthorityEntity.setLetterOfAuthorityRole(letterOfAuthorityRoleEntity);
        letterOfAuthorityEntity.setOwner(owner);
        letterOfAuthorityEntity.setScope(scopeObject);
        letterOfAuthorityRepository.save(letterOfAuthorityEntity);

        for (String name : attributes.keySet()) {
            String value = attributes.get(name);
            LetterOfAuthorityAttributeEntity attributeEntity = new LetterOfAuthorityAttributeEntity();
            attributeEntity.setName(name);
            attributeEntity.setValue(value);
            attributeEntity.setLetterOfAuthority(letterOfAuthorityEntity);
            letterOfAuthorityAttributeRepository.save(attributeEntity);
        }
    }

    /**
     * Обновить доверенность
     * @param letterOfAuthorityDto
     */
    @Override
    public void updateLetterOfAuthority(LetterOfAuthorityDto letterOfAuthorityDto) {
        UserEntity currentUser = sharerDao.getById(SecurityUtils.getUser().getId());
        LetterOfAuthorityEntity letterOfAuthority = letterOfAuthorityRepository.getOne(letterOfAuthorityDto.getId());
        if (!letterOfAuthority.getOwner().equals(currentUser)) {
            throw new RuntimeException("У Вас нет прав изменять данную доверенность.");
        }
        if (letterOfAuthorityDto.getExpiredDate() != null) { // Нужно создать новый документ
            Date expiredDate = DateUtils.parseDate(letterOfAuthorityDto.getExpiredDate(), new Date());
            DocumentEntity document = createDocument(letterOfAuthority.getLetterOfAuthorityRole(), expiredDate, letterOfAuthority.getScope(), letterOfAuthority.getDelegate(), currentUser);
            letterOfAuthority.setDocument(document);

            // Удаляем в сохранённых настройках участников инфу об оповещениях по доверенности
            List<SharerSetting> sharerSettingsForDeleted = sharerSettingDao.findByKeyPreffix(SEND_NOTIFY_PREFFIX_SHARER_SETTINGS_KEY + letterOfAuthority.getId(), letterOfAuthority.getOwner());
            sharerSettingsForDeleted.addAll(sharerSettingDao.findByKeyPreffix(SEND_NOTIFY_PREFFIX_SHARER_SETTINGS_KEY + letterOfAuthority.getId(), letterOfAuthority.getDelegate()));

            for (SharerSetting sharerSetting : sharerSettingsForDeleted) {
                sharerSettingDao.delete(sharerSetting);
            }
        }
        documentService.setActiveDocument(letterOfAuthority.getDocument().getId(), letterOfAuthorityDto.isActive());
        letterOfAuthorityRepository.save(letterOfAuthority);
    }

    /**
     * Удалить доверенность
     * @param letterOfAuthorityId
     */
    @Override
    public void deleteLetterOfAuthority(Long letterOfAuthorityId) {
        LetterOfAuthorityEntity letterOfAuthority = letterOfAuthorityRepository.getOne(letterOfAuthorityId);
        DocumentEntity document = letterOfAuthority.getDocument();
        documentService.setActiveDocument(document.getId(), false); // Установить документ доверенности как неактивный

        // Удаляем атрибуты
        for (LetterOfAuthorityAttributeEntity attributeEntity : letterOfAuthority.getAttributes()) {
            letterOfAuthorityAttributeRepository.delete(attributeEntity);
        }
        letterOfAuthorityRepository.delete(letterOfAuthority);
    }

    @Override
    public LetterOfAuthorityEntity getLetterOfAuthority(Long id){
        return letterOfAuthorityRepository.getOne(id);
    }

    /**
     * Список доверенностей, выданных текущим пользователем
     * @param searchString
     * @return
     */
    @Override
    public List<LetterOfAuthorityEntity> getOwnerLettersOfAuthority(String searchString, int page) {
        Pageable pageable = new PageRequest(page, COUNT_ELEMENTS_IN_GRID_PAGE);
        return letterOfAuthorityRepository.findByOwnerIdAndDelegateSearchStringLikeIgnoreCase(SecurityUtils.getUser().getId(), "%" + searchString + "%", pageable);
    }

    /**
     * Список доверенностей, выданных текущему пользователю
     * @param searchString
     * @return
     */
    @Override
    public List<LetterOfAuthorityEntity> getMyLettersOfAuthority(String searchString, int page){
        Pageable pageable = new PageRequest(page, COUNT_ELEMENTS_IN_GRID_PAGE);
        return letterOfAuthorityRepository.findByDelegateIdAndOwnerSearchStringLikeIgnoreCase(SecurityUtils.getUser().getId(), "%" + searchString + "%", pageable);
    }

    /**
     * Проверка доверенности для выполнения действий по ней
     * @param roleKey ключ роли
     * @return true - если есть валидная доверенность
     */
    @Override
    public boolean checkLetterOfAuthority(String roleKey){
        return checkLetterOfAuthority(roleKey, SecurityUtils.getUser(), null, new HashMap<String, String>());
    }

    /**
     * Проверка доверенности для выполнения действий по ней
     * @param roleKey код роли доверенности
     * @param delegate делегат у которого проверяется доверенность
     * @return true - если есть валидная доверенность
     */
    @Override
    public boolean checkLetterOfAuthority(String roleKey, UserEntity delegate) {
        return checkLetterOfAuthority(roleKey, delegate, null, new HashMap<String, String>());
    }

    /**
     * Проверка доверенности для выполнения действий по ней
     * @param roleKey код роли доверенности
     * @param delegate делегат у которого проверяется доверенность
     * @param scope объект в рамках которого нужно проверять доверенность
     * @return true - если есть валидная доверенность
     */
    @Override
    public boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope) {
        return checkLetterOfAuthority(roleKey, delegate, scope, new HashMap<String, String>());
    }

    /**
     * Проверка доверенности для выполнения действий по ней
     * @param roleKey код роли доверенности
     * @param delegate делегат у которого проверяется доверенность
     * @param scope объект в рамках которого нужно проверять доверенность
     * @param attributes список атрибутов
     * @return true - если есть валидная доверенность
     */
    @Override
    public boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope, Map<String, String> attributes) {
        boolean result = false;
        Date currentDate = new Date();
        List<LetterOfAuthorityEntity> letterOfAuthorityEntities = letterOfAuthorityRepository.findByDelegateIdAndLetterOfAuthorityRoleKey(delegate.getId(), roleKey);
        if (letterOfAuthorityEntities != null && letterOfAuthorityEntities.size() > 0) {
            for (LetterOfAuthorityEntity letterOfAuthorityEntity : letterOfAuthorityEntities) {
                if (letterOfAuthorityEntity.getDocument() != null &&
                        letterOfAuthorityEntity.getDocument().getExpiredDate() != null &&
                        currentDate.getTime() < letterOfAuthorityEntity.getDocument().getExpiredDate().getTime() &&
                        letterOfAuthorityEntity.getDocument().getActive()) {
                    // Проверяем подписан ли документ
                    boolean isSignedDocument = true;
                    List<DocumentParticipantEntity> participants = letterOfAuthorityEntity.getDocument().getParticipants();
                    if (participants != null && participants.size() > 0) {
                        for (DocumentParticipantEntity participant : participants) {
                            if (participant.getIsNeedSignDocument() && !participant.getIsSigned()) {
                                isSignedDocument = false;
                                break;
                            }
                        }
                    }
                    // Проверяем объект в рамках которого создана доверенность
                    boolean scopeValid = true;
                    if (scope != null) {
                        if (!letterOfAuthorityEntity.getScope().getId().equals(scope.getId()) || !letterOfAuthorityEntity.getScope().getObjectType().equals(scope.getObjectType())) {
                            scopeValid = false;
                        }
                    }
                    // Проверка наличия атрибутов
                    boolean attributesFinded = true;
                    if (attributes != null) {
                        Set<LetterOfAuthorityAttributeEntity> attributeEntities = letterOfAuthorityEntity.getAttributes();
                        for(String attrName : attributes.keySet()) {
                            boolean attrFinded = false;
                            String attrValue = attributes.get(attrName);
                            for (LetterOfAuthorityAttributeEntity attributeEntity : attributeEntities) {
                                if (attributeEntity.getName().equals(attrName) && attributeEntity.getValue().equals(attrValue)) {
                                    attrFinded = true;
                                    break;
                                }
                            }
                            if (!attrFinded) {
                                attributesFinded = false;
                                break;
                            }
                        }
                    }


                    if (isSignedDocument && scopeValid && attributesFinded) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Проверка доверенности для выполнения действий по ней
     * @param roleKey код роли доверенности
     * @param delegate делегат у которого проверяется доверенность
     * @param scope объект в рамках которого нужно проверять доверенность
     * @param nonExistentAttributes список атрибутов которых не должно быть у доверенности
     * @return true - если есть валидная доверенность
     */
    @Override
    public boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope, List<String> nonExistentAttributes) {
        boolean result = false;
        Date currentDate = new Date();
        List<LetterOfAuthorityEntity> letterOfAuthorityEntities = letterOfAuthorityRepository.findByDelegateIdAndLetterOfAuthorityRoleKey(delegate.getId(), roleKey);
        if (letterOfAuthorityEntities != null && letterOfAuthorityEntities.size() > 0) {
            for (LetterOfAuthorityEntity letterOfAuthorityEntity : letterOfAuthorityEntities) {
                if (letterOfAuthorityEntity.getDocument() != null &&
                        letterOfAuthorityEntity.getDocument().getExpiredDate() != null &&
                        currentDate.getTime() < letterOfAuthorityEntity.getDocument().getExpiredDate().getTime() &&
                        letterOfAuthorityEntity.getDocument().getActive()) {
                    // Проверяем подписан ли документ
                    boolean isSignedDocument = true;
                    List<DocumentParticipantEntity> participants = letterOfAuthorityEntity.getDocument().getParticipants();
                    if (participants != null && participants.size() > 0) {
                        for (DocumentParticipantEntity participant : participants) {
                            if (participant.getIsNeedSignDocument() && !participant.getIsSigned()) {
                                isSignedDocument = false;
                                break;
                            }
                        }
                    }
                    // Проверяем объект в рамках которого создана доверенность
                    boolean scopeValid = true;
                    if (scope != null) {
                        if (!letterOfAuthorityEntity.getScope().getId().equals(scope.getId()) || !letterOfAuthorityEntity.getScope().getObjectType().equals(scope.getObjectType())) {
                            scopeValid = false;
                        }
                    }

                    // Проверка наличия атрибутов
                    boolean attributesFound = true;

                    if (nonExistentAttributes != null) {
                        Set<LetterOfAuthorityAttributeEntity> attributeEntities = letterOfAuthorityEntity.getAttributes();
                        for(String attributeName : nonExistentAttributes) {
                            boolean attributeFound = false;
                            for (LetterOfAuthorityAttributeEntity attribute : attributeEntities) {
                                if (attribute.getName().equals(attributeName)) {
                                    attributeFound = true;
                                    break;
                                }
                            }
                            if (!attributeFound) {
                                attributesFound = false;
                                break;
                            }
                        }
                    }

                    if (isSignedDocument && scopeValid && !attributesFound) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public boolean checkLetterOfAuthority(String roleKey, User delegate) {
        return checkLetterOfAuthority(roleKey, sharerDao.getById(delegate.getId()));
    }

    public boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope) {
        return checkLetterOfAuthority(roleKey, sharerDao.getById(delegate.getId()), getFromLetterOfAuthorityScope(scope));
    }

    public boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope, Map<String, String> attributes) {
        return checkLetterOfAuthority(roleKey, sharerDao.getById(delegate.getId()), getFromLetterOfAuthorityScope(scope), attributes);
    }

    public boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope, List<String> nonExistentAttributes) {
        return checkLetterOfAuthority(roleKey, sharerDao.getById(delegate.getId()), getFromLetterOfAuthorityScope(scope), nonExistentAttributes);
    }

    private RadomAccount getFromLetterOfAuthorityScope(LetterOfAuthorityScope scope) {
        RadomAccount result = null;
        if (scope instanceof Community) {
            result = communityRepository.findOne(scope.getId());
        } else if (scope instanceof User) {
            result = sharerDao.getById(scope.getId());
        }
        ExceptionUtils.check(result == null, "Не определён объект в раках которого создана доверенность");
        return result;
    }

    /**
     * Метод проверки доверенностей.
     * Перед тем как доверенность становится не активной из за того, что срок действия закончился
     * необходимо уведомить участников доверенности.
     */
    @Override
    public void checkLetterOfAuthorities() {
        String daysBeforeExpiredDate = settingsManager.getSystemSetting(NOTIFY_EXPIRED_DATE_LETTERS_OF_AUTHORITIES_SETTINGS_KEY, NOTIFY_EXPIRED_DATE_LETTERS_OF_AUTHORITIES_DEFAULT_VALUE);
        String[] daysStr = daysBeforeExpiredDate.split(",");
        List<Integer> daysBefore = new ArrayList<>();
        for (String dayStr : daysStr) {
            daysBefore.add(VarUtils.getInt(dayStr, -1));
        }
        Date currentDate = new Date();
        long millisecondsInDay = 1000*60*60*24;
        List<LetterOfAuthorityEntity> letterOfAuthorityEntities = letterOfAuthorityRepository.findAll();
        for (LetterOfAuthorityEntity letterOfAuthorityEntity : letterOfAuthorityEntities) {
            if (letterOfAuthorityEntity.getDocument() != null && letterOfAuthorityEntity.getDocument().getExpiredDate() != null) {
                Date expiredDate = letterOfAuthorityEntity.getDocument().getExpiredDate();
                long leftMilliseconds = expiredDate.getTime() - currentDate.getTime();
                long countDays = leftMilliseconds / millisecondsInDay;
                boolean foundDay = false;
                for (int dayBefore : daysBefore){
                    if (countDays == dayBefore) {
                        foundDay = true;
                        break;
                    }
                }
                if (foundDay) {
                    String sendedNotify = sharerSettingDao.get(letterOfAuthorityEntity.getOwner().getId(), getSendNotifySharerSettingsKey(countDays, letterOfAuthorityEntity.getId()), null);
                    if (sendedNotify == null) {
                        // Отправить оповещение
                        blagosferaEventPublisher.publishEvent(new LetterOfAuthorityEvent(this, letterOfAuthorityEntity, LetterOfAuthorityEventType.NOTIFY_OWNER));
                        sharerSettingDao.set(letterOfAuthorityEntity.getOwner(), getSendNotifySharerSettingsKey(countDays, letterOfAuthorityEntity.getId()), "true");
                    }
                    sendedNotify = sharerSettingDao.get(letterOfAuthorityEntity.getDelegate().getId(), getSendNotifySharerSettingsKey(countDays, letterOfAuthorityEntity.getId()), null);
                    if (sendedNotify == null) {
                        // Отправить оповещение
                        blagosferaEventPublisher.publishEvent(new LetterOfAuthorityEvent(this, letterOfAuthorityEntity, LetterOfAuthorityEventType.NOTIFY_DELEGATE));
                        sharerSettingDao.set(letterOfAuthorityEntity.getDelegate(), getSendNotifySharerSettingsKey(countDays, letterOfAuthorityEntity.getId()), "true");
                    }
                }
            }
        }
    }

    /**
     *
     * @param letterOfAuthorityId
     * @param name
     * @param page
     * @return
     */
    public List<LetterOfAuthorityAttributeEntity> getAttributes(Long letterOfAuthorityId, String name, int page) {
        // page c 0
        Pageable pageable = new PageRequest(page, COUNT_ELEMENTS_IN_GRID_PAGE);
        return letterOfAuthorityAttributeRepository.findByLetterOfAuthority_IdAndNameLikeIgnoreCase(letterOfAuthorityId, "%" + name + "%", pageable);
    }

    /**
     * Получить общее количество атрибутов по доверенности
     * @param letterOfAuthorityId
     * @param name
     * @return
     */
    public int getAttributesCount(Long letterOfAuthorityId, String name) {
        return letterOfAuthorityAttributeRepository.countByLetterOfAuthorityIdAndNameLikeIgnoreCase(letterOfAuthorityId, "%" + name + "%");
    }

    @Override
    public void saveAttribute(Long letterOfAuthorityId, String name, String value) {
        LetterOfAuthorityEntity letterOfAuthority = letterOfAuthorityRepository.getOne(letterOfAuthorityId);
        if (letterOfAuthority == null) {
            throw new RuntimeException("Доверенность с ИД " + letterOfAuthorityId + " не найдена!");
        }
        if (!letterOfAuthority.getOwner().getId().equals(SecurityUtils.getUser().getId())) {
            throw new RuntimeException("Доверенность Вам не принадлежит!");
        }

        LetterOfAuthorityAttributeEntity attributeEntity = new LetterOfAuthorityAttributeEntity();
        attributeEntity.setName(name);
        attributeEntity.setValue(value);
        attributeEntity.setLetterOfAuthority(letterOfAuthority);
        letterOfAuthorityAttributeRepository.save(attributeEntity);
    }

    @Override
    public void updateAttribute(Long id, String name, String value) {
        LetterOfAuthorityAttributeEntity attributeEntity = letterOfAuthorityAttributeRepository.getOne(id);
        if (attributeEntity == null) {
            throw new RuntimeException("Аттрибут доверенности с ИД " + id + " не найден!");
        }
        if (attributeEntity.getLetterOfAuthority() == null) {
            throw new RuntimeException("У атрибута не найдена доверенность!");
        }
        if (!attributeEntity.getLetterOfAuthority().getOwner().getId().equals(SecurityUtils.getUser().getId())) {
            throw new RuntimeException("Доверенность аттрибута Вам не принадлежит!");
        }
        attributeEntity.setName(name);
        attributeEntity.setValue(value);
        letterOfAuthorityAttributeRepository.save(attributeEntity);
    }

    @Override
    public void deleteAttribute(Long id) {
        LetterOfAuthorityAttributeEntity attributeEntity = letterOfAuthorityAttributeRepository.getOne(id);
        if (attributeEntity == null) {
            throw new RuntimeException("Аттрибут доверенности с ИД " + id + " не найден!");
        }
        if (attributeEntity.getLetterOfAuthority() == null) {
            throw new RuntimeException("У атрибута не найдена доверенность!");
        }
        if (!attributeEntity.getLetterOfAuthority().getOwner().getId().equals(SecurityUtils.getUser().getId())) {
            throw new RuntimeException("Доверенность аттрибута Вам не принадлежит!");
        }
        letterOfAuthorityAttributeRepository.delete(attributeEntity);
    }

    private static final String getSendNotifySharerSettingsKey(long countDays, long letterOfAuthorityId) {
        return SEND_NOTIFY_PREFFIX_SHARER_SETTINGS_KEY + letterOfAuthorityId + "." + countDays;
    }

    /**
     * Обернуть список объектов RadomAccount в Dto
     * @param radomAccounts
     * @return
     */
    public static List<RadomAccountDto> toDto(List<RadomAccount> radomAccounts) {
        List<RadomAccountDto> result = new ArrayList<>();
        for (RadomAccount radomAccount : radomAccounts) {
            result.add(toDto(radomAccount));
        }
        return result;
    }

    public static RadomAccountDto toDto(RadomAccount radomAccount) {
        return new RadomAccountDto(
                radomAccount.getId(),
                radomAccount.getObjectType(),
                radomAccount.getName(),
                radomAccount.getAvatar(),
                radomAccount.getLink(),
                radomAccount.getIkp()
        );
    }
}
