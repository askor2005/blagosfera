package ru.radom.kabinet.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.RameraTextEntity;

/**
 * Created by ebelyaev on 26.08.2015.
 */
@Repository("rameraTextDao")
public class RameraTextDao extends Dao<RameraTextEntity> {
    public RameraTextEntity getByCode(String code) {
        return findFirst(Restrictions.eq("code", code));
    }
}
