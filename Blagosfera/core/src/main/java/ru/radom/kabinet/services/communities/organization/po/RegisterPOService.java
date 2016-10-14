package ru.radom.kabinet.services.communities.organization.po;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityData;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.voting.BPMBatchVotingService;
import ru.radom.kabinet.services.script.ScriptEngineService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.web.communities.organization.po.dto.CreatePOPageDataDto;
import ru.radom.kabinet.web.communities.organization.po.dto.RegisterPODto;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Сервис для регистрации Юр лица - ПО
 * Created by vgusev on 12.12.2015.
 */
@Transactional
@Service
public class RegisterPOService {

    @Autowired
    private OkvedDao okvedDao;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private ScriptEngineService scriptEngineService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private BPMBatchVotingService bpmBatchVotingService;

    /**
     * Ключ настройки - сигнал для запуска процесса регистрации ПО
     */
    private static final String REGISTER_PO_SIGNAL_SETTINGS_KEY = "organization.register.po.signal";

    /**
     * Значение сигнала по умолчанию
     */
    private static final String REGISTER_PO_SIGNAL_DEFAULT = "registerPO";

    /**
     * Ключ настройки - скрипт получения переменных для таска создания ПО
     */
    private static final String REGISTER_PO_SCRIPT_VARIABLES_SETTINGS_KEY = "organization.register.po.script.variables";

    /**
     * Значение скрипта по умолчанию
     */
    private static final String REGISTER_PO_SCRIPT_VARIABLES_DEFAULT = "";

    /**
     * Настройка - скрипт генерации устава ПО
     */
    private static final String GENERATE_PO_REGULATIONS_SCRIPT_SYSTEM_SETTINGS_KEY = "generate.po.regulations.script";

    /**
     * Настройка - минимальное количество учредителей для регистрации ПО
     */
    private static final String REGISTER_PO_FOUNDERS_MIN_COUNT_SYS_ATTR_NAME = "register.po.founders.min.count";

    private static final String REGISTER_PO_SOVIET_MIN_COUNT_SYS_ATTR_NAME = "register.po.soviet.min.count";

    private static final String REGISTER_PO_AUDIT_MIN_COUNT_SYS_ATTR_NAME = "register.po.audit.min.count";

    /**
     * Настройка - скрипт генерации данных для генерации устава ПО
     */
    private static final String GENERATE_PO_REGULATIONS_SCRIPT_DATA_SYSTEM_SETTINGS_KEY = "generate.po.regulations.script.data";

    /**
     * Название переменной с данными для генерации устава ПО
     */
    private static final String REGULATIONS_MAP_VAR_NAME = "userFieldsMap";

    /**
     * Значение по умолчанию - минимальное количество учредителей для регистрации ПО
     */
    private static final int REGISTER_PO_FOUNDERS_MIN_COUNT_DEFAULT = 2;

    private static final int REGISTER_PO_SOVIET_MIN_COUNT_DEFAULT = 2;

    private static final int REGISTER_PO_AUDIT_MIN_COUNT_DEFAULT = 2;

    /**
     * Мнемокод должности директора в ПО
     */
    //private static final String COMMUNITY_PO_DIRECTOR_POSITION_MNEMO_CODE = "president_of_soviet";

    /**
     * Возможные значения поля - на какой основе работает председатель совета
     */
    private static final List<String> PRESIDENT_OF_SOVIET_KIND_WORKING_POSSIBLE_VALUES = Arrays.asList("free", "charge");

    /**
     * Возможные значения поля - Правление отсчитывается перед Обществом
     */
    private static final List<String> BOARD_REPORT_FREQUENCY_POSSIBLE_VALUES = Arrays.asList("onePerMonth", "onePerQuarter", "onePerYear");

    /**
     * Возможные значения поля - Кто утверждает положения Потребительского Общества
     */
    private static final List<String> WHO_APPROVE_POSITION_POSSIBLE_VALUES = Arrays.asList(
            "commonBatchVotingPO", "sovietPO", "presidentOfSovietPO", "boardPO", "presidentOfBoardPO");

    /**
     * Возможные значения поля - Кто утверждает дату выплат выбывающему пайщику
     */
    private static final List<String> WHO_APPROVE_DATE_PAY_POSSIBLE_VALUES = Arrays.asList(
            "whoApproveDatePayCommonBatchVotingPO", "whoApproveDatePaySovietPO");

    /**
     * Возможные значения поля - Момент с которого начинает отчитываться время на выплату выбывшему пайщику
     */
    private static final List<String> START_PERIOD_PAY_POSSIBLE_VALUES = Arrays.asList(
            "quarter", "year");

    /**
     * Возможные значения поля - действия в случае смерти пайщика
     */
    private static final List<String> ON_SHARE_DEATH_POSSIBLE_VALUES = Arrays.asList(
            "childMayBeSharer", "payToChild");

    /**
     * Скрипт создания устава ПО по умолчанию
     */
    private static final String DEFAULT_PO_REGULATIONS_SCRIPT =
            "var createDocumentParameters = new ArrayList(); " +
                    "var foundersParameters = new ParticipantCreateDocumentParameter('INDIVIDUAL_LIST', founderIds, 'po_founders'); " +
                    "var userFields = new ArrayList(); " +
                    "var additionalOkvedsStr = ''; " +
                    "var additionalOkvedsStrArr = []; " +
                    "if (additionalOkveds != null && additionalOkveds.length > 0){ " +
                    "   for (var i=0; i<additionalOkveds.length; i++) { " +
                    "       additionalOkvedsStrArr.push(additionalOkveds[i].code + ' ' + additionalOkveds[i].longName); " +
                    "   }" +
                    "   additionalOkvedsStr = additionalOkvedsStrArr.join(', '); " +
                    "} "+
                    "userFields.add(UserFieldValueBuilder.createStringValue('poName', name)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('mainOkved', mainOkved.code + ' ' + mainOkved.longName)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('additionalOkveds', additionalOkvedsStr)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('short_targets', shortTargets)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('full_targets', fullTargets)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('country', address.country)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('zip_code', address.postalCode)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('region', address.region)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('district', address.district)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('city', address.city)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('street', address.street)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('building', address.building)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('office', address.room)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('count_in_soviet', countInSoviet)); " +
                    "userFields.add(UserFieldValueBuilder.createStringValue('count_audit_committee', countAuditCommittee)); " +
                    "createDocumentParameters.add(new CreateDocumentParameter(foundersParameters, userFields)); " +
                    "var flowOfDocumentDTO = flowOfDocumentService.generateDocumentDTO('test_po_regulations', createDocumentParameters);";

    private void check(boolean condition, String message, String errorField) {
        if (condition) throw new RegisterPOException(message, errorField);
    }

