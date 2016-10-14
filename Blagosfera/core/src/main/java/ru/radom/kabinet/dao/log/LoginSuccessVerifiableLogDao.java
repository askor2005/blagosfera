package ru.radom.kabinet.dao.log;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.log.LoginSuccessVerifiableLog;

/**
 * Created by ebelyaev on 13.08.2015.
 */
@Repository("loginSuccessVerifiableLogDao")
public class LoginSuccessVerifiableLogDao extends VerifiableLogDao<LoginSuccessVerifiableLog> {
}
