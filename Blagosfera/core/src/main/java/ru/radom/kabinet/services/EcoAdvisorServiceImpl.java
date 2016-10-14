package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import padeg.lib.Padeg;
import ru.askor.blagosfera.data.jpa.repositories.cashbox.CashboxProductRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.ecoadvisor.AdvisorParametersRepository;
import ru.askor.blagosfera.data.jpa.repositories.ecoadvisor.AdvisorProductGroupRepository;
import ru.askor.blagosfera.data.jpa.repositories.ecoadvisor.AdvisorProductRepository;
import ru.askor.blagosfera.data.jpa.repositories.ecoadvisor.AdvisorSystemParametersRepository;
import ru.askor.blagosfera.domain.cashbox.CashboxBasketItem;
import ru.askor.blagosfera.domain.cashbox.CashboxException;
import ru.askor.blagosfera.domain.cashbox.CashboxExchangeTotals;
import ru.askor.blagosfera.domain.cashbox.CashboxWorkplace;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusAllocation;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusReceiverType;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorProduct;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorSystemParameters;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.xml.cashbox.*;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.model.cashbox.CashboxProductEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorBonusAllocationEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorParametersEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductGroupEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.PadegConstants;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.AdvisorSettingsDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.BonusAllocationDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.CommunityDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.ProductGroupDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service("ecoAdvisor")
@Transactional
public class EcoAdvisorServiceImpl implements EcoAdvisorService {

    @Autowired
    private AdvisorParametersRepository advisorParametersRepository;

    @Autowired
    private AdvisorProductRepository advisorProductRepository;

    @Autowired
    private CashboxProductRepository cashboxProductRepository;

    @Autowired
    private AdvisorSystemParametersRepository advisorSystemParametersRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private AdvisorProductGroupRepository productGroupRepository;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private FieldValueDao fieldValueDao;

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public EcoAdvisorServiceImpl() {
    }