    /**
     * Валидация формы для создания запроса на регистрацию ПО
     * @param registerPODto параметры формы
     */
    @Transactional(readOnly = true)
    private RegisterPoResolvedObjects validateFormData(RegisterPODto registerPODto) {
        RegisterPoResolvedObjects resolvedObjects = commonValidateFormData(registerPODto);
        check(StringUtils.isBlank(registerPODto.getOrganizationRegulations()), "Не установлен устав", registerPODto.getErrorBlockPoOrganizationRegulations());
        check(registerPODto.getStartDateBatchVoting() == null, "Не установлена дата начала проведения собрания по регистрации Общества", registerPODto.getErrorBlockPoStartDateBatchVoting());
        check(registerPODto.getRegistrationEndDateBatchVoting() == null, "Не установлена дата окончания регистрации в собрании по регистрации Общества", registerPODto.getErrorBlockPoRegistrationEndDateBatchVoting());
        check(registerPODto.getEndDateBatchVoting() == null, "Не установлена дата окончания собрания по регистрации Общества", registerPODto.getErrorBlockPoEndDateBatchVoting());
        long currentTimeStamp = new Date().getTime();
        check(
                (currentTimeStamp > registerPODto.getRegistrationEndDateBatchVoting().getTime()),
                "Дата окончания регистрации в собрании для регистрации Общества меньше текущей даты.",
                registerPODto.getErrorBlockPoRegistrationEndDateBatchVoting()
        );
        check((currentTimeStamp > registerPODto.getEndDateBatchVoting().getTime()), "Дата окончания собрания для регистрации Общества меньше текущей даты", registerPODto.getErrorBlockPoEndDateBatchVoting());

        check((registerPODto.getStartDateBatchVoting().getTime() > registerPODto.getRegistrationEndDateBatchVoting().getTime()), "Дата окончания регистрации в собрании для регистрации Общества меньше даты начала собрания", registerPODto.getErrorBlockPoRegistrationEndDateBatchVoting());
        check((registerPODto.getStartDateBatchVoting().getTime() > registerPODto.getEndDateBatchVoting().getTime()), "Дата окончания регистрации в собрании для регистрации Общества меньше даты начала собрания", registerPODto.getErrorBlockPoEndDateBatchVoting());
        check(StringUtils.isBlank(registerPODto.getBatchVotingDescription()), "Не установлено описание учредительного собрания", registerPODto.getErrorBlockPoBatchVotingDescription());
        check(registerPODto.getSecretVoting() == null, "Не выбран вид голосования", registerPODto.getErrorBlockPoSecretVoting());


        return resolvedObjects;
    }

    /**
     * Добавить виды деятельности в контейнер
     * @param okvedsContainer контейнер с видами деятельности
     * @param ids ИДы видов деятельности
     * @param errorField поле в котором будетотображена ошибка
     */
    private void addOkveds(List<OkvedDomain> okvedsContainer, List<Long> ids, String errorField) {
        for (Long okvedId : ids) {
            OkvedDomain okved = getOkved(okvedId, errorField);
            check(okved == null, "Не найден вид деятельности с ИД " + okvedId, errorField);
            okvedsContainer.add(okved);
        }
    }

    private OkvedDomain getOkved(Long okvedId, String errorField) {
        OkvedDomain okved = null;
        OkvedEntity okvedEntity = okvedDao.getById(okvedId);
        if (okvedEntity != null) {
            okved = okvedEntity.toDomain();
        }
        check(okved == null, "Не найден вид деятельности с ИД " + okvedId, errorField);
        return okved;
    }

    /**
     * Проверить и добавить участников Совета Общества в контейнер с данными
     * @param participantsInSoviet коллекция куда добавлять найденных участников
     * @param founders коллекция со всеми учредителями
     * @param ids ИДы участников Совета Общества
     * @param errorField поле в котором отображать ошибку
     * @param minCountSoviet
     */
    private void addParticipantsInSoviet(Set<User> participantsInSoviet, Set<User> founders, Set<Long> ids, String errorField, int minCountSoviet) {
        String participantsInSovietError = null;
        if (ids != null && ids.size() > 0) {
            for (Long participantId : ids) {
                User foundParticipant = null;
                for (User founder : founders) {
                    if (founder.getId().equals(participantId)) {
                        foundParticipant = founder;
                        break;
                    }
                }
                if (foundParticipant == null) {
                    participantsInSovietError = "Предлагаемый участник в Совет Общества c ИД " + participantId + " не найден в учредителях";
                    break;
                }
                participantsInSoviet.add(foundParticipant);
            }
        } else {
            participantsInSovietError = "Не переданы участники Совета Общества";
        }
        check(participantsInSovietError != null, participantsInSovietError, errorField);
        check(minCountSoviet > participantsInSoviet.size(), "Минимальное количество членов Совета Потребительского общества: " + minCountSoviet + ".", errorField);
    }

    /**
     * Проверить и добавить участников ревизионной комиссии в контейнер с данными
     * @param participantsInAuditCommittee коллекция куда добавлять найденных участников
     * @param participantsInSoviet коллекция с участниками Совета Общества
     * @param founders коллекция со всеми учредителями
     * @param ids ИДы участников ревизионной комиссии
     * @param errorField поле в котором отображать ошибку
     * @param minCountAudit
     */
    private void addParticipantsAuditCommittee(Set<User> participantsInAuditCommittee, Set<User> participantsInSoviet, Set<User> founders, Set<Long> ids, String errorField, int minCountAudit) {
        String participantsInAuditCommitteeError = null;
        if (ids != null && ids.size() > 0) {
            for (Long participantId : ids) {
                User foundParticipant = null;
                for (User founder : founders) {
                    if (founder.getId().equals(participantId)) {
                        foundParticipant = founder;
                        break;
                    }
                }
                if (foundParticipant == null) {
                    participantsInAuditCommitteeError = "Предлагаемый участник в ревизионную комиссию Общества c ИД " + participantId + " не найден в учредителях";
                    break;
                }
                if (participantsInSoviet.contains(foundParticipant)) {
                    participantsInAuditCommitteeError = "Предлагаемый участник в ревизионную комиссию Общества " + foundParticipant.getFullName() + " предложен в Совет Общества";
                    break;
                }
                participantsInAuditCommittee.add(foundParticipant);
            }
        } else {
            participantsInAuditCommitteeError = "Не переданы участники ревизионной комиссии";
        }
        check(participantsInAuditCommitteeError != null, participantsInAuditCommitteeError, errorField);
        check(minCountAudit > participantsInSoviet.size(), "Минимальное количество членов ревизионной комиссии Потребительского общества: " + minCountAudit + ".", errorField);
    }

