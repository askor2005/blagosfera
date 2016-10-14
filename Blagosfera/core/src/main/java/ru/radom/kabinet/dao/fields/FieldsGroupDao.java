package ru.radom.kabinet.dao.fields;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Repository("fieldsGroupDao")
public class FieldsGroupDao extends Dao<FieldsGroupEntity> {

	private static final List<FieldType> EXCLUDED_TYPES = Arrays.asList(FieldType.SYSTEM, FieldType.SYSTEM_IMAGE);

	/**
	 * TODO Нужно переделать!Не смог осилить создание критерия для списка дочерних элементов.
	 * @param fieldsGroup
	 * @param withoutTypes
	 */
	private static void removeTypes(FieldsGroupEntity fieldsGroup, List<FieldType> withoutTypes) {
		Iterator<FieldEntity> fieldIterator = fieldsGroup.getFields().iterator();
		while(fieldIterator.hasNext()){
			FieldEntity field = fieldIterator.next();
			if (withoutTypes.contains(field.getType())) {
				fieldIterator.remove();
			}
		}
	}

	private static void removeTypes(List<FieldsGroupEntity> fieldsGroups, List<FieldType> withoutTypes) {
		Iterator<FieldsGroupEntity> fieldsGroupIterator = fieldsGroups.iterator();
		while(fieldsGroupIterator.hasNext()){
			FieldsGroupEntity fieldsGroup = fieldsGroupIterator.next();
			removeTypes(fieldsGroup, withoutTypes);
		}
	}

	public List<FieldsGroupEntity> getByObjectType(String objectType) {
		return find(Order.asc("position"), Restrictions.eq("objectType", objectType));
	}

	public List<FieldsGroupEntity> getByInternalNamePrefix(String prefix) {
		return getByInternalNamePrefix(prefix, EXCLUDED_TYPES);
	}

	public List<FieldsGroupEntity> getByInternalNamePrefix(String prefix, List<FieldType> withoutTypes) {
		if (withoutTypes == null) {
			withoutTypes = new ArrayList<>();
		}
		List<FieldsGroupEntity> result = find(Order.asc("position"), Restrictions.ilike("internalName", prefix, MatchMode.START));
		removeTypes(result, withoutTypes);
		return result;
	}

	public FieldsGroupEntity getByInternalName(String internalName) {
		return getByInternalName(internalName, EXCLUDED_TYPES);
	}

	public FieldsGroupEntity getByInternalName(String internalName, List<FieldType> withoutTypes) {
		if (withoutTypes == null) {
			withoutTypes = new ArrayList<>();
		}
		FieldsGroupEntity result = findFirst(Restrictions.eq("internalName", internalName));
		removeTypes(result, withoutTypes);
		return result;
	}

	public List<FieldsGroupEntity> getByInternalNames(String ... internalNames) {
		List<FieldsGroupEntity> result = find(Order.asc("position"), Restrictions.in("internalName", Arrays.asList(internalNames)));
		removeTypes(result, EXCLUDED_TYPES);
		return result;
	}

	public List<FieldsGroupEntity> getByRameraListEditorItem(RameraListEditorItem rameraListEditorItem) {
		List<RameraListEditorItem> list = new ArrayList<>();
		list.add(rameraListEditorItem);
		// TODO http://projects.ramera.ru/browse/RAMERA-480 16 пункт
		/*while (rameraListEditorItem.getParent() != null) {
			rameraListEditorItem = rameraListEditorItem.getParent();
			list.add(rameraListEditorItem);
		}*/

		Criteria criteria = getCriteria();
		criteria.createAlias("associationForms", "associationFormsAlias");
		criteria.addOrder(Order.asc("position"));
		criteria.add(Restrictions.eq("associationFormsAlias.id", rameraListEditorItem.getId()));

		List<FieldsGroupEntity> result = find(criteria);

		/*Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.in("rameraListEditorItem", list));
		List<FieldsGroupEntity> result = find(Order.asc("position"), conjunction);*/

		removeTypes(result, EXCLUDED_TYPES);
		return result;
	}

	public List<FieldsGroupEntity> getAllAdditionalFieldsGroup() {
		Criteria criteria = getCriteria();
		criteria.addOrder(Order.asc("position"));
		criteria.add(Restrictions.sizeGt("associationForms", 0));
		return find(criteria);
	}
}