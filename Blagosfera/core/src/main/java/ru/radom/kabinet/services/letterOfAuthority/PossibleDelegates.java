package ru.radom.kabinet.services.letterOfAuthority;

import ru.radom.kabinet.model.UserEntity;

import java.util.List;

/**
 * Created by vgusev on 22.09.2015.
 */
public class PossibleDelegates {

    private int count;

    private List<UserEntity> delegates;

    public PossibleDelegates(int count, List<UserEntity> delegates) {
        this.count = count;
        this.delegates = delegates;
    }

    public int getCount() {
        return count;
    }

    public List<UserEntity> getDelegates() {
        return delegates;
    }
}
