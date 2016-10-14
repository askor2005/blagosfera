package ru.radom.kabinet.dao.fields;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.fields.MetaField;

import java.util.Collections;
import java.util.List;

@Repository("metaFieldDao")
public class MetaFieldDao extends Dao<MetaField> {
	public MetaField getByInternalName(String internalName) {
		return findFirst(Restrictions.eq("internalName", internalName));
	}

	public List<MetaField> getByGroups(List<FieldsGroupEntity> fieldsGroups) {
		if (fieldsGroups.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return find(Restrictions.in("fieldsGroup", fieldsGroups));
		}
	}

	public List<MetaField> getByParticipantType(String participantType) {
		return find(Restrictions.eq("participantType", participantType));
	}

	public List<MetaField> getList(String participantType, List<FieldsGroupEntity> fieldsGroups) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("participantType", participantType));
		if (!fieldsGroups.isEmpty()) {
			conjunction.add(Restrictions.in("fieldsGroup", fieldsGroups));
		}
		return find(conjunction);
	}
}