    private Integer getIntegerField(String strValue, String errorBlock) {
        Integer value = VarUtils.getInt(strValue, null);
        check(value == null, "Значение поля должно быть числом", errorBlock);
        return value;
    }

    private Long getLongField(String strValue, String errorBlock) {
        Long value = VarUtils.getLong(strValue, null);
        check(value == null, "Значение поля должно быть числом", errorBlock);
        return value;
    }

    @Transactional(readOnly = true)
    private void validateGenerateRegulationsFormData(RegisterPODto registerPODto, RegisterPoResolvedObjects resolvedObjects) {
        //boolean isGeneratedRegulations = BooleanUtils.toBooleanDefaultIfNull(registerPODto.getIsGeneratedRegulations(), true);
        //++
        Integer sovietOfficePeriod = getIntegerField(registerPODto.getSovietOfficePeriod(), registerPODto.getErrorBlockPoSovietOfficePeriod());
        check(
                sovietOfficePeriod < 1,
                "Не установлен срок, на который избираются члены Совета и Председатель Совета",
                registerPODto.getErrorBlockPoSovietOfficePeriod()
        );
        resolvedObjects.setSovietOfficePeriod(sovietOfficePeriod);
        //++
        check(
                !PRESIDENT_OF_SOVIET_KIND_WORKING_POSSIBLE_VALUES.contains(registerPODto.getPresidentOfSovietKindWorking()),
                "Не устанолено на какой основе работает Председатель Совета Общества",
                registerPODto.getErrorBlockPoPresidentOfSovietKindWorking()
        );

        //++
        Integer participantsOfBoardOfficePeriod = getIntegerField(registerPODto.getSovietOfficePeriod(), registerPODto.getErrorBlockPoParticipantsOfBoardOfficePeriod());
        check(
                participantsOfBoardOfficePeriod < 1,
                "Не установлен срок, на который избираются члены и Председатель Правления",
                registerPODto.getErrorBlockPoParticipantsOfBoardOfficePeriod()
        );
        resolvedObjects.setParticipantsOfBoardOfficePeriod(participantsOfBoardOfficePeriod);
        //++
        Integer countDaysPerMeetingOfBoard = getIntegerField(registerPODto.getCountDaysPerMeetingOfBoard(), registerPODto.getErrorBlockPoCountDaysPerMeetingOfBoard());
        check(
                countDaysPerMeetingOfBoard < 1,
                "Не установлено как часто проходит заседание Правления",
                registerPODto.getErrorBlockPoCountDaysPerMeetingOfBoard()
        );
        resolvedObjects.setCountDaysPerMeetingOfBoard(countDaysPerMeetingOfBoard);
        //++
        Integer quorumMeetingOfBoard = getIntegerField(registerPODto.getQuorumMeetingOfBoard(), registerPODto.getErrorBlockPoQuorumMeetingOfBoard());
        check(
                (quorumMeetingOfBoard < 1 || quorumMeetingOfBoard > 100),
                "Значение кворума заседаний членов Правления Общества должно быть от 1 до 100",
                registerPODto.getErrorBlockPoQuorumMeetingOfBoard()
        );
        resolvedObjects.setQuorumMeetingOfBoard(quorumMeetingOfBoard);
        //++
        check(
                !BOARD_REPORT_FREQUENCY_POSSIBLE_VALUES.contains(registerPODto.getBoardReportFrequency()),
                "Не установлено как часто Правление отсчитывается перед Обществом",
                registerPODto.getErrorBlockPoBoardReportFrequency()
        );
        //++
        Integer participantsAuditCommitteeOfficePeriod = getIntegerField(registerPODto.getParticipantsAuditCommitteeOfficePeriod(), registerPODto.getErrorBlockPoParticipantsAuditCommitteeOfficePeriod());
        check(
                participantsAuditCommitteeOfficePeriod < 1,
                "Не установлен срок, на который избираются члены ревизионной комиссии",
                registerPODto.getErrorBlockPoParticipantsAuditCommitteeOfficePeriod()
        );
        resolvedObjects.setParticipantsAuditCommitteeOfficePeriod(participantsAuditCommitteeOfficePeriod);

        //++
        check(
                registerPODto.getHasStamp() == null,
                "Не установлено поле - имеет ли ПО свою печать с полным наименованием Потребительского общества на русском языке",
                registerPODto.getErrorBlockPoHasStamp()
        );
        boolean hasStamp = BooleanUtils.toBoolean(registerPODto.getHasStamp());
        registerPODto.setHasStamp(String.valueOf(hasStamp));
        resolvedObjects.setHasStamp(hasStamp);

        //++
        check(
                !WHO_APPROVE_POSITION_POSSIBLE_VALUES.contains(registerPODto.getWhoApprovePosition()),
                "Не установлено кто утверждает положения Потребительского Общества",
                registerPODto.getErrorBlockPoWhoApprovePosition()
        );

        //++
        Integer countDaysToQuiteFromPo = getIntegerField(registerPODto.getCountDaysToQuiteFromPo(), registerPODto.getErrorBlockPoCountDaysToQuiteFromPo());
        check(
                countDaysToQuiteFromPo < 1,
                "Не установлено количество дней на рассмотрение заявления о выходе пайщика",
                registerPODto.getErrorBlockPoCountDaysToQuiteFromPo()
        );
        resolvedObjects.setCountDaysToQuiteFromPo(countDaysToQuiteFromPo);
        //++
        check(
                !WHO_APPROVE_DATE_PAY_POSSIBLE_VALUES.contains(registerPODto.getWhoApproveDatePay()),
                "Не установлено кто утверждает дату выплат выбывающему пайщику",
                registerPODto.getErrorBlockPoWhoApproveDatePay()
        );
        //++
        Integer countMonthToSharerPay = getIntegerField(registerPODto.getCountMonthToSharerPay(), registerPODto.getErrorBlockPoCountMonthToSharerPay());
        check(
                countMonthToSharerPay < 1,
                "Не установлено количество месяцев на выплату выбывшему пайщику",
                registerPODto.getErrorBlockPoCountMonthToSharerPay()
        );
        resolvedObjects.setCountMonthToSharerPay(countMonthToSharerPay);
        //++
        check(
                !START_PERIOD_PAY_POSSIBLE_VALUES.contains(registerPODto.getStartPeriodPay()),
                "Не установлен момент с которого начинает отчитываться время на выплату выбывшему пайщику",
                registerPODto.getErrorBlockPoStartPeriodPay()
        );
        //++
        check(
                !ON_SHARE_DEATH_POSSIBLE_VALUES.contains(registerPODto.getOnShareDeath()),
                "Не установлено что происходит в случае смерти пайщика",
                registerPODto.getErrorBlockPoOnShareDeath()
        );
        //++
        Long minCreditApproveSovietPO = getLongField(registerPODto.getMinCreditApproveSovietPO(), registerPODto.getErrorBlockPoMinCreditApproveSovietPO());
        check(
                minCreditApproveSovietPO < 1,
                "Не установлена минимальная сумма заемных средств, решение о получении которых принимает Общее собрание пайщиков ПО",
                registerPODto.getErrorBlockPoMinCreditApproveSovietPO()
        );
        resolvedObjects.setMinCreditApproveSovietPO(minCreditApproveSovietPO);
        //++
        Long minContractSumApproveSovietPO = getLongField(registerPODto.getMinContractSumApproveSovietPO(), registerPODto.getErrorBlockPoMinContractSumApproveSovietPO());
        check(
                minContractSumApproveSovietPO < 1,
                "Не установлена минимальная сумма (в МРОТ) по сделкам, решение об утверждении которых принимает Общее собрание пайщиков ПО",
                registerPODto.getErrorBlockPoMinContractSumApproveSovietPO()
        );
        resolvedObjects.setMinContractSumApproveSovietPO(minContractSumApproveSovietPO);
    }

