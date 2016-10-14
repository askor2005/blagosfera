package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.ScoreInfo;
import ru.radom.kabinet.model.cyberbrain.ScoreObject;

import java.util.Date;
import java.util.List;

@Repository("scoreInfoDao")
public class ScoreInfoDao extends Dao<ScoreInfo> {
    public List<ScoreInfo> getAllList() {
        return find();
    }

    public ScoreInfo getByObject(ScoreObject scoreObject, Date date) {
        // Если дата не задана значит возьмем текущую дату
        if (date == null) {
            date = new Date(System.currentTimeMillis());
        }

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("scoreObject", scoreObject));
        conjunction.add(Restrictions.le("periodFrom", date));
        conjunction.add(Restrictions.ge("periodTo", date));

        return findFirst(conjunction);
    }
}
