package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.CyberbrainFile;

import java.util.List;

@Repository("cyberbrainFileDao")
public class CyberbrainFileDao extends Dao<CyberbrainFile> {
    public List<CyberbrainFile> getAllList() {
        return find();
    }

    public CyberbrainFile getByName(String name) {
        return findFirst(Restrictions.eq("name", name));
    }
}
