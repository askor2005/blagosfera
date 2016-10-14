package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.cyberbrain.ScoreSharer;
import ru.radom.kabinet.security.SecurityUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Repository("scoreSharerDao")
public class ScoreSharerDao extends Dao<ScoreSharer> {
    private static class Query {
        /**
         * Получить текущее количество набранных баллов пользователем
         */
        public static final String GET_CURRENT_SCORE_SHARER = "SELECT coalesce(sum(" + ScoreSharer.Columns.SCORE + "), 0) FROM " + ScoreSharer.TABLE_NAME + " where " + ScoreSharer.Columns.SHARER + " = :sharerId";

        /**
         * Получить рейтинги по пользователеям системы
         */
        public static final String GET_RATING_SYSTEM = "SELECT tbl.user_id, tbl.user, tbl.score_sharer " +
                "FROM (SELECT s.id as user_id, s.search_string as user, coalesce(sum(score_sharers." + ScoreSharer.Columns.SCORE + "), 0) as score_sharer" +
                " FROM " + ScoreSharer.TABLE_NAME + " AS score_sharers INNER JOIN sharers AS s ON s.id = score_sharers." + ScoreSharer.Columns.SHARER +
                " GROUP BY s.id, s.search_string) as tbl " +
                "GROUP BY tbl.user_id, tbl.user, tbl.score_sharer " +
                "ORDER BY tbl.score_sharer DESC, tbl.user";
    }

    @Autowired
    private SharerDao sharerDao;

    public List<ScoreSharer> getAllList() {
        return find();
    }

    public BigDecimal getCurrentUserScore() {
        String queryString = Query.GET_CURRENT_SCORE_SHARER;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameter("sharerId", SecurityUtils.getUser().getId());

        return (BigDecimal) query.uniqueResult();
    }

    public List getRatingSystem(int firstResult, int maxResults) {
        String queryString = Query.GET_RATING_SYSTEM;

        SQLQuery query = createSQLQuery(queryString);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List list = query.setFirstResult(firstResult).setMaxResults(maxResults).list();

        UserEntity userEntity;
        for (Object obj : list) {
            BigInteger sharerId = (BigInteger) ((HashMap) obj).get("user_id");
            userEntity = sharerDao.getById(sharerId.longValue());
            ((HashMap) obj).put("user", userEntity.getFullName());
        }

        return list;
    }

    public BigInteger getRatingSystemCount() {
        String queryString = Query.GET_RATING_SYSTEM;

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();
        queryString = "SELECT COUNT(*) " + queryString.substring(startIndex, endIndex);

        return (BigInteger) createSQLQuery(queryString).uniqueResult();
    }
}