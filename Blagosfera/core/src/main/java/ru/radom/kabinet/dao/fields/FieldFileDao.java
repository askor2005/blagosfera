package ru.radom.kabinet.dao.fields;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 11.09.2015.
 */
@Repository("fieldFileDao")
public class FieldFileDao extends Dao<FieldFileEntity> {

    public List<FieldFileEntity> getByFieldValue(FieldValueEntity fieldValue) {
        return find(Restrictions.eq("fieldValue", fieldValue));
    }
}
