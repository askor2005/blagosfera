package ru.radom.kabinet.security.bio;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.finger.FingerService;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class TokenProtectedAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProtectedAspect.class);

    private static final String TOKEN_IKP_PROTECT_ENABLE_SYS_ATTR_NAME = "token.ikp.protect.enable";

    @Autowired
    private FingerService fingerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private SettingsManager settingsManager;

    @Around("@annotation(tokenProtected) && @annotation(requestMapping)")
    public Object tokenProtected(ProceedingJoinPoint proceedingJoinPoint, TokenProtected tokenProtected, RequestMapping requestMapping) throws Throwable {
        try {
            String value = tokenProtected.systemOption();
            boolean verifyToken = true;

            if (!value.isEmpty()) {
                verifyToken = "1".equals(settingsManager.getSystemSetting(value));
            }

            if (verifyToken) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String token = request.getHeader("FINGER_TOKEN");
                fingerService.useToken(SecurityUtils.getUser(), token);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return proceedingJoinPoint.proceed();
    }

    @Around("@annotation(tokenIkpProtected) && @annotation(requestMapping)")
    public Object tokenIkpProtected(ProceedingJoinPoint proceedingJoinPoint, TokenIkpProtected tokenIkpProtected, RequestMapping requestMapping) throws Throwable {
        try {
            boolean tokenIkpEnable = settingsManager.getSystemSettingAsBool(TOKEN_IKP_PROTECT_ENABLE_SYS_ATTR_NAME, true);

            if (!tokenIkpEnable) {
                UserEntity userEntity = userRepository.findOne(SecurityUtils.getUser().getId());
                radomRequestContext.setTempUserEntity(userEntity);
            } else {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String token = request.getHeader("FINGER_TOKEN");
                String ikp = request.getHeader("IKP");
                UserEntity userEntity = userRepository.findOneByIkp(ikp);
                fingerService.useToken(userEntity.toDomain(), token);
                radomRequestContext.setTempUserEntity(userEntity);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return proceedingJoinPoint.proceed();
    }
}