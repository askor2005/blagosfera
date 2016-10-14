package ru.radom.kabinet.dao.discussion;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.services.discuss.CommentsTreeQueryResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("commentDao")
public class CommentDao extends Dao<CommentEntity> {
    /**
     * Query uses postgresql specific features
     */
    private static final java.lang.String SQL_QUERY = "with recursive tree ( id, parent_id, message, createdAt,  owner_id, owner_ikp, depth, path) AS (\n" +
            "select t1.id, t1.parent_id, t1.message, t1.createdAt, t1.sharer_id, s.ikp, 0 as depth, ARRAY[CAST(0 AS BIGINT)] as path\n"
            + "from comments t1 \n"
            + "    join discussions d on d.root_id=t1.id\n"
            + "    join sharers s on s.id = t1.sharer_id\n"
            + "where d.id=:discussion\n"
            + "union \n"
            + "select t2.id, t2.parent_id, t2.message, t2.createdAt, s.id,s.ikp, t.depth + 1, array_append(t.path,\n"
            + "CASE WHEN t.depth=0 THEN -t2.id\n"
            + "     ELSE t2.id\n"
            + "END)\n"
            + "from comments t2 \n"
            + "    join tree t on t2.parent_id = t.id\n"
            + "    join sharers s on s.id = t2.sharer_id\n"
            + ") select id, parent_id, message, createdAt, owner_id, owner_ikp, depth, lag(id, 1) over w as insert_after, (SELECT SUM(CASE WHEN votes.vote = 'UP' THEN 1 ELSE -1 END) FROM comment_votes votes WHERE votes.comment_id = tree.id) as rating "
            + "from tree "
            + "where depth > 0 " + "window w as (order by path)" + "order by path limit :num offset :start";

    public List<CommentEntity> getChildren(CommentEntity parent) {
        return find(getCriteria().add(Restrictions.eq("parent", parent)).addOrder(Order.desc("id")));
    }

    public List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion) {
        return getCommentsTree(discussion, 0, Integer.MAX_VALUE);
    }

    public List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion, int start, int limit) {

        return getCurrentSession().
                createSQLQuery(SQL_QUERY).
                setResultSetMapping("CommentsTreeQueryResult").
                setLong("discussion", discussion.getId()).
                setInteger("num", limit).
                setInteger("start", start).
                list();
    }

    // public Long countByDiscussion(Discussion parentDiscussion) {
    // // -1, т.к. корневой комментарий не считаем
    // return (Long)
    // getCurrentSession().createQuery("SELECT COUNT(c)-1 FROM CommentEntity c WHERE c.parentDiscussion = :parentDiscussion").setParameter("parentDiscussion",
    // parentDiscussion).uniqueResult();
    // }

    public CommentEntity getLast(Discussion discussion) {
        if (discussion == null) {
            return null;
        } else {
            return (CommentEntity) getCurrentSession().
                    createQuery("SELECT c FROM CommentEntity c WHERE c.parentDiscussion = :parentDiscussion AND c.id <> :parentRootId ORDER BY c.id DESC").
                    setParameter("parentDiscussion", discussion).
                    setParameter("parentRootId", discussion.getRoot().getId()).
                    setMaxResults(1).uniqueResult();
        }
    }

    public Map<Discussion, CommentEntity> getLasts(List<Discussion> discussions) {

        List<Long> discussionIds = getIdsList(discussions);
        List<Long> commentIds = discussionIds.isEmpty() ?
                Collections.emptyList() :
                createSQLQuery("WITH summary AS (SELECT c.*, ROW_NUMBER() OVER(PARTITION BY c.parent_discussion_id ORDER BY c.createdat DESC) AS rn FROM commentEntities c) SELECT s.id FROM summary s where rn = 1 and parent_discussion_id in (:discussions);").
                        addScalar("id", LongType.INSTANCE).
                        setParameterList("discussions", discussionIds, LongType.INSTANCE).
                        list();
        List<CommentEntity> commentEntities = getByIds(commentIds);
        //List<CommentEntity> commentEntities = ids.isEmpty() ? Collections.emptyList() : createSQLQuery("WITH summary AS (SELECT c.*, ROW_NUMBER() OVER(PARTITION BY c.parent_discussion_id ORDER BY c.createdat DESC) AS rn	FROM commentEntities c) SELECT s.* FROM summary s where rn = 1 and parent_discussion_id in (:discussions);").addEntity(CommentEntity.class).setParameterList("discussions", ids, LongType.INSTANCE).list();
        Map<Discussion, CommentEntity> map = new HashMap<Discussion, CommentEntity>();
        for (CommentEntity commentEntity : commentEntities) {
            map.put(commentEntity.getParentDiscussion(), commentEntity);
        }
        return map;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void flush() {
        getCurrentSession().flush();
    }

    public int getCommentRating(CommentEntity commentEntity) {
        final Long result = (Long) createQuery("SELECT COALESCE(SUM(CASE WHEN v.vote = 'UP' THEN 1 ELSE -1 END),0) FROM CommentVote v WHERE v.commentEntity = :commentEntity")
                .setParameter("comment", commentEntity)
                .uniqueResult();
        return result == null ? 0 : result.intValue();
    }

}
