package ru.radom.kabinet.services.news.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsFilterRepository;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.news.NewsFilterEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.news.NewsFilterService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service("newsFilterService")
public class NewsFilterServiceImpl implements NewsFilterService {

    @Autowired
    private NewsFilterRepository newsFilterRepository;

    @Autowired
    private ListEditorItemDomainService rameraListEditorItemService;

    @Override
    @Transactional(readOnly = true)
    public boolean isNewsSuitableForSharersFilter(News news, UserEntity userEntity) {

        NewsFilterEntity newsFilterEntity = null;

        //Проверяем, кто является инициатором: отдельный пользователь или объединение.
        RadomAccount scope = news.getScope();

        if (scope instanceof CommunityEntity) {
            newsFilterEntity = newsFilterRepository.findOneByUser_IdAndCommunity_Id(userEntity.getId(), scope.getId());
        } else if (scope instanceof UserEntity) {
            newsFilterEntity = newsFilterRepository.findOneBySharerWithoutCommunity(userEntity.getId());
        }

        //Пользователь заполнял фильтр. Выполняем детальную проверку.
        if (newsFilterEntity != null) {

            //Проверка автора
            if (newsFilterEntity.getAuthorId() != null && !newsFilterEntity.getAuthorId().equals(news.getAuthorId())) {
                return false;
            }

            //Проверка категории
            if (newsFilterEntity.getCategoryId() != null) {
                List<Long> categoryIds = rameraListEditorItemService.getDescendantIdsWithParent(newsFilterEntity.getCategoryId());

                if (categoryIds.indexOf(news.getCategory().getId()) == -1) {
                    return false;
                }
            }

            //Проверка даты "ОТ"
            if (newsFilterEntity.getDateFrom() != null) {
                if (news.getDate().before(newsFilterEntity.getDateFrom())) {
                    return false;
                }
            }

            //Проверка даты "ДО"
            if (newsFilterEntity.getDateTo() != null) {
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(newsFilterEntity.getDateTo().toInstant(), ZoneId.systemDefault()).plusDays(1);

                if (news.getDate().after(DateUtils.toDate(dateTime))) {
                    return false;
                }
            }

            //Проверка тегов
            if (newsFilterEntity.getTags() != null && !newsFilterEntity.getTags().isEmpty()) {
                if (CollectionUtils.intersection(newsFilterEntity.getTags(), news.getTags()).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

}
