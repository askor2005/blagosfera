package ru.radom.kabinet.dao.fields;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;

import java.util.Collections;
import java.util.List;

@Repository("fieldDao")
public class FieldDao extends Dao<FieldEntity> {
	public FieldEntity getByInternalName(String internalName) {
		return findFirst(Restrictions.eq("internalName", internalName));
	}

	public List<FieldEntity> getByInternalNames(List<String> internalNames) {
		return find(Restrictions.in("internalName", internalNames));
	}

	public List<FieldEntity> getByGroups(List<FieldsGroupEntity> fieldsGroups) {
		if (fieldsGroups.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return find(Restrictions.in("fieldsGroup", fieldsGroups)); 
		}
	}

	/**
	 * Поиск по типу поля
	 * @param fieldType
	 * @return
	 */
	public List<FieldEntity> getByType(FieldType fieldType) {
		return find(Restrictions.eq("type", fieldType));
	}

	public List<FieldEntity> getListByGroupsAndTypes(List<FieldsGroupEntity> fieldsGroups, List<FieldType> fieldTypes) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.in("fieldsGroup", fieldsGroups));
		criteria.add(Restrictions.in("type", fieldTypes));
		return find(criteria);
	}

	public List<FieldEntity> getListByGroupsAndType(List<FieldsGroupEntity> fieldsGroups, FieldType fieldType) {
		return getListByGroupsAndTypes(fieldsGroups, Collections.singletonList(fieldType));
	}

	public List<FieldEntity> getListByObjectType(String objectType) {
		Criteria criteria = getCriteria();
		criteria.createAlias("fieldsGroup", "fieldsGroupAlias");
		criteria.add(Restrictions.eq("fieldsGroupAlias.objectType", objectType));
		return find(criteria);
	}

	public List<FieldEntity> getListByObjectAndTypes(String objectType, List<FieldType> fieldTypes) {
		Criteria criteria = getCriteria();
		criteria.createAlias("fieldsGroup", "fieldsGroupAlias");
		criteria.add(Restrictions.eq("fieldsGroupAlias.objectType", objectType));
		criteria.add(Restrictions.in("type", fieldTypes));
		return find(criteria);
	}

	public List<FieldEntity> getListByObjectAndType(String objectType, FieldType fieldType) {
		return getListByObjectAndTypes(objectType, Collections.singletonList(fieldType));
	}
}