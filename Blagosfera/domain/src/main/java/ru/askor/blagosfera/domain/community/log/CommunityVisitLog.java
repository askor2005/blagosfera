package ru.askor.blagosfera.domain.community.log;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;

import java.util.Date;

/**
 * Created by vtarasenko on 14.07.2016.
 */
@Data
public class CommunityVisitLog {
    private Long id;
    private Community community;
    private User user;
    private Date visitTime;

}
