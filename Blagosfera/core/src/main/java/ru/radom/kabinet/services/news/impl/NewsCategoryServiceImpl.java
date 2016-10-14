package ru.radom.kabinet.services.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsCategoryRepository;
import ru.askor.blagosfera.domain.news.NewsCategoryTreeNode;
import ru.radom.kabinet.model.news.NewsCategory;
import ru.radom.kabinet.services.news.NewsCategoryService;

import java.util.ArrayList;
import java.util.List;

@Service("newsCategoryService")
public class NewsCategoryServiceImpl implements NewsCategoryService {

    @Autowired
    private NewsCategoryRepository newsCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NewsCategoryTreeNode> getNewsCategoryTree() {
        List<NewsCategory> roots = newsCategoryRepository.findAllRoot();

        return fillNodes(roots);
    }

    /**
     * Рекурсивно строит domain дерево из списка узлов одного уровня и одного родителя, переданного в качестве параметра.
     * @return List<NewsCategory> или null, если список пуст
     */
    private List<NewsCategoryTreeNode> fillNodes(List<NewsCategory> nodes) {

        //Сюда пишем результат
        List<NewsCategoryTreeNode> result = new ArrayList<>();

        //Если список пуст, то возвращаем нуль
        if (nodes == null || nodes.isEmpty()) {
            return result;
        }

        //Обходим список узлов одного уровня и одного родителя в цикле (или список корневых узлов)
        for (NewsCategory newsCategory : nodes) {
            //Доменное олицетворение узла
            NewsCategoryTreeNode newsCategoryTreeNode = newsCategory.toDomain();
            //Закладываем доменный узел список его детей
            newsCategoryTreeNode.setChildrenNodes(fillNodes(newsCategoryRepository.findAllByParent(newsCategory)));
            //Добавляем узел в результирующий список
            result.add(newsCategoryTreeNode);
        }

        return result;
    }



    @Override
    public NewsCategoryTreeNode createNewsCategory(NewsCategoryTreeNode newsCategoryTreeNode, Long parentId) {

        if (newsCategoryRepository.existsByKey(newsCategoryTreeNode.getKey())) {
            throw new RuntimeException("Категория с указанным ключом уже существует!");
        }

        //Ищем родителя для нового узла
        NewsCategory parent = null;

        if (parentId != null) {
            parent = newsCategoryRepository.findOne(parentId);
        }

        //Вычисляем позицию нового узла
        Integer maxPositionByParent;

        if (parent == null) {
            maxPositionByParent = newsCategoryRepository.findMaxPositionOfRoot();
        } else {
            maxPositionByParent = newsCategoryRepository.findMaxPositionByParent(parent);
        }

        if (maxPositionByParent == null) {
            maxPositionByParent = 1;
        } else {
            maxPositionByParent++;
        }

        newsCategoryTreeNode.setPosition(maxPositionByParent);

        //Создаем и сохраняем новую категорию
        NewsCategory newsCategory = new NewsCategory(newsCategoryTreeNode, parent);
        newsCategory = newsCategoryRepository.save(newsCategory);


        return newsCategory.toDomain();
    }

