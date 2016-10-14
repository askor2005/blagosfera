package ru.radom.kabinet.dao.log;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.log.DocumentSignVerifiableLog;

/**
 * Created by ebelyaev on 13.08.2015.
 */
@Repository("documentSignVerifiableLog")
public class DocumentSignVerifiableLogDao extends VerifiableLogDao<DocumentSignVerifiableLog> {
}
