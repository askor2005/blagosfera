package ru.radom.kabinet.module.blagosfera.bp.service;

import ru.radom.kabinet.module.blagosfera.bp.model.ProcessTreeItem;

import java.util.List;

/**
 * Created by Otts Alexey on 28.10.2015.<br/>
 * Сервис для работы с деревом процессов
 */
public interface ProcessTreeService {

    enum InsertPosition {
        First,
        Last
    }

    /**
     * Создать папку
     * @param parentId          родительский элемент
     * @param name              название папки
     * @param insertPosition    позиция куда нужно вставить элемент
     */
    ProcessTreeItem createFolder(Long parentId, String name, InsertPosition insertPosition);

    /**
     * Создать модель
     * @param parentId          родительский элемент
     * @param name              название папки
     * @param insertPosition    позиция куда нужно вставить элемент
     */
    ProcessTreeItem createModel(Long parentId, String name, InsertPosition insertPosition);

    /**
     * Удалить папку
     * @param id                папка
     * @param moveChildrenUp    нужно ли переместить детей папки на уровень выше, если нет то всё содержимое удаляется
     */
    void deleteFolder(Long id, boolean moveChildrenUp);

    /**
     * Удалить модель
     * @param id                модель
     */
    void deleteModel(Long id);

    /**
     * Переместить узел
     * @param itemId      узел который перемещяем
     * @param parentId    родитель в которого переместили
     * @param position    позиция в родителе куда переместить
     */
    ProcessTreeItem moveItem(Long itemId, Long parentId, int position);

    /**
     * Выбрать дочерние элементы узла
     */
    List<ProcessTreeItem> getChildren(Long parentId);
}