    /**
     * Валидация формы достаточной для генерации Устава ПО
     * @param registerPODto параметры формы
     */
    @Transactional(readOnly = true)
    private RegisterPoResolvedObjects commonValidateFormData(RegisterPODto registerPODto) {
        RegisterPoResolvedObjects resolvedObjects = new RegisterPoResolvedObjects();
        check(StringUtils.isBlank(registerPODto.getName()), "Не установлено полное название на русском языке", registerPODto.getErrorBlockPoName());
        check(StringUtils.isBlank(registerPODto.getNameShort()), "Не установлено короткое название на русском языке", registerPODto.getErrorBlockPoNameShort());
        check(StringUtils.isBlank(registerPODto.getEngName()), "Не установлено полное название на английском языке", registerPODto.getErrorBlockPoEngName());
        check(StringUtils.isBlank(registerPODto.getEngNameShort()), "Не установлено короткое название на английском языке", registerPODto.getErrorBlockPoEngNameShort());

        check(registerPODto.getAssociationFormCode() == null, "Не выбрана форма объединения", registerPODto.getErrorBlockPoAssociationFormBlock());
        ListEditorItem associationFormListItem = null;
        RameraListEditorItem rameraAssociationFormListItem = rameraListEditorItemDAO.getByCode(registerPODto.getAssociationFormCode());
        if (rameraAssociationFormListItem != null) {
            associationFormListItem = rameraAssociationFormListItem.toDomain();
        }

        check(associationFormListItem == null, "Не найдена форма объединения с кодом " + registerPODto.getAssociationFormCode(), registerPODto.getErrorBlockPoAssociationFormBlock());
        resolvedObjects.setAssociationFormListItem(associationFormListItem);

        check(registerPODto.getFounderIds() == null || registerPODto.getFounderIds().size() == 0, "Не выбраны учредители", registerPODto.getErrorBlockPoFounders());

        int minCountFounders = getMinCountFounders();
        int minCountSoviet = getMinCountSoviet();
        int minCountAudit = getMinCountAudit();

        check(registerPODto.getFounderIds().size() < minCountFounders, "Для регистрации ПО необходимо, чтобы количество учредителей физ. лиц не моложе 16 лет было не меньше " + minCountFounders, registerPODto.getErrorBlockPoFounders());
        for (Long founderId : registerPODto.getFounderIds()) {
            User founder = userDataService.getByIdMinData(founderId);
            check(founder == null, "Не найден учредитель с ИД " + founderId, registerPODto.getErrorBlockPoFounders());
            check(!founder.isVerified(), "Учредитель " + founder.getFullName() + " не сертифицирован", registerPODto.getErrorBlockPoFounders());
            resolvedObjects.getFounders().add(founder);
        }
        check(StringUtils.isBlank(registerPODto.getShortTargets()), "Не установлено краткое описание целей и задачи", registerPODto.getErrorBlockPoShortTargets());
        check(StringUtils.isBlank(registerPODto.getFullTargets()), "Не установлено полное описание целей и задачи", registerPODto.getErrorBlockPoFullTargets());
        check(registerPODto.getMainOkvedId() == null, "Не установлен основной виды деятельности", registerPODto.getErrorBlockPoMainOkved());
        resolvedObjects.setMainOkved(getOkved(registerPODto.getMainOkvedId(), registerPODto.getErrorBlockPoMainOkved()));
        if (registerPODto.getAdditionalOkvedIds() != null && registerPODto.getAdditionalOkvedIds().size() > 0) {
            addOkveds(resolvedObjects.getAdditionalOkveds(), registerPODto.getAdditionalOkvedIds(), registerPODto.getErrorBlockPoAdditionalOkveds());
        }
        check(registerPODto.getActivityScopeIds() == null || registerPODto.getActivityScopeIds().isEmpty(), "Не установлена сфера деятельности", registerPODto.getErrorBlockActivityScopes());
        List<RameraListEditorItem> activityScopeEditorItems = rameraListEditorItemDAO.getByIds(registerPODto.getActivityScopeIds());
        check(activityScopeEditorItems == null, "Не найдена сфера деятельности с ИД " + registerPODto.getActivityScopeIds().get(0), registerPODto.getErrorBlockActivityScopes());

        check(registerPODto.getAddress() == null, "Не установлен адресный блок", registerPODto.getErrorBlockPoAddress());
        check(StringUtils.isBlank(registerPODto.getAddress().getCountry()), "Не установлена страна в адресе", registerPODto.getErrorBlockPoCountry());
        check(StringUtils.isBlank(registerPODto.getAddress().getRegion()), "Не установлен регион", registerPODto.getErrorBlockPoRegion());
        //check(StringUtils.isBlank(registerPODto.getAddress().getCity()), "Не установлен населённый пункт", registerPODto.getErrorBlockPoCity());
        check(StringUtils.isBlank(registerPODto.getAddress().getStreet()), "Не установлена улица", registerPODto.getErrorBlockPoStreet());
        check(StringUtils.isBlank(registerPODto.getAddress().getBuilding()), "Не установлен дом", registerPODto.getErrorBlockPoBuilding());
        check(StringUtils.isBlank(registerPODto.getAddress().getRoom()), "Не установлен офис", registerPODto.getErrorBlockPoRoom());

        Long officeOwnerShipTypeId = registerPODto.getOfficeOwnerShipType() == null ? -1l : registerPODto.getOfficeOwnerShipType();
        RameraListEditorItem rameraOfficeOwnerShipListEditorItem = rameraListEditorItemDAO.getById(officeOwnerShipTypeId);
        ListEditorItem officeOwnerShipListEditorItem = null;
        if (rameraOfficeOwnerShipListEditorItem != null) {
            officeOwnerShipListEditorItem = rameraOfficeOwnerShipListEditorItem.toDomain();
        }

        check(officeOwnerShipListEditorItem == null, "Не установлен тип владения офисом", registerPODto.getErrorBlockPoOfficeOwnerShipType());
        resolvedObjects.setOfficeOwnerShipListEditorItem(officeOwnerShipListEditorItem);

        // Если город не установлен - значит город фередарльного уровня. Берём его из региона
        if (StringUtils.isBlank(registerPODto.getAddress().getCity())) {
            registerPODto.getAddress().setCity(registerPODto.getAddress().getRegion());
        }

        if ("acceptParticipants".equals(registerPODto.getParticipantsSourceType())) {
            // Добавляем участников Совета в контейнер
            addParticipantsInSoviet(
                    resolvedObjects.getParticipantsInSoviet(),
                    resolvedObjects.getFounders(),
                    registerPODto.getParticipantsInSovietIds(),
                    registerPODto.getErrorBlockPoParticipantsInSoviet(), minCountSoviet);

            // Добавляем участников ревизионной комиссии в контейнер
            addParticipantsAuditCommittee(
                    resolvedObjects.getParticipantsInAuditCommittee(),
                    resolvedObjects.getParticipantsInSoviet(),
                    resolvedObjects.getFounders(),
                    registerPODto.getParticipantsInAuditCommitteeIds(),
                    registerPODto.getErrorBlockPoParticipantsInAuditCommittee(), minCountAudit);
        } else {
            check(
                    registerPODto.getCountParticipantsInSoviet() == null ||
                            registerPODto.getCountParticipantsInSoviet() == 0,
                    "Не установлено количество членов Совета Общества",
                    registerPODto.getErrorBlockPoCountParticipantsInSoviet()
            );

            check(
                    registerPODto.getCountParticipantsInAuditCommittee() == null ||
                            registerPODto.getCountParticipantsInSoviet() == 0,
                    "Не установлено количество членов ревизионной комиссии Общества",
                    registerPODto.getErrorBlockPoCountParticipantsInAuditCommittee()
            );

            check(
                    minCountAudit > registerPODto.getCountParticipantsInSoviet(),
                    "Минимальное количество членов ревизионной комиссии Потребительского общества: " + minCountAudit + ".",
                    registerPODto.getErrorBlockPoCountParticipantsInAuditCommittee()
            );
            check(
                    minCountSoviet > registerPODto.getCountParticipantsInSoviet(),
                    "Минимальное количество членов Совета Потребительского общества: " + minCountAudit + ".",
                    registerPODto.getErrorBlockPoCountParticipantsInSoviet()
            );

            check(registerPODto.getFounderIds().size() <
                            (registerPODto.getCountParticipantsInSoviet() + registerPODto.getCountParticipantsInAuditCommittee()),
                    "Количество членов Общества должно быть больше членов Совета и членов ревизионной комиссии",
                    registerPODto.getErrorBlockPoCountParticipantsInSoviet()
            );
        }

        checkNumberField(registerPODto.getEntranceShareFees(), "Вступительный взнос (Физ. лица)", registerPODto.getErrorBlockPoEntranceShareFees());
        checkNumberField(registerPODto.getMinShareFees(), "Минимальный паевой взнос (Физ. лица)", registerPODto.getErrorBlockPoMinShareFees());
        checkNumberField(registerPODto.getMembershipFees(), "Членский взнос (Физ. лица)", registerPODto.getErrorBlockPoMembershipFees());
        checkNumberField(registerPODto.getCommunityEntranceShareFees(), "Вступительный взнос (Юр. лица)", registerPODto.getErrorBlockPoCommunityEntranceShareFees());
        checkNumberField(registerPODto.getCommunityMinShareFees(), "Минимальный паевой взнос (Юр. лица)", registerPODto.getErrorBlockPoCommunityMinShareFees());
        checkNumberField(registerPODto.getCommunityMembershipFees(), "Членский взнос (Юр. лица)", registerPODto.getErrorBlockPoCommunityMembershipFees());

        check(registerPODto.getDirectorPositionId() == null, "Не выбрана должность руководтсва организации", registerPODto.getErrorBlockPoDirectorPosition());

        return resolvedObjects;
    }

