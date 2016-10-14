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
import ru.radom.kabinet.model.cyberbrain.Thesaurus;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.Roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository("thesaurusDao")
public class ThesaurusDao extends Dao<Thesaurus> {

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

    public Thesaurus getByEssence(String essence, CommunityEntity community) {
        Conjunction conjunction = new Conjunction();

        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        conjunction.add(Restrictions.ilike("essence", essence));
        return findFirst(conjunction);
    }

    public List<Thesaurus> getByEssenceList(String essence) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.ilike("essence", essence));

        return find(conjunction);
    }

    public List<Thesaurus> getByEssenceList(String essence, CommunityEntity community) {
        Conjunction conjunction = new Conjunction();

        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        conjunction.add(Restrictions.ilike("essence", essence));

        return find(conjunction);
    }

    public List<Thesaurus> getThesaurusList(String filterField, String filterText, int firstResult, int maxResults, String sort) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        List<Thesaurus> list;
        String property = "";
        String direction = "";

        if (! sort.equals("")) {
            JSONArray jsonArray = new JSONArray(sort);
            property = jsonArray.getJSONObject(0).getString("property");
            direction = jsonArray.getJSONObject(0).getString("direction");

            int idx = property.indexOf("_");
            if (idx > 0) {
                property = property.substring(0, idx) +
                        property.substring(idx + 1, idx + 2).toUpperCase() +
                        property.substring(idx + 2);
            }
        }

        if (filterText.equals("")) {
            if (!filterField.equals("all")) {
                if (sort.equals("")) {
                    list = find(firstResult, maxResults, conjunction);
                } else {
                    if (direction.equals("ASC")) {
                        list = find(Order.asc(property), firstResult, maxResults, conjunction);
                    } else {
                        list = find(Order.desc(property), firstResult, maxResults, conjunction);
                    }
                }
            } else {
                list = new ArrayList<>();
            }
        } else {
            if (!filterField.equals("all")) {
                conjunction.add(Restrictions.ilike(filterField, filterText, MatchMode.ANYWHERE));
            } else {
                conjunction.add(Restrictions.disjunction(Restrictions.ilike("essence", filterText, MatchMode.ANYWHERE), Restrictions.ilike("sinonim", filterText, MatchMode.ANYWHERE)));
            }

            if (sort.equals("")) {
                list = find(firstResult, maxResults, conjunction);
            } else {
                if (direction.equals("ASC")) {
                    list = find(Order.asc(property), firstResult, maxResults, conjunction);
                } else {
                    list = find(Order.desc(property), firstResult, maxResults, conjunction);
                }
            }
        }

        return list;
    }

    public int getThesaurusCount(String filterField, String filterText) {
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return 0;
        }

        if (!filterField.equals("all")) {
            if (!filterText.equals("")) {
                conjunction.add(Restrictions.ilike(filterField, filterText, MatchMode.ANYWHERE));
            }
        } else {
            if (!filterText.equals("")) {
                conjunction.add(Restrictions.disjunction(Restrictions.ilike("essence", filterText, MatchMode.ANYWHERE), Restrictions.ilike("sinonim", filterText, MatchMode.ANYWHERE)));
            }
        }

        return count(conjunction);
    }

    public List<Thesaurus> search(String query, int firstResult, int maxResults, Long communityId) {
        Conjunction conjunction = new Conjunction();

        CommunityEntity community = communityDao.getById(communityId);
        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        conjunction.add(Restrictions.ilike("essence", query, MatchMode.ANYWHERE));
        return find(Order.asc("essence"), firstResult, maxResults, conjunction);
    }
}