package ru.radom.kabinet.model.log;

import ru.askor.blagosfera.domain.sessions.UserSession;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Лог неудачных попыток входа
 *
 * Created by ebelyaev on 12.08.2015.
 */
@Entity
@Table(name = "login_failure_verifiable_logs")
public class LoginFailureVerifiableLog extends LoginVerifiableLog {
    public LoginFailureVerifiableLog() {
    }

    public LoginFailureVerifiableLog(Date loginDate, String ip, String referer, String useragent, String sessionId, boolean success, String username, LoginType loginType) {
        super(loginDate, ip, referer, useragent, sessionId, success, username, loginType);
    }

    public LoginFailureVerifiableLog(UserSession userSession) {
        super(userSession);
    }
}
