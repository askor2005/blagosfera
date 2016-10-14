package ru.radom.kabinet.services.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.common.TagRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsRepository;
import ru.askor.blagosfera.data.jpa.specifications.common.TagSpecifications;
import ru.askor.blagosfera.data.jpa.specifications.news.NewsSpecifications;
import ru.askor.blagosfera.domain.common.Tag;
import ru.radom.kabinet.enums.system.SystemSetting;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.services.common.TagService;

import java.util.List;
import java.util.stream.Collectors;

@Service("tagService")
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private SettingsManager settingsManager;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTagsForAutocompleteByTerm(String term) {

        List<Tag> result;

        Integer maxCount = Integer.valueOf(settingsManager.getSystemSetting(SystemSetting.TAGS_MAX_COUNT_FOR_AUTOCOMPLETE.getKey(), "10"));
        Long tagMinUsagesToAutocomplete = Long.valueOf(settingsManager.getSystemSetting(SystemSetting.TAGS_MIN_USAGES_TO_AUTOCOMPLETE.getKey(), "5"));

        //Генерируем спецификации
        Specification<TagEntity> startsWithSpec = TagSpecifications.startsWithInLowerCase(term);
        //Specification<TagEntity> usageCountGtOrEqSpec = TagSpecifications.usagesCountGtOrEqThan(tagMinUsagesToAutocomplete);

        //Подготавливаем запрос страницы
        PageRequest pageRequest = new PageRequest(0, maxCount);

        //Выполняем запрос
        Page<TagEntity> page = tagRepository.findAll(Specifications.where(startsWithSpec),
                pageRequest);


        //Собираем результирующий список
        result = page.getContent()
                .stream()
                .map(t -> t.toDomain())
                .collect(Collectors.toList());

        return result;
    }

    @Override
    @Transactional
    public Tag saveTag(Tag tag) {

        TagEntity tagEntity;

        tagEntity = tagRepository.findOneByText(tag.getText());

        //У каждого тега свой уникальный текст
        if (tagEntity != null) {
            return tagEntity.toDomain();
        }

        tagEntity = tagRepository.save(new TagEntity(tag));

        return tagEntity.toDomain();
    }



    @Override
    @Transactional
    public List<Tag> saveTags(List<Tag> tags) {
        return tags.stream()
                .map(tag -> saveTag(tag))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Tag updateUsageCount(Tag tag) {

        TagEntity tagEntity = tagRepository.findOne(tag.getId());
        Specification<News> hasTagSpec = NewsSpecifications.hasTag(tagEntity);

        Long usageCount = newsRepository.count(Specifications.where(hasTagSpec));

        tagEntity.setUsageCount(usageCount);
        tagEntity = tagRepository.save(tagEntity);

        return tagEntity.toDomain();
    }

    @Override
    @Transactional
    public List<Tag> updateUsageCount(List<Tag> tags) {
        return tags.stream()
                .map(tag -> updateUsageCount(tag))
                .collect(Collectors.toList());
    }
}
