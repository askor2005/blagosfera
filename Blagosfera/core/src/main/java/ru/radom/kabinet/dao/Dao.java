package ru.radom.kabinet.dao;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.radom.kabinet.model.LongIdentifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Базовый класс доступа к данным. Для каждого класса модели должен быть создан
 * параметризованный наследник этого класса, например для Sharer имеется
 * SharerDao и т.д.
 * 
 * @author rkorablin
 * 
 * */

public abstract class Dao<E extends LongIdentifiable> extends AbstractDao<E, Long> {

	public List<Long> getIdsList(List<? extends LongIdentifiable> entities) {
		ArrayList<Long> ids = new ArrayList<Long>();
		for (LongIdentifiable entity : entities) {
			if (entity != null) {
				ids.add(entity.getId());
			}
		}
		return ids;
	}

	/**
	 * Загрузить по ИДам
	 * @param ids
	 * @return
	 */
	public List<E> loadByIds(List<Long> ids) {
		List<E> result = new ArrayList<>();
		for (Long id : ids) {result.add(loadById(id));}
		return result;
	}

	public List<E> getByIds(List<Long> ids) {
		return getByIds(null, ids);
	}

	public List<E> getByIds(Order order, List<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		} else {

			if (order != null) {
				return find(order, Restrictions.in("id", ids));
			} else {
				return find(Restrictions.in("id", ids));
			}
		}
	}

	public List<E> getByIds(long[] idsArray) {
		return getByIds(null, idsArray);
	}

	public List<E> getByIds(Order order, long[] idsArray) {
		List<Long> idsList = new ArrayList<Long>();
		for (long id : idsArray) {
			idsList.add(id);
		}
		return getByIds(order, idsList);
	}

}
