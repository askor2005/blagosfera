package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

@Entity
@Table(name = UserSubtask.TABLE_NAME)
public class UserSubtask extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_user_subtasks";

    public static class Columns {
        public static final String USER_TASK = "user_task_id";
        public static final String USER_SUBTASKS = "user_subtask_id";
    }

    @JoinColumn(name = Columns.USER_TASK)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserTask userTask;       // задача

    @JoinColumn(name = Columns.USER_SUBTASKS)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserTask userSubtask;   // подзадача

    public UserTask getUserTask() {
        return userTask;
    }

    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }

    public UserTask getUserSubtask() {
        return userSubtask;
    }

    public void setUserSubtask(UserTask userSubtask) {
        this.userSubtask = userSubtask;
    }
}
