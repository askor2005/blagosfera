package ru.radom.kabinet.module.rameralisteditor.service;

import ru.askor.blagosfera.domain.listEditor.ListEditorItem;

import java.util.List;

/**
 * Интерфейс сервиса для работы с сущностями типа RameraListEditorItem
 */
public interface ListEditorItemDomainService {

    /**
     *
     * @param id
     * @return
     */
    ListEditorItem getById(Long id);

    /**
     * Позволяет получить список идентификаторов потомков сущности RameraListEditorItem
     * вместе с идентификатором их предка по идентификатору предка
     * @param parentId идентификатор предка
     * @return List<Long>
     */
    List<Long> getDescendantIdsWithParent(Long parentId);

    /**
     *
     * @param ids
     * @return
     */
    List<ListEditorItem> getByIds(List<Long> ids);

    /**
     *
     * @param code
     * @return
     */
    ListEditorItem getByCode(String code);

    /**
     *
     * @param listEditorItem
     * @return
     */
    ListEditorItem save(ListEditorItem listEditorItem);

}
