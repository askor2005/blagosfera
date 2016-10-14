package ru.askor.blagosfera.core.services.invite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.invite.InviteRelationshipTypeRepository;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;
import ru.radom.kabinet.model.invite.InviteRelationshipType;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 17.04.2016.
 */
@Service
@Transactional
public class InviteRelationShipTypeDataServiceImpl implements InviteRelationShipTypeDataService {
    @Autowired
    private InviteRelationshipTypeRepository inviteRelationshipTypeRepository;
    @Override
    public List<InviteRelationshipTypeDomain> findAllOrderByIndexAsc() {
        return inviteRelationshipTypeRepository.findAllByOrderByIndexAsc().stream().map(inviteRelationshipType -> inviteRelationshipType.toDomain()).collect(Collectors.toList());
    }

    @Override
    public InviteRelationshipTypeDomain save(InviteRelationshipTypeDomain inviteRelationshipTypeDomain) {
        boolean create = (inviteRelationshipTypeDomain.getId() == null);
        InviteRelationshipType inviteRelationshipType = inviteRelationshipTypeDomain.getId() != null ? inviteRelationshipTypeRepository.findOne(inviteRelationshipTypeDomain.getId()) : new InviteRelationshipType();
        inviteRelationshipType.setIndex(inviteRelationshipTypeDomain.getIndex());
        inviteRelationshipType.setName(inviteRelationshipTypeDomain.getName());
        InviteRelationshipType result = inviteRelationshipTypeRepository.saveAndFlush(inviteRelationshipType);
        if (create) {
            inviteRelationshipTypeDomain.setId(inviteRelationshipType.getId());
        }
        return result.toDomain();
    }
    @Override
    public void delete(Long id) {
        inviteRelationshipTypeRepository.delete(id);
    }

    @Override
    public InviteRelationshipTypeDomain getById(Long id) {
        InviteRelationshipType inviteRelationshipType = inviteRelationshipTypeRepository.findOne(id);
        return inviteRelationshipType != null ? inviteRelationshipType.toDomain() : null;
    }
}