    /**
     * Проверка числового поля
     * @param number число в виде строки
     * @param fieldName наименование поля
     * @param errorBlockField блок ошибки поля
     */
    private void checkNumberField(String number, String fieldName, String errorBlockField) {
        check(number == null, "Не установлено поле \"" + fieldName + "\"", errorBlockField);
        BigDecimal bd = null;
        try {
            number = number.replace(",", ".");
            bd = new BigDecimal(number);
            bd.setScale(2, BigDecimal.ROUND_DOWN);
        } catch (Exception e) {
            check(number == null, "Значение поля \"" + fieldName + "\" не является числом", errorBlockField);
        }
        check(bd == null, "Не установлено поле \"" + fieldName + "\"", errorBlockField);
        if (bd.compareTo(BigDecimal.ZERO) == 0) {
            check(number == null, "Значение поля \"" + fieldName + "\" не может быть нулевым", errorBlockField);
        }
        if (bd.compareTo(BigDecimal.ZERO) == -1) {
            check(number == null, "Значение поля \"" + fieldName + "\" не может быть отрицательным", errorBlockField);
        }
    }

    /**
     * Создать список участников для отображения в описании голосования.
     * Список в виде столбика с ФИО
     * @param participants коллекция с загруженными данными по параметрам с формы
     * @return строка со списком участников
     */
    private String createParticipantsStringList(Set<User> participants) {
        List<String> resultList = new ArrayList<>();
        for(User founder : participants){
            resultList.add(founder.getFullName());
        }
        return StringUtils.join(resultList, "<br/>");
    }

