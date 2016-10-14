package ru.askor.blagosfera.core.services.invite;

import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;

import java.util.List;

/**
 * Created by vtarasenko on 17.04.2016.
 */
public interface InviteRelationService {
    void delete(Long id);

    InviteRelationshipTypeDomain create();

    InviteRelationshipTypeDomain update(Long id, String name);

    List<InviteRelationshipTypeDomain> findAll();

    void updateIndexes(Long[] ids);
}
