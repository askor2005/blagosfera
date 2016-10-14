package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.repositories.CommunityInventoryUnitRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountRepository;
import ru.askor.blagosfera.data.jpa.repositories.cashbox.*;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentFolderRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.data.jpa.specifications.CashboxOperatorSessionSpecifications;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.cashbox.*;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorProduct;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorSystemParameters;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.xml.cashbox.Basket;
import ru.askor.blagosfera.domain.xml.cashbox.ImportProductsRequest;
import ru.askor.blagosfera.domain.xml.cashbox.Payment;
import ru.askor.blagosfera.domain.xml.cashbox.UpdatePricesRequest;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentFolderEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.cashbox.*;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorBonusAllocationEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorParametersEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.services.communities.CommunityInventoryDomainService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityService;
import ru.radom.kabinet.utils.*;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service("cashboxOperatorSessionService")
@Transactional(rollbackFor = CashboxException.class)
public class CashboxServiceImpl implements CashboxService {

    private static final Long KASSIR_WORKPLACE_TYPE_ID = 2L;

    @Autowired
    private LetterOfAuthorityService letterOfAuthorityService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SharerCommunityMemberService sharerCommunityMemberService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

    @Autowired
    private EcoAdvisorService ecoAdvisorService;

    /*@Autowired
    private CommunityInventoryService communityInventoryService;*/

    @Autowired
    private CommunityInventoryDomainService communityInventoryDomainService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private CashboxOperatorSessionRepository operatorSessionRepository;

    @Autowired
    private CommunityInventoryUnitRepository inventoryUnitRepository;

    @Autowired
    private CashboxRegisterShareholderRepository cashboxRegisterShareholderRepository;

    @Autowired
    private CashboxExchangeRepository cashboxExchangeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CashboxBasketItemRepository cashboxBasketItemRepository;

    @Autowired
    private CashboxProductRepository cashboxProductRepository;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private DocumentFolderRepository documentFolderRepository;

    private static final boolean DO_NOT_NOTIFY_ABOUT_SIGN = false;
    private static final List<FlowOfDocumentStateEvent> NO_EVENTS = Collections.emptyList();
    private static final List<UserFieldValue> NO_FIELDS = Collections.emptyList();
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public CashboxServiceImpl() {
    }

