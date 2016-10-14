package ru.radom.kabinet.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Абстрактный класс для доступа к данным. Позволяет работать с любым типом
 * первичного ключа.
 * 
 * @author aatapin
 *
 * @param <ID>
 *            тип идентификатора
 * @param <E>
 *            тип сущности
 */
@Transactional
public abstract class AbstractDao<E, ID extends Serializable> {

	@PersistenceContext(unitName = "kabinetPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
	
	private Class<E> persistentClass;

	protected Class<E> getPersistentClass() {
		return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Session getCurrentSession() {
        return em.unwrap(Session.class);
	}

	public SQLQuery createSQLQuery(String sql) {
		return getCurrentSession().createSQLQuery(sql);
	}

	public Query createQuery(String hql) {
		return getCurrentSession().createQuery(hql);
	}

	public AbstractDao() {
		this.persistentClass = getPersistentClass();
	}

	/**
	 * Создает прокси объект и не делает селект в базу, полезен для выставления fk
	 */
	public E loadById(ID id) {
		return (E) getCurrentSession().load(persistentClass, id);
	}

	public E getById(ID id) {
		return (E) getCurrentSession().get(persistentClass, id);
	}

	public void delete(ID id) {
		getCurrentSession().delete(getById(id));
	}

	public E getByIdOrEmpty(ID id) {
		E entity = id != null ? (E) getCurrentSession().get(persistentClass, id) : null;
		if (entity == null) {
			try {
				return persistentClass.newInstance();
			} catch (InstantiationException exception) {
				exception.printStackTrace();
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
			}
		}
		return entity;
	}

	protected Criteria getCriteria() {
		return getCurrentSession().createCriteria(persistentClass);
	}

	public Criteria getCriteria(Order order) {
		return getCriteria().addOrder(order);
	}
	
	public Criteria getCriteria(Order order, int firstResult, int maxResults) {
		return getCriteria().addOrder(order).setFirstResult(firstResult).setMaxResults(maxResults);
	}

	public E findFirst(Criterion... criterions) {
		return findFirst(null, criterions);
	}

	public E findFirst(Order order, Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		criteria.setMaxResults(1);
		if (order != null) {
			criteria.addOrder(order);
		}
		E entity = (E) criteria.uniqueResult();
		return entity;
	}

	public List<E> find(int firstResult, int maxResults, Criterion... criterions) {
		return find(null, firstResult, maxResults, criterions);
	}

	public List<E> find(Criteria criteria) {
		// TODO GusevV: Добавил уникальность возвращаемых сущностей.
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}

	public List<E> find(Order order, int firstResult, int maxResults, Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		if (order != null) {
			criteria.addOrder(order);
		}
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);

		return find(criteria);
	}

	public List<E> find(Order order, Criterion... criterions) {
		return find(order, 0, Integer.MAX_VALUE, criterions);
	}

	public List<E> find(Criterion... criterions) {
		return find(null, criterions);
	}

    /**
     *  Возвращаем результат работы агрегирующей функции
     * @param criterions
     * @return
     */
    public Object aggregate(Projection projection,Criterion... criterions) {
        // return find(null, criterions).size();
        Criteria criteria = getCriteria();
        for (Criterion criterion : criterions) {
            criteria.add(criterion);
        }
        return criteria.setProjection(projection).uniqueResult();
    }


    public int count(Criterion... criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		return count(criteria);
	}

	public int count(Criteria criteria) {
		return ((Long) criteria.setProjection(Projections.count("id")).uniqueResult()).intValue();
	}

	public boolean exists(Criterion... criterions) {
		return count(criterions) > 0;
	}

	public Long save(E entity) {
		return (Long) getCurrentSession().save(entity);
	}

    public E merge(E entity) {
        return (E) getCurrentSession().merge(entity);
    }

	public void update(E entity) {
        getCurrentSession().update(entity);
	}

	public void saveOrUpdate(E entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	public void delete(E entity) {
		if (!getCurrentSession().contains(entity)) {
			entity = (E) getCurrentSession().merge(entity);
		}
		getCurrentSession().delete(entity);
	}

	public List<E> findAll() {
		return find();
	}

	public List<E> findAll(Order order) {
		return find(order);
	}

	public void refresh(E entity) {
		getCurrentSession().refresh(entity);
	}
}
