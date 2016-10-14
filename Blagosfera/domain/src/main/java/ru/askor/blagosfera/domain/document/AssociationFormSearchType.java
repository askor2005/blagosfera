package ru.askor.blagosfera.domain.document;

/**
 * Типы применения фильтра источников данных при загрузке возможных участников документа
 * Created by vgusev on 06.10.2015.
 */
public enum AssociationFormSearchType {

    SEARCH_EQUALS,          // 0 Поиск на точное совпадение
    SEARCH_SUB_STRUCTURES   // 1 Поиск с учётом подструктуры
}
