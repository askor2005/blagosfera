package ru.radom.kabinet.web.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;

@Deprecated
public class SharerResolver implements HandlerMethodArgumentResolver {

	@Autowired
    private SharerDao sharerDao;

	@Override
	public Object resolveArgument(MethodParameter mp,
			ModelAndViewContainer mvContainer, NativeWebRequest nativeWebRequest,
			WebDataBinderFactory webDataBinderFactory) throws Exception {
		return sharerDao.getById(SecurityUtils.getUser().getId());
	}

	@Override
	public boolean supportsParameter(MethodParameter param) {
		return param.getParameterType().equals(UserEntity.class) && !param.hasParameterAnnotations();
	}

}
