package ru.radom.kabinet.document.dao;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.AbstractDao;
import ru.radom.kabinet.document.model.DocumentParameterEntity;

/**
 * Created by vgusev on 11.08.2015.
 * Класс доступа к данным сущности параметры Документа.
 */
@Repository("flowOfDocumentParameterDao")
public class FlowOfDocumentParameterDao extends AbstractDao<DocumentParameterEntity, Long> {

}
