package ru.askor.blagosfera.data.jpa.repositories.schema;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitEntity;

/**
 * Created by mnikitin on 21.06.2016.
 */
public interface CommunitySchemaUnitRepository extends JpaRepository<CommunitySchemaUnitEntity, Long> {

}
