package ru.radom.kabinet.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.EmailTemplate;

/**
 *
 * @author dfilinberg
 */
@Repository
public class EmailTemplateDao extends Dao<EmailTemplate> {

    public EmailTemplate findByTitle(String title) {
        return findFirst(Restrictions.eq("title", title));
    }

}
