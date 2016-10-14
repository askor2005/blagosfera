package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.UserProblem;
import ru.radom.kabinet.model.cyberbrain.UserProblemPerformer;

import java.util.ArrayList;
import java.util.List;

@Repository("userProblemPerformerDao")
public class UserProblemPerformerDao extends Dao<UserProblemPerformer> {

    /**
     * Вернуть список всех объектов которые препятствуют выполнению работы
     * @param userProblem проблема для которой нужно вернуть спискок
     * @return List<UserTask>
     */
    public List<UserProblem> getObjectsByUserProblemList(UserProblem userProblem) {
        List<UserProblemPerformer> userProblemPerformers = find(Restrictions.eq("userProblem", userProblem));

        List<UserProblem> list = new ArrayList<>();
        for(UserProblemPerformer userProblemPerformer : userProblemPerformers) {
            list.add(userProblemPerformer.getUserProblem());
        }

        return list;
    }

    public List<UserProblemPerformer> getByProblemList(UserProblem userProblem) {
        Conjunction conjunction = new Conjunction();

        if (userProblem != null) {
            conjunction.add(Restrictions.eq("userProblem", userProblem));
        }

        return find(conjunction);
    }
}