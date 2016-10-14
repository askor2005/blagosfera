package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.schema.CommunitySchema;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaConnectionTypeEntity;

import java.util.List;

/**
 * Created by mnikitin on 17.06.2016.
 */
public interface CommunitySchemaService {

    List<CommunitySchemaConnectionTypeEntity> getConnectionTypes();

    void saveSchema(Long communityId, CommunitySchema schema);
}
