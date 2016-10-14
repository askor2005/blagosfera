package ru.radom.kabinet.services.common;

import ru.askor.blagosfera.domain.common.Tag;

import java.util.List;

/**
 * Сервис для обработки логики, связанной с тегами
 */
public interface TagService {


    /**
     * Позволяет получить список domain тегов для автозаполнения
     * @param term терм, с которого должен начинаться текст каждого тега из результирующего списка
     * @return List<Tag>
     */
    List<Tag> getTagsForAutocompleteByTerm(String term);

    /**
     * Позволяет сохранить тег в БД
     * @param tag domain тег
     * @return сохраненный тег
     */
    Tag saveTag(Tag tag);

    /**
     * Позволяет сохранить список тегов в БД
     * @param tags список domain егов
     * @return список сохраненных domain тегов
     */
    List<Tag> saveTags(List<Tag> tags);

    /**
     * Позволяет обновить информацию об использованиях тега
     * @param tag domain тег
     * @return обновленный тег
     */
    Tag updateUsageCount(Tag tag);

    /**
     * Позволяет обновить информацию об использованиях тегов из списка
     * @param tags список domain тегов
     * @return список обновленных тегов
     */
    List<Tag> updateUsageCount(List<Tag> tags);

}
