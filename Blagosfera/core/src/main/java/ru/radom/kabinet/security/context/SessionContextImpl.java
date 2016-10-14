package ru.radom.kabinet.security.context;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("sessionContext")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContextImpl implements SessionContext,Serializable {

    private String generatedPassword;

    @Override
    public String getGeneratedPassword() {
        return generatedPassword;
    }

    @Override
    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }
}
