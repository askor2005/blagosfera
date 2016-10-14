package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 09.03.2016.
 */
@Data
public class CommunityMember implements Serializable {
    private boolean online;
    public static final long serialVersionUID = 1L;

    private Long id;

    private User user;

    private CommunityMemberStatus status;

    private User inviter;

    private boolean isCreator;

    private Date requestDate;

    private Community community;

    private List<CommunityPost> posts;
}
