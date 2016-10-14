package ru.askor.blagosfera.domain.community.log;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.user.User;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Data
public class CommunityLogEvent implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private Date date;

    private User user;

    private Community community;

    private CommunityEventType type;
}
