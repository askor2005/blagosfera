package ru.radom.kabinet.dao.rameralisteditor;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;

/**
 * Created by vgusev on 02.06.2015.
 */
@Repository("rameraListEditorDao")
public class RameraListEditorDAO extends Dao<RameraListEditor> {

    /**
     * Получить компонент по имени.
     * @param name
     * @return
     */
    public RameraListEditor getByName(String name) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("name", name));
        return (RameraListEditor)criteria.uniqueResult();
    }
}
