package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.CyberbrainObject;

import java.util.List;

@Repository("cyberbrainObjectDao")
public class CyberbrainObjectDao extends Dao<CyberbrainObject> {
    public List<CyberbrainObject> getAllList() {
        return find();
    }

    public CyberbrainObject getByName(String name) {
        return findFirst(Restrictions.eq("name", name));
    }
}