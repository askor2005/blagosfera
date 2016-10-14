package ru.radom.kabinet.dao.log;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.log.LoginFailureVerifiableLog;

/**
 * Created by ebelyaev on 13.08.2015.
 */
@Repository("loginFailureVerifiableLogDao")
public class LoginFailureVerifiableLogDao extends VerifiableLogDao<LoginFailureVerifiableLog> {
}
