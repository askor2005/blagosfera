package ru.radom.kabinet.dao.notifications;

/**
 * Created by rkorablin on 13.05.2015.
 */

// TODO Удалить
/*
@Repository
public class NotificationTemplateDao extends Dao<NotificationTemplate> {

    public List<NotificationTemplate> getAll() {
        return findAll(Order.asc("mnemo"));
    }

    public boolean checkMnemo(NotificationTemplate template) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("mnemo", template.getMnemo()));
        if (template.getId() != null) {
            conjunction.add(Restrictions.ne("id", template.getId()));
        }
        return count(conjunction) == 0;
    }

    public NotificationTemplate getByMnemo(String mnemo) {
        return findFirst(Restrictions.eq("mnemo", mnemo));
    }
}*/
