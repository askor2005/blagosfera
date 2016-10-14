package ru.radom.kabinet.dao.invite;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.invite.InviteRelationshipType;

import java.util.List;

@Repository("inviteRelationshipTypeDao")
public class InviteRelationshipTypeDao extends Dao<InviteRelationshipType> {
    public List<InviteRelationshipType> getList() {
        return find(Order.asc("index"));
    }
}