package ru.radom.kabinet.document.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.AbstractDao;
import ru.radom.kabinet.document.model.DocumentEntity;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by vgusev on 15.06.2015.
 * Класс доступа к данным сущности Документ.
 */
@Repository("flowOfDocumentDao")
public class FlowOfDocumentDao extends AbstractDao<DocumentEntity, Long> {

    // Получить документ по хеш коду
    public DocumentEntity getByHashCode(String hashCode) {
        return (DocumentEntity)getCriteria().add(Restrictions.eq("hashCode", hashCode)).uniqueResult();
    }

    /**
     * Получить список документов по ИД класса документов.
     * @param documentClassId
     * @return
     */
    public List<DocumentEntity> find(Long documentClassId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("documentClassId", documentClassId));
        return find(criteria);
    }

    private static final String GET_FILTERED_DOCS_SQL =
            "select distinct docs.id from flowofdocument as docs " +
                    "where ";

    /**
     * Фильтрация по полям документа.
     * @param documentClassId
     * @param createDateStart
     * @param createDateEnd
     * @param name
     * @param participantsFilters
     * @param content
     * @return
     */
    public List<DocumentEntity> filter(Long documentClassId, Date createDateStart, Date createDateEnd, String name, Map<Long, List<String>> participantsFilters, String content) {

        List<String> expressions = new ArrayList<>();
        expressions.add("1 = 1");

        if (documentClassId != null && documentClassId > 0) {
            expressions.add("docs.class_id = :documentClassId");
        }
        if (createDateStart != null && createDateEnd != null) {
            expressions.add("docs.create_date between :createDateStart and :createDateEnd");
        }
        if (name != null && !name.equals("")) {
            expressions.add("lower(docs.name) like lower(:name)");
        }
        if (content != null && !content.equals("")) {
            expressions.add("lower(docs.content) like lower(:content)");
        }
        for (Long participantId : participantsFilters.keySet()) {
            List<String> participantTypes = participantsFilters.get(participantId);
            List<String> orExpressions = new ArrayList<>();
            for (int i=0; i<participantTypes.size(); i++) {
                orExpressions.add("part_" + participantId + ".participant_type_name = :participantType_" + participantId + "_" +  i);
            }
            String orExpression = "(" + StringUtils.join(orExpressions, " or ") + ")";
            String participantExpression =
                    "exists (select id from flowofdocumentparticipant as part_" + participantId +
                    " where (" + orExpression + " and part_" + participantId + ".source_participant_id = :participantId_" + participantId + ") and part_" + participantId + ".document_id = docs.id)";
            expressions.add(participantExpression);
        }

        Query query = createSQLQuery(GET_FILTERED_DOCS_SQL + StringUtils.join(expressions, " and "));

        if (documentClassId != null && documentClassId > 0) {
            query.setLong("documentClassId", documentClassId);
        }
        if (createDateStart != null && createDateEnd != null) {
            query.setDate("createDateStart", createDateStart);
            query.setDate("createDateEnd", createDateEnd);
        }
        if (name != null && !name.equals("")) {
            query.setString("name", "%" + name + "%");
        }
        if (content != null && !content.equals("")) {
            query.setString("content", "%" + content + "%");
        }
        for (Long participantId : participantsFilters.keySet()) {
            List<String> participantTypes = participantsFilters.get(participantId);
            for (int i=0; i<participantTypes.size(); i++) {
                String participantType = participantTypes.get(i);
                query.setString("participantType_" + participantId + "_" +  i, participantType);
            }
            query.setLong("participantId_" + participantId, participantId);
        }
        return getDocumentsByQuery(query);
    }

    private static final String GET_NOT_SIGNED_BY_PARTICIPANT_DOCS_SQL =
            "select distinct docs.id from flowofdocument as docs " +
            "join flowofdocumentparticipant as part on part.document_id = docs.id " +
            "where part.is_signed = false and " +
            "part.source_participant_id = :source_participant_id and " +
            "part.participant_type_name in (:participant_types) and " +
            "(part.need_sign_document = true or part.need_sign_document is null);";

    /**
     * Найти все неподписанные документы участника
     * @param participantTypes
     * @param participantId
     * @return
     */
    public List<DocumentEntity> findNotSignedDocuments(List<String> participantTypes, Long participantId) {
        Query query = createSQLQuery(GET_NOT_SIGNED_BY_PARTICIPANT_DOCS_SQL)
                .setLong("source_participant_id", participantId)
                .setParameterList("participant_types", participantTypes, StringType.INSTANCE);

        return getDocumentsByQuery(query);
/*
        Criteria criteria = getCriteria();

        criteria.createAlias("participants", "participant_list");
        criteria.add(Restrictions.eq("participant_list.sourceParticipantId", participantId));

        Disjunction disjunction = Restrictions.or();
        for (String participantType : participantTypes) {
            disjunction.add(Restrictions.eq("participant_list.participantTypeName", participantType));
        }

        criteria.add(Restrictions.eq("participant_list.isSigned", false))
                .add(Restrictions.eq("participant_list.isNeedSignDocument", true))
                .addOrder(Order.asc("id")).add(disjunction);
        return criteria.list();*/
    }

    private static final String GET_NOT_SIGNED_BY_CHILD_PARTICIPANT_DOCS_SQL =
            "select distinct docs.id from flowofdocument as docs " +
            "join flowofdocumentparticipant as part on part.document_id = docs.id " +
            "join flowofdocumentparticipant as child on child.parent_id = part.id " +
            "where child.is_signed = false and child.need_sign_document and " +
            "part.source_participant_id = :parent_source_participant_id and " +
            "child.source_participant_id = :source_participant_id and ";

    /**
     * Найти все неподписанные документы дочернего участника
     * @param participantTypes
     * @param participantId
     * @return
     */
    public List<DocumentEntity> findNotSignedChildParticipants(List<String> participantTypes, Long participantId, List<String> parentParticipantTypes, Long parentParticipantId) {
        List<String> orStrings = new ArrayList<>();
        for (int i = 0; i < participantTypes.size(); i++){
            orStrings.add("child.participant_type_name = :participant_type_name_" + i);
        }
        List<String> orStringsParent = new ArrayList<>();
        for (int i = 0; i < parentParticipantTypes.size(); i++){
            orStringsParent.add("part.participant_type_name = :parent_participant_type_name_" + i);
        }
        String orExpression = " (" + StringUtils.join(orStrings, " or ") + ") ";
        String orExpressionParent = "and (" + StringUtils.join(orStringsParent, " or ") + ") ";
        Query query = createSQLQuery(GET_NOT_SIGNED_BY_CHILD_PARTICIPANT_DOCS_SQL + orExpression + orExpressionParent);
        query.setLong("source_participant_id", participantId);
        query.setLong("parent_source_participant_id", parentParticipantId);
        for (int i = 0; i < participantTypes.size(); i++){
            String participantType = participantTypes.get(i);
            query.setString("participant_type_name_" + i, participantType);
        }
        for (int i = 0; i < parentParticipantTypes.size(); i++){
            String parentParticipantType = parentParticipantTypes.get(i);
            query.setString("parent_participant_type_name_" + i, parentParticipantType);
        }
        return getDocumentsByQuery(query);
    }

    private List<DocumentEntity> getDocumentsByQuery(Query query) {
        List<BigInteger> docIds = query.list();
        List<Long> ids = new ArrayList<>();
        for (BigInteger bigInteger : docIds) {
            ids.add(bigInteger.longValue());
        }
        List<DocumentEntity> documents;
        if (ids != null && ids.size() > 0) {
            documents = getByIds(ids);
        } else {
            documents = Collections.emptyList();
        }
        return documents;
    }

    public List<DocumentEntity> getByIds(List<Long> ids) {
        return find(Restrictions.in("id", ids));
    }

    public DocumentEntity getByCode(String code) {
        DocumentEntity result = null;
        List<DocumentEntity> found = find(Restrictions.eq("code", code));
        if (found != null && found.size() == 1) {
            result = found.get(0);
        }
        return result;
    }

    /**
     * Получить количество документов по префиксу кода документа.
     * @param codePrefix
     * @return
     */
    public int countByCodePrefix(String codePrefix) {
        return count(Restrictions.ilike("code", codePrefix, MatchMode.START));
    }

    /**
     * Найти неподписанные документы по параметрам
     * @param parameterName
     * @param participantTypeName
     * @param participantId
     * @return
     */
    public List<DocumentEntity> findByParameterAndParticipant(String parameterName, String participantTypeName, Long participantId) {
        Criteria criteria = getCriteria();

        criteria.add(Restrictions.isNull("code")); // Если код null значит он подписан не всеми
        criteria.addOrder(Order.desc("createDate"));
        criteria.createAlias("participants", "participantsAlias");
        criteria.createAlias("parameters", "parametersAlias");

        criteria.add(Restrictions.eq("participantsAlias.participantTypeName", participantTypeName));
        criteria.add(Restrictions.eq("participantsAlias.sourceParticipantId", participantId));
        criteria.add(Restrictions.eq("parametersAlias.name", parameterName));

        return find(criteria);
    }

    /**
     * Получить документы для участника
     * @param participant           участник
     * @param signedByParticipant   если null, то параметр не учитывается, если нет, то только с необходимым признаком подписаны для этого участника
     * @param signed                если null, то параметр не учитывается, если нет, то только с необходимым признаком подписаны
     * @param startDate             если не null, то выбираются документы старше этой даты
     * @param endDate               если не null, то выбираются документы младше этой даты
     */
    public List<DocumentEntity> findDocumentsOfParticipant(
        long participant,
        Boolean signedByParticipant,
        Boolean signed,
        Date startDate,
        Date endDate
    ) {
        Criteria criteria = getCriteria();

        if(signed != null) {
            criteria.add(signed ? Restrictions.isNotNull("code") : Restrictions.isNull("code"));
        }
        criteria.add(Restrictions.or(
            createParticipantConjunction(participant, signedByParticipant, criteria, "participants", "p", JoinType.INNER_JOIN),
            createParticipantConjunction(participant, signedByParticipant, criteria, "p.children", "c", JoinType.LEFT_OUTER_JOIN)
        ));
        if(startDate != null) {
            criteria.add(Restrictions.ge("createDate", truncate(startDate, true)));
        }
        if(endDate != null) {
            criteria.add(Restrictions.le("createDate", truncate(endDate, false)));
        }
        criteria.addOrder(Order.desc("id"));
        return find(criteria);
    }

    private Date truncate(Date d, boolean toStartOfDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, toStartOfDay ? 0 : 23);
        cal.set(Calendar.MINUTE, toStartOfDay ? 0 : 59);
        cal.set(Calendar.SECOND, toStartOfDay ? 0 : 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Conjunction createParticipantConjunction(
        long participant, Boolean signedByParticipant, Criteria criteria, String path, String alias, JoinType joinType
    ) {
        criteria.createAlias(path, alias, joinType);
        Conjunction p = Restrictions.and(Restrictions.eq(alias + ".sourceParticipantId", participant));
        p.add(Restrictions.eq(alias + ".isNeedSignDocument", true));
        if(signedByParticipant != null) {
            p.add(Restrictions.eq(alias + ".isSigned", signedByParticipant));
        }
        return p;
    }
}
