package ru.radom.kabinet.dao.flowofdocuments;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.model.DocumentTemplateFilterValueEntity;

import java.util.List;

@Repository("documentTemplateFilterValueDao")
public class DocumentTemplateFilterValueDao extends Dao<DocumentTemplateFilterValueEntity> {
	public List<DocumentTemplateFilterValueEntity> getByDocumentTemplate(DocumentTemplateEntity documentTemplate) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("documentTemplate", documentTemplate));
		return find(conjunction);
	}
}