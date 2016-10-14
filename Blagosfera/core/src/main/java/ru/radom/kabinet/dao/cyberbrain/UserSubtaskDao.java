package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.cyberbrain.UserSubtask;
import ru.radom.kabinet.model.cyberbrain.UserTask;

import java.util.ArrayList;
import java.util.List;

@Repository("userSubtaskDao")
public class UserSubtaskDao extends Dao<UserSubtask> {

    /**
     * Вернуть список всех подзадачь для конкретной задачи
     * @param userTask задача для которой нужно вернуть спискок подзадач
     * @return List<UserTask>
     */
    public List<UserTask> getSubtasksByUserTask(UserTask userTask) {
        List<UserSubtask> userSubtasks =  find(Restrictions.eq("userTask", userTask));

        List<UserTask> list = new ArrayList<>();
        for(UserSubtask userSubtask : userSubtasks) {
            list.add(userSubtask.getUserSubtask());
        }

        return list;
    }
}