    @Override
    public CashboxExchangeTotals calculateExchangeTotals(Basket basket, Payment payment, CashboxWorkplace workplace) throws CashboxException {
        Payment.Cash cash = payment.getCash();

        AdvisorParametersEntity advisorParametersEntity = getAdvisorParameters(workplace.getShop().getId(), false);

        if (advisorParametersEntity == null)
            throw new CashboxException("Не заданы параметры ЭКС.");

        BigDecimal totalWholesaleAmount = BigDecimal.ZERO;                                                            // паевой взнос
        BigDecimal membershipFee;                                                                                     // членский взнос
        BigDecimal totalFinalAmount = BigDecimal.ZERO;                                                                // общий взнос
        BigDecimal paymentAmount = cash != null ? divide(cash.getAmount().getValue(), ONE_HUNDRED) : BigDecimal.ZERO; // полученная сумма
        BigDecimal changeAmount = BigDecimal.ZERO;                                                                    // сдача
        BigDecimal totalMargin = BigDecimal.ZERO;                                                                     // наценка (доход)
        BigDecimal totalProfit = BigDecimal.ZERO;                                                                     // прибыль

        List<CashboxBasketItem> basketItems = new ArrayList<>();

        for (Basket.Item item : basket.getItem()) {
            CashboxProductEntity cashboxProductEntity = cashboxProductRepository.findOneByCodeAndShop(item.getCode(), workplace.getShop().getId());
            AdvisorProductEntity advisorProductEntity = advisorProductRepository.findOneByCodeAndParameters_CommunityId(item.getCode(), workplace.getShop().getId());

            if ((cashboxProductEntity == null) || (advisorProductEntity == null)) {
                // TODO Переделать на доменную модель объединения
                String cooperativeDepartmentFullName = null;
                FieldValueEntity cooperativeDepartmentFullNameFV = fieldValueDao.get(workplace.getCooperativeDepartment(), FieldConstants.COMMUNITY_FULL_RU_NAME);
                if (cooperativeDepartmentFullNameFV != null) {
                    cooperativeDepartmentFullName = FieldsService.getFieldStringValue(cooperativeDepartmentFullNameFV);
                }

                String cooperativeFullName = null;
                FieldValueEntity cooperativeFullNameFV = fieldValueDao.get(workplace.getCooperative(), FieldConstants.COMMUNITY_FULL_RU_NAME);
                if (cooperativeFullNameFV != null) {
                    cooperativeFullName = FieldsService.getFieldStringValue(cooperativeFullNameFV);
                }

                throw new CashboxException("Продукт \"" + item.getName() + "\" с кодом \"" + item.getCode() + "\" не найден в номенклатуре ЭКС " +
                        Padeg.getOfficePadeg(cooperativeDepartmentFullName, PadegConstants.PADEG_R) + " " +
                        Padeg.getOfficePadeg(cooperativeFullName, PadegConstants.PADEG_R) + ". " +
                        "Произведите синхронизацию каталога товаров с Торговым Предприятием."
                );
            }

            BigDecimal count = item.getCoefficient(); // количество товара
            BigDecimal wholesaleAmount = advisorProductEntity.getWholesalePriceWithVat();
            BigDecimal finalAmount = advisorProductEntity.getFinalPriceWithVat();
            BigDecimal margin = advisorProductEntity.getMargin(); // наценка или доход
            BigDecimal profit = percents(margin, ONE_HUNDRED.subtract(advisorParametersEntity.getGeneralRunningCosts())); // прибыль равна доход - ОХР
            profit = withoutPercents(profit, advisorParametersEntity.getTaxOnProfits()); // вычитаем налог на прибыль

            totalWholesaleAmount = totalWholesaleAmount.add(wholesaleAmount.multiply(count));
            totalFinalAmount = totalFinalAmount.add(finalAmount.multiply(count));
            totalMargin = totalMargin.add(margin.multiply(count));
            totalProfit = totalProfit.add(profit.multiply(count));

            basketItems.add(new CashboxBasketItem(null, item.getName(), item.getCode(),
                    count, advisorProductEntity.getCount(), advisorProductEntity.getUnitOfMeasure(),
                    advisorProductEntity.getWholesalePrice(), advisorProductEntity.getWholesalePriceWithVat(), advisorProductEntity.getWholesaleCurrency(),
                    advisorProductEntity.getFinalPrice(), advisorProductEntity.getFinalPriceWithVat(), advisorProductEntity.getFinalCurrency(),
                    advisorProductEntity.getVat()));
        }

        if ((cash != null) && totalFinalAmount.compareTo(paymentAmount) > 0)
            throw new CashboxException("Внесено недостаточно средств. "
                    + "Минимальная сумма " + DECIMAL_FORMAT.format(totalFinalAmount)
                    + ". Внесено " + DECIMAL_FORMAT.format(paymentAmount));

        membershipFee = totalFinalAmount.subtract(totalWholesaleAmount);

        if ((cash != null) && payment.getCash().isGetChange()) {
            changeAmount = paymentAmount.subtract(totalFinalAmount);
            paymentAmount = paymentAmount.subtract(changeAmount);
        }

        CashboxExchangeTotals exchangeTotals = new CashboxExchangeTotals(totalWholesaleAmount, membershipFee, totalFinalAmount,
                paymentAmount, changeAmount, totalMargin, totalProfit, cash != null);
        exchangeTotals.getBasketItems().addAll(basketItems);
        return exchangeTotals;
    }

    private void setWholesalePrice(ImportProductsRequest.Products.Product product, AdvisorProductEntity advisorProductEntity) {
        // НДС
        BigDecimal vat = product.getVat();
        // ???
        //BigDecimal antiVat = BigDecimal.ONE.subtract(divide(vat, ONE_HUNDRED));
        //  Оптовая цена
        BigDecimal wholesalePrice = divide(product.getWholesalePrice().getValue(), ONE_HUNDRED);

        // TODO Странная старая логика
        /*
        if (product.getWholesalePrice().isWithVat()) {
            advisorProductEntity.setWholesalePrice(withoutPercents(wholesalePrice, vat));
            advisorProductEntity.setWholesalePriceWithVat(wholesalePrice);
        } else {
            advisorProductEntity.setWholesalePrice(wholesalePrice);
            if (antiVat.compareTo(BigDecimal.ZERO) == 0) {
                advisorProductEntity.setWholesalePriceWithVat(wholesalePrice);
            } else {
                advisorProductEntity.setWholesalePriceWithVat(divide(wholesalePrice, antiVat));
            }
        }
        */

        if (product.getWholesalePrice().isWithVat()) {
            advisorProductEntity.setWholesalePrice(divide(wholesalePrice, BigDecimal.ONE.add(divide(vat, ONE_HUNDRED))));
            advisorProductEntity.setWholesalePriceWithVat(wholesalePrice);
        } else {
            advisorProductEntity.setWholesalePrice(wholesalePrice);
            advisorProductEntity.setWholesalePriceWithVat(wholesalePrice.add(wholesalePrice.multiply(divide(vat, ONE_HUNDRED))));
        }
    }

