package ru.radom.kabinet.dao.registration;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;
import ru.radom.kabinet.model.registration.RegistratorLevel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.math.BigInteger;
import java.util.*;

/**
 *
 * Created by vzuev on 17.03.2015.
 */
@Transactional
@Repository
public class RegistratorDao {
    final static double RADIUS = 6371; // Radius of the earth

    @Autowired
    private SharerDao sharerDao;

    final static Comparator<RegistratorDomain> DISTANCE_COMPARATOR = (s1, s2) -> {
        if (Objects.equals(s1.getDistance(), s2.getDistance())) return 0;
        if (s1.getDistance() == null) return -1;
        if (s2.getDistance() == null) return 1;
        return s1.getDistance().compareTo(s2.getDistance());
    };

    @PersistenceContext(unitName = "kabinetPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    /**
     * Получить список регистраторов из участников сообществ.
     * @param members
     * @param latitude
     * @param longitude
     * @return
     */
    private List<RegistratorDomain> getRegistratorsFromCommunityMembers(List<CommunityMemberEntity> members, final Double latitude, final Double longitude){
        final List<RegistratorDomain> result = new ArrayList<>();

        for (final CommunityMemberEntity member : members) {
            Double distance = null;
            final Address address = sharerDao.getRegistratorOfficeAddress(member.getUser());
            if (longitude != null && latitude != null && address.getLongitude() != null && address.getLatitude() != null){
                distance = RegistratorDao.calculateDistance(latitude, address.getLatitude(), longitude, address.getLongitude(), 0d, 0d);
            }
            RegistratorLevel level = null;
            for(final CommunityPostEntity post: member.getPosts()){
                if((post.getMnemo() != null) && post.getMnemo().startsWith(RegistratorLevel.PREFIX)){
                    level = RegistratorLevel.getByMnemo(post.getMnemo());
                    break;
                }
            }
            UserEntity userEntity = sharerDao.getById(member.getUser().getId());
            if (userEntity != null) {
                result.add(new RegistratorDomain(userEntity.toDomain(), level, distance, null, null, null, null,false,false));
            }
        }
        Collections.sort(result, RegistratorDao.DISTANCE_COMPARATOR);
        return result;
    }

    /**
     * Получить регистратора по ИД пользователя.
     * @param id
     * @return
     */
    public RegistratorDomain getRegistratorById(Long id) {
        final Criteria criteria = em.unwrap(Session.class).createCriteria(CommunityMemberEntity.class);
        criteria.createAlias("user", "user");
        criteria.createAlias("posts", "posts");
        criteria.addOrder(Order.asc("user.searchString"));

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user.id", id));
        conjunction.add(Restrictions.ilike("posts.mnemo", RegistratorLevel.PREFIX, MatchMode.START));

        criteria.add(conjunction);
        final List<CommunityMemberEntity> communityMembers = criteria.list();
        List<RegistratorDomain> registrators = getRegistratorsFromCommunityMembers(communityMembers, null, null);

        RegistratorDomain result = null;
        if (registrators != null && registrators.size() > 0) {
            result = registrators.get(0);
        }
        return result;
    }

    public Integer count(Long currentUserId, List<String> excludeLevels) {
        final Criteria criteria = em.unwrap(Session.class).createCriteria(CommunityMemberEntity.class);
        criteria.createAlias("user", "user");
        criteria.createAlias("posts", "posts");

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.ilike("posts.mnemo", RegistratorLevel.PREFIX, MatchMode.START));
        if (currentUserId != null) conjunction.add(Restrictions.ne("user.id", currentUserId));

        if (excludeLevels != null && excludeLevels.size() > 0) {
            conjunction.add(Restrictions.not(Restrictions.in("posts.mnemo", excludeLevels)));
        }

        criteria.add(conjunction);

        return ((Long) criteria.setProjection(Projections.count("id")).uniqueResult()).intValue();
    }

