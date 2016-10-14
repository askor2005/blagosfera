package ru.radom.kabinet.dao;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.SmtpServer;

import java.util.List;
import java.util.Map;

/**
 *
 * @author dfilinberg
 */
@Repository("smtpServerDao")
public class SmtpServerDao extends Dao<SmtpServer> {
    public SmtpServer findActual() {
        return findFirst(Restrictions.eq("using", true));
    }

    public List<SmtpServer> getList(Map<String, String> filters, int firstResult, int maxResults, String sort) {
        List<SmtpServer> list;
        Conjunction conjunction = new Conjunction();

        String sortProperty = "";
        String sortDirection = "";
        if (!sort.equals("")) {
            JSONArray jsonArray = new JSONArray(sort);
            sortProperty = jsonArray.getJSONObject(0).getString("property");
            sortDirection = jsonArray.getJSONObject(0).getString("direction");
        }

        if (filters != null) {
            if (!filters.get("host").equals("")) {
                conjunction.add(Restrictions.ilike("host", filters.get("host"), MatchMode.ANYWHERE));
            }
        }

        if (sort.equals("")) {
            list = find(firstResult, maxResults, conjunction);
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
            if (!filters.get("host").equals("")) {
                conjunction.add(Restrictions.ilike("host", filters.get("host"), MatchMode.ANYWHERE));
            }
        }
        return count(conjunction);
    }
}