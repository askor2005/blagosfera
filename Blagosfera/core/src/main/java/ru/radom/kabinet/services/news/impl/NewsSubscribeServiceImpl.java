package ru.radom.kabinet.services.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsSubscribeRepository;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.news.NewsSubscribe;
import ru.radom.kabinet.services.news.NewsSubscribeService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Transactional
@Service("newsSubscribeService")
public class NewsSubscribeServiceImpl implements NewsSubscribeService {

    @Autowired
    private NewsSubscribeRepository newsSubscribeRepository;

    @Override
    public List<User> getAuthorsBySharer(Long userId) {

        List<User> result = new ArrayList<>();

        List<NewsSubscribe> subscribes = newsSubscribeRepository.findAllByUser_Id(userId);
        subscribes.sort(new Comparator<NewsSubscribe>() {
            @Override
            public int compare(NewsSubscribe o1, NewsSubscribe o2) {
                String firstName = null;
                String secondName = null;
                if (o1.getScope() instanceof UserEntity) {
                   firstName =  ((UserEntity) o1.getScope()).getSearchString();
                }
                else if (o1.getScope() instanceof CommunityEntity) {
                    firstName =  ((CommunityEntity) o1.getScope()).getName();
                }
                if (o2.getScope() instanceof UserEntity) {
                    secondName =  ((UserEntity) o2.getScope()).getSearchString();
                }
                else if (o2.getScope() instanceof CommunityEntity) {
                    secondName =  ((CommunityEntity) o2.getScope()).getName();
                }
                if (firstName == null)
                    firstName = "";
                if (secondName == null) {
                    secondName = "";
                }
                return firstName.compareTo(secondName);

            }
        });
        for (NewsSubscribe subscribe : subscribes) {
            if (subscribe.getScope() instanceof UserEntity) {
                try {
                    result.add(((UserEntity) subscribe.getScope()).toDomain());
                } catch (Throwable ignored) {
                    // TODO this is baaaaad because database objects missed
                }
            }
        }

        return result;
    }

}
