package ru.radom.kabinet.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.domain.news.NewsCategoryTreeNode;
import ru.radom.kabinet.dto.news.NewsCategoryDto;
import ru.radom.kabinet.services.news.NewsCategoryService;
import ru.radom.kabinet.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с управлением категориями новостей, от админа
 */
@Controller
@RequestMapping("/admin/news/categories")
public class NewsCategoryAdminController {

    @Autowired
    private NewsCategoryService newsCategoryService;


    /**
     * Возвращает имя view, позволяющее управлять деревом категорий новостей
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showNewsCategoriesPage(Model model) {
        model.addAttribute("currentPageTitle", "Категории новостей");
        return "adminNewsCategories";
    }


    /**
     * Возвращает Json с полным деревом категорий новостей
     * @return
     */
    @RequestMapping(value = "/tree.json", method = RequestMethod.GET)
    public @ResponseBody NewsCategoryDto getNewsCategoriesTree() {
        //Корневой узел дерева
        NewsCategoryDto root = new NewsCategoryDto();
        root.setExpanded(true);

        List<NewsCategoryTreeNode> domainNodes = newsCategoryService.getNewsCategoryTree();

        //Дети корневого узла (логический верхний слой дерева)
        List<NewsCategoryDto> rootChildren = new ArrayList<>();

        for (NewsCategoryTreeNode newsCategoryTreeNode : domainNodes) {
            rootChildren.add(newsCategoryTreeNode.toAdminsDto());
        }

        root.setChildren(rootChildren);

        return root;
    }


    /**
     * Позволяет обработать запрос на сохранение новой категории новостей
     * и вернуть Json с новыми данными нового узла дерева.
     * @param newsCategoryDto dto новой категории
     * @param parentId идентификатор родителя
     * @return json, описывающий созданный узел
     */
    @RequestMapping(value = "/create.json", method = RequestMethod.POST)
    public @ResponseBody NewsCategoryDto createNewsCategory(NewsCategoryDto newsCategoryDto,
                                                            @RequestParam(value = "parent_id") Long parentId) {
        //Первичная валидация
        validateNewsCategoryDto(newsCategoryDto);

        //Для сохранения id должен быть null
        if (newsCategoryDto.getId() != null) {
            newsCategoryDto.setId(null);
        }

        //Обрезаем пробелы по краям
        newsCategoryDto.setText(newsCategoryDto.getText().trim());
        newsCategoryDto.setDescription(newsCategoryDto.getDescription().trim());
        newsCategoryDto.setKey(newsCategoryDto.getKey().trim());

        //Сохранение новой категории
        NewsCategoryTreeNode domainResult = newsCategoryService.createNewsCategory(new NewsCategoryTreeNode(newsCategoryDto), parentId);

        //Отправка результата на сторону клиента
        NewsCategoryDto result = domainResult.toAdminsDto();
        return result;
    }


    /**
     * Позволяет обработать запрос на обновление данных указанной категории новостей
     * @param newsCategoryDto dto категории новостей для обновления
     * @return dto обновленной категории новостей
     */
    @RequestMapping(value = "/updateData.json", method = RequestMethod.POST)
    public @ResponseBody NewsCategoryDto editNewsCategory(NewsCategoryDto newsCategoryDto) {

        //Первичная валидация
        if (newsCategoryDto.getId() == null) {
            throw new RuntimeException("Указанной категории новостей не существует");
        }
        validateNewsCategoryDto(newsCategoryDto);

        //Обрезаем пробелы по краям
        newsCategoryDto.setText(newsCategoryDto.getText().trim());
        newsCategoryDto.setDescription(newsCategoryDto.getDescription().trim());
        newsCategoryDto.setKey(newsCategoryDto.getKey().trim());

        //Обновление категории и возвращение результата
        return newsCategoryService.updateNewsCategory(new NewsCategoryTreeNode(newsCategoryDto)).toAdminsDto();
    }

    /**
     * Позволяет обработать запрос на удаление категории новостей с указанным id, а также всех ее детей и потомков.
     * @param id идентификатор категории новостей для удаления
     * @return json с результатом запроса
     */
    @RequestMapping(value = "/delete.json", method = RequestMethod.POST)
    public @ResponseBody String deleteNewsCategory(@RequestParam(value = "id", required = true) Long id) {
        newsCategoryService.deleteNewsCategory(id);
        return JsonUtils.getSuccessJson().toString();
    }


    /**
     * Позволяет обработать запрос на перемещение узла внутри иерархии дерева
     * @param id идентификатор узла для перемещения
     * @param parentId идентификатор нового родителя перемещаемого узла
     * @param nextSiblingId идентификатор нового соседа после перемещаемого узла
     * @return
     */
    @RequestMapping(value = "/changeHierarchy.json", method = RequestMethod.POST)
    public @ResponseBody String changeNewsCategoriesHierarchy(@RequestParam(value = "id", required = true) Long id,
                                                              @RequestParam(value = "parent_id", required = true) Long parentId,
                                                              @RequestParam(value = "next_sibling_id", required = true) Long nextSiblingId) {
        if (id == null) {
            throw new RuntimeException("Не указана перемещаемая категория новостей!");
        }

        newsCategoryService.changeNewsCategoriesHierarchy(id, parentId, nextSiblingId);

        return JsonUtils.getSuccessJson().toString();
    }


    /**
     * Позволяет провести общую валидацию dto категории новостей при сохранении/редактировании.
     * В случае неудачи бросает RuntimeException
     * @param dto веб представление категории новостей
     */
    private void validateNewsCategoryDto(NewsCategoryDto dto) {
        if (dto == null) {
            throw new RuntimeException("Категория новостей не заполнена!");
        } else if (dto.getText() == null || dto.getText().trim().isEmpty()) {
            throw new RuntimeException("Не указано наименование категории новостей!");
        } else if (dto.getText().length() > 200) {
            throw new RuntimeException("Наименование не может превышать длину в 200 символов!");
        } else if (dto.getKey() == null || dto.getKey().trim().isEmpty()) {
            throw new RuntimeException("Не указан ключ категории новостей!");
        } else if (dto.getKey().length() > 200) {
            throw new RuntimeException("Наименование ключа не может превышать длину в 200 символов!");
        } else if (dto.getDescription() != null && dto.getDescription().length() > 1000) {
            throw new RuntimeException("Описание не может превышать длину в 1000 символов!");
        }
    }



}
