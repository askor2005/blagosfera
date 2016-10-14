package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.JournalAttention;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.Roles;

import java.util.*;

@Repository("journalAttentionDao")
public class JournalAttentionDao extends Dao<JournalAttention> {
    private static class Query {
        /**
         * Обновление тега в журнале (используется регулярное выражение)
         */
        public static final String UPDATE_TAG_KVANT = "UPDATE " + JournalAttention.TABLE_NAME + " SET " +
                JournalAttention.Columns.TAG_KVANT + " = regexp_replace(" + JournalAttention.Columns.TAG_KVANT + ", :oldTag, :newTag, 'g') " +
                "WHERE " +
                "  " + JournalAttention.Columns.TAG_KVANT + " like :tagKvant and " +
                "  " + JournalAttention.Columns.COMMUNITY + " = :communityId";
    }

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SharerDao sharerDao;

    /**
     * Получить список всех сообществ в которых состоит пользователь
     * @return List<CommunityEntity>
     */
    private List<CommunityEntity> getCurrentUserCommunitiesList() {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        return communityDao.getList(userEntity.getId(), userDetails.hasRole(Roles.ROLE_ADMIN), Arrays.asList(CommunityMemberStatus.MEMBER, CommunityMemberStatus.REQUEST_TO_LEAVE), null, 0, 20, "", null, null, null, null, true, false, "name", true);
    }

    public List<JournalAttention> getListByFixTimeKvant(Date fixTimeKvantBegin, Date fixTimeKvantEnd) {
        return find(Restrictions.between("fixTimeKvant", fixTimeKvantBegin, fixTimeKvantEnd));
    }

    public List<JournalAttention> getListByTextKvant(String textKvant) {
        return find(Restrictions.like("textKvant", textKvant));
    }

    public List<JournalAttention> getListByTagKvant(String tagKvant, int firstResult, int maxResults) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.ilike("tagKvant", tagKvant, MatchMode.ANYWHERE));
        return find(Order.desc("fixTimeKvant"), firstResult, maxResults, conjunction);
    }

    public List<JournalAttention> getListByPerformersKvant(Collection<UserEntity> userEntities) {
        return find(Restrictions.in("performerKvant", userEntities));
    }

    public void updateTagKvant(String oldTag, String newTag, Long communityId) {
        createSQLQuery(Query.UPDATE_TAG_KVANT)
                .setString("oldTag", "[[:<:]]" + oldTag + "[[:>:]]")
                .setString("newTag", newTag)
                .setString("tagKvant", "%" + oldTag + "%")
                .setLong("communityId", communityId)
                .executeUpdate();
    }

    public List<JournalAttention> getJournalAttentionList(Map<String, String> filters, int firstResult, int maxResults, String sort) {
        List<JournalAttention> list;
        String sortProperty = "";
        String sortDirection = "";

        if (! sort.equals("")) {
            JSONArray jsonArray = new JSONArray(sort);
            sortProperty = jsonArray.getJSONObject(0).getString("property");
            sortDirection = jsonArray.getJSONObject(0).getString("direction");
        }

        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        if (!filters.get("filterField").equals("all")) {
            if (!filters.get("tagKvant").equals("")) {
                conjunction.add(Restrictions.ilike("tagKvant", filters.get("tagKvant"), MatchMode.ANYWHERE));
            }

            if (!filters.get("fixTimeKvantBegin").equals("") && !filters.get("fixTimeKvantEnd").equals("")) {
                Date fixTimeKvantBegin = DateUtils.stringToDate(filters.get("fixTimeKvantBegin"), "yyyy-MM-dd'T'HH:mm:ss");
                Date fixTimeKvantEnd = DateUtils.stringToDate(filters.get("fixTimeKvantEnd"), "yyyy-MM-dd'T'HH:mm:ss");

                conjunction.add(Restrictions.between("fixTimeKvant", fixTimeKvantBegin, fixTimeKvantEnd));
            }
        } else {
            if (!filters.get("filterText").equals("")) {
                conjunction.add(Restrictions.disjunction(Restrictions.ilike("tagKvant", filters.get("filterText"), MatchMode.ANYWHERE), Restrictions.ilike("textKvant", filters.get("filterText"), MatchMode.ANYWHERE)));
            } else {
                return new ArrayList<>();
            }
        }

        if (sort.equals("")) {
            list = find(firstResult, maxResults, conjunction);
        } else {
            if (sortDirection.equals("ASC")) {
                list = find(Order.asc(sortProperty), firstResult, maxResults, conjunction);
            } else {
                list = find(Order.desc(sortProperty), firstResult, maxResults, conjunction);
            }
        }

        return list;
    }

    public int getJournalAttentionCount(Map<String, String> filters) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return 0;
        }

        if (filters != null) {
            if (!filters.get("filterField").equals("all")) {
                if (!filters.get("tagKvant").equals("")) {
                    conjunction.add(Restrictions.ilike("tagKvant", filters.get("tagKvant"), MatchMode.ANYWHERE));
                }

                if (!filters.get("fixTimeKvantBegin").equals("") && !filters.get("fixTimeKvantEnd").equals("")) {
                    Date fixTimeKvantBegin = DateUtils.stringToDate(filters.get("fixTimeKvantBegin"), "yyyy-MM-dd'T'HH:mm:ss");
                    Date fixTimeKvantEnd = DateUtils.stringToDate(filters.get("fixTimeKvantEnd"), "yyyy-MM-dd'T'HH:mm:ss");

                    conjunction.add(Restrictions.between("fixTimeKvant", fixTimeKvantBegin, fixTimeKvantEnd));
                }
            } else {
                if (!filters.get("filterText").equals("")) {
                    conjunction.add(Restrictions.disjunction(Restrictions.ilike("tagKvant", filters.get("filterText"), MatchMode.ANYWHERE), Restrictions.ilike("textKvant", filters.get("filterText"), MatchMode.ANYWHERE)));
                } else {
                    return 0;
                }
            }
        }

        return count(conjunction);
    }

    public List<JournalAttention> searchTextKvant(String query, int firstResult, int maxResults) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.ilike("textKvant", query, MatchMode.ANYWHERE));
        return find(Order.asc("textKvant"), firstResult, maxResults, conjunction);
    }

    public List<JournalAttention> searchTagKvant(String query, int firstResult, int maxResults) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.ilike("tagKvant", query, MatchMode.ANYWHERE));
        return find(Order.asc("tagKvant"), firstResult, maxResults, conjunction);
    }
}