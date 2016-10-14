package ru.askor.blagosfera.data.jpa.repositories.invite;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.invite.InviteRelationshipType;

import java.util.List;

/**
 * Created by vtarasenko on 17.04.2016.
 */
public interface InviteRelationshipTypeRepository extends JpaRepository<InviteRelationshipType,Long>{
    List<InviteRelationshipType> findAllByOrderByIndexAsc();

}
