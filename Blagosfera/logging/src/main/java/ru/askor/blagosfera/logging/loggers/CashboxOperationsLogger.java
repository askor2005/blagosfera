package ru.askor.blagosfera.logging.loggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.cashbox.CashboxOperationStatus;
import ru.askor.blagosfera.logging.data.jpa.services.LoggingService;
import ru.askor.blagosfera.logging.domain.CashboxOperation;
import ru.askor.blagosfera.logging.domain.CashboxOperationLogItem;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.askor.blagosfera.domain.xml.cashbox.*;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Date;

@Transactional
@Component("cashboxOperationsLogger")
public class CashboxOperationsLogger {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    @Qualifier("jaxbMarshaller")
    private Jaxb2Marshaller jaxb2Marshaller;

    public CashboxOperationsLogger() {
    }

    public void logOperation(CashboxOperation operation, Object request, Object response) {
        String requestPayload = serialize(request);
        String responsePayload = null;
        String exceptionMessage = null;
        String exceptionStacktrace = null;
        String workplaceId = null;
        String operatorIkp = null;
        CashboxOperationStatus status;

        switch (operation) {
            case OPERATOR_START: {
                OperatorStartRequest req = (OperatorStartRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case OPERATOR_STOP: {
                OperatorStopRequest req = (OperatorStopRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case SESSION_CHECK: {
                workplaceId = ((SessionCheckRequest) request).getWorkplaceId();
                break;
            }
            case IDENTIFICATION: {
                IdentificationRequest req = (IdentificationRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case REGISTER: {
                RegisterRequest req = (RegisterRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case REGISTER_ACCEPT: {
                AcceptRegistrationRequest req = (AcceptRegistrationRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case EXCHANGE: {
                ExchangeRequest req = (ExchangeRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case EXCHANGE_ACCEPT: {
                AcceptExchangeRequest req = (AcceptExchangeRequest) request;
                workplaceId = req.getWorkplaceId();
                operatorIkp = req.getOperatorIkp();
                break;
            }
            case IMPORT_PRODUCTS:{
                ImportProductsRequest req = (ImportProductsRequest) request;
                workplaceId = String.valueOf(req.getShop());
                operatorIkp = workplaceId;
                break;
            }
            case UPDATE_PRICES: {
                UpdatePricesRequest req = (UpdatePricesRequest) request;
                workplaceId = String.valueOf(req.getShop());
                operatorIkp = workplaceId;
                break;
            }
        }

        if (response instanceof Throwable) {
            status = CashboxOperationStatus.FAIL;
            Throwable e = (Throwable) response;
            exceptionMessage = e.getMessage();
            exceptionStacktrace = ExceptionUtils.getStackTrace(e);
        } else {
            status = CashboxOperationStatus.SUCCESS;
            responsePayload = serialize(response);

            switch (operation) {
                case SESSION_CHECK: {
                    operatorIkp = ((SessionCheckResponse) response).getOperatorIkp();
                    break;
                }
            }
        }

        CashboxOperationLogItem logItem = new CashboxOperationLogItem();
        logItem.setCreatedDate(new Date());
        logItem.setOperation(operation);
        logItem.setStatus(status);
        logItem.setWorkplaceId(workplaceId);
        logItem.setOperatorIkp(operatorIkp);
        logItem.setRequestPayload(requestPayload);
        logItem.setResponsePayload(responsePayload);
        logItem.setExceptionMessage(exceptionMessage);
        logItem.setExceptionStacktrace(exceptionStacktrace);

        loggingService.saveCashboxOperationLogItem(logItem);
    }

    private String serialize(Object object) {
        StringWriter writer = new StringWriter();
        jaxb2Marshaller.marshal(object, new StreamResult(writer));
        return writer.toString();
    }
}
