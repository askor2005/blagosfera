package ru.askor.blagosfera.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.logging.loggers.BlagosferaLogger;

@Transactional
@Component("blagosferaLoggerAspect")
@Aspect
public class BlagosferaLoggerAspect {

    @Autowired
    private BlagosferaLogger blagosferaLogger;

    public BlagosferaLoggerAspect() {
    }

    /*@Around("execution(* ru.radom.kabinet..*.*(..))")
    public Object kabinetAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return blagosferaLogger.logExecutionFlow(joinPoint);
    }*/

    @Around("execution(* ru.askor.blagosfera.core..*.*(..))")
    public Object blagosferaCoreAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return blagosferaLogger.logExecutionFlow(joinPoint);
    }

    @Around("execution(* ru.askor.blagosfera.data..*.*(..))")
    public Object blagosferaDataAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return blagosferaLogger.logExecutionFlow(joinPoint);
    }

    @Around("execution(* ru.askor.blagosfera.web.controllers..*.*(..))")
    public Object blagosferaWebAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return blagosferaLogger.logExecutionFlow(joinPoint);
    }

    @Around("execution(* ru.askor.voting..*.*(..))")
    public Object votingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return blagosferaLogger.logExecutionFlow(joinPoint);
    }
}
