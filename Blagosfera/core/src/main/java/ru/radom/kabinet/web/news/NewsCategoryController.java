package ru.radom.kabinet.web.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.domain.news.NewsCategoryTreeNode;
import ru.radom.kabinet.dto.news.editing.NewsCategoryDto;
import ru.radom.kabinet.services.news.NewsCategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igolovko on 08.02.2016.
 * Контроллер, обрабатывающий запросы на получение информации о категориях новостей
 */
@Deprecated
@Controller
@RequestMapping(value = "/news/categories/")
public class NewsCategoryController {

    @Autowired
    private NewsCategoryService newsCategoryService;

    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public @ResponseBody List<NewsCategoryDto> children(NewsCategoryDto newsCategoryDto) {

        List<NewsCategoryDto> result = new ArrayList<>();
        List<NewsCategoryTreeNode> domains = newsCategoryService.getChildrenByParent(new NewsCategoryTreeNode(newsCategoryDto));

        for (NewsCategoryTreeNode domain : domains) {
            result.add(domain.toUsersDto());
        }

        return result;
    }

}