    @Override
    public CashboxOperatorSession createSession(UserEntity operator, String workplaceId) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);
        CashboxOperatorSessionEntity entity = getOperatorSessionEntityIfNotExpired(operator.getId(), workplaceId, false);

        if (entity != null) throw new CashboxException("Сессия уже активна.");
        if (!isCommunityMember(workplace.getCooperativeDepartment(), operator))
            throw new CashboxException("Кассир не найден.");

        Map<String, String> loaAttributes = new HashMap<>();
        loaAttributes.put("рабочее место", workplaceId);
        List<String> loaAttributesNames = new ArrayList<>();
        loaAttributesNames.add("рабочее место");

        boolean hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_CREATE_SESSION, operator, workplace.getCooperative(), loaAttributes);
        if (!hasLOA)
            hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_CREATE_SESSION, operator, workplace.getCooperative(), loaAttributesNames);
        if (!hasLOA) throw new CashboxException("Не найдена доверенность на использование рабочего места.");

        entity = new CashboxOperatorSessionEntity();
        entity.setOperator(operator);
        entity.setWorkplaceId(workplaceId);
        entity.setCreatedDate(new Date());
        entity.setActive(true);
        entity = operatorSessionRepository.save(entity);
        return entity.toDomain();
    }

    @Override
    public CashboxOperatorSession stopSession(UserEntity operator, String workplaceId) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace worklace = findWorkplace(workplaceId);
        CashboxOperatorSession operatorSession = null;
        CashboxOperatorSessionEntity operatorSessionEntity = getOperatorSessionEntityIfNotExpired(operator.getId(), workplaceId, false);

        if (operatorSessionEntity != null) {
            operatorSessionEntity.setActive(false);
            operatorSessionEntity.setEndDate(new Date());
            operatorSessionRepository.save(operatorSessionEntity);

            operatorSession = operatorSessionEntity.toDomain();

            BigDecimal exchangesTotal = BigDecimal.ZERO;
            List<CashboxExchangeEntity> exchangeEntities = cashboxExchangeRepository.findByOperatorSession_IdAndAcceptedDateIsNotNull(operatorSessionEntity.getId());

            Map<String, Map<String, Object>> basketItems = new HashMap<>();

            for (CashboxExchangeEntity exchangeEntity : exchangeEntities) {
                CashboxExchangeTotalsEntity exchangeTotalsEntity = exchangeEntity.getTotals();
                if (exchangeTotalsEntity.isCash())
                    exchangesTotal = exchangesTotal.add(exchangeTotalsEntity.getPaymentAmount());

                for (CashboxBasketItemEntity basketItemEntity : exchangeTotalsEntity.getBasketItems()) {
                    AdvisorProduct advisorProduct = ecoAdvisorService.getProductByCodeAndCommunityId(basketItemEntity.getCode(), worklace.getShop().getId());
                    Map<String, Object> basketItem = basketItems.get(basketItemEntity.getCode());

                    if (basketItem == null) {
                        basketItem = new HashMap<>();
                        basketItems.put(basketItemEntity.getCode(), basketItem);

                        basketItem.put("name", basketItemEntity.getName());
                        basketItem.put("code", basketItemEntity.getCode());
                    }

                    BigDecimal count = ecoAdvisorService.divide(basketItemEntity.getCount(), basketItemEntity.getBaseCount());

                    BigDecimal totalCount = (BigDecimal) basketItem.get("count");
                    if (totalCount == null) totalCount = BigDecimal.ZERO;
                    basketItem.put("count", totalCount.add(count));

                    BigDecimal amount = (BigDecimal) basketItem.get("amount");
                    if (amount == null) amount = BigDecimal.ZERO;
                    BigDecimal amountWithVat = ecoAdvisorService.withPercents(basketItemEntity.getFinalPrice(), advisorProduct.getVat());
                    basketItem.put("amount", amount.add(amountWithVat.multiply(count)));
                }
            }

            operatorSession.setExchangesCount(exchangeEntities.size());
            operatorSession.setExchangesTotal(exchangesTotal.multiply(ONE_HUNDRED));
            operatorSession.getBasketItems().addAll(basketItems.values());
        }

        return operatorSession;
    }

    @Override
    public String checkOperatorSession(UserEntity operator, String workplaceId) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        findWorkplace(workplaceId);

        return getOperatorSessionEntityIfNotExpired(operator.getId(), workplaceId, true).getOperator().getIkp();
    }

    @Override
    public CashboxOperatorSession checkSession(String workplaceId) throws CashboxException {
        CashboxWorkplace workplace = findWorkplace(workplaceId);
        return getSessionEntityIfNotExpired(workplaceId, true).toDomain().setWorkplace(workplace);
    }

    @Override
    public UserEntity identifySharer(String ikp) throws CashboxException {
        UserEntity userEntity = sharerDao.getByIkp(ikp);
        if (userEntity == null) throw new CashboxException("Пайщик не найден.");
        return userEntity;
    }

    @Override
    public boolean isCommunityMember(CommunityEntity community, UserEntity userEntity) {
        boolean isMember = false;

        for (CommunityMemberEntity member : community.getMembers()) {
            if (member.getUser().getId().equals(userEntity.getId())) {
                if (member.getStatus() == CommunityMemberStatus.MEMBER) {
                    isMember = true;
                }
                break;
            }
        }

        return isMember;
    }

    @Override
    public CashboxRegisterShareholder findMemberRegistrationEntry(String workplaceId, UserEntity userEntity) throws CashboxException {
        if (userEntity == null) throw new CashboxException("Пайщик не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);
        CashboxRegisterShareholderEntity registerShareholder = cashboxRegisterShareholderRepository.findFirstBySharerIkpAndCommunityIdOrderByRequestCreatedDateDesc(userEntity.getIkp(), workplace.getCooperative().getId());

        if (registerShareholder != null) {
            if ((registerShareholder.getRequestAcceptedDate() == null) && isCommunityMember(workplace.getCooperative(), userEntity)) {
                registerShareholder.setAcceptDocumentId(null);
                registerShareholder.setRequestAcceptedDate(new Date());
                registerShareholder = cashboxRegisterShareholderRepository.save(registerShareholder);
            }
        }

        return registerShareholder == null ? null : registerShareholder.toDomain();
    }

    @Override
    public void registerSharer(UserEntity operator, String workplaceId, String sharerIkp) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);

        UserEntity userEntity = identifySharer(sharerIkp);
        if (!userEntity.isVerified()) throw new CashboxException("Пайщик не идентифицирован.");

        if (!letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, operator, workplace.getCooperative()))
            throw new CashboxException("Не найдена доверенность на прием пайщиков.");

        if (isCommunityMember(workplace.getCooperative(), userEntity))
            throw new CashboxException("Пользователь уже является пайщиком.");

        try {
            CashboxRegisterShareholderEntity registerShareholder = cashboxRegisterShareholderRepository.findOneBySharerIkpAndCommunityIdAndRequestAcceptedDateIsNull(sharerIkp, workplace.getCooperative().getId());

            if (registerShareholder != null)
                throw new CashboxException("Пользователь уже подал заявку на регистрацию.");

            registerShareholder = new CashboxRegisterShareholderEntity();
            registerShareholder.setRequestOperatorSession(getSessionEntityIfNotExpired(workplaceId, true));
            registerShareholder.setSharerIkp(sharerIkp);
            registerShareholder.setCommunityId(workplace.getCooperative().getId());

            Long userId = userEntity.getId();
            Long cooperativeId = workplace.getCooperative().getId();

            CommunityMemberResponseDto responseDto = sharerCommunityMemberService.request(cooperativeId, userId, false);
            Document document = responseDto.getDocument();
            documentService.signDocument(document, userId);

            registerShareholder.setRequestCreatedDate(new Date());
            registerShareholder.setRequestDocumentId(document.getId());

            cashboxRegisterShareholderRepository.save(registerShareholder);
        } catch (Throwable e) {
            throw new CashboxException(e.getMessage(), e);
        }
    }

    @Override
    public void acceptSharerRegistration(UserEntity operator, String workplaceId, String sharerIkp) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);

        UserEntity userEntity = identifySharer(sharerIkp);
        if (!userEntity.isVerified()) throw new CashboxException("Пайщик не идентифицирован.");

        if (!letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, operator, workplace.getCooperative()))
            throw new CashboxException("Не найдена доверенность на прием пайщиков.");

        if (isCommunityMember(workplace.getCooperative(), userEntity))
            throw new CashboxException("Пользователь уже является пайщиком.");

        try {
            CashboxRegisterShareholderEntity registerShareholder = cashboxRegisterShareholderRepository.findOneBySharerIkpAndCommunityIdAndRequestAcceptedDateIsNull(sharerIkp, workplace.getCooperative().getId());

            if (registerShareholder == null)
                throw new CashboxException("Пользователь не подавал заявку на регистрацию.");

            registerShareholder.setAcceptOperatorSession(getSessionEntityIfNotExpired(workplaceId, true));

            CommunityMemberEntity member = communityMemberDao.get(workplace.getCooperative(), userEntity.getId());
            List<Long> members = Collections.singletonList(member.getId());

            CommunityMemberResponseDto responseDto = sharerCommunityMemberService.acceptRequests(members, operator.getId(), false);
            Document document = responseDto.getDocument();
            documentService.signDocument(document.getId(), operator.getId());

            registerShareholder.setRequestAcceptedDate(new Date());
            registerShareholder.setAcceptDocumentId(document.getId());

            cashboxRegisterShareholderRepository.save(registerShareholder);
        } catch (Exception e) {
            throw new CashboxException(e.getMessage(), e);
        }
    }

    @Override
    public String exchange(UserEntity operator, String workplaceId, UserEntity userEntity, Basket basket, Payment payment) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);

        if (!letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_EXCHANGE, operator, workplace.getCooperative()))
            throw new CashboxException("Не найдена доверенность на прием/возврат пая.");

        if (!isCommunityMember(workplace.getCooperative(), userEntity))
            throw new CashboxException("Пользователь не является пайщиком.");

        Account cooperativeAccount = accountService.getCommunityAccount(workplace.getCooperative().getId(),
                accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY).getId());
        if (cooperativeAccount == null) throw new CashboxException("Счет потребительского общества не найден.");

        Account sharerAccount = accountService.getUserAccount(userEntity.getId(), accountDataService.getPrimaryAccountType(Discriminators.SHARER).getId());
        if (sharerAccount == null) throw new CashboxException("Личный счет пайщика не найден.");

        Account shopAccount = accountService.getCommunityAccount(workplace.getShop().getId(), accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY).getId());
        if (shopAccount == null) throw new CashboxException("Счет объединения магазина не найден.");

        SharebookEntity sharerSharebook = accountService.getSharebook(userEntity, workplace.getCooperative().getId());
        if (sharerSharebook == null) throw new CashboxException("Паевая книжка пайщика не найдена.");

        SharebookEntity shopSharebook = accountService.getSharebook(workplace.getShop(), workplace.getCooperative().getId());
        if (shopSharebook == null) throw new CashboxException("Паевая книжка магазина не найдена.");

        CashboxExchangeEntity cashboxExchangeEntity = new CashboxExchangeEntity();
        cashboxExchangeEntity.setRequestId(UUID.randomUUID().toString());
        cashboxExchangeEntity.setUserEntity(userEntity);
        cashboxExchangeEntity.setOperatorSession(getSessionEntityIfNotExpired(workplaceId, true));
        cashboxExchangeEntity.setCreatedDate(new Date());

        CashboxExchangeTotalsEntity totalsEntity = new CashboxExchangeTotalsEntity(ecoAdvisorService.calculateExchangeTotals(basket, payment, workplace));
        cashboxExchangeEntity.setTotals(totalsEntity);

        createExchangeDocuments(cashboxExchangeEntity, workplace, userEntity, operator, basket, payment, totalsEntity);
        signExchangeDocumentsBySharer(cashboxExchangeEntity, userEntity);

        cashboxExchangeEntity = cashboxExchangeRepository.save(cashboxExchangeEntity);
        return cashboxExchangeEntity.getRequestId();
    }

    @Override
    public CashboxExchangeProtocols acceptExchange(UserEntity operator, String workplaceId, String requestId) throws CashboxException {
        if (operator == null) throw new CashboxException("Оператор не задан.");
        CashboxWorkplace workplace = findWorkplace(workplaceId);

        if (!letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_EXCHANGE, operator, workplace.getCooperative()))
            throw new CashboxException("Не найдена доверенность на прием/возврат пая.");

        CashboxExchangeEntity cashboxExchangeEntity = cashboxExchangeRepository.findOneByRequestIdAndOperatorSession_Id(requestId, getSessionEntityIfNotExpired(workplaceId, true).getId());

        if (cashboxExchangeEntity == null) throw new CashboxException("Заявка не найдена.");
        if (cashboxExchangeEntity.getAcceptedDate() != null)
            throw new CashboxException("Заявка уже обработана.");

        AccountTypeEntity communityAccountType = accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY);
        AccountTypeEntity sharerAccountType = accountDataService.getPrimaryAccountType(Discriminators.SHARER);

        Map<String, Account> accountsMap = new HashMap<>();
        Map<String, SharebookEntity> sharebooksMap = new HashMap<>();
        Account account;

        account = accountService.getCommunityAccount(workplace.getCooperative().getId(), communityAccountType.getId());
        if (account == null) throw new CashboxException("Счет потребительского общества не найден.");
        accountsMap.put("cooperativeAccount", account);

        account = accountService.getCommunityAccount(workplace.getCooperativeDepartment().getId(), communityAccountType.getId());
        if (account == null) throw new CashboxException("Счет кооперативного участка не найден.");
        accountsMap.put("cooperativeDepartmentAccount", account);

        account = accountService.getUserAccount(cashboxExchangeEntity.getUserEntity().getId(), sharerAccountType.getId());
        if (account == null) throw new CashboxException("Личный счёт пайщика не найден.");
        accountsMap.put("sharerAccount", account);

        account = accountService.getCommunityAccount(workplace.getShop().getId(), communityAccountType.getId());
        if (account == null) throw new CashboxException("Счет объединения магазина не найден.");
        accountsMap.put("shopAccount", account);

        account = accountService.getUserAccount(workplace.getShop().getCreator().getId(), sharerAccountType.getId());
        if (account == null) throw new CashboxException("Личный счёт хозяина магазина не найден.");
        accountsMap.put("shopOwnerAccount", account);

        SharebookEntity sharebook = accountService.getSharebook(cashboxExchangeEntity.getUserEntity(), workplace.getCooperative().getId());
        if (sharebook == null) throw new CashboxException("Паевая книжка пайщика не найдена.");
        sharebooksMap.put("sharerSharebook", sharebook);

        sharebook = accountService.getSharebook(workplace.getShop(), workplace.getCooperative().getId());
        if (sharebook == null) throw new CashboxException("Паевая книжка магазина не найдена.");
        sharebooksMap.put("shopSharebook", sharebook);

        sharebook = accountService.getSharebook(workplace.getShop().getCreator(), workplace.getCooperative().getId());
        if (sharebook == null) throw new CashboxException("Паевая книжка хозяина магазина не найдена.");
        sharebooksMap.put("shopOwnerSharebook", sharebook);

        signExchangeDocumentsByOperator(cashboxExchangeEntity, operator);

        try {
            createExchangeTransactions(workplace, operator, cashboxExchangeEntity, accountsMap, sharebooksMap);
        } catch(TransactionException e) {
            throw new CashboxException(e.getMessage(), e);
        }

        cashboxExchangeEntity.setAcceptedDate(new Date());

        cashboxExchangeRepository.save(cashboxExchangeEntity);

        CashboxExchangeProtocols exchangeProtocols = new CashboxExchangeProtocols();
        exchangeProtocols.setSharerContributionProtocolCode(documentRepository.getCodeById(cashboxExchangeEntity.getSharerContributionProtocolDocumentId()));
        exchangeProtocols.setSharerRefundProtocolCode(documentRepository.getCodeById(cashboxExchangeEntity.getSharerRefundProtocolDocumentId()));
        exchangeProtocols.setSharerContributionProtocolCreatedDate(documentRepository.getCreatedDateById(cashboxExchangeEntity.getSharerContributionProtocolDocumentId()));
        exchangeProtocols.setSharerRefundProtocolCreatedDate(documentRepository.getCreatedDateById(cashboxExchangeEntity.getSharerRefundProtocolDocumentId()));
        return exchangeProtocols;
    }

    @Override
    public void importProducts(long shop, ImportProductsRequest.Products products) throws CashboxException {
        ecoAdvisorService.importProducts(shop, products);
    }

    @Override
    public void updatePrices(long shop, UpdatePricesRequest.Products products) throws CashboxException {
        ecoAdvisorService.updatePrices(shop, products);
    }

    private CashboxOperatorSessionEntity getOperatorSessionEntityIfNotExpired(Long operatorId, String workplaceId, boolean checkIfNotExists) throws CashboxException {
        CashboxOperatorSessionEntity entity = getSessionEntityIfNotExpired(workplaceId, checkIfNotExists);

        if ((entity != null) && !entity.getOperator().getId().equals(operatorId))
            throw new CashboxException("Найдена активная сессия другого оператора.");

        return entity;
    }

    private CashboxOperatorSessionEntity getSessionEntityIfNotExpired(String workplaceId, boolean checkIfNotExists) throws CashboxException {
        CashboxOperatorSessionEntity entity = operatorSessionRepository.findOneByWorkplaceIdAndActive(workplaceId, true);

        // TODO session duration setting disabled
        /*if ((entity != null) && DateUtils.isOlderThan(entity.getCreatedDate(), Calendar.HOUR_OF_DAY, getSessionDurationMax())) {
            entity.setActive(false);
            entity.setEndDate(new Date());
            operatorSessionRepository.save(entity);
            entity = null;
        }*/

        if (entity == null && checkIfNotExists) throw new CashboxException("Сессия не найдена.");

        return entity;
    }

    public int getSessionDurationMax() {
        return settingsManager.getSystemSettingAsInt("cashbox.operator.session.duration.hours", 8);
    }

    @Override
    public BigDecimal getAffiliationFee(CashboxWorkplace workplace) {
        // TODO Переделать на доменную модель объединения
        FieldValueEntity fieldValue = fieldValueDao.get(workplace.getCooperative(), FieldConstants.ENTRANCE_SHARE_FEES_FIELD_NAME);
        return new BigDecimal(FieldsService.getFieldStringValue(fieldValue));
    }

    @Override
    public BigDecimal getMinShareAmount(CashboxWorkplace workplace) {
        // TODO Переделать на доменную модель объединения
        FieldValueEntity fieldValue = fieldValueDao.get(workplace.getCooperative(), FieldConstants.MIN_SHARE_FEES_FIELD_NAME);;
        return new BigDecimal(FieldsService.getFieldStringValue(fieldValue));
    }

    @Override
    public List<CashboxWorkplaceDto> getWorkplaces(Long communityId) {
        List<CashboxWorkplaceDto> result = new ArrayList<>();
        CommunityEntity community = communityRepository.findOne(communityId);

        List<CommunityInventoryUnit> inventoryUnits = communityInventoryDomainService.getList(communityId, KASSIR_WORKPLACE_TYPE_ID);

        for (CommunityInventoryUnit inventoryUnit : inventoryUnits) {
            result.add(new CashboxWorkplaceDto(inventoryUnit));
        }

        for (CommunityEntity subgroup : community.getChildren()) {
            inventoryUnits = communityInventoryDomainService.getList(subgroup.getId(), KASSIR_WORKPLACE_TYPE_ID);

            for (CommunityInventoryUnit inventoryUnit : inventoryUnits) {
                result.add(new CashboxWorkplaceDto(inventoryUnit));
            }
        }

        return result;
    }

    @Override
    public CashboxOperatorSessionsResponseDto getSessions(Long communityId, List<Long> workplaceIds,
                                                          int page, int size, String sortDirection, String sortColumn,
                                                          String operator, Date createdDateFrom, Date createdDateTo, String active) {
        CashboxOperatorSessionsResponseDto responseDto = new CashboxOperatorSessionsResponseDto();
        List<String> workplaceGuids = new ArrayList<>();

        for (Long workplaceId : workplaceIds) {
            workplaceGuids.add(communityInventoryDomainService.getGuidById(workplaceId));
        }

        PageRequest pageRequest;

        if ((sortColumn != null) && sortColumn.equals("workplace")) {
            sortColumn = "workplaceId";
        }

        if (sortDirection == null || sortColumn == null) {
            pageRequest = new PageRequest(page, size);
        } else if (sortDirection.equals("asc")) {
            pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sortColumn);
        } else {
            pageRequest = new PageRequest(page, size, Sort.Direction.DESC, sortColumn);
        }

        Specification<CashboxOperatorSessionEntity> workplaceGuidsSpec = CashboxOperatorSessionSpecifications.workplaceIdIn(workplaceGuids);
        Specification<CashboxOperatorSessionEntity> operatorSpec = CashboxOperatorSessionSpecifications.operatorLike(operator);
        Specification<CashboxOperatorSessionEntity> createdDateFromSpec = CashboxOperatorSessionSpecifications.createdDateGreaterThan(createdDateFrom);
        Specification<CashboxOperatorSessionEntity> createdDateToSpec = CashboxOperatorSessionSpecifications.createdDateLessThan(createdDateTo);
        Specification<CashboxOperatorSessionEntity> activeSpec = CashboxOperatorSessionSpecifications.activeState(active);

        Page<CashboxOperatorSessionEntity> sessions = operatorSessionRepository.findAll(Specifications.where(workplaceGuidsSpec)
                        .and(operatorSpec)
                        .and(createdDateFromSpec)
                        .and(createdDateToSpec)
                        .and(activeSpec),
                pageRequest);
        responseDto.total = sessions.getTotalElements();

        for (CashboxOperatorSessionEntity session : sessions.getContent()) {
            responseDto.data.add(new CashboxOperatorSessionDto(session));
        }

        return responseDto;
    }

    @Override
    public CashboxExchangeOperationsResponseDto getOperations(Long communityId, Long sessionId,
                                                              int page, int size, String sortDirection, String sortColumn) {
        CashboxExchangeOperationsResponseDto responseDto = new CashboxExchangeOperationsResponseDto();
        PageRequest pageRequest;

        if ((sortColumn != null) && sortColumn.equals("customer")) {
            sortColumn = "sharer";
        }

        if (sortDirection == null || sortColumn == null) {
            pageRequest = new PageRequest(page, size);
        } else if (sortDirection.equals("asc")) {
            pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sortColumn);
        } else {
            pageRequest = new PageRequest(page, size, Sort.Direction.DESC, sortColumn);
        }

        Page<CashboxExchangeEntity> operations = cashboxExchangeRepository.findByOperatorSession_IdAndAcceptedDateIsNotNull(sessionId, pageRequest);
        responseDto.total = operations.getTotalElements();

        for (CashboxExchangeEntity operation : operations) {
            CashboxExchangeOperationDto operationDto = new CashboxExchangeOperationDto(operation);

            DocumentDto documentDto = createDocumentDto(operation.getSharerContributionStatementDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getShopContributionStatementDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getSharerContributionProtocolDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getShopContributionProtocolDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getSharerRefundStatementDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getShopRefundStatementDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getSharerRefundProtocolDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getShopRefundProtocolDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getSharerMembershipFeeStatementDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            documentDto = createDocumentDto(operation.getSharerMembershipFeeProtocolDocumentId());
            if (documentDto != null) operationDto.documents.add(documentDto);

            responseDto.data.add(operationDto);
        }

        return responseDto;
    }

    private DocumentDto createDocumentDto(Long documentId) {
        if (documentId != null) {
            DocumentEntity document = documentRepository.findOne(documentId);

            if (document != null) {
                DocumentDto documentDto = new DocumentDto();
                documentDto.id = document.getId();
                documentDto.code = document.getCode();
                documentDto.createdDate = document.getCreateDate();
                documentDto.name = document.getShortName();
                documentDto.link = CommonConstants.BASE_DOCUMENT_LINK + document.getId();

                return documentDto;
            }
        }

        return null;
    }

    @Override
    public CashboxExchangeProductsResponseDto getProducts(Long communityId, List<Long> exchangeIds,
                                                          int page, int size, String sortDirection, String sortColumn) {
        CashboxExchangeProductsResponseDto responseDto = new CashboxExchangeProductsResponseDto();
        PageRequest pageRequest;

        if (sortDirection == null || sortColumn == null) {
            pageRequest = new PageRequest(page, size);
        } else if (sortDirection.equals("asc")) {
            pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sortColumn);
        } else {
            pageRequest = new PageRequest(page, size, Sort.Direction.DESC, sortColumn);
        }

        Page<CashboxBasketItemEntity> products = cashboxBasketItemRepository.findByTotals_ExchangeOperation_IdIn(exchangeIds, pageRequest);
        responseDto.total = products.getTotalElements();

        for (CashboxBasketItemEntity product : products) {
            ProductDto exchangeProductDto = null;

            for (ProductDto productDto : responseDto.data) {
                if (product.getCode().equals(productDto.code)
                        && product.getName().equals(productDto.name)) {
                    exchangeProductDto = productDto;
                    exchangeProductDto.count = exchangeProductDto.count.add(product.getCount());
                    exchangeProductDto.wholesalePriceTotal = exchangeProductDto.wholesalePrice.multiply(exchangeProductDto.count);
                    exchangeProductDto.wholesalePriceWithVatTotal = exchangeProductDto.wholesalePriceWithVat.multiply(exchangeProductDto.count);
                    exchangeProductDto.finalPriceTotal = exchangeProductDto.finalPrice.multiply(exchangeProductDto.count);
                    exchangeProductDto.finalPriceWithVatTotal = exchangeProductDto.finalPriceWithVat.multiply(exchangeProductDto.count);
                    break;
                }
            }

            if (exchangeProductDto == null) {
                exchangeProductDto = new ProductDto(product);
                responseDto.data.add(exchangeProductDto);
            }

            responseDto.wholesalePriceTotal = responseDto.wholesalePriceTotal.add(exchangeProductDto.wholesalePriceTotal);
            responseDto.wholesalePriceWithVatTotal = responseDto.wholesalePriceWithVatTotal.add(exchangeProductDto.wholesalePriceWithVatTotal);
            responseDto.finalPriceTotal = responseDto.finalPriceTotal.add(exchangeProductDto.finalPriceTotal);
            responseDto.finalPriceWithVatTotal = responseDto.finalPriceWithVatTotal.add(exchangeProductDto.finalPriceWithVatTotal);
        }

        return responseDto;
    }

    @Override
    public CashboxOperatorSessionDto closeOperatorSession(Long communityId, Long sessionId) {
        CashboxOperatorSessionEntity session = operatorSessionRepository.findOne(sessionId);

        if (session.isActive()) {
            session.setActive(false);
            session.setEndDate(new Date());
            session = operatorSessionRepository.save(session);
        }

        return new CashboxOperatorSessionDto(session);
    }

    @Override
    public StoreResponseDto getProductsFromStore(Long communityId, int page, int size, String sortDirection, String sortColumn, Long productGroupId) {
        StoreResponseDto responseDto = new StoreResponseDto();
        Page<AdvisorProductEntity> advisorProducts = ecoAdvisorService.getProductsFromStore(communityId, page, size, sortDirection, sortColumn, productGroupId);
        responseDto.total = advisorProducts.getTotalElements();
        responseDto.finalCosts = BigDecimal.ZERO;
        responseDto.directCosts = BigDecimal.ZERO;

        for (AdvisorProductEntity product : advisorProducts) {
            ProductDto productDto = new ProductDto(product);
            CashboxProductEntity cashboxProduct = cashboxProductRepository.findByShopAndCode(communityId, product.getCode());
            productDto.inStore = cashboxProduct != null;
            responseDto.data.add(productDto);
            responseDto.finalCosts = responseDto.finalCosts.add(product.getFinalPriceWithVat());
            responseDto.directCosts = responseDto.directCosts.add(product.getWholesalePriceWithVat());
        }

        return responseDto;
    }

    @Override
    public CashboxWorkplace findWorkplace(String workplaceId) throws CashboxException {
        if (workplaceId == null) throw new CashboxException("Рабочее место оператора не задано.");
        CommunityInventoryUnitEntity workplace = inventoryUnitRepository.findOneByGuid(workplaceId);
        if (workplace == null) throw new CashboxException("Касса не найдена.");
        if (workplace.getLeasedTo() == null) throw new CashboxException("Кооперативный участок не найден.");
        if (workplace.getLeasedTo().getParent() == null)
            throw new CashboxException("Потребительское общество не найдено.");
        return new CashboxWorkplace(workplace);
    }

    private void createExchangeTransactions(CashboxWorkplace workplace, UserEntity operator, CashboxExchangeEntity cashboxExchangeEntity,
                                            Map<String, Account> accountsMap, Map<String, SharebookEntity> sharebooksMap) throws CashboxException {
        CashboxExchangeTotalsEntity totalsEntity = cashboxExchangeEntity.getTotals();

        Account sharerAccount = accountsMap.get("sharerAccount");
        Account shopAccount = accountsMap.get("shopAccount");
        Account cooperativeDepartmentAccount = accountsMap.get("cooperativeDepartmentAccount");
        Account shopOwnerAccount = accountsMap.get("shopOwnerAccount");

        SharebookEntity sharerSharebook = sharebooksMap.get("sharerSharebook");
        SharebookEntity shopSharebook = sharebooksMap.get("shopSharebook");
        SharebookEntity shopOwnerSharebook = sharebooksMap.get("shopOwnerSharebook");

        BigDecimal totalWholesaleAmount = totalsEntity.getTotalWholesaleAmount();
        BigDecimal membershipFee = totalsEntity.getMembershipFee();

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("cashbox");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        DocumentFolder documentFolder = documentFolderEntity.toDomain(false, false);

        if (totalsEntity.isCash()) {
            {
                BigDecimal amount = totalsEntity.getPaymentAmount()
                        .add(totalsEntity.getChangeAmount());

                TransactionDetail credit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                        .setAccountId(sharerAccount.getId()).build();

                Transaction transaction = new TransactionBuilder()
                        .setAmount(amount).setDescription("пополнение личного счета")
                        .addDetail(credit).setDocumentFolder(documentFolder)
                        .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                        .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, "cashbox")
                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                transaction = accountService.submitTransaction(transaction);
                accountService.postTransaction(transaction.getId());

                //accountService.doCredit(sharerAccount, amount, "пополнение личного счета");
                sharerAccount.setBalance(sharerAccount.getBalance().add(amount));
            }
            {
                TransactionDetail credit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                        .setAccountId(sharerSharebook.getAccount().getId()).build();

                TransactionDetail debit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.DEBIT).setAmount(totalWholesaleAmount)
                        .setAccountId(sharerAccount.getId()).build();

                Transaction transaction = new TransactionBuilder()
                        .setAmount(totalWholesaleAmount).setDescription("паевой взнос")
                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                        .setTransactionType(TransactionType.SHAREBOOK)
                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                transaction = accountService.submitTransaction(transaction);
                accountService.postTransaction(transaction.getId());

                //accountService.doAccountToSharebookMove(sharerAccount, sharerShareBook, totalWholesaleAmount,
                //        "паевой взнос", cashboxExchangeEntity.getSharerContributionStatementDocumentId());
                sharerAccount.setBalance(sharerAccount.getBalance().subtract(totalWholesaleAmount));
                sharerSharebook.getAccount().setBalance(sharerSharebook.getAccount().getBalance().add(totalWholesaleAmount));
            }
        } else {
            {
                BigDecimal sharebookBalance = sharerSharebook.getBalance().subtract(getMinShareAmount(workplace));
                BigDecimal totalRequiredAmount = totalsEntity.getTotalWholesaleAmount().add(membershipFee);

                if (sharebookBalance.compareTo(totalRequiredAmount) < 0) {
                    BigDecimal accountBalance = sharerAccount.getBalance();
                    BigDecimal amount = totalRequiredAmount.subtract(sharebookBalance);

                    if (accountBalance.compareTo(amount) >= 0) {
                        TransactionDetail credit = new TransactionDetailBuilder()
                                .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                .setAccountId(sharerSharebook.getAccount().getId()).build();

                        TransactionDetail debit = new TransactionDetailBuilder()
                                .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                .setAccountId(sharerAccount.getId()).build();

                        Transaction transaction = new TransactionBuilder()
                                .setAmount(amount).setDescription("паевой взнос")
                                .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                .setTransactionType(TransactionType.SHAREBOOK)
                                .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                        transaction = accountService.submitTransaction(transaction);
                        accountService.postTransaction(transaction.getId());

                        //accountService.doAccountToSharebookMove(sharerAccount, sharerSharebook, amount,
                        //        "паевой взнос", 123L); // TODO document
                        sharerAccount.setBalance(sharerAccount.getBalance().subtract(amount));
                        sharerSharebook.getAccount().setBalance(sharerSharebook.getAccount().getBalance().add(amount));
                    } else {
                        throw new CashboxException("Недостаточно средств на личном счете. "
                                + "Сумма необходимая для проведения всех операций " + DECIMAL_FORMAT.format(totalRequiredAmount)
                                + ". Сумма средств на паевой книжке (без учета минимального паевого взноса) и личном счету " + DECIMAL_FORMAT.format(accountBalance.add(sharebookBalance)));
                    }
                }
            }
            {
                TransactionDetail credit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.CREDIT).setAmount(membershipFee)
                        .setAccountId(sharerAccount.getId()).build();

                TransactionDetail debit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.DEBIT).setAmount(membershipFee)
                        .setAccountId(sharerSharebook.getAccount().getId()).build();

                Transaction transaction = new TransactionBuilder()
                        .setAmount(membershipFee).setDescription("частичный возврат пая")
                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                        .setTransactionType(TransactionType.SHAREBOOK)
                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                transaction = accountService.submitTransaction(transaction);
                accountService.postTransaction(transaction.getId());

                //accountService.doSharebookToAccountMove(sharerShareBook, sharerAccount, membershipFee,
                //        "частичный возврат пая", 123L); // TODO document
                sharerSharebook.getAccount().setBalance(sharerSharebook.getAccount().getBalance().subtract(membershipFee));
                sharerAccount.setBalance(sharerAccount.getBalance().add(membershipFee));
            }
        }

        {
            TransactionDetail credit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(membershipFee)
                    .setAccountId(cooperativeDepartmentAccount.getId()).build();

            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.DEBIT).setAmount(membershipFee)
                    .setAccountId(sharerAccount.getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(membershipFee).setDescription("членский взнос")
                    .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.COMMUNITY)
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doAccountsMoveWithDocument(operator, sharerAccount, cooperativeDepartmentAccount, membershipFee,
            //        "членский взнос", cashboxExchangeEntity.getSharerContributionStatementDocumentId());
            sharerAccount.setBalance(sharerAccount.getBalance().subtract(membershipFee));
            cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().add(membershipFee));
        }
        {
            TransactionDetail credit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(shopAccount.getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("пополнение счета объединения")
                    .addDetail(credit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                    .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, "cashbox")
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doCredit(shopAccount, totalWholesaleAmount, "пополнение счета объединения");
            shopAccount.setBalance(shopAccount.getBalance().add(totalWholesaleAmount));
        }
        {
            TransactionDetail credit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(sharerSharebook.getAccount().getId()).build();

            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.DEBIT).setAmount(totalWholesaleAmount)
                    .setAccountId(shopAccount.getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("паевой взнос")
                    .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.SHAREBOOK)
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doAccountToSharebookMove(shopAccount, shopShareBook, totalWholesaleAmount,
            //        "паевой взнос", cashboxExchangeEntity.getShopContributionStatementDocumentId());
            shopAccount.setBalance(shopAccount.getBalance().subtract(totalWholesaleAmount));
            shopSharebook.getAccount().setBalance(shopSharebook.getAccount().getBalance().add(totalWholesaleAmount));
        }
        {
            TransactionDetail credit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(sharerAccount.getId()).build();

            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.DEBIT).setAmount(totalWholesaleAmount)
                    .setAccountId(sharerSharebook.getAccount().getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("частичный возврат пая")
                    .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.SHAREBOOK)
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doSharebookToAccountMove(sharerShareBook, sharerAccount, totalWholesaleAmount,
            //        "частичный возврат пая", cashboxExchangeEntity.getSharerRefundStatementDocumentId());
            sharerSharebook.getAccount().setBalance(sharerSharebook.getAccount().getBalance().subtract(totalWholesaleAmount));
            sharerAccount.setBalance(sharerAccount.getBalance().add(totalWholesaleAmount));
        }
        {
            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(sharerAccount.getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("снятие с личного счета")
                    .addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                    .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, "cashbox")
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doDebit(sharerAccount, totalWholesaleAmount, "снятие с личного счета");
            sharerAccount.setBalance(sharerAccount.getBalance().subtract(totalWholesaleAmount));
        }
        {
            BigDecimal changeAmount = totalsEntity.getChangeAmount();

            if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
                TransactionDetail debit = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.CREDIT).setAmount(changeAmount)
                        .setAccountId(sharerAccount.getId()).build();

                Transaction transaction = new TransactionBuilder()
                        .setAmount(changeAmount).setDescription("снятие с личного счета (сдача)")
                        .addDetail(debit).setDocumentFolder(documentFolder)
                        .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                        .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, "cashbox")
                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                transaction = accountService.submitTransaction(transaction);
                accountService.postTransaction(transaction.getId());

                //accountService.doDebit(sharerAccount, changeAmount, "снятие с личного счета (сдача)");
                sharerAccount.setBalance(sharerAccount.getBalance().subtract(changeAmount));
            }
        }
        {
            TransactionDetail credit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(shopAccount.getId()).build();

            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.DEBIT).setAmount(totalWholesaleAmount)
                    .setAccountId(shopSharebook.getAccount().getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("частичный возврат пая")
                    .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.SHAREBOOK)
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doSharebookToAccountMove(shopShareBook, shopAccount, totalWholesaleAmount,
            //        "частичный возврат пая", cashboxExchangeEntity.getShopRefundStatementDocumentId());
            shopSharebook.getAccount().setBalance(shopSharebook.getAccount().getBalance().subtract(totalWholesaleAmount));
            shopAccount.setBalance(shopAccount.getBalance().add(totalWholesaleAmount));
        }
        {
            TransactionDetail debit = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.CREDIT).setAmount(totalWholesaleAmount)
                    .setAccountId(shopAccount.getId()).build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(totalWholesaleAmount).setDescription("снятие со счета объединения")
                    .addDetail(debit).setDocumentFolder(documentFolder)
                    .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                    .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, "cashbox")
                    .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

            //accountService.doDebit(shopAccount, totalWholesaleAmount, "снятие со счета объединения");
            shopAccount.setBalance(shopAccount.getBalance().subtract(totalWholesaleAmount));
        }

        {
            AdvisorSystemParameters advisorSystemParameters = ecoAdvisorService.getSystemParameters();
            if (advisorSystemParameters == null) throw new CashboxException("Системные параметры ЭКС не найдены.");

            AdvisorParametersEntity advisorParameters = ecoAdvisorService.getAdvisorParameters(workplace.getShop().getId(), false);
            if (advisorParameters == null) throw new CashboxException("Параметры ЭКС не найдены.");

            AccountEntity systemAccount = accountRepository.findOne(advisorSystemParameters.getSystemBonusAccountId());
            if (systemAccount == null)
                throw new CashboxException("Системный аккаунт #" + advisorSystemParameters.getSystemBonusAccountId() + " не найден.");

            BigDecimal remainingAmount = membershipFee;

            for (Iterator<AdvisorBonusAllocationEntity> it = advisorParameters.getBonusAllocations().iterator(); it.hasNext();) {
                AdvisorBonusAllocationEntity bonusAllocation = it.next();

                if (bonusAllocation.getAllocationPercent().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal amount = !it.hasNext() ? remainingAmount : ecoAdvisorService.percents(membershipFee, bonusAllocation.getAllocationPercent());

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        switch (bonusAllocation.getReceiverType()) {
                            case USER_ACCOUNT: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(shopOwnerAccount.getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountsMoveWithDocument(operator, cooperativeDepartmentAccount, shopOwnerAccount, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                shopOwnerAccount.setBalance(shopOwnerAccount.getBalance().add(amount));
                                break;
                            }
                            case USER_SHAREBOOK: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(shopOwnerSharebook.getAccount().getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountToShareBookBonusMove(cooperativeDepartmentAccount, shopOwnerShareBook, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                shopOwnerSharebook.getAccount().setBalance(shopOwnerSharebook.getAccount().getBalance().add(amount));
                                break;
                            }
                            case COMMUNITY_ACCOUNT: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(shopAccount.getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountsMoveWithDocument(operator, cooperativeDepartmentAccount, shopAccount, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                shopAccount.setBalance(shopAccount.getBalance().add(amount));
                                break;
                            }
                            case COMMUNITY_SHAREBOOK: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(shopSharebook.getAccount().getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountToShareBookBonusMove(cooperativeDepartmentAccount, shopShareBook, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                shopSharebook.getAccount().setBalance(shopSharebook.getAccount().getBalance().add(amount));
                                break;
                            }
                            case CONSUMER_SHAREBOOK: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(sharerSharebook.getAccount().getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountToShareBookBonusMove(cooperativeDepartmentAccount, sharerShareBook, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                sharerSharebook.getAccount().setBalance(sharerSharebook.getAccount().getBalance().add(amount));
                                break;
                            }
                            case SYSTEM: {
                                TransactionDetail credit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.CREDIT).setAmount(amount)
                                        .setAccountId(systemAccount.getId()).build();

                                TransactionDetail debit = new TransactionDetailBuilder()
                                        .setType(TransactionDetailType.DEBIT).setAmount(amount)
                                        .setAccountId(cooperativeDepartmentAccount.getId()).build();

                                Transaction transaction = new TransactionBuilder()
                                        .setAmount(amount).setDescription("бонус")
                                        .addDetail(credit).addDetail(debit).setDocumentFolder(documentFolder)
                                        .setTransactionType(TransactionType.COMMUNITY)
                                        .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(operator.getId())).build();

                                transaction = accountService.submitTransaction(transaction);
                                accountService.postTransaction(transaction.getId());

                                //accountService.doAccountsMoveWithDocument(operator, cooperativeDepartmentAccount, systemAccount, amount,
                                //        "бонус", 123L); // TODO document
                                cooperativeDepartmentAccount.setBalance(cooperativeDepartmentAccount.getBalance().subtract(amount));
                                systemAccount.setBalance(systemAccount.getBalance().add(amount));
                                break;
                            }
                        }
                    }

                    remainingAmount = remainingAmount.subtract(amount);
                }
            }
        }
    }

    private void createExchangeDocuments(CashboxExchangeEntity cashboxExchangeEntity, CashboxWorkplace workplace, UserEntity userEntity, UserEntity operator,
                                         Basket basket, Payment payment, CashboxExchangeTotalsEntity totalsEntity) {
        // заявление пайщика физ лица о паевом взносе деньгами
        cashboxExchangeEntity.setSharerContributionStatementDocumentId(createSharerContributionStatement(workplace, userEntity, totalsEntity).getId());
        // протокол совета по о приеме паевого взноса от пайщика физ лица деньгами
        cashboxExchangeEntity.setSharerContributionProtocolDocumentId(createSharerContributionProtocol(workplace, userEntity, totalsEntity, operator).getId());
        // заявление пайщика юр лица о паевом взносе имуществом
        cashboxExchangeEntity.setShopContributionStatementDocumentId(createShopContributionStatement(workplace, operator, basket).getId());
        // протокол совета по о приеме паевого взноса от пайщика юр лица имуществом
        cashboxExchangeEntity.setShopContributionProtocolDocumentId(createShopContributionProtocol(workplace, operator, basket).getId());
        // заявление пайщика физ лица о частичном возврате паевого взноса имуществом
        cashboxExchangeEntity.setSharerRefundStatementDocumentId(createSharerRefundStatement(workplace, userEntity, basket, payment).getId());
        // протокол совета по о частичном возврате паевого взноса пайщику физ лицу имуществом
        cashboxExchangeEntity.setSharerRefundProtocolDocumentId(createSharerRefundProtocol(workplace, operator, userEntity).getId());
        // заявление пайщика юр лица о частичном возврате паевого взноса деньгами
        cashboxExchangeEntity.setShopRefundStatementDocumentId(createShopRefundStatement(workplace, operator, totalsEntity).getId());
        // протокол совета по о частичном возврате паевого взноса пайщику юр лицу деньгами
        cashboxExchangeEntity.setShopRefundProtocolDocumentId(createShopRefundProtocol(workplace, operator, totalsEntity).getId());
        // заявление пайщика физ лица о членском взносе
        cashboxExchangeEntity.setSharerMembershipFeeStatementDocumentId(createSharerMembershipFeeStatement(workplace, userEntity, totalsEntity).getId());
        // протокол совета по о приеме членского взноса от пайщика физ лица
        cashboxExchangeEntity.setSharerMembershipFeeProtocolDocumentId(createSharerMembershipFeeProtocol(workplace, userEntity, totalsEntity, operator).getId());
    }

    private void signExchangeDocumentsBySharer(CashboxExchangeEntity cashboxExchangeEntity, UserEntity userEntity) {
        documentService.signDocument(cashboxExchangeEntity.getSharerContributionStatementDocumentId(), userEntity.getId());
        documentService.signDocument(cashboxExchangeEntity.getSharerMembershipFeeStatementDocumentId(), userEntity.getId());
        documentService.signDocument(cashboxExchangeEntity.getSharerRefundStatementDocumentId(), userEntity.getId());
    }

    private void signExchangeDocumentsByOperator(CashboxExchangeEntity cashboxExchangeEntity, UserEntity operator) {
        documentService.signDocument(cashboxExchangeEntity.getSharerContributionProtocolDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getShopContributionStatementDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getShopContributionProtocolDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getSharerRefundProtocolDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getShopRefundStatementDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getShopRefundProtocolDocumentId(), operator.getId());
        documentService.signDocument(cashboxExchangeEntity.getSharerMembershipFeeProtocolDocumentId(), operator.getId());
    }

    private DocumentEntity createSharerContributionStatement(CashboxWorkplace workplace, UserEntity userEntity, CashboxExchangeTotalsEntity totals) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getTotalWholesaleAmount())))));
        return createDocument("shareholder_currency_contribution_statement", documentParameters, userEntity);
    }

    private DocumentEntity createSharerContributionProtocol(CashboxWorkplace workplace, UserEntity userEntity, CashboxExchangeTotalsEntity totals, UserEntity operator) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCooperative(operator), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getTotalWholesaleAmount())))));
        return createDocument("shareholder_currency_contribution_protocol_with_lof", documentParameters, operator);
    }

    private DocumentEntity createSharerMembershipFeeStatement(CashboxWorkplace workplace, UserEntity userEntity, CashboxExchangeTotalsEntity totals) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getMembershipFee())))));
        return createDocument("shareholder_membership_fee_statement", documentParameters, userEntity);
    }

    private DocumentEntity createSharerMembershipFeeProtocol(CashboxWorkplace workplace, UserEntity userEntity, CashboxExchangeTotalsEntity totals, UserEntity operator) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCooperative(operator), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getMembershipFee())))));
        return createDocument("shareholder_membership_fee_protocol_with_lof", documentParameters, operator);
    }

    private DocumentEntity createShopContributionStatement(CashboxWorkplace workplace, UserEntity operator, Basket basket) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createCommunityParticipant(workplace.getShop()), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("BASKET", formatBasketForDocument(basket, workplace.getShop().getId())))));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCommunity(operator), NO_FIELDS));
        return createDocument("community_property_contribution_statement_with_lof", documentParameters, operator);
    }

    private DocumentEntity createShopContributionProtocol(CashboxWorkplace workplace, UserEntity operator, Basket basket) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createCommunityParticipant(workplace.getShop()), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("BASKET", formatBasketForDocument(basket, workplace.getShop().getId())))));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCooperative(operator), NO_FIELDS));
        return createDocument("community_property_contribution_protocol_with_lof", documentParameters, operator);
    }

    private DocumentEntity createSharerRefundStatement(CashboxWorkplace workplace, UserEntity userEntity, Basket basket, Payment payment) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("BASKET", formatBasketForDocument(basket, workplace.getShop().getId())))));
        return createDocument("shareholder_property_refund_statement", documentParameters, userEntity);
    }

    private DocumentEntity createSharerRefundProtocol(CashboxWorkplace workplace, UserEntity operator, UserEntity userEntity) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCooperative(operator), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createSharerParticipant(userEntity), NO_FIELDS));
        return createDocument("shareholder_property_refund_protocol_with_lof", documentParameters, operator);
    }

    private DocumentEntity createShopRefundStatement(CashboxWorkplace workplace, UserEntity operator, CashboxExchangeTotalsEntity totals) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createCommunityParticipant(workplace.getShop()), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getTotalWholesaleAmount())))));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCommunity(operator), NO_FIELDS));
        return createDocument("community_currency_refund_statement_with_lof", documentParameters, operator);
    }

    private DocumentEntity createShopRefundProtocol(CashboxWorkplace workplace, UserEntity operator, CashboxExchangeTotalsEntity totals) {
        List<CreateDocumentParameter> documentParameters = new ArrayList<>();
        documentParameters.add(new CreateDocumentParameter(createCooperativeParticipant(workplace.getCooperative()), NO_FIELDS));
        documentParameters.add(new CreateDocumentParameter(createCommunityParticipant(workplace.getShop()), Collections.singletonList(
                UserFieldValueBuilder.createStringValue("PAYMENT", formatAmountForDocument(totals.getTotalWholesaleAmount())))));
        documentParameters.add(new CreateDocumentParameter(createSharerDelegateForCooperative(operator), NO_FIELDS));
        return createDocument("community_currency_refund_protocol_with_lof", documentParameters, operator);
    }

    private ParticipantCreateDocumentParameter createSharerParticipant(UserEntity userEntity) {
        return new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(),
                userEntity.getId(), "Пайщик (физическое лицо) Потребительского Общества");
    }

    private ParticipantCreateDocumentParameter createCommunityParticipant(CommunityEntity community) {
        return new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                community.getId(), "Пайщик (юридическое лицо) Потребительского Общества");
    }

    private ParticipantCreateDocumentParameter createSharerDelegateForCooperative(UserEntity operator) {
        return new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(),
                operator.getId(), "Представитель (физическое лицо) Потребительского Общества");
    }

    private ParticipantCreateDocumentParameter createSharerDelegateForCommunity(UserEntity operator) {
        return new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(),
                operator.getId(), "Представитель (физическое лицо) пайщика (юридического лица) Потребительского Общества");
    }

    private ParticipantCreateDocumentParameter createCooperativeParticipant(CommunityEntity community) {
        return new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(),
                community.getId(), "Потребительское Общество");
    }

    private DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> documentParameters, UserEntity creator) {
        return documentService.createDocument(templateCode, documentParameters, creator.getId(), NO_EVENTS, DO_NOT_NOTIFY_ABOUT_SIGN);
    }

    private String formatBasketForDocument(Basket basket, Long shop) {
        StringBuilder basketStringBuilder = new StringBuilder();
        basketStringBuilder.append("<ol>");

        for (int i = 0; i < basket.getItem().size(); i++) {
            Basket.Item item = basket.getItem().get(i);
            AdvisorProduct product = ecoAdvisorService.getProductByCodeAndCommunityId(item.getCode(), shop);
            BigDecimal count = item.getCount().divide(product.getCount(), 2, RoundingMode.HALF_UP);
            String money = DECIMAL_FORMAT.format(product.getWholesalePrice().multiply(count));

            basketStringBuilder.append("<li><strong>");
            basketStringBuilder.append(Padeg.getOfficePadeg(item.getName(), PadegConstants.PADEG_T));
            basketStringBuilder.append(", стоимостью ");
            basketStringBuilder.append(money);
            basketStringBuilder.append(" (").append(HumansStringUtils.money2string(money.replace(",", "."), "RUR")).append(")");
            basketStringBuilder.append("</strong></li>");
        }

        basketStringBuilder.append("</ol>");
        return basketStringBuilder.toString();
    }

    private String formatAmountForDocument(BigDecimal amount) {
        StringBuilder paymentStringBuilder = new StringBuilder();
        String money = DECIMAL_FORMAT.format(amount);

        paymentStringBuilder.append("<ol><li><strong>");
        paymentStringBuilder.append("денежными средствами в размере ");
        paymentStringBuilder.append(money);
        paymentStringBuilder.append(" (").append(HumansStringUtils.money2string(money.replace(",", "."), "RUR")).append(")");
        paymentStringBuilder.append("</strong></li></ol>");

        return paymentStringBuilder.toString();
    }
}
