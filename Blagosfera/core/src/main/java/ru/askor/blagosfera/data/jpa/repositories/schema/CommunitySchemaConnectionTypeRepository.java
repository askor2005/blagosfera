package ru.askor.blagosfera.data.jpa.repositories.schema;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaConnectionTypeEntity;

/**
 * Created by mnikitin on 21.06.2016.
 */
public interface CommunitySchemaConnectionTypeRepository extends JpaRepository<CommunitySchemaConnectionTypeEntity, Long> {
}
