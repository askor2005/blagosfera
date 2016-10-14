package ru.radom.kabinet.dao.log;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.log.RecoveryPasswordVerifiableLog;

/**
 * Created by ebelyaev on 13.08.2015.
 */
@Repository("recoveryPasswordVerifiableLogDao")
public class RecoveryPasswordVerifiableLogDao extends VerifiableLogDao<RecoveryPasswordVerifiableLog> {
}
