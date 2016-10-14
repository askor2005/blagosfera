package ru.radom.kabinet.document.dao;

import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.radom.kabinet.dao.AbstractDao;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vgusev on 30.06.2015.
 */
@Repository("flowOfDocumentParticipantDao")
public class FlowOfDocumentParticipantDao extends AbstractDao<DocumentParticipantEntity, Long> {

    public List<DocumentParticipantEntity> find(String participantType, Long participantId) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("source_participant_id", participantId)).add(Restrictions.eq("participant_type_name", participantType));
        return find(conjunction);
    }

    /*public List<DocumentParticipantEntity> filterParticipants(Long documentClassId, Long participantId) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("source_participant_id", participantId)).add(Restrictions.eq("participant_type_name", participantType));
        return find(conjunction);
    }*/

    /**
     * Ищет участников документа, которые должна подписывать его, по id или коду
     */
    public List<DocumentParticipantEntity> findDocumentParticipants(Long id, String code) {
        if(id == null && code == null) {
            return Collections.emptyList();
        }
        Query query = getCurrentSession().createQuery("" +
                "from FlowOfDocumentParticipant p" +
                " left outer join fetch p.children" +
                " where p.document." + (id == null ? "code" : "id") + " = :var"
        );
        query.setParameter("var", id == null ? code : id);
        List<DocumentParticipantEntity> list = query.list();
        return list.stream()
            .flatMap(p -> {
                ParticipantsTypes type = ParticipantsTypes.valueOf(p.getParticipantTypeName());
                switch (type) {
                    case COMMUNITY_WITH_ORGANIZATION:
                    case COMMUNITY_WITH_ORGANIZATION_LIST:
                        return CollectionUtils.isEmpty(p.getChildren()) ? Stream.empty() : p.getChildren().stream();
                    default:
                        return Stream.of(p);
                }
            })
            .filter(DocumentParticipantEntity::getIsNeedSignDocument)
            .collect(Collectors.toList());
    }
}
