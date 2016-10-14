package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.UserTask;
import ru.radom.kabinet.model.cyberbrain.UserTaskLifecycle;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.Roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository("userTaskDao")
public class UserTaskDao extends Dao<UserTask> {

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

    public List<UserTask> getTasksByPerformer(UserEntity performer) {
        return find(Restrictions.eq("performer", performer));
    }

    public List<UserTask> getTasksByCustomer(UserEntity customer) {
        return find(Restrictions.eq("customer", customer));
    }

    /**
     * Получить список моих заказчиков (это те задачи где я Исполнитель)
     * @return список задач
     */
    public List<UserTask> getMyCustomers(int firstResult, int maxResults) {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.eq("performer", userEntity));
        conjunction.add(Restrictions.ne("customer", userEntity));

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.eq("lifecycle", UserTaskLifecycle.NEW.getIndex()));
        disjunction.add(Restrictions.eq("lifecycle", UserTaskLifecycle.REJECTED.getIndex()));
        conjunction.add(disjunction);

        return find(firstResult, maxResults, conjunction);
    }

    /**
     * Получить количество моих заказчиков (это те задачи где я Исполнитель)
     * @return int
     */
    public int getMyCustomersCount() {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return 0;
        }

        conjunction.add(Restrictions.eq("performer", userEntity));
        conjunction.add(Restrictions.ne("customer", userEntity));

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.eq("lifecycle", UserTaskLifecycle.NEW.getIndex()));
        disjunction.add(Restrictions.eq("lifecycle", UserTaskLifecycle.REJECTED.getIndex()));
        conjunction.add(disjunction);

        return count(conjunction);
    }

    /**
     * Мои субподрядчики. (это задачи где я заказчик)
     * @return список задач
     */
    public List<UserTask> getMySubcontractors(int firstResult, int maxResults) {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.eq("customer", userEntity));
        conjunction.add(Restrictions.ne("performer", userEntity));
        conjunction.add(Restrictions.lt("lifecycle", UserTaskLifecycle.CONFIRMED.getIndex()));

        return find(firstResult, maxResults, conjunction);
    }

    /**
     * Мои субподрядчики. (это задачи где я заказчик)
     * @return int
     */
    public int getMySubcontractorsCount() {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return 0;
        }

        conjunction.add(Restrictions.eq("customer", userEntity));
        conjunction.add(Restrictions.ne("performer", userEntity));
        conjunction.add(Restrictions.lt("lifecycle", UserTaskLifecycle.CONFIRMED.getIndex()));

        return count(conjunction);
    }


    /**
     * Мои цели (это задачи где исполнитель и заказчик я)
     * @return список задач
     */
    public List<UserTask> getMyGoals(int firstResult, int maxResults) {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.eq("customer", userEntity));
        conjunction.add(Restrictions.eq("performer", userEntity));
        conjunction.add(Restrictions.lt("lifecycle", UserTaskLifecycle.SOLVED.getIndex()));

        return find(firstResult, maxResults, conjunction);
    }

    /**
     * Мои цели (это задачи где исполнитель и заказчик я)
     * @return int
     */
    public int getMyGoalsCount() {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        Conjunction conjunction = new Conjunction();

        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        if (communitiesList.size() > 0) {
            conjunction.add(Restrictions.in("community", communitiesList));
        } else {
            return 0;
        }

        conjunction.add(Restrictions.eq("customer", userEntity));
        conjunction.add(Restrictions.eq("performer", userEntity));
        conjunction.add(Restrictions.lt("lifecycle", UserTaskLifecycle.SOLVED.getIndex()));

        return count(conjunction);
    }

    public List<UserTask> search(String query, int firstResult, int maxResults, Long communityId) {
        Conjunction conjunction = new Conjunction();

        CommunityEntity community = communityDao.getById(communityId);
        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        conjunction.add(Restrictions.ilike("description", query, MatchMode.ANYWHERE));
        return find(Order.asc("description"), firstResult, maxResults, conjunction);
    }
}