    /**
     * Запустить процесс регистрации ПО
     * @param registerPODto параметры формы
     */
    public void registerPO(RegisterPODto registerPODto) {
        RegisterPoResolvedObjects resolvedObjects = validateFormData(registerPODto);
        registerPODto.setShortTargets(Jsoup.parse(registerPODto.getShortTargets()).text());
        Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put("serializeService", serializeService);
        scriptVariables.put("registerPODto", registerPODto);
        scriptVariables.put("resolvedObjects", resolvedObjects);

        /*Map<String, Object> registerPODtoMap = serializeService.toPrimitiveObject(registerPODto);
        Map<String, Object> resolvedObjectsMap =  serializeService.toPrimitiveObject(resolvedObjects);
        Map<String, Object> payload = new HashMap<>();
        payload.put("registerData", registerPODtoMap);
        payload.put("resolvedObjectsMap", resolvedObjectsMap);
        payload.put("startDate", DateUtils.formatDate(registerPODto.getStartDateBatchVoting(), DateUtils.Format.DATE_TIME_SHORT));
        payload.put("foundersFullNameList", createParticipantsStringList(resolvedObjects.getFounders()));
        payload.put("participantsInSovietFullNameList", createParticipantsStringList(resolvedObjects.getParticipantsInSoviet()));
        payload.put("participantsInAuditCommitteeFullNameList", createParticipantsStringList(resolvedObjects.getParticipantsInAuditCommittee()));

        payload.put("participantsInSovietIdsByComma", StringUtils.join(registerPODto.getParticipantsInSovietIds(), ","));
        payload.put("participantsInSovietIdsBySemicolon", StringUtils.join(registerPODto.getParticipantsInSovietIds(), ";"));

        payload.put("founderIdsByComma", StringUtils.join(registerPODto.getFounderIds(), ","));
        payload.put("founderIdsBySemicolon", StringUtils.join(registerPODto.getFounderIds(), ";"));
        payload.put("okvedIdsByComma", StringUtils.join(registerPODto.getOkvedIds(), ","));

        payload.put("associationFormId", resolvedObjects.getAssociationFormListItem().getId());
        payload.put("directorPositionId", registerPODto.getDirectorPositionId());
        payload.put("communityType", ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName());
        payload.put("participantsInAuditIdsBySemicolon", StringUtils.join(registerPODto.getParticipantsInAuditCommitteeIds(), ";"));

        Map<Long, String> idToNameFounders = new HashMap<>();
        for (Sharer founder : resolvedObjects.getFounders()) {
            idToNameFounders.put(founder.getId(), founder.getFullName());
        }
        payload.put("idToNameFounders", idToNameFounders);*/

        String scriptGetVariables = settingsManager.getSystemSetting(REGISTER_PO_SCRIPT_VARIABLES_SETTINGS_KEY, REGISTER_PO_SCRIPT_VARIABLES_DEFAULT);
        Map<String, Object> payload = scriptEngineService.runScript(scriptGetVariables, "payload", scriptVariables);
        /*try {
            payload.put("batchVoting", serializeService.toPrimitiveObject(
                    bpmBatchVotingService.getBpmBatchVotingExtended(
                        batchVotingService.getBatchVoting(268, true, true))
                    )
            );
        } catch (VotingSystemException e) {
            e.printStackTrace();
        }*/
        //payload.put(REGULATIONS_MAP_VAR_NAME, createPoRegulationsData(registerPODto, resolvedObjects));
        String createPoSignal = settingsManager.getSystemSetting(REGISTER_PO_SIGNAL_SETTINGS_KEY, REGISTER_PO_SIGNAL_DEFAULT);
        blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(this, createPoSignal, payload));
    }

    private Community generateCommunity(RegisterPoResolvedObjects resolvedObjects, RegisterPODto registerPODto) {
        //List<ListEditorItem> activityScopes = null;

        Community community = new Community();
        community.setFullRuName(registerPODto.getName());
        community.setAccessType(CommunityAccessType.CLOSE);
        community.setVisible(true);
        community.setAnnouncement(null);
        community.setMainOkved(resolvedObjects.getMainOkved());
        community.getOkveds().addAll(resolvedObjects.getAdditionalOkveds());
        community.setSeoLink(null);
        //community.getActivityScopes().addAll(activityScopes);

        // Собираем поля
        List<String> fieldInternalNames = Arrays.asList(
                "COMMUNITY_NAME",//"Полное название на русском языке"
                "COMMUNITY_SHORT_NAME",//"Короткое название на русском языке"
                "COMMUNITY_ENG_NAME",//"Полное название на английском языке"
                "COMMUNITY_ENG_SHORT_NAME",//"Короткое название на английском языке"
                "COMMUNITY_ASSOCIATION_FORM",//"Идентификатор формы объединения"
                "COMMUNITY_BEGIN_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_BOARD_MEMBERS",//"Дата начала полномочий членов Совета ПО""04.06.2014"
                "COMMUNITY_END_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_BOARD_MEMBERS",//"Дата окончания полномочий членов Совета ПО""04.06.2014"
                "COMMUNITY_BEGIN_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_THE_PRESIDENT",//"Дата начала полномочий Председателя Совета ПО"
                "COMMUNITY_END_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_THE_PRESIDENT",//"Дата окончания полномочий Председателя Совета ПО"
                "COMMUNITY_MEMBERS_OF_THE_BOARD1",//"Члены Совета"
                "COMMUNITY_CHAIRMAN_OF_THE_BOARD1",//"Председатель Совета"
                "COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID",

                "COMMUNITY_DESCRIPTION", // Полное описание целей и задач
                "COMMUNITY_BRIEF_DESCRIPTION", // Краткое описание целей и задач

                "ENTRANCE_SHARE_FEES",//"Вступительный взнос (Физ. лица)"
                "MIN_SHARE_FEES",//"Минимальный паевой взнос (Физ. лица)"
                "MEMBERSHIP_FEES",//"Членский взнос (Физ. лица)"

                "COMMUNITY_ENTRANCE_SHARE_FEES",//"Вступительный взнос (Юр. лица)"
                "COMMUNITY_MIN_SHARE_FEES",//"Минимальный паевой взнос (Юр. лица)"
                "COMMUNITY_MEMBERSHIP_FEES",//"Членский взнос (Юр. лица)"

                "COMMUNITY_LEGAL_COUNTRY",//"Страна"
                "COMMUNITY_LEGAL_POST_CODE",//"Индекс"
                "COMMUNITY_LEGAL_REGION",//"Регион"
                "COMMUNITY_LEGAL_AREA",//"Район"
                "COMMUNITY_LEGAL_LOCALITY",//"Населенный пункт"
                "COMMUNITY_LEGAL_STREET",//"Улица"
                "COMMUNITY_LEGAL_HOUSE",//"Дом"
                "COMMUNITY_LEGAL_OFFICE",//"Офис"
                "COMMUNITY_LEGAL_GEO_LOCATION",//"Адрес"
                "COMMUNITY_LEGAL_GEO_POSITION",//"Координаты"
                "OFFICE_OWNERSHIP_TYPE",//"Тип владения офисом"
                "OFFICE_RENT_PERIOD",//"Срок аренды"

                "COMMUNITY_LEGAL_F_COUNTRY",//"Страна"
                "COMMUNITY_LEGAL_F_POST_CODE",//"Индекс"
                "COMMUNITY_LEGAL_F_REGION",//"Регион"
                "COMMUNITY_LEGAL_F_AREA",//"Район"
                "COMMUNITY_LEGAL_F_LOCALITY",//"Населенный пункт"
                "COMMUNITY_LEGAL_F_STREET",//"Улица"
                "COMMUNITY_LEGAL_F_HOUSE",//"Дом"
                "COMMUNITY_LEGAL_F_OFFICE",//"Офис фактический"
                "COMMUNITY_LEGAL_F_GEO_LOCATION",//"Адрес"
                "COMMUNITY_LEGAL_F_GEO_POSITION",//"Координаты"
                "FACT_OFFICE_OWNERSHIP_TYPE",//"Тип владения офисом"
                "FACT_OFFICE_RENT_PERIOD",//;"Срок аренды"

                "COMMUNITY_TYPE",//"Тип объединения"

                "COMMUNITY_CHARTER_DESCRIPTION",//"Устав"

                "COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE", // Председатель ревизионной комиссии
                "COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE_ID", // Председатель ревизионной комиссии
                "COMMUNITY_MEMBERS_REVISOR_COMMITTEE", // Члены ревизионной комиссии
                "COMMUNITY_PROTOCOL_MEMBERS_REVISOR_COMMITTEE", //Протокол общего собрания ПО по выборам ревизионной комиссии
                "COMMUNITY_BEGIN_DATE_MEMBERS_REVISOR_COMMITTEE", //Дата начала полномочий членов ревизионной комиссии
                "COMMUNITY_END_DATE_MEMBERS_REVISOR_COMMITTEE", //Дата окончания полномочий членов ревизионной комиссии
                "COMMUNITY_PROTOCOL_CHAIRMAN_REVISOR", //Протокол собрания Совета ПО по выборам Председателя ревизионной комиссии
                "COMMUNITY_BEGIN_DATE_REVISOR", //Дата начала полномочий Председателя ревизионной комиссии"
                "COMMUNITY_END_DATE_REVISOR", //Дата окончания полномочий Председателя ревизионной комиссии

                "COMMUNITY_DIRECTOR_POSITION", // ИД должности руководства организации

                FieldConstants.COMMUNITY_FULL_OKVEDS_FIELD_NAME,
                FieldConstants.COMMUNITY_SHORT_OKVEDS_FIELD_NAME
        );

        List<FieldEntity> fields = fieldDao.getByInternalNames(fieldInternalNames);

        String currentDateFormatted = DateUtils.formatDate(new Date(), DateUtils.Format.DATE);
        Map<Long, FieldsGroup> allFieldsGroups = new HashMap<>();
        for (FieldEntity field : fields) {
            String stringValue = "";
            // TODO Создать константы
            switch (field.getInternalName()) {
                case "COMMUNITY_NAME":
                    stringValue = registerPODto.getName();
                    break;
                case "COMMUNITY_SHORT_NAME":
                    stringValue = registerPODto.getNameShort();
                    break;
                case "COMMUNITY_ENG_NAME":
                    stringValue = registerPODto.getEngName();
                    break;
                case "COMMUNITY_ENG_SHORT_NAME":
                    stringValue = registerPODto.getEngNameShort();
                    break;
                case "COMMUNITY_ASSOCIATION_FORM":
                    stringValue = String.valueOf(resolvedObjects.getAssociationFormListItem().getId());
                    break;
                case "COMMUNITY_BEGIN_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_BOARD_MEMBERS":
                    stringValue = currentDateFormatted;
                    break;
                case "COMMUNITY_END_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_BOARD_MEMBERS":
                    //stringValue = DateUtils.formatDate(registerPODto.getSovietMembersOfficeEndDate(), DateUtils.Format.DATE);
                    break;
                case "COMMUNITY_BEGIN_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_THE_PRESIDENT":
                    stringValue = currentDateFormatted;
                    break;
                case "COMMUNITY_END_DATE_OF_COMMENCEMENT_OF_OFFICE_OF_THE_PRESIDENT":
                    //stringValue = DateUtils.formatDate(registerPODto.getPresidentOfSovietOfficeEndDate(), DateUtils.Format.DATE);
                    break;
                /*case "COMMUNITY_MEMBERS_OF_THE_BOARD1":
                    stringValue = StringUtils.join(registerPODto.getParticipantsInSovietIds(), ";");
                    break;*/
                case "COMMUNITY_CHAIRMAN_OF_THE_BOARD1":// TODO
                    break;
                case "COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID":
                    break;
                case "COMMUNITY_DESCRIPTION":
                    stringValue = registerPODto.getFullTargets();
                    break;
                case "COMMUNITY_BRIEF_DESCRIPTION":
                    stringValue = registerPODto.getShortTargets();
                    break;

                case "ENTRANCE_SHARE_FEES":
                    stringValue = registerPODto.getEntranceShareFees();
                    break;
                case "MIN_SHARE_FEES":
                    stringValue = registerPODto.getMinShareFees();
                    break;
                case "MEMBERSHIP_FEES":
                    stringValue = registerPODto.getMembershipFees();
                    break;
                case "COMMUNITY_ENTRANCE_SHARE_FEES":
                    stringValue = registerPODto.getCommunityEntranceShareFees();
                    break;
                case "COMMUNITY_MIN_SHARE_FEES":
                    stringValue = registerPODto.getCommunityMinShareFees();
                    break;
                case "COMMUNITY_MEMBERSHIP_FEES":
                    stringValue = registerPODto.getCommunityMembershipFees();
                    break;

                case "COMMUNITY_LEGAL_COUNTRY":
                    stringValue = registerPODto.getAddress().getCountry();
                    break;
                case "COMMUNITY_LEGAL_POST_CODE":
                    stringValue = registerPODto.getAddress().getPostalCode();
                    break;
                case "COMMUNITY_LEGAL_REGION":
                    stringValue = registerPODto.getAddress().getRegion();
                    break;
                case "COMMUNITY_LEGAL_AREA":
                    stringValue = registerPODto.getAddress().getDistrict();
                    break;
                case "COMMUNITY_LEGAL_LOCALITY":
                    stringValue = registerPODto.getAddress().getCity();
                    break;
                case "COMMUNITY_LEGAL_STREET":
                    stringValue = registerPODto.getAddress().getStreet();
                    break;
                case "COMMUNITY_LEGAL_HOUSE":
                    stringValue = registerPODto.getAddress().getBuilding();
                    break;
                case "COMMUNITY_LEGAL_OFFICE":
                    stringValue = registerPODto.getAddress().getRoom();
                    break;
                case "COMMUNITY_LEGAL_GEO_LOCATION":
                    stringValue = registerPODto.getAddress().getGeoLocation();
                    break;
                case "COMMUNITY_LEGAL_GEO_POSITION":
                    stringValue = registerPODto.getAddress().getGeoPosition();
                    break;
                case "OFFICE_OWNERSHIP_TYPE":
                    stringValue = String.valueOf(registerPODto.getOfficeOwnerShipType());
                    break;
                case "OFFICE_RENT_PERIOD":
                    stringValue = registerPODto.getOfficeRentPeriod();
                    break;

                case "COMMUNITY_LEGAL_F_COUNTRY":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getCountry();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_POST_CODE":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getPostalCode();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_REGION":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getRegion();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_AREA":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getDistrict();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_LOCALITY":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getCity();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_STREET":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getStreet();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_HOUSE":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getBuilding();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_OFFICE":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getRoom();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_GEO_LOCATION":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getGeoLocation();
                    }
                    break;
                case "COMMUNITY_LEGAL_F_GEO_POSITION":
                    if (registerPODto.getFactAddress() != null) {
                        stringValue = registerPODto.getFactAddress().getGeoPosition();
                    }
                    break;
                case "FACT_OFFICE_OWNERSHIP_TYPE":
                    if (registerPODto.getFactOfficeOwnerShipType() != null) {
                        stringValue = String.valueOf(registerPODto.getFactOfficeOwnerShipType());
                    }
                    break;
                case "FACT_OFFICE_RENT_PERIOD":
                    if (registerPODto.getFactOfficeRentPeriod() != null) {
                        stringValue = registerPODto.getFactOfficeRentPeriod();
                    }
                    break;

                case FieldConstants.COMMUNITY_TYPE:
                    stringValue = ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName();
                    break;

                case "COMMUNITY_CHARTER_DESCRIPTION":
                    stringValue = registerPODto.getOrganizationRegulations();
                    break;

                /*case "COMMUNITY_MEMBERS_REVISOR_COMMITTEE": // Члены ревизионной комиссии
                    stringValue = StringUtils.join(registerPODto.getParticipantsInAuditCommitteeIds(), ";");
                    break;*/

                case "COMMUNITY_DIRECTOR_POSITION":
                    stringValue = String.valueOf(registerPODto.getDirectorPositionId());
                    break;
            }

            FieldsGroupEntity fieldsGroup = field.getFieldsGroup();
            FieldsGroup fieldGroupDomain;
            if (!allFieldsGroups.containsKey(fieldsGroup.getId())) {
                fieldGroupDomain = fieldsGroup.toDomain(false, false);
                allFieldsGroups.put(fieldsGroup.getId(), fieldGroupDomain);
            } else {
                fieldGroupDomain = allFieldsGroups.get(fieldsGroup.getId());
            }

            Field fieldDomain = new Field();
            fieldDomain.setId(field.getId());
            fieldDomain.setName(field.getName());
            fieldDomain.setInternalName(field.getInternalName());
            fieldDomain.setType(field.getType());
            fieldDomain.setExample(field.getExample());
            fieldDomain.setPoints(field.getPoints());
            fieldDomain.setValue(stringValue);
            fieldGroupDomain.getFields().add(fieldDomain);
        }
        community.setCommunityData(new CommunityData());
        community.getCommunityData().setFieldGroups(new ArrayList<>(allFieldsGroups.values()));
        return community;
    }

    /**
     *
     * @param registerPODto
     * @return
     */
    private Map<String, Object> createPoRegulationsData(RegisterPODto registerPODto, RegisterPoResolvedObjects resolvedObjects) {
        String script = settingsManager.getSystemSetting(GENERATE_PO_REGULATIONS_SCRIPT_DATA_SYSTEM_SETTINGS_KEY);
        if (script == null) {
            throw new RuntimeException("Не установлен скрипт генерации данных для создания устава ПО");
        }
        Map<String, Object> scriptVars = serializeService.toPrimitiveObject(registerPODto);
        scriptVars.putAll(serializeService.toPrimitiveObject(resolvedObjects));

        return scriptEngineService.runScript(script, REGULATIONS_MAP_VAR_NAME, scriptVars);
    }

    /**
     * Сгенерировать устав ПО
     * @param registerPODto параметры для генерации
     * @return обёртка документа (не сохранённая)
     */
    public FlowOfDocumentDTO generateOrganizationRegulations(RegisterPODto registerPODto) {
        // TODO Нужно сделать отдельным BPM таском генерацию обёртки документа чтобы не лепить настройки
        FlowOfDocumentDTO flowOfDocumentDTO = null;
        try {
            String script = settingsManager.getSystemSetting(GENERATE_PO_REGULATIONS_SCRIPT_SYSTEM_SETTINGS_KEY, DEFAULT_PO_REGULATIONS_SCRIPT);

            Map<String, Object> scriptVars = new HashMap<>();
            RegisterPoResolvedObjects resolvedObjects = commonValidateFormData(registerPODto);
            validateGenerateRegulationsFormData(registerPODto, resolvedObjects);

            scriptVars.put(REGULATIONS_MAP_VAR_NAME, createPoRegulationsData(registerPODto, resolvedObjects));
            scriptVars.put("participantsInSovietFullNameList", createParticipantsStringList(resolvedObjects.getParticipantsInSoviet()));
            scriptVars.put("participantsInAuditCommitteeFullNameList", createParticipantsStringList(resolvedObjects.getParticipantsInAuditCommittee()));
            scriptVars.put("community", generateCommunity(resolvedObjects, registerPODto));

            flowOfDocumentDTO = documentService.generateDocumentDTOByScript(script, "flowOfDocumentDTO", scriptVars);
        } catch (ScriptException e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return flowOfDocumentDTO;
    }

    private int getMinCountFounders() {
        return settingsManager.getSystemSettingAsInt(REGISTER_PO_FOUNDERS_MIN_COUNT_SYS_ATTR_NAME, REGISTER_PO_FOUNDERS_MIN_COUNT_DEFAULT);
    }

    private int getMinCountSoviet() {
        return settingsManager.getSystemSettingAsInt(REGISTER_PO_SOVIET_MIN_COUNT_SYS_ATTR_NAME, REGISTER_PO_SOVIET_MIN_COUNT_DEFAULT);
    }

    private int getMinCountAudit() {
        return settingsManager.getSystemSettingAsInt(REGISTER_PO_AUDIT_MIN_COUNT_SYS_ATTR_NAME, REGISTER_PO_AUDIT_MIN_COUNT_DEFAULT);
    }

    public CreatePOPageDataDto getCreatePOPageData() {
        CreatePOPageDataDto result = new CreatePOPageDataDto();
        result.minCountFounders = getMinCountFounders();
        result.minCountSoviet = getMinCountSoviet();
        result.minCountAudit = getMinCountAudit();

        return result;
    }
}
