package ru.askor.blagosfera.core.services.invite;

import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;

import java.util.List;

/**
 * Created by vtarasenko on 17.04.2016.
 */
public interface InviteRelationShipTypeDataService {
    List<InviteRelationshipTypeDomain> findAllOrderByIndexAsc();

    InviteRelationshipTypeDomain save(InviteRelationshipTypeDomain inviteRelationshipTypeDomain);

    void delete(Long id);

    InviteRelationshipTypeDomain getById(Long id);
}
