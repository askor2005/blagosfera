package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.ScoreObject;

import java.util.List;

@Repository("scoreObjectDao")
public class ScoreObjectDao extends Dao<ScoreObject> {
    public List<ScoreObject> getAllList() {
        return find();
    }

    public ScoreObject getByName(String name) {
        return findFirst(Restrictions.eq("name", name));
    }
}
