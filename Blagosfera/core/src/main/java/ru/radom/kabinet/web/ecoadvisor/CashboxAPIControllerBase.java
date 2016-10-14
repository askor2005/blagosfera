package ru.radom.kabinet.web.ecoadvisor;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import padeg.lib.Padeg;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.TLSConstants;
import ru.askor.blagosfera.crypt.tls.trust.DNBasedTrustManager;
import ru.askor.blagosfera.domain.cashbox.*;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.bio.FingerToken;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.security.bio.TokenIkpProtected;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.CashboxService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.finger.FingerService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.PadegConstants;
import ru.radom.kabinet.web.finger.dto.InitByIkpRequestDto;
import ru.askor.blagosfera.domain.xml.cashbox.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class CashboxAPIControllerBase {

    private final ObjectFactory cashboxObjectFactory;

    @Autowired
    private CashboxService cashboxService;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private FingerService fingerService;

    @Autowired
    private FieldValueDao fieldValueDao;

    public CashboxAPIControllerBase() {
        cashboxObjectFactory = new ObjectFactory();
    }

    @TokenIkpProtected
    @RequestMapping(value = "operatorstart", method = RequestMethod.POST)
    public OperatorStartResponse operatorstart(@RequestBody OperatorStartRequest request) {
        OperatorStartResponse response = cashboxObjectFactory.createOperatorStartResponse();

        try {
            cashboxService.createSession(radomRequestContext.getTempUserEntity(), request.getWorkplaceId());
            response.setStatus(createAcceptedStatus());
            //throw new CashboxException("Это сообщение об ошибке пришло с сервера Системы БЛАГОСФЕРА");
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    @TokenIkpProtected
    @RequestMapping(value = "operatorstop", method = RequestMethod.POST)
    public OperatorStopResponse operatorstop(@RequestBody OperatorStopRequest request) {
        OperatorStopResponse response = cashboxObjectFactory.createOperatorStopResponse();

        try {
            CashboxOperatorSession session = cashboxService.stopSession(radomRequestContext.getTempUserEntity(), request.getWorkplaceId());

            if (session != null) {
                response.setDuration(createDurationBetween(session.getCreatedDate(), session.getEndDate()));

                Money total = cashboxObjectFactory.createMoney();
                total.setCurrency("RUR");
                total.setValue(session.getExchangesTotal());

                response.setExchangesTotal(total);
                response.setExchangesCount(session.getExchangesCount());

                OperatorStopResponse.Products products = cashboxObjectFactory.createOperatorStopResponseProducts();
                response.setProducts(products);

                for (Map<String, Object> basketItem : session.getBasketItems()) {
                    Money amount = cashboxObjectFactory.createMoney();
                    amount.setCurrency("RUR");
                    amount.setValue((BigDecimal) basketItem.get("amount"));

                    OperatorStopResponse.Products.Product product = new OperatorStopResponse.Products.Product();
                    product.setCode((String) basketItem.get("code"));
                    product.setName((String) basketItem.get("name"));
                    product.setCount((BigDecimal) basketItem.get("count"));
                    product.setFinalPrice(amount);

                    products.getProduct().add(product);
                }
            }

            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    //@DigestProtected
    @RequestMapping(value = "sessioncheck", method = RequestMethod.POST)
    public SessionCheckResponse sessioncheck(@RequestBody SessionCheckRequest request) {
        SessionCheckResponse response = cashboxObjectFactory.createSessionCheckResponse();

        try {
            CashboxOperatorSession session = cashboxService.checkSession(request.getWorkplaceId());

            // TODO Переделать на доменную модель объединения
            //session.getWorkplace().getShop()
            String cooperativeDepartmentFullName = null;
            FieldValueEntity cooperativeDepartmentFullNameFV = fieldValueDao.get(session.getWorkplace().getCooperativeDepartment(), FieldConstants.COMMUNITY_FULL_RU_NAME);
            if (cooperativeDepartmentFullNameFV != null) {
                cooperativeDepartmentFullName = FieldsService.getFieldStringValue(cooperativeDepartmentFullNameFV);
            }

            String cooperativeFullName = null;
            FieldValueEntity cooperativeFullNameFV = fieldValueDao.get(session.getWorkplace().getCooperative(), FieldConstants.COMMUNITY_FULL_RU_NAME);
            if (cooperativeFullNameFV != null) {
                cooperativeFullName = FieldsService.getFieldStringValue(cooperativeFullNameFV);
            }

            String shopFullName = null;
            FieldValueEntity shopFullNameFV = fieldValueDao.get(session.getWorkplace().getShop(), FieldConstants.COMMUNITY_FULL_RU_NAME);
            if (shopFullNameFV != null) {
                shopFullName = FieldsService.getFieldStringValue(shopFullNameFV);
            }

            response.setStartDate(dateToXml(session.getCreatedDate()));
            response.setDuration(cashboxService.getSessionDurationMax());
            response.setOperatorIkp(session.getOperator().getIkp());
            response.setOperatorName(session.getOperator().getFullName());
            response.setShopCommunityName(declineOfficialName(shopFullName));
            response.setCooperativeName(declineOfficialName(cooperativeFullName));
            response.setCooperativeDepartmentName(declineOfficialName(cooperativeDepartmentFullName));
            response.setWorkplaceName(session.getWorkplace().getWorkplace().getNumber());
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    //@DigestProtected
    @RequestMapping(value = "identification", method = RequestMethod.POST)
    public IdentificationResponse identification(@RequestBody IdentificationRequest request) {
        IdentificationResponse response = cashboxObjectFactory.createIdentificationResponse();

        try {
            response.setShareHolder(identifySharer(request.getIkp(), request.getWorkplaceId()));

            CashboxOperatorSession session = cashboxService.checkSession(request.getWorkplaceId());

            Money money = cashboxObjectFactory.createMoney();
            money.setValue(cashboxService.getAffiliationFee(session.getWorkplace()));
            money.setCurrency("RUR");
            response.setAffiliationFee(money);

            money = cashboxObjectFactory.createMoney();
            money.setValue(cashboxService.getMinShareAmount(session.getWorkplace()));
            money.setCurrency("RUR");
            response.setShareAmountMin(money);

            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    @TokenIkpProtected
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        RegisterResponse response = cashboxObjectFactory.createRegisterResponse();

        try {
            UserEntity operator = cashboxService.identifySharer(request.getOperatorIkp());
            String operatorIkp = cashboxService.checkOperatorSession(operator, request.getWorkplaceId());
            if (!operatorIkp.equals(request.getOperatorIkp()))
                throw new CashboxException("Активна сессия другого оператора.");
            ShareHolder shareHolder = identifySharer(radomRequestContext.getTempUserEntity().getIkp(), request.getWorkplaceId());
            if (shareHolder.isIsMember()) throw new CashboxException("Пайщик уже зарегистрирован.");
            cashboxService.registerSharer(operator, request.getWorkplaceId(), radomRequestContext.getTempUserEntity().getIkp());
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    @TokenIkpProtected
    @RequestMapping(value = "acceptRegistration", method = RequestMethod.POST)
    public AcceptRegistrationResponse acceptRegistration(@RequestBody AcceptRegistrationRequest request) {
        AcceptRegistrationResponse response = cashboxObjectFactory.createAcceptRegistrationResponse();

        try {
            String operatorIkp = cashboxService.checkOperatorSession(radomRequestContext.getTempUserEntity(), request.getWorkplaceId());
            if (!operatorIkp.equals(request.getOperatorIkp()))
                throw new CashboxException("Активна сессия другого оператора.");
            ShareHolder shareHolder = identifySharer(request.getIkp(), request.getWorkplaceId());
            cashboxService.acceptSharerRegistration(radomRequestContext.getTempUserEntity(), request.getWorkplaceId(), shareHolder.getIkp());
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    @TokenIkpProtected
    @RequestMapping(value = "exchange", method = RequestMethod.POST)
    public ExchangeResponse exchange(@RequestBody ExchangeRequest request) {
        ExchangeResponse response = cashboxObjectFactory.createExchangeResponse();

        try {
            UserEntity operator = cashboxService.identifySharer(request.getOperatorIkp());
            UserEntity userEntity = radomRequestContext.getTempUserEntity();
            String operatorIkp = cashboxService.checkOperatorSession(operator, request.getWorkplaceId());
            if (!operatorIkp.equals(request.getOperatorIkp()))
                throw new CashboxException("Активна сессия другого оператора.");
            response.setRequestId(cashboxService.exchange(operator, request.getWorkplaceId(), userEntity, request.getBasket(), request.getPayment()));
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    @TokenIkpProtected
    @RequestMapping(value = "acceptExchange", method = RequestMethod.POST)
    public AcceptExchangeResponse acceptExchange(@RequestBody AcceptExchangeRequest request) {
        AcceptExchangeResponse response = cashboxObjectFactory.createAcceptExchangeResponse();

        try {
            String operatorIkp = cashboxService.checkOperatorSession(radomRequestContext.getTempUserEntity(), request.getWorkplaceId());
            if (!operatorIkp.equals(request.getOperatorIkp()))
                throw new CashboxException("Активна сессия другого оператора.");
            CashboxExchangeProtocols exchangeProtocols = cashboxService.acceptExchange(radomRequestContext.getTempUserEntity(), request.getWorkplaceId(), request.getRequestId());

            response.setCommunityContributionProtocolNumber(exchangeProtocols.getSharerContributionProtocolCode());
            response.setCommunityContributionProtocolDate(dateToXml(exchangeProtocols.getSharerContributionProtocolCreatedDate()));
            response.setCommunityRefundProtocolNumber(exchangeProtocols.getSharerRefundProtocolCode());
            response.setCommunityRefundProtocolDate(dateToXml(exchangeProtocols.getSharerRefundProtocolCreatedDate()));
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    //@DigestProtected
    @RequestMapping(value = "importProducts", method = RequestMethod.POST)
    public ImportProductsResponse importProducts(@RequestBody ImportProductsRequest request) {
        ImportProductsResponse response = cashboxObjectFactory.createImportProductsResponse();

        try {
            cashboxService.importProducts(request.getShop(), request.getProducts());
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    //@DigestProtected
    @RequestMapping(value = "updatePrices", method = RequestMethod.POST)
    public UpdatePricesResponse updatePrices(@RequestBody UpdatePricesRequest request) {
        UpdatePricesResponse response = cashboxObjectFactory.createUpdatePricesResponse();

        try {
            cashboxService.updatePrices(request.getShop(), request.getProducts());
            response.setStatus(createAcceptedStatus());
        } catch (CashboxException e) {
            response.setStatus(createRejectedStatus(e.getMessage()));
        }

        return response;
    }

    // TODO move to FingerController
    @Deprecated
    @RequestMapping(value = "finger/init_by_ikp.json", method = RequestMethod.POST, consumes = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    public InitByIkpRequestDto initTokenByIkp(HttpServletRequest request,
                                              @RequestParam("ikp") String ikp) throws Exception {
        UserEntity userEntity = sharerDao.getByIkp(ikp);
        if (userEntity == null) throw new Exception("Участник не найден");

        FingerToken fingerToken = fingerService.initToken(userEntity.getId(), request.getRemoteAddr());

        InitByIkpRequestDto requestDto = new InitByIkpRequestDto();
        requestDto.requestId = fingerToken.getRequestId();
        requestDto.finger = fingerToken.getFinger();
        requestDto.ikp = userEntity.getIkp();
        return requestDto;
    }

    private Status createAcceptedStatus() {
        Status status = cashboxObjectFactory.createStatus();
        status.setCode(StatusCode.ACCEPTED);
        status.setMessage("");
        return status;
    }

    private Status createRejectedStatus(String errorMessage) {
        Status status = cashboxObjectFactory.createStatus();
        status.setCode(StatusCode.REJECTED);
        status.setMessage(errorMessage == null ? "Произошла ошибка при обработке запроса на сервере системы Благосфера" : errorMessage);
        return status;
    }

    private ShareHolder identifySharer(String ikp, String workplaceId) throws CashboxException {
        CashboxWorkplace workplace = cashboxService.findWorkplace(workplaceId);
        UserEntity userEntity = cashboxService.identifySharer(ikp);

        CashboxRegisterShareholder registerShareholder = cashboxService.findMemberRegistrationEntry(workplaceId, userEntity);
        ShareHolder shareHolder = cashboxObjectFactory.createShareHolder();
        shareHolder.setIkp(userEntity.getIkp());
        shareHolder.setAvatar(requestAvatar(userEntity.getAvatar()));
        shareHolder.setFirstName(userEntity.getFirstName());
        shareHolder.setMiddleName(userEntity.getSecondName());
        shareHolder.setLastName(userEntity.getLastName());
        shareHolder.setFullName(declineName(userEntity.getFullName(), userEntity.getSex()));
        shareHolder.setGender(userEntity.getSex() ? "M" : "F");
        shareHolder.setIsVerified(userEntity.isVerified());
        shareHolder.setIsMember(cashboxService.isCommunityMember(workplace.getCooperative(), userEntity));
        shareHolder.setIsRegistrationRequested(registerShareholder != null);
        shareHolder.setIsRegistrationAccepted((registerShareholder != null) && (registerShareholder.getRequestAcceptedDate() != null));
        return shareHolder;
    }

    private XMLGregorianCalendar dateToXml(Date date) throws CashboxException {
        try {
            GregorianCalendar createdDateGregorian = new GregorianCalendar();
            createdDateGregorian.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(createdDateGregorian);
        } catch (DatatypeConfigurationException e) {
            throw new CashboxException("Ошибка создания XMLGregorianCalendar");
        }
    }

    private String createDurationBetween(Date from, Date to) throws CashboxException {
        try {
            Duration duration = DatatypeFactory.newInstance().newDuration(to.getTime() - from.getTime());
            StringBuilder stringBuilder = new StringBuilder();
            if (duration.getDays() > 0) stringBuilder.append(duration.getDays()).append("д ");
            if (duration.getHours() > 0) stringBuilder.append(duration.getHours()).append("ч ");
            if (duration.getMinutes() > 0) stringBuilder.append(duration.getMinutes()).append("м");
            return stringBuilder.toString();
        } catch (DatatypeConfigurationException e) {
            throw new CashboxException("Ошибка создания Duration");
        }
    }

    private byte[] requestAvatar(String url) {
        try {
            HTTP http = new HTTP(new DNBasedTrustManager(TLSConstants.DN_LETSENCRYPT, TLSConstants.DN_BLAGOSFERA_IMAGES, false));
            return Base64.encodeBase64String(http.doGet(url).getData()).getBytes(StandardCharsets.UTF_8);
        } catch (HttpException e) {
            return null;
        }
    }

    private String declineName(String name, boolean sex) {
        StringBuilder result = new StringBuilder();

        result.append("И=");
        result.append(name);
        result.append(",Р=");
        result.append(Padeg.getFIOPadegFS(name, sex, PadegConstants.PADEG_R));
        result.append(",Д=");
        result.append(Padeg.getFIOPadegFS(name, sex, PadegConstants.PADEG_D));
        result.append(",В=");
        result.append(Padeg.getFIOPadegFS(name, sex, PadegConstants.PADEG_V));
        result.append(",Т=");
        result.append(Padeg.getFIOPadegFS(name, sex, PadegConstants.PADEG_T));
        result.append(",П=");
        result.append(Padeg.getFIOPadegFS(name, sex, PadegConstants.PADEG_P));

        return result.toString();
    }

    private String declineOfficialName(String name) {
        StringBuilder result = new StringBuilder();

        result.append("И=");
        result.append(name);
        result.append(",Р=");
        result.append(Padeg.getOfficePadeg(name, PadegConstants.PADEG_R));
        result.append(",Д=");
        result.append(Padeg.getOfficePadeg(name, PadegConstants.PADEG_D));
        result.append(",В=");
        result.append(Padeg.getOfficePadeg(name, PadegConstants.PADEG_V));
        result.append(",Т=");
        result.append(Padeg.getOfficePadeg(name, PadegConstants.PADEG_T));
        result.append(",П=");
        result.append(Padeg.getOfficePadeg(name, PadegConstants.PADEG_P));

        return result.toString();
    }
}
