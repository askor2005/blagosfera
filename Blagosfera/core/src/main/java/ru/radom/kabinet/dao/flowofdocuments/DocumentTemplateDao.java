package ru.radom.kabinet.dao.flowofdocuments;

import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.utils.VarUtils;

import java.util.List;
import java.util.Map;

@Repository("flowOfDocumentTemplateDao")
public class DocumentTemplateDao extends Dao<DocumentTemplateEntity> {
	public List<DocumentTemplateEntity> getByDocumentType(DocumentClassEntity documentType) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("documentType", documentType));
		return find(Order.asc("position"), conjunction);
	}

	public List<DocumentTemplateEntity> getList(Map<String, String> filters) {

		Conjunction conjunction = new Conjunction();

		if (filters != null) {
			if (!filters.get("name").equals("")) {
				conjunction.add(Restrictions.ilike("name", filters.get("name"), MatchMode.ANYWHERE));
			}
		}

		return find(Order.asc("position"), conjunction);
	}

	public List<DocumentTemplateEntity> getList(Map<String, String> filters, int firstResult, int maxResults, String sort) {
		List<DocumentTemplateEntity> list;
		Conjunction conjunction = new Conjunction();

		String sortProperty = "";
		String sortDirection = "";
		if (!sort.equals("")) {
			JSONArray jsonArray = new JSONArray(sort);
			sortProperty = jsonArray.getJSONObject(0).getString("property");
			sortDirection = jsonArray.getJSONObject(0).getString("direction");
		}

		if (filters != null) {
			if (filters.get("name") != null && !filters.get("name").equals("")) {
				conjunction.add(Restrictions.ilike("name", filters.get("name"), MatchMode.ANYWHERE));
			}
			if (filters.get("classId") != null && !filters.get("classId").equals("")) {
				conjunction.add(Restrictions.eq("documentType.id", VarUtils.getLong(filters.get("classId"), -1l)));
			}
		}

		if (sort.equals("")) {
			list = find(Order.asc("position"), firstResult, maxResults, conjunction);
		} else {
			if (sortDirection.equals("ASC")) {
				list = find(Order.asc(sortProperty), firstResult, maxResults, conjunction);
			} else {
				list = find(Order.desc(sortProperty), firstResult, maxResults, conjunction);
			}
		}

		return list;
	}

	public int getCount(Map<String, String> filters) {
		Conjunction conjunction = new Conjunction();
		if (filters != null) {
			if (filters.get("name") != null && !filters.get("name").equals("")) {
				conjunction.add(Restrictions.ilike("name", filters.get("name"), MatchMode.ANYWHERE));
			}
			if (filters.get("classId") != null && !filters.get("classId").equals("")) {
				conjunction.add(Restrictions.eq("documentType.id", VarUtils.getLong(filters.get("classId"), -1l)));
			}
		}
		return count(conjunction);
	}

	/**
	 * Получить шаблон документа по его коду.
	 * @param code
	 * @return
	 */
	public DocumentTemplateEntity getByCode(String code) {
		DocumentTemplateEntity result = null;
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("code", code));
		List<DocumentTemplateEntity> list = find(Order.asc("name"), conjunction);
		if (list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	/**
	 * Найти шаблоны где в имени есть queryName и участник документа - participantsType
	 * @param queryName фильтр по имени
	 * @param participantsType тип участника
	 * @param maxResults поличество в результате
	 * @return список шаблонов
	 */
	public List<DocumentTemplateEntity> getList(String queryName, ParticipantsTypes participantsType, int maxResults){
		Query query = createQuery(
				"select dt from flowOfDocumentTemplate dt " +
				"inner join dt.documentType doctype " +
				"inner join doctype.participants p " +
				"where p.size = 1 and p.participantType = :participantType and lower(dt.name) like lower(:queryName)");
		query.setMaxResults(maxResults);
		query.setString("participantType", participantsType.getName());
		query.setString("queryName", "%" + queryName + "%");
		//query.setResultTransformer(Transformers.aliasToBean(DocumentTemplateEntity.class));
		return query.list();
	}
}