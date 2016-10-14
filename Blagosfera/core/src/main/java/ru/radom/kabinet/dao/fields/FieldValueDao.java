package ru.radom.kabinet.dao.fields;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;

import java.util.Collections;
import java.util.List;

@Repository("fieldValueDao")
public class FieldValueDao extends Dao<FieldValueEntity> {

	public List<FieldValueEntity> getByObject(LongIdentifiable object) {
		return find(Restrictions.eq("object", object));
	}

    public List<FieldValueEntity> getByObject(Long objectId, String objectType) {
		return find(Restrictions.eq("objectId", objectId), Restrictions.eq("objectType", objectType));
	}

	// Загрузить значение поля по держателю поля и по полю
	@Deprecated
    public FieldValueEntity get(LongIdentifiable object, FieldEntity field) {
		return findFirst(Restrictions.eq("object", object), Restrictions.eq("field", field));
	}

    public FieldValueEntity get(Long objectId, String objectType, FieldEntity field) {
        return findFirst(Restrictions.eq("objectId", objectId), Restrictions.eq("objectType", objectType), Restrictions.eq("field", field));
    }

	/**
	 * Загрузить значение поля по держателю поля и по мнемокоду полей
	 * @param object
	 * @param fieldInternalNames
	 * @return
	 */
    @Deprecated
	public List<FieldValueEntity> getByFieldList(LongIdentifiable object, List<String> fieldInternalNames) {
		Criteria criteria = getCriteria();
		criteria.createAlias("field", "fieldAlias");
		criteria.add(Restrictions.eq("object", object));
		criteria.add(Restrictions.in("fieldAlias.internalName", fieldInternalNames));
		FieldValueEntity result = null;
		return find(criteria);
	}

    public List<FieldValueEntity> getByFieldList(Long objectId, String objectType, List<String> fieldInternalNames) {
        Criteria criteria = getCriteria();
        criteria.createAlias("field", "fieldAlias");
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("objectType", objectType));
        criteria.add(Restrictions.in("fieldAlias.internalName", fieldInternalNames));
        FieldValueEntity result = null;
        return find(criteria);
    }

	/**
	 * Загрузить значение поля по держателю поля и по мнемокоду поля
	 * @param object
	 * @param fieldInternalName
	 * @return
	 */
    @Deprecated
	public FieldValueEntity get(LongIdentifiable object, String fieldInternalName) {
		List<FieldValueEntity> fieldValues = getByFieldList(object, Collections.singletonList(fieldInternalName));
		FieldValueEntity result = null;
		if (fieldValues != null && fieldValues.size() > 0) {
			result = fieldValues.get(0);
		}
		return result;
	}

    public FieldValueEntity get(Long objectId, String objectType, String fieldInternalName) {
        List<FieldValueEntity> fieldValues = getByFieldList(objectId, objectType, Collections.singletonList(fieldInternalName));
        FieldValueEntity result = null;
        if (fieldValues != null && fieldValues.size() > 0) {
            result = fieldValues.get(0);
        }
        return result;
    }

	public FieldValueEntity getByValue(String stringValue) {
		return findFirst(Restrictions.eq("stringValue", stringValue));
	}

	public List<FieldValueEntity> getByValue(String fieldInternalName, String stringValue) {
		Criteria criteria = getCriteria();
		criteria.createAlias("field", "fieldAlias");
		criteria.add(Restrictions.eq("fieldAlias.internalName", fieldInternalName));
		criteria.add(Restrictions.eq("stringValue", stringValue));
		return find(criteria);
	}

	public List<FieldValueEntity> getList(FieldEntity field, String stringValue) {
		return find(Restrictions.eq("field", field), Restrictions.eq("stringValue", stringValue));
	}

	public List<FieldValueEntity> getList(FieldEntity field) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("field", field));
		return find(criteria);
	}

	public List<FieldValueEntity> getOrderedList(FieldEntity field, String stringValue) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("field", field));
		criteria.addOrder(Order.asc("id"));
		criteria.add(Restrictions.eq("stringValue", stringValue));
		return find(criteria);
	}

	public List<FieldValueEntity> getListWhereEmptyValue(FieldEntity field) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.or(Restrictions.eq("stringValue", ""), Restrictions.isNull("stringValue")));
		criteria.add(Restrictions.eq("field", field));
		return find(criteria);
	}

	public List<FieldValueEntity> getListWhereNotEmptyValue(FieldEntity field) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.and(Restrictions.ne("stringValue", ""), Restrictions.isNotNull("stringValue")));
		criteria.add(Restrictions.eq("field", field));
		return find(criteria);
	}

	/**
	 * Поиск значений полей по группам полей
	 * @param fieldsGroups
	 * @param object
	 * @return
	 */
    @Deprecated
	public List<FieldValueEntity> getListByFieldsGroups(List<FieldsGroupEntity> fieldsGroups, LongIdentifiable object) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("object", object));
		criteria.createAlias("field", "fieldAlias");
		criteria.add(Restrictions.in("fieldAlias.fieldsGroup", fieldsGroups));
		return find(criteria);
	}

    public List<FieldValueEntity> getListByFieldsGroups(List<FieldsGroupEntity> fieldsGroups, Long objectId, String objectType) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("objectType", objectType));
        criteria.createAlias("field", "fieldAlias");
        criteria.add(Restrictions.in("fieldAlias.fieldsGroup", fieldsGroups));
        return find(criteria);
    }
}