    @Override
    public NewsCategoryTreeNode updateNewsCategory(NewsCategoryTreeNode newsCategoryTreeNode) {

        if (!newsCategoryRepository.exists(newsCategoryTreeNode.getId())) {
            throw new RuntimeException("Указанной категории новостей не существует!");
        } else if (newsCategoryRepository.existsByKey(newsCategoryTreeNode.getKey())) {
            throw new RuntimeException("Категория с указанным ключом уже существует!");
        }

        NewsCategory newsCategory = newsCategoryRepository.findOne(newsCategoryTreeNode.getId());

        newsCategory.setTitle(newsCategoryTreeNode.getTitle());
        newsCategory.setDescription(newsCategoryTreeNode.getDescription());
        newsCategory.setKey(newsCategoryTreeNode.getKey());

        newsCategoryRepository.save(newsCategory);

        return newsCategoryTreeNode;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteNewsCategory(Long id) {

        if (!newsCategoryRepository.exists(id)) {
            throw new RuntimeException("Указанной категории новостей не существует!");
        }

        //Получаем нужный узел
        NewsCategory newsCategory = newsCategoryRepository.getOne(id);
        //Удаляем детей и потомков
        deleteNodes(newsCategoryRepository.findAllByParent(newsCategory));
        //Удаляем сам узел
        newsCategoryRepository.delete(newsCategory);
    }

    /**
     * Рекурсивно удаляет список узлов одного уровня и одного родителя вместе со всеми их детьми и потомками
     * @param nodes
     */
    private void deleteNodes(List<NewsCategory> nodes) {

        //Пустой обрабатывать нет смысла, уходим на уровень выше
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        //Обходим список узлов одного уровня и одного родиителя в цикле и удаляем их
        for (NewsCategory node : nodes) {
            //Удаление дочерних
            deleteNodes(newsCategoryRepository.findAllByParent(node));
            //Удаление текущего узла
            newsCategoryRepository.delete(node);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void changeNewsCategoriesHierarchy(Long id, Long parentId, Long nextSiblingId) {

        //Валидация
        if (!newsCategoryRepository.exists(id)) {
            throw new RuntimeException("Перемещаемой категории новостей не существует!");
        } else if (parentId != null && !newsCategoryRepository.exists(parentId)) {
            throw new RuntimeException("Родителя перемещаемой категории новостей не существует!");
        } else if (nextSiblingId != null && !newsCategoryRepository.exists(nextSiblingId)) {
            throw new RuntimeException("Следующего соседа перемещаемой категории новостей не существует!");
        }

        //Получаем перемещаемый узел
        NewsCategory newsCategory = newsCategoryRepository.findOne(id);

        //Получаем ссылку на родительский узел или null, если его id не передан
        NewsCategory parentNewsCategory = null;

        if (parentId != null) {
            parentNewsCategory = newsCategoryRepository.findOne(parentId);
        }

        //Выставляем нового родителя
        newsCategory.setParent(parentNewsCategory);

        //Получаем ссылку на узел, перед которым осуществляется вставка или null, если его id не передан
        NewsCategory nextSiblingNewsCategory = null;

        if (nextSiblingId != null) {
            nextSiblingNewsCategory = newsCategoryRepository.findOne(nextSiblingId);
        }

        //Вставляем новый узел в список детей родителя перед nextSiblingNewsCategory
        insertBefore(parentNewsCategory, newsCategory, nextSiblingNewsCategory);
    }


    /**
     * Позволяет вставить новый дочерний узел перед указанным дочерним узлом родителя
     * @param parentNode родительский узел
     * @param newNode узел для вставки
     * @param existingNode узел, перед которым вставляется новый
     */
    private void insertBefore(NewsCategory parentNode, NewsCategory newNode, NewsCategory existingNode) {

        List<NewsCategory> children;

        //Получаем список детей указанного родителя
        if (parentNode != null) {
            children = newsCategoryRepository.findAllByParent(parentNode);
        } else {
            children = newsCategoryRepository.findAllRoot();
        }

        //Если новый узел перемещается в рамках своего родителя, то вырезаем старые данные из списка детей
        if (children.indexOf(newNode) != -1) {
            children.remove(newNode);
        }

        if (existingNode != null) {
            //Получаем индекс узла, перед которым вставляется новый
            int indexOfExistingNode = children.indexOf(existingNode);

            //И проверяем его принадлежность указанному родителю
            if (indexOfExistingNode != -1) {
                children.add(indexOfExistingNode, newNode);
            } else {
                throw new RuntimeException("Категория, перед которой осуществляется вставка узла, имеет другого родителя!");
            }

        } else {
            //Узел, перед которым вставляется новый не указан - вставляем в конец списка
            children.add(newNode);
        }

        //Обновляем позиции для всего списка. Позиции могут быть выставлены с "пробелами" или повторениями,
        //например администратором БД.
        int counter = 0;

        for(NewsCategory newsCategory : children) {
            newsCategory.setPosition(++counter);
        }

        //Сохраняем изменения
        newsCategoryRepository.save(children);
    }


    @Override
    public List<NewsCategoryTreeNode> getChildrenByParent(NewsCategoryTreeNode parent) {
        //Результирующий список доменных объектов
        List<NewsCategoryTreeNode> result = new ArrayList<>();

        Long id = parent.getId();

        //Список сущностей
        List<NewsCategory> childrenEntities;

        if (id == null) {
            //Родитель не указан - достаются корневые узлы
            childrenEntities = newsCategoryRepository.findAllRoot();
        } else {

            if (!newsCategoryRepository.exists(parent.getId())) {
                throw new RuntimeException("Указанной категории новостей не существует.");
            }

            NewsCategory parentEntity = newsCategoryRepository.findOne(parent.getId());

            childrenEntities = newsCategoryRepository.findAllByParent(parentEntity);
        }

        //Перегоняем из Entity в Domain
        for (NewsCategory newsCategory : childrenEntities) {
            result.add(newsCategory.toDomain());
        }

        return result;
    }
}
