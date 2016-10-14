package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;
import ru.radom.kabinet.dao.communities.schema.CommunitySchemaUnitDao;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Service
@Transactional
public class CommunitySchemaUnitDomainServiceImpl implements CommunitySchemaUnitDomainService {

    @Autowired
    private CommunitySchemaUnitDao communitySchemaUnitDao;

    @Override
    public CommunitySchemaUnit getById(Long id) {
        return CommunitySchemaUnitEntity.toDomainSafe(communitySchemaUnitDao.getById(id));
    }

    @Override
    public List<CommunitySchemaUnit> getByCommunityId(Long communityId, CommunitySchemaUnitType type) {
        return CommunitySchemaUnitEntity.toListDomain(communitySchemaUnitDao.getByCommunityId(communityId, type));
    }
}
