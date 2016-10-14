package ru.radom.kabinet.module.blagosfera.bp.dao;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.AbstractDao;
import ru.radom.kabinet.module.blagosfera.bp.model.ProcessTreeItem;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * DAO для {@link ProcessTreeItem}
 */
@Repository
public class ProcessTreeItemDAO extends AbstractDao<ProcessTreeItem, Long> {

    /**
     * Сдвинуть всех детей на {@code shift} позиций
     * @param parent    родитель чьих детей двигаем
     * @param shift     сдвиг
     */
    public void shiftAllChildrenBy(ProcessTreeItem parent, long shift) {
        if(shift == 0) {
            return;
        }
        Query query = getCurrentSession().createQuery(
                "" +
                        "update ProcessTreeItem item " +
                        " set item.position = item.position + :shift" +
                        " where item.parent " + (parent == null ? "is null" : "= :parent")
        );
        if(parent != null) {
            query.setParameter("parent", parent);
        }
        query.setParameter("shift", shift)
                .executeUpdate();
    }

    /**
     * Посчитать количество корневых элементов
     */
    public int countRootElements() {
        return count(Restrictions.isNull("parent"));
    }
}
