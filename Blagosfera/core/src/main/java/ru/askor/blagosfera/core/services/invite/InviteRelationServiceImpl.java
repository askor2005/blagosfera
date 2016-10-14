package ru.askor.blagosfera.core.services.invite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by vtarasenko on 17.04.2016.
 */
@Transactional
@Service
public class InviteRelationServiceImpl implements InviteRelationService {
    @Autowired
    private InviteRelationShipTypeDataService inviteRelationShipTypeDataService;
    @Override
    public void delete(Long id) {
       inviteRelationShipTypeDataService.delete(id);
    }
    @Override
    public InviteRelationshipTypeDomain create() {
        InviteRelationshipTypeDomain relationshipType = new InviteRelationshipTypeDomain();
        relationshipType.setName("");
        relationshipType.setIndex(0);
        return inviteRelationShipTypeDataService.save(relationshipType);
    }
    @Override
    public InviteRelationshipTypeDomain update(Long id, String name) {
        InviteRelationshipTypeDomain inviteRelationshipTypeDomain  = inviteRelationShipTypeDataService.getById(id);
        assert inviteRelationshipTypeDomain != null;
        if (name != null && !name.equals("")) {
            inviteRelationshipTypeDomain.setName(name);
        }
        return inviteRelationShipTypeDataService.save(inviteRelationshipTypeDomain);
    }
    @Override
    public List<InviteRelationshipTypeDomain> findAll() {
        return inviteRelationShipTypeDataService.findAllOrderByIndexAsc();
    }

    @Override
    public void updateIndexes(Long[] ids) {
        for (int i = 0;i < ids.length;++i) {
            InviteRelationshipTypeDomain inviteRelationshipTypeDomain = inviteRelationShipTypeDataService.getById(ids[i]);
            if (inviteRelationshipTypeDomain != null) {
                inviteRelationshipTypeDomain.setIndex(i);
                inviteRelationShipTypeDataService.save(inviteRelationshipTypeDomain);
            }
        }
    }
}
