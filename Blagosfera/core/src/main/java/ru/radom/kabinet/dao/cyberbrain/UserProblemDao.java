package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.UserProblem;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.Roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository("userProblemDao")
public class UserProblemDao extends Dao<UserProblem> {
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

    public List<UserProblem> getProblemsList(Map<String, String> filters, int firstResult, int maxResults) {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            if (filters != null) {
                if (filters.get("communityId") != null) {
                    if (!filters.get("communityId").equals("")) {
                        CommunityEntity community = communityDao.getById(Long.valueOf(filters.get("communityId")));
                        conjunction.add(Restrictions.eq("community", community));
                    } else {
                        conjunction.add(Restrictions.in("community", communitiesList));
                    }
                } else {
                    conjunction.add(Restrictions.in("community", communitiesList));
                }
            } else {
                conjunction.add(Restrictions.in("community", communitiesList));
            }
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.eq("user", userEntity));
        return find(firstResult, maxResults, conjunction);
    }

    public int getProblemsCount(Map<String, String> filters) {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            if (filters != null) {
                if (filters.get("communityId") != null) {
                    if (!filters.get("communityId").equals("")) {
                        CommunityEntity community = communityDao.getById(Long.valueOf(filters.get("communityId")));
                        conjunction.add(Restrictions.eq("community", community));
                    } else {
                        conjunction.add(Restrictions.in("community", communitiesList));
                    }
                } else {
                    conjunction.add(Restrictions.in("community", communitiesList));
                }
            } else {
                conjunction.add(Restrictions.in("community", communitiesList));
            }
        } else {
            return 0;
        }

        conjunction.add(Restrictions.eq("user", userEntity));
        return count(conjunction);
    }
}