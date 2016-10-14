package ru.radom.kabinet.security.communities;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;

@Aspect
@Component
public class CommunityPermissionAspect {

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private RequestContext radomRequestContext;

    @Around("@annotation(commuityPermissionRequired) && @annotation(requestMapping)")
    public Object checkCommunityPermission(ProceedingJoinPoint proceedingJoinPoint,
                                           CommunityPermissionRequired commuityPermissionRequired,
                                           RequestMapping requestMapping) throws Throwable {
        if (!communitiesService.hasPermission(radomRequestContext.getCommunityId(),
                                              SecurityUtils.getUser().getId(),
                                              commuityPermissionRequired.value())) {
            throw new AccessDeniedException("Доступ запрещен");
        }

        return proceedingJoinPoint.proceed();
    }

    @Around("@annotation(commuityMembershipRequired) && @annotation(requestMapping)")
    public Object checkCommunityMembership(ProceedingJoinPoint proceedingJoinPoint,
                                           CommunityMembershipRequired commuityMembershipRequired,
                                           RequestMapping requestMapping) throws Throwable {
        if (!communitiesService.isMember(radomRequestContext.getCommunityId(),
                                         SecurityUtils.getUser().getId())) {
            throw new AccessDeniedException("Доступ запрещен");
        }

        return proceedingJoinPoint.proceed();
    }
}