    private void setFinalPrice(BigDecimal vat, Price finalPrice, AdvisorProductEntity advisorProductEntity) {
        // ???
        //BigDecimal antiVat = BigDecimal.ONE.subtract(divide(vat, ONE_HUNDRED));
        // Розничная цена
        BigDecimal finalPriceVal = divide(finalPrice.getValue(), ONE_HUNDRED);

        if (finalPrice.isWithVat()) {
            advisorProductEntity.setFinalPrice(divide(finalPriceVal, BigDecimal.ONE.add(divide(vat, ONE_HUNDRED))));
            //advisorProductEntity.setFinalPrice(withoutPercents(finalPriceVal, vat));
            advisorProductEntity.setFinalPriceWithVat(finalPriceVal);
        } else {
            advisorProductEntity.setFinalPrice(finalPriceVal);
            //advisorProductEntity.setFinalPriceWithVat(divide(finalPriceVal, antiVat));
            advisorProductEntity.setFinalPriceWithVat(finalPriceVal.add(finalPriceVal.multiply(divide(vat, ONE_HUNDRED))));
        }
    }

    @Override
    public void importProducts(long shop, ImportProductsRequest.Products products) throws CashboxException {
        Date currentDate = new Date();

        try {
            cashboxProductRepository.deleteByShop(shop);

            AdvisorParametersEntity advisorParametersEntity = getAdvisorParameters(shop);

            for (ImportProductsRequest.Products.Product product : products.getProduct()) {
                AdvisorProductEntity advisorProductEntity = advisorProductRepository.findOneByCodeAndParameters_CommunityId(product.getCode(), shop);

                if (advisorProductEntity == null) {
                    advisorProductEntity = new AdvisorProductEntity();
                    advisorProductEntity.setCreatedDate(currentDate);
                }
                //  Оптовая цена
                BigDecimal wholesalePrice = divide(product.getWholesalePrice().getValue(), ONE_HUNDRED);
                // Розничная цена
                BigDecimal finalPrice = divide(product.getFinalPrice().getValue(), ONE_HUNDRED);
                // НДС
                BigDecimal vat = product.getVat();
                // ???
                BigDecimal antiVat = BigDecimal.ONE.subtract(divide(vat, ONE_HUNDRED));

                /*if (product.getWholesalePrice().isWithVat()) {
                    advisorProductEntity.setWholesalePrice(withoutPercents(wholesalePrice, vat));
                    advisorProductEntity.setWholesalePriceWithVat(wholesalePrice);
                } else {
                    advisorProductEntity.setWholesalePrice(wholesalePrice);
                    advisorProductEntity.setWholesalePriceWithVat(divide(wholesalePrice, antiVat));
                }*/

                setWholesalePrice(product, advisorProductEntity);
                setFinalPrice(vat, product.getFinalPrice(), advisorProductEntity);
                /*if (product.getFinalPrice().isWithVat()) {
                    advisorProductEntity.setFinalPrice(withoutPercents(finalPrice, vat));
                    advisorProductEntity.setFinalPriceWithVat(finalPrice);
                } else {
                    advisorProductEntity.setFinalPrice(finalPrice);
                    advisorProductEntity.setFinalPriceWithVat(divide(finalPrice, antiVat));
                }*/

                advisorProductEntity.setName(product.getName());
                advisorProductEntity.setCode(product.getCode());
                advisorProductEntity.setCount(product.getCount());
                advisorProductEntity.setUnitOfMeasure(product.getUnitOfMeasure());
                advisorProductEntity.setWholesaleCurrency(product.getWholesalePrice().getCurrency());
                advisorProductEntity.setFinalCurrency(product.getFinalPrice().getCurrency());
                advisorProductEntity.setVat(product.getVat());
                advisorProductEntity.setUpdatedDate(currentDate);
                advisorProductEntity.setParameters(advisorParametersEntity);
                updateMargin(advisorProductEntity);
                advisorProductRepository.save(advisorProductEntity);

                advisorParametersEntity.getProducts().add(advisorProductEntity);

                CashboxProductEntity cashboxProductEntity = new CashboxProductEntity();
                cashboxProductEntity.setCode(product.getCode());
                cashboxProductEntity.setShop(shop);
                cashboxProductRepository.save(cashboxProductEntity);
            }
        } catch (Throwable e) {
            throw new CashboxException(e.getMessage(), e);
        }
    }