    public UserEntity getById(final Long registratorId) {
        final Criteria criteria = em.unwrap(Session.class).createCriteria(CommunityMemberEntity.class);
        criteria.createAlias("user", "user");
        criteria.createAlias("posts", "posts");
        criteria.setMaxResults(1);

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user.id", registratorId));
        conjunction.add(Restrictions.ilike("posts.mnemo", RegistratorLevel.PREFIX, MatchMode.START));
        criteria.add(conjunction);
        final CommunityMemberEntity member = (CommunityMemberEntity) criteria.uniqueResult();

        return (member == null) ? null : member.getUser();
    }

    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     */
    public static double calculateDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
        Double latDistance = deg2rad(lat2 - lat1);
        Double lonDistance = deg2rad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS * c * 1000; // convert to meters

        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public String getRegistratorLevel(final Long registratorId) {
        final Criteria criteria = em.unwrap(Session.class).createCriteria(CommunityMemberEntity.class);
        criteria.createAlias("user", "user");
        criteria.createAlias("posts", "posts");
        criteria.setMaxResults(1);

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user.id", registratorId));
        conjunction.add(Restrictions.ilike("posts.mnemo", RegistratorLevel.PREFIX, MatchMode.START));
        criteria.add(conjunction);
        final CommunityMemberEntity member = (CommunityMemberEntity) criteria.uniqueResult();

        String level = "";
        if (member != null) {
            if (member.getPosts() != null) {
                if (member.getPosts().size() > 0) {
                    level = member.getPosts().get(0).getName();
                }
            }
        }

        return level;
    }

    public String getRegistratorLevelMnemo(final Long registratorId) {
        final Criteria criteria = em.unwrap(Session.class).createCriteria(CommunityMemberEntity.class);
        criteria.createAlias("user", "user");
        criteria.createAlias("posts", "posts");
        criteria.setMaxResults(1);

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user.id", registratorId));
        conjunction.add(Restrictions.ilike("posts.mnemo", RegistratorLevel.PREFIX, MatchMode.START));
        criteria.add(conjunction);
        final CommunityMemberEntity member = (CommunityMemberEntity) criteria.uniqueResult();

        String level = "";
        if (member != null) {
            if (member.getPosts() != null) {
                if (member.getPosts().size() > 0) {
                    level = member.getPosts().get(0).getMnemo();
                }
            }
        }

        return level;
    }

    private static final String VERIFIED_REGISTRATORS_BY_SHARER_COUNT_SQL = "SELECT COUNT(*)\n" +
            "FROM sharers S\n" +
            "INNER JOIN (\n" +
            "\tSELECT CM.sharer_id, CP.mnemo\n" +
            "\tFROM community_members CM\n" +
            "\tINNER JOIN community_members_posts CMP ON CM.id=CMP.member_id\n" +
            "\tINNER JOIN community_posts CP ON CMP.post_id=CP.id\n" +
            "\tWHERE CP.mnemo ILIKE 'registrator.%'\n" +
            ") AS P ON S.id=P.sharer_id\n" +
            "WHERE S.id != :verifier_id AND S.verifier_id = :verifier_id AND S.deleted=false AND S.verified=true";

    /**
     *Возвращает количество сертифицированных sharer'ом объединений
     * @param userId
     * @return
     */
    public Long getVerifiedRegistratorsCount(Long userId) {
        Query query = em.unwrap(Session.class).createSQLQuery(VERIFIED_REGISTRATORS_BY_SHARER_COUNT_SQL).setLong("verifier_id", userId);
        return ((BigInteger)query.uniqueResult()).longValue();
    }

    private static final String VERIFIED_REGISTRATORS_BY_SHARER_SQL = "SELECT S.id AS sharer_id\n" +
            "FROM sharers S\n" +
            "INNER JOIN (\n" +
            "\tSELECT CM.sharer_id, CP.mnemo\n" +
            "\tFROM community_members CM\n" +
            "\tINNER JOIN community_members_posts CMP ON CM.id=CMP.member_id\n" +
            "\tINNER JOIN community_posts CP ON CMP.post_id=CP.id\n" +
            "\tWHERE CP.mnemo ILIKE 'registrator.%'\n" +
            ") AS P ON S.id=P.sharer_id\n" +
            "WHERE S.id != :verifier_id AND S.verifier_id = :verifier_id AND S.deleted=false AND S.verified=true";

    public List<UserEntity> getVerifiedRegistrators(UserEntity userEntity) {
        Query query = em.unwrap(Session.class).createSQLQuery(VERIFIED_REGISTRATORS_BY_SHARER_SQL).addScalar("sharer_id", LongType.INSTANCE).setLong("verifier_id", userEntity.getId());
        List<Long> ids = (List<Long> )query.list();
        List<UserEntity> result = sharerDao.getByIds(ids);
        return result;
    }
}