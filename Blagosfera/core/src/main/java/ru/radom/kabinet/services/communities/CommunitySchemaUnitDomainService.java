package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;

import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
public interface CommunitySchemaUnitDomainService {

    /**
     *
     * @param id
     * @return
     */
    CommunitySchemaUnit getById(Long id);

    /**
     *
     * @param communityId
     * @param type
     * @return
     */
    List<CommunitySchemaUnit> getByCommunityId(Long communityId, CommunitySchemaUnitType type);
}
