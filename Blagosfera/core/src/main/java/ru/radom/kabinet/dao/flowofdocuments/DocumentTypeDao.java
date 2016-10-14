package ru.radom.kabinet.dao.flowofdocuments;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.document.model.DocumentClassEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("documentTypeDao")
public class DocumentTypeDao extends Dao<DocumentClassEntity> {
    public DocumentClassEntity getByKey(String key) {
        return findFirst(Restrictions.eq("key", key));
    }

    public List<DocumentClassEntity> getList(Map<String, String> filters, String sort) {
        List<DocumentClassEntity> list;
        Conjunction conjunction = new Conjunction();
        //conjunction.add(Restrictions.isNull("parent"));

        String sortProperty = "";
        String sortDirection = "";
        if (!sort.equals("")) {
            JSONArray jsonArray = new JSONArray(sort);
            sortProperty = jsonArray.getJSONObject(0).getString("property");
            sortDirection = jsonArray.getJSONObject(0).getString("direction");
        }

        if (filters != null) {
            if (filters.containsKey("name") && !filters.get("name").equals("")) {
                conjunction.add(Restrictions.ilike("name", filters.get("name"), MatchMode.ANYWHERE));
            }
            if (filters.containsKey("parent")) {
                if (filters.get("parent") == null) {
                    conjunction.add(Restrictions.isNull("parent"));
                } else {
                    conjunction.add(Restrictions.eq("parent.id", Long.parseLong(filters.get("parent"))));
                }
            }
        }

        if (sort.equals("")) {
            //fin
            list = find(Order.asc("position"), conjunction);
        } else {
            if (sortDirection.equals("ASC")) {
                list = find(Order.asc(sortProperty), conjunction);
            } else {
                list = find(Order.desc(sortProperty), conjunction);
            }
        }

        return list;
    }

	public List<DocumentClassEntity> getList() {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.isNull("parent"));
		return find(Order.asc("position"), conjunction);
	}

    public int getCount(Map<String, String> filters) {
        Conjunction conjunction = new Conjunction();
        if (filters != null) {
            if (!filters.get("name").equals("")) {
                conjunction.add(Restrictions.ilike("name", filters.get("name"), MatchMode.ANYWHERE));
            }
        }
        return count(conjunction);
    }

    public List<DocumentClassEntity> getChildrenList(DocumentClassEntity parent) {
        return find(Restrictions.eq("parent", parent));
    }

	/*public List<DocumentClassEntity> getChildrenList(DocumentClassEntity parent, Boolean isCategory) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("parent", parent));
		if (isCategory != null) {
			conjunction.add(Restrictions.eq("isCategory", isCategory));
		}
		return find(conjunction);
	}*/

    public int getChildrenCount(DocumentClassEntity parent) {
        return count(Restrictions.eq("parent", parent));
    }

	/*public int getChildrenCount(DocumentClassEntity parent, Boolean isCategory) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("parent", parent));
		if (isCategory != null) {
			conjunction.add(Restrictions.eq("isCategory", isCategory));
		}
		return count(conjunction);
	}*/

    /**
     * Получить отфильтрованные классы документа <br/>
     * Параметр <code><b>query</b></code> задает фильтр. <br/>
     * Например: <br/>
     * <code>'Док / уча / кас'</code><br/>
     * будет искать классы у которых в названии есть 'кас', в предке - 'уча', а в предке предка 'док'.
     * Таким образом можно делать поиск по дереву
     */
    public List<DocumentClassEntity> searchByQuery(String query, boolean onlyWithTemplates, Integer page, Integer perPage) {
        int firstResult;
        int maxResult;
        if(page != null && perPage != null) {
            firstResult = perPage * (page - 1);
            maxResult = perPage;
        } else {
            firstResult = 0;
            maxResult = Integer.MAX_VALUE;
        }
        query = query.trim();
        String[] split = query.split("\\s*/\\s*");
        if(query.endsWith("/")) {
            split = (String[]) ArrayUtils.add(split, "");
        }
        StringBuilder queryString = new StringBuilder("select distinct d, char_length(d.name) from DocumentClassEntity d ");
        if(onlyWithTemplates) {
            queryString.append("inner join d.documentTemplates ");
        }
        queryString.append("where ");
        queryString.append(buildQueryAddon(split));
        queryString.append(" order by char_length(d.name)");
        Query q = getCurrentSession().createQuery(queryString.toString());
        for (int i = split.length - 1; i >= 0; i--) {
            String part = "%" + split[i].trim().replaceAll("\\s+", "%") + "%";
            q.setParameter("name" + i, part);
        }
        return (List<DocumentClassEntity>) q.setFirstResult(firstResult).setMaxResults(maxResult).list().stream()
            .map((Object arr) -> ((Object[]) arr)[0])
            .collect(Collectors.toList());
    }

    protected String buildQueryAddon(String[] split) {
        StringBuilder lvl = new StringBuilder("d.");
        StringBuilder s = new StringBuilder();
        for (int i = split.length - 1; i >= 0; i--) {
            s.append("lower(").append(lvl).append("name) like lower(:name").append(i).append(")");
            if(i != 0) {
                s.append(" and ");
            }
            lvl.append("parent.");
        }
        return s.toString();
    }
}