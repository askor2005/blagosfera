package ru.radom.kabinet.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public AuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	@Override
	protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
		String url = super.determineUrlToUseForThisRequest(request, response, exception);
		String referer = request.getHeader("referer");
		url += (url.indexOf('?') == -1 ? "?" : "&") + "from=" + referer;
		return url;
	}
	
}