    @Override
    public AdvisorParametersEntity getDefaultAdvisorParameters() throws CashboxException {
        return advisorParametersRepository.findDefault();
    }

    @Override
    public AdvisorParametersEntity getAdvisorParameters(long communityId) throws CashboxException {
        return getAdvisorParameters(communityId, true);
    }

    @Override
    public AdvisorParametersEntity getAdvisorParameters(long communityId, boolean createIfNotExists) throws CashboxException {
        AdvisorParametersEntity advisorParametersEntity = advisorParametersRepository.findOneByCommunityId(communityId);

        if ((advisorParametersEntity == null) && createIfNotExists) {
            AdvisorParametersEntity defaultAdvisorParametersEntity = getDefaultAdvisorParameters();

            if (defaultAdvisorParametersEntity == null)
                throw new CashboxException("Параметры ЭКС по-умолчанию не найдены.");

            advisorParametersEntity = new AdvisorParametersEntity(defaultAdvisorParametersEntity);
            advisorParametersEntity.setCommunityId(communityId);
        }

        if (advisorParametersEntity.getBonusAllocations().size() != AdvisorBonusReceiverType.values().length) {
            advisorParametersEntity.getBonusAllocations().clear();

            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.USER_ACCOUNT);
            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.USER_SHAREBOOK);
            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.COMMUNITY_ACCOUNT);
            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.COMMUNITY_SHAREBOOK);
            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.CONSUMER_SHAREBOOK);
            createAllocationEntity(advisorParametersEntity).setReceiverType(AdvisorBonusReceiverType.SYSTEM);

            advisorParametersEntity = advisorParametersRepository.save(advisorParametersEntity);
        }

        return advisorParametersEntity;
    }

    private AdvisorBonusAllocationEntity createAllocationEntity(AdvisorParametersEntity advisorParametersEntity) {
        AdvisorBonusAllocationEntity bonusAllocationEntity = new AdvisorBonusAllocationEntity();
        bonusAllocationEntity.setParameters(advisorParametersEntity);
        bonusAllocationEntity.setAllocationPercent(BigDecimal.ZERO);
        bonusAllocationEntity.setReceiverType(AdvisorBonusReceiverType.USER_ACCOUNT);
        advisorParametersEntity.getBonusAllocations().add(bonusAllocationEntity);
        return bonusAllocationEntity;
    }

    @Override
    public void updatePrices(long shop, UpdatePricesRequest.Products products) throws CashboxException {
        try {
            for (UpdatePricesRequest.Products.Product product : products.getProduct()) {
                CashboxProductEntity cashboxProductEntity = cashboxProductRepository.findOneByCodeAndShop(product.getCode(), shop);

                if (cashboxProductEntity == null)
                    throw new CashboxException("Продукт с кодом \"" + product.getCode() + "\" не найден.");

                AdvisorProductEntity advisorProductEntity = advisorProductRepository.findOneByCodeAndParameters_CommunityId(product.getCode(), shop);

                if (advisorProductEntity == null)
                    throw new CashboxException("Продукт с кодом \"" + product.getCode() + "\" не найден в базе ЭКС.");

                advisorProductEntity.setFinalCurrency(product.getFinalPrice().getCurrency());

                //BigDecimal finalPrice = divide(product.getFinalPrice().getValue(), ONE_HUNDRED);
                BigDecimal vat = advisorProductEntity.getVat();
                //BigDecimal antiVat = BigDecimal.ONE.subtract(divide(vat, ONE_HUNDRED));

                setFinalPrice(vat, product.getFinalPrice(), advisorProductEntity);
                /*if (product.getFinalPrice().isWithVat()) {
                    advisorProductEntity.setFinalPrice(withoutPercents(finalPrice, vat));
                    advisorProductEntity.setFinalPriceWithVat(finalPrice);
                } else {
                    advisorProductEntity.setFinalPrice(finalPrice);
                    advisorProductEntity.setFinalPriceWithVat(divide(finalPrice, antiVat));
                }*/

                updateMargin(advisorProductEntity);
                cashboxProductRepository.save(cashboxProductEntity);
            }
        } catch (Throwable e) {
            throw new CashboxException(e.getMessage(), e);
        }
    }

    @Override
    public AdvisorProduct getProductByCodeAndCommunityId(String code, long communityId) {
        return advisorProductRepository.findOneByCodeAndParameters_CommunityId(code, communityId).toDomain();
    }

    private void updateMargin(AdvisorProductEntity product) {
        BigDecimal finalPriceWithVat = product.getFinalPriceWithVat();
        BigDecimal wholesalePriceWithVat = product.getWholesalePriceWithVat();
        BigDecimal margin = finalPriceWithVat.subtract(wholesalePriceWithVat);
        product.setMargin(margin);
        product.setMarginPercentage(divide(margin, wholesalePriceWithVat).multiply(ONE_HUNDRED));
    }

    public BigDecimal divide(BigDecimal dividend, BigDecimal divisor, boolean bank) {
        return dividend.divide(divisor, 2, bank ? RoundingMode.HALF_EVEN : RoundingMode.CEILING);
    }

    public BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return divide(dividend, divisor, false);
    }

    public BigDecimal percents(BigDecimal value, BigDecimal percents) {
        return divide(value.multiply(percents), ONE_HUNDRED);
    }

    public BigDecimal withPercents(BigDecimal value, BigDecimal percents) {
        return value.add(percents(value, percents));
    }

    public BigDecimal withoutPercents(BigDecimal value, BigDecimal percents) {
        return value.subtract(divide(value.multiply(percents), ONE_HUNDRED.add(percents)));
    }

    @Override
    public AdvisorSystemParameters getSystemParameters() {
        return advisorSystemParametersRepository.findOne(0L).toDomain();
    }

    @Override
    public List<CommunityDto> getCommunities(User user) {
        List<CommunityDto> result = new ArrayList<>();
        List<Community> communities = communityDomainService.findCommunitiesByUserPermission(user, ECO_ADVISOR_USER_PERMISSION);

        for (Community community : communities) {
            result.add(new CommunityDto(community, false));
        }

        return result;
    }

    @Override
    public List<CommunityDto> getSubgroups(User user, Long parentId) {
        List<CommunityDto> result = new ArrayList<>();
        List<Community> communities = communityDomainService.findCommunitiesByParentAndUserPermission(user, ECO_ADVISOR_USER_PERMISSION, parentId);

        for (Community community : communities) {
            result.add(new CommunityDto(community, false));
        }

        return result;
    }

    @Override
    public CommunityDto getCommunity(Long communityId) {
        Community community = communityDomainService.getByIdFullData(communityId);
        return new CommunityDto(community);
    }

    @Override
    public Page<AdvisorProductEntity> getProductsFromStore(Long communityId, int page, int size, String sortDirection, String sortColumn, Long productGroupId) {
        PageRequest pageRequest;

        if (sortDirection == null || sortColumn == null) {
            pageRequest = new PageRequest(page, size);
        } else if (sortDirection.equals("asc")) {
            pageRequest = new PageRequest(page, size, Sort.Direction.ASC, sortColumn);
        } else {
            pageRequest = new PageRequest(page, size, Sort.Direction.DESC, sortColumn);
        }

        if (productGroupId != null) {
            return advisorProductRepository.findByGroup_IdAndParameters_CommunityId(productGroupId, communityId, pageRequest);
        } else {
            return advisorProductRepository.findByParameters_CommunityId(communityId, pageRequest);
        }
    }

    @Override
    public List<AdvisorBonusAllocation> getBonusAllocationDtos(Long communityId) throws CashboxException {
        Set<AdvisorBonusAllocationEntity> bonusAllocations = getAdvisorParameters(communityId).getBonusAllocations();
        List<AdvisorBonusAllocation> result = new ArrayList<>();

        for (AdvisorBonusAllocationEntity bonusAllocation : bonusAllocations) {
            result.add(bonusAllocation.toDomain());
        }

        return result;
    }

    @Override
    public void saveBonusAllocations(Long communityId, List<BonusAllocationDto> bonusAllocationDtos) throws CashboxException {
        AdvisorParametersEntity advisorParameters = getAdvisorParameters(communityId, false);
        Set<AdvisorBonusAllocationEntity> bonusAllocations = advisorParameters.getBonusAllocations();

        for (AdvisorBonusAllocationEntity bonusAllocation : bonusAllocations) {
            int count = bonusAllocationDtos.size();

            for (int i = count - 1; i >= 0; i--) {
                BonusAllocationDto allocationDto = bonusAllocationDtos.get(i);

                if ((allocationDto.id != null) && allocationDto.id.equals(bonusAllocation.getId())) {
                    bonusAllocation.setAllocationPercent(allocationDto.allocationPercent);
                    break;
                }
            }
        }

        advisorParametersRepository.save(advisorParameters);
    }

    @Override
    public AdvisorSettingsDto getAdvisorSettings(Long communityId, Long productGroupId) throws CashboxException {
        AdvisorParametersEntity advisorParameters = getAdvisorParameters(communityId);
        AdvisorSettingsDto responseDto = new AdvisorSettingsDto();

        if (productGroupId != null) {
            for (AdvisorProductGroupEntity productGroup : advisorParameters.getProductGroups()) {
                if (productGroup.getId().equals(productGroupId)) {
                    responseDto.generalRunningCosts = productGroup.getGeneralRunningCosts();
                    responseDto.wage = productGroup.getWage();
                    responseDto.vat = productGroup.getVat();
                    responseDto.taxOnProfits = productGroup.getTaxOnProfits();
                    responseDto.incomeTax = productGroup.getIncomeTax();
                    responseDto.proprietorshipInterest = productGroup.getProprietorshipInterest();
                    responseDto.taxOnDividends = productGroup.getTaxOnDividends();
                    responseDto.companyProfit = productGroup.getCompanyProfit();
                    responseDto.margin = productGroup.getMargin();
                    responseDto.shareValue = productGroup.getShareValue();
                    responseDto.departmentPart = productGroup.getDepartmentPart();
                    break;
                }
            }
        } else {
            responseDto.generalRunningCosts = advisorParameters.getGeneralRunningCosts();
            responseDto.wage = advisorParameters.getWage();
            responseDto.vat = advisorParameters.getVat();
            responseDto.taxOnProfits = advisorParameters.getTaxOnProfits();
            responseDto.incomeTax = advisorParameters.getIncomeTax();
            responseDto.proprietorshipInterest = advisorParameters.getProprietorshipInterest();
            responseDto.taxOnDividends = advisorParameters.getTaxOnDividends();
            responseDto.companyProfit = advisorParameters.getCompanyProfit();
            responseDto.margin = advisorParameters.getMargin();
            responseDto.shareValue = advisorParameters.getShareValue();
            responseDto.departmentPart = advisorParameters.getDepartmentPart();
        }

        return responseDto;
    }

    @Override
    public void saveAdvisorSettings(Long communityId, AdvisorSettingsDto advisorSettingsDto, Long productGroupId) throws CashboxException {
        if (advisorSettingsDto.generalRunningCosts == null
                || advisorSettingsDto.wage == null
                || advisorSettingsDto.vat == null
                || advisorSettingsDto.taxOnProfits == null
                || advisorSettingsDto.incomeTax == null
                || advisorSettingsDto.proprietorshipInterest == null
                || advisorSettingsDto.taxOnDividends == null
                || advisorSettingsDto.companyProfit == null
                || advisorSettingsDto.margin == null
                || advisorSettingsDto.shareValue == null
                || advisorSettingsDto.departmentPart == null) throw new CashboxException("Неверные параметры");

        AdvisorParametersEntity advisorParameters = getAdvisorParameters(communityId, false);

        if (productGroupId != null) {
            for (AdvisorProductGroupEntity productGroup : advisorParameters.getProductGroups()) {
                if (productGroup.getId().equals(productGroupId)) {
                    productGroup.setGeneralRunningCosts(advisorSettingsDto.generalRunningCosts);
                    productGroup.setWage(advisorSettingsDto.wage);
                    productGroup.setVat(advisorSettingsDto.vat);
                    productGroup.setTaxOnProfits(advisorSettingsDto.taxOnProfits);
                    productGroup.setIncomeTax(advisorSettingsDto.incomeTax);
                    productGroup.setProprietorshipInterest(advisorSettingsDto.proprietorshipInterest);
                    productGroup.setTaxOnDividends(advisorSettingsDto.taxOnDividends);
                    productGroup.setCompanyProfit(advisorSettingsDto.companyProfit);
                    productGroup.setMargin(advisorSettingsDto.margin);
                    productGroup.setShareValue(advisorSettingsDto.shareValue);
                    productGroup.setDepartmentPart(advisorSettingsDto.departmentPart);

                    productGroupRepository.save(productGroup);
                    break;
                }
            }
        } else {
            advisorParameters.setGeneralRunningCosts(advisorSettingsDto.generalRunningCosts);
            advisorParameters.setWage(advisorSettingsDto.wage);
            advisorParameters.setVat(advisorSettingsDto.vat);
            advisorParameters.setTaxOnProfits(advisorSettingsDto.taxOnProfits);
            advisorParameters.setIncomeTax(advisorSettingsDto.incomeTax);
            advisorParameters.setProprietorshipInterest(advisorSettingsDto.proprietorshipInterest);
            advisorParameters.setTaxOnDividends(advisorSettingsDto.taxOnDividends);
            advisorParameters.setCompanyProfit(advisorSettingsDto.companyProfit);
            advisorParameters.setMargin(advisorSettingsDto.margin);
            advisorParameters.setShareValue(advisorSettingsDto.shareValue);
            advisorParameters.setDepartmentPart(advisorSettingsDto.departmentPart);

            advisorParametersRepository.save(advisorParameters);
        }
    }

    @Override
    public List<ProductGroupDto> getProductGroups(Long communityId) throws CashboxException {
        AdvisorParametersEntity advisorParameters = getAdvisorParameters(communityId);
        List<ProductGroupDto> result = new ArrayList<>();

        for(AdvisorProductGroupEntity groupEntity : advisorParameters.getProductGroups()) {
            result.add(new ProductGroupDto(groupEntity));
        }

        return result;
    }

    @Override
    public void deleteProductGroup(Long communityId, Long groupId) {
        productGroupRepository.delete(groupId);
    }

    @Override
    public ProductGroupDto saveProductGroup(Long communityId, ProductGroupDto group) throws CashboxException {
        AdvisorParametersEntity advisorParameters = getAdvisorParameters(communityId);
        AdvisorProductGroupEntity groupEntity;

        if (group.id != null) {
            groupEntity = productGroupRepository.findOne(group.id);
        } else {
            groupEntity = new AdvisorProductGroupEntity();
            groupEntity.setParameters(advisorParameters);
        }

        groupEntity.setName(group.name);
        groupEntity = productGroupRepository.save(groupEntity);
        return new ProductGroupDto(groupEntity);
    }

    @Override
    public void setProductGroup(Long communityId, Long groupId, List<Long> productIds) {
        AdvisorProductGroupEntity groupEntity = productGroupRepository.findOne(groupId);

        for (Long productId : productIds) {
            AdvisorProductEntity productEntity = advisorProductRepository.findOneByIdAndParameters_CommunityId(productId, communityId);
            productEntity.setGroup(groupEntity);
            advisorProductRepository.save(productEntity);
        }
    }

    @Override
    public void resetProductGroup(Long communityId, List<Long> productIds) {
        for (Long productId : productIds) {
            AdvisorProductEntity productEntity = advisorProductRepository.findOneByIdAndParameters_CommunityId(productId, communityId);
            productEntity.setGroup(null);
            advisorProductRepository.save(productEntity);
        }
    }
}
