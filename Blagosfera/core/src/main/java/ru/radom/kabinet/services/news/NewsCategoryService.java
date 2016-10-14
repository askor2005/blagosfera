package ru.radom.kabinet.services.news;

import ru.askor.blagosfera.domain.news.NewsCategoryTreeNode;

import java.util.List;

/**
 * Интерфейс сервиса для обработки данных категорий новостей
 */
public interface NewsCategoryService {

    /**
     * Позволяет получить список корневых узлов дерева категорий.
     * Каждый такой узел хранит в себе информацию о детях, а они в свою очередь о своих...и так до самых нижних слоев.
     * @return List<NewsCategoryTreeNode>. В случае пустого дерева возвращается пустой список.
     */
    List<NewsCategoryTreeNode> getNewsCategoryTree();

    /**
     * Позволяет сохранить в БД новую категорию новостей из домена категории и идентификатора ее родителя
     * @param newsCategoryTreeNode доменное представление категории новостей
     * @param parentId идентификатор родительского узла
     * @return экземпляр класса NewsCategoryTreeNode
     */
    NewsCategoryTreeNode createNewsCategory(NewsCategoryTreeNode newsCategoryTreeNode, Long parentId);

    /**
     * Позволяет обновить данные указанной категории новостей (текст/описание/ключ)
     * @param newsCategoryTreeNode категория новостей для обновления
     * @return экземпляр класса NewsCategoryTreeNode
     */
    NewsCategoryTreeNode updateNewsCategory(NewsCategoryTreeNode newsCategoryTreeNode);


    /**
     * Позволяет удалить узел вместе со всеми его детьми и потомками
     * @param id идентификатор удаляемого узла
     */
    void deleteNewsCategory(Long id);

    /**
     * Позволяет переместить узел категорий новостей внутри иерархии дерева
     * @param id идентификатор перемещаемого узла
     * @param parentId идентификатор нового родителя перемещаемого узла
     * @param nextSiblingId идентификатор нового соседа после перемещаемого узла
     */
    void changeNewsCategoriesHierarchy(Long id, Long parentId, Long nextSiblingId);


    /**
     * Позволяет получить детей по категории
     * @param parent родительская категория (domain)
     * @return список детей (domain) категории, переданной в качестве параметра
     */
    List<NewsCategoryTreeNode> getChildrenByParent(NewsCategoryTreeNode parent);
}
