package ru.radom.kabinet.services;

import ru.askor.blagosfera.domain.cashbox.*;
import ru.askor.blagosfera.domain.xml.cashbox.Basket;
import ru.askor.blagosfera.domain.xml.cashbox.ImportProductsRequest;
import ru.askor.blagosfera.domain.xml.cashbox.Payment;
import ru.askor.blagosfera.domain.xml.cashbox.UpdatePricesRequest;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CashboxService {

    CashboxOperatorSession createSession(UserEntity operator, String workplaceId) throws CashboxException;

    CashboxOperatorSession stopSession(UserEntity operator, String workplaceId) throws CashboxException;

    CashboxOperatorSession checkSession(String workplaceId) throws CashboxException;

    String checkOperatorSession(UserEntity operator, String workplaceId) throws CashboxException;

    UserEntity identifySharer(String ikp) throws CashboxException;

    boolean isCommunityMember(CommunityEntity community, UserEntity userEntity);

    CashboxRegisterShareholder findMemberRegistrationEntry(String workplaceId, UserEntity userEntity) throws CashboxException;

    void registerSharer(UserEntity operator, String workplaceId, String sharerIkp) throws CashboxException;

    void acceptSharerRegistration(UserEntity operator, String workplaceId, String sharerIkp) throws CashboxException;

    String exchange(UserEntity operator, String workplaceId, UserEntity userEntity, Basket basket, Payment payment) throws CashboxException;

    CashboxExchangeProtocols acceptExchange(UserEntity operator, String workplaceId, String requestId) throws CashboxException;

    void importProducts(long shop, ImportProductsRequest.Products products) throws CashboxException;

    void updatePrices(long shop, UpdatePricesRequest.Products products) throws CashboxException;

    CashboxWorkplace findWorkplace(String workplaceId) throws CashboxException;

    int getSessionDurationMax();

    BigDecimal getAffiliationFee(CashboxWorkplace workplace);

    BigDecimal getMinShareAmount(CashboxWorkplace workplace);

    List<CashboxWorkplaceDto> getWorkplaces(Long communityId);

    CashboxOperatorSessionsResponseDto getSessions(Long communityId, List<Long> workplaceIds,
                                                   int page, int size, String sortDirection, String sortColumn,
                                                   String operator, Date createdDateFrom, Date createdDateTo, String active);

    CashboxExchangeOperationsResponseDto getOperations(Long communityId, Long sessionId,
                                                       int page, int size, String sortDirection, String sortColumn);

    CashboxExchangeProductsResponseDto getProducts(Long communityId, List<Long> exchangeIds,
                                                   int page, int size, String sortDirection, String sortColumn);

    CashboxOperatorSessionDto closeOperatorSession(Long communityId, Long sessionId);

    StoreResponseDto getProductsFromStore(Long communityId, int page, int size, String sortDirection, String sortColumn, Long productGroupId);
}
