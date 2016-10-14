package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = UserProblemPerformer.TABLE_NAME)
public class UserProblemPerformer extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_user_problems_performers";

    public static class Columns {
        public static final String USER_PROBLEM = "user_problem_id";
        public static final String TAG_OBJECT = "tag_object_id";
        public static final String TAG_MANY = "tag_many_id";
        public static final String PERFORMER = "performer_id";
    }

    @JoinColumn(name = Columns.USER_PROBLEM)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserProblem userProblem;

    @JoinColumn(name = Columns.TAG_OBJECT)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus tagObject;

    @JoinColumn(name = Columns.TAG_MANY)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus tagMany;

    @JoinColumn(name = Columns.PERFORMER)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity performer;

    public UserProblem getUserProblem() {
        return userProblem;
    }

    public void setUserProblem(UserProblem userProblem) {
        this.userProblem = userProblem;
    }

    public Thesaurus getTagObject() {
        return tagObject;
    }

    public void setTagObject(Thesaurus tagObject) {
        this.tagObject = tagObject;
    }

    public Thesaurus getTagMany() {
        return tagMany;
    }

    public void setTagMany(Thesaurus tagMany) {
        this.tagMany = tagMany;
    }

    public UserEntity getPerformer() {
        return performer;
    }

    public void setPerformer(UserEntity performer) {
        this.performer = performer;
    }
}