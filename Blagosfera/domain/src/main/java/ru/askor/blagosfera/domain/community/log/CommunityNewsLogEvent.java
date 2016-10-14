package ru.askor.blagosfera.domain.community.log;

import lombok.Data;
import ru.askor.blagosfera.domain.news.NewsItem;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 05.04.2016.
 */
@Data
public class CommunityNewsLogEvent extends CommunityLogEvent implements Serializable {

    public static final long serialVersionUID = 1L;

    private NewsItem news;
}
