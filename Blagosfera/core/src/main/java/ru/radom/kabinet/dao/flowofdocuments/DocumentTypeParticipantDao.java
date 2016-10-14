package ru.radom.kabinet.dao.flowofdocuments;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;

import java.util.List;

@Repository("documentTypeParticipantDao")
public class DocumentTypeParticipantDao extends Dao<DocumentClassDataSourceEntity> {
	/**
	 * Получить список всех участников для конкретного типа документа
	 */
	public List<DocumentClassDataSourceEntity> getListByDocumentType(DocumentClassEntity documentType) {
		return find(Restrictions.eq("documentType", documentType));
	}

	/**
	 * Получить список участников для конкретного типа документа и конкретного типа участника
	 */
	public List<DocumentClassDataSourceEntity> getListByDocumentType(DocumentClassEntity documentType, String participantType) {
		return find(Restrictions.eq("documentType", documentType), Restrictions.eq("participantType", participantType));
	}

	/**
	 * Получить список всех участников (физ. лиц) для конкретного типа документа
	 */
	public List<DocumentClassDataSourceEntity> getIndividualListByDocumentType(DocumentClassEntity documentType) {
		return find(Restrictions.eq("documentType", documentType), Restrictions.eq("participantType", ParticipantsTypes.INDIVIDUAL.getName()));
	}
}