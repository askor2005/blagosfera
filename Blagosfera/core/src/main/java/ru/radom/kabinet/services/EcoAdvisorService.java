package ru.radom.kabinet.services;

import org.springframework.data.domain.Page;
import ru.askor.blagosfera.domain.cashbox.CashboxException;
import ru.askor.blagosfera.domain.cashbox.CashboxExchangeTotals;
import ru.askor.blagosfera.domain.cashbox.CashboxWorkplace;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusAllocation;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorProduct;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorSystemParameters;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.xml.cashbox.Basket;
import ru.askor.blagosfera.domain.xml.cashbox.ImportProductsRequest;
import ru.askor.blagosfera.domain.xml.cashbox.Payment;
import ru.askor.blagosfera.domain.xml.cashbox.UpdatePricesRequest;
import ru.radom.kabinet.model.ecoadvisor.AdvisorParametersEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductEntity;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.AdvisorSettingsDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.BonusAllocationDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.CommunityDto;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.ProductGroupDto;

import java.math.BigDecimal;
import java.util.List;

public interface EcoAdvisorService {

    String ECO_ADVISOR_USER_PERMISSION = "ECO_ADVISOR_USER";

    CashboxExchangeTotals calculateExchangeTotals(Basket basket, Payment payment, CashboxWorkplace workplace) throws CashboxException;

    void importProducts(long shop, ImportProductsRequest.Products products) throws CashboxException;

    AdvisorParametersEntity getDefaultAdvisorParameters() throws CashboxException;

    AdvisorParametersEntity getAdvisorParameters(long communityId) throws CashboxException;

    AdvisorParametersEntity getAdvisorParameters(long communityId, boolean createIfNotExists) throws CashboxException;

    void updatePrices(long shop, UpdatePricesRequest.Products products) throws CashboxException;

    AdvisorProduct getProductByCodeAndCommunityId(String code, long communityId);

    BigDecimal divide(BigDecimal dividend, BigDecimal divisor, boolean bank);

    BigDecimal divide(BigDecimal dividend, BigDecimal divisor);

    BigDecimal percents(BigDecimal value, BigDecimal percents);

    BigDecimal withPercents(BigDecimal value, BigDecimal percents);

    BigDecimal withoutPercents(BigDecimal value, BigDecimal percents);

    AdvisorSystemParameters getSystemParameters();

    List<CommunityDto> getCommunities(User user);

    List<CommunityDto> getSubgroups(User user, Long parentId);

    CommunityDto getCommunity(Long communityId);

    Page<AdvisorProductEntity> getProductsFromStore(Long communityId, int page, int size, String sortDirection, String sortColumn, Long productGroupId);

    List<AdvisorBonusAllocation> getBonusAllocationDtos(Long communityId) throws CashboxException;

    void saveBonusAllocations(Long communityId, List<BonusAllocationDto> bonusAllocationDtos) throws CashboxException;

    AdvisorSettingsDto getAdvisorSettings(Long communityId, Long productGroupId) throws CashboxException;

    void saveAdvisorSettings(Long communityId, AdvisorSettingsDto advisorSettingsDto, Long productGroupId) throws CashboxException;

    List<ProductGroupDto> getProductGroups(Long communityId) throws CashboxException;

    void deleteProductGroup(Long communityId, Long groupId);

    ProductGroupDto saveProductGroup(Long communityId, ProductGroupDto group) throws CashboxException;

    void setProductGroup(Long communityId, Long groupId, List<Long> productIds);

    void resetProductGroup(Long communityId, List<Long> productIds);
}
