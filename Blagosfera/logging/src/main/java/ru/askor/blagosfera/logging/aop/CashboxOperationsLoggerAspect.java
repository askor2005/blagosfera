package ru.askor.blagosfera.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.logging.domain.CashboxOperation;
import ru.askor.blagosfera.logging.loggers.CashboxOperationsLogger;

@Transactional
@Component("cashboxOperationsAspect")
@Aspect
public class CashboxOperationsLoggerAspect {

    @Autowired
    private CashboxOperationsLogger cashboxOperationsLogger;

    public CashboxOperationsLoggerAspect() {
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.OperatorStartResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.operatorstart(ru.askor.blagosfera.domain.xml.cashbox.OperatorStartRequest))")
    public Object operatorstart(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.OPERATOR_START, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.OperatorStopResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.operatorstop(ru.askor.blagosfera.domain.xml.cashbox.OperatorStopRequest))")
    public Object operatorstop(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.OPERATOR_STOP, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.SessionCheckResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.sessioncheck(ru.askor.blagosfera.domain.xml.cashbox.SessionCheckRequest))")
    public Object sessioncheck(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.SESSION_CHECK, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.IdentificationResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.identification(ru.askor.blagosfera.domain.xml.cashbox.IdentificationRequest))")
    public Object identification(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.IDENTIFICATION, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.RegisterResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.register(ru.askor.blagosfera.domain.xml.cashbox.RegisterRequest))")
    public Object register(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.REGISTER, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.AcceptRegistrationResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.acceptRegistration(ru.askor.blagosfera.domain.xml.cashbox.AcceptRegistrationRequest))")
    public Object acceptRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.REGISTER_ACCEPT, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.ExchangeResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.exchange(ru.askor.blagosfera.domain.xml.cashbox.ExchangeRequest))")
    public Object exchange(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.EXCHANGE, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.AcceptExchangeResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.acceptExchange(ru.askor.blagosfera.domain.xml.cashbox.AcceptExchangeRequest))")
    public Object acceptExchange(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.EXCHANGE_ACCEPT, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.ImportProductsResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.importProducts(ru.askor.blagosfera.domain.xml.cashbox.ImportProductsRequest))")
    public Object importProducts(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.IMPORT_PRODUCTS, joinPoint);
    }

    @Around("execution(ru.askor.blagosfera.domain.xml.cashbox.UpdatePricesResponse ru.radom.kabinet.web.ecoadvisor.CashboxAPIControllerBase.updatePrices(ru.askor.blagosfera.domain.xml.cashbox.UpdatePricesRequest))")
    public Object updatePrices(ProceedingJoinPoint joinPoint) throws Throwable {
        return processJoinPoint(CashboxOperation.UPDATE_PRICES, joinPoint);
    }

    private Object processJoinPoint(CashboxOperation operation, ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object response = joinPoint.proceed();
            cashboxOperationsLogger.logOperation(operation, joinPoint.getArgs()[0], response);
            return response;
        } catch (Throwable e) {
            cashboxOperationsLogger.logOperation(operation, joinPoint.getArgs()[0], e);
            throw e;
        }
    }
}
