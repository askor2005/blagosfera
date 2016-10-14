package ru.radom.kabinet.dao.fields;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldPossibleValueEntity;

import java.util.List;

@Repository("fieldPossibleValueDao")
public class FieldPossibleValueDao extends Dao<FieldPossibleValueEntity> {
    public List<FieldPossibleValueEntity> getByField(FieldEntity field) {
        return find(Restrictions.eq("field", field));
    }
}