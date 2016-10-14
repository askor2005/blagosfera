package ru.radom.kabinet.model.communities.postappointbehavior;

import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.PostAppointData;

/**
 * Интерфейс поведения назначения на должность участника объединения
 * Created by vgusev on 28.08.2015.
 */
public interface IPostAppointBehavior {

    /**
     * Запустить механизм назначения на должность участника объединения
     * @param communityPostRequest
     */
    PostAppointData start(CommunityPostRequest communityPostRequest);
}
