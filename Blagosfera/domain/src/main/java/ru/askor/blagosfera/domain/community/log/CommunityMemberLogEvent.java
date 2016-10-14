package ru.askor.blagosfera.domain.community.log;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 05.04.2016.
 */
@Data
public class CommunityMemberLogEvent extends CommunityLogEvent implements Serializable {

    public static final long serialVersionUID = 1L;

    private User memberUser;

}
