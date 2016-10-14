package ru.askor.blagosfera.data.jpa.repositories.cms;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.radom.kabinet.model.web.Section;

/**
 * Created by vtarasenko on 25.06.2016.
 */
public interface SectionRepository extends CrudRepository<Section,Long>, JpaSpecificationExecutor<Section> {
}
