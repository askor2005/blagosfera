package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.radom.kabinet.model.skbcontur.SkbConturUL;
import ru.radom.kabinet.services.SkbConturService;

/**
 * Created by vgusev on 25.06.2015.
 * Контроллер запросов к данным, которые возвращает сервис СКБ Контур Фокус
 */
@Controller
@RequestMapping("/ulinfo")
public class SkbConturController {

    @Autowired
    private SkbConturService skbConturService;

    @RequestMapping(value = "/getByInn", method = RequestMethod.GET)
    public String getULInfoByINN(Model model, @RequestParam(value = "inn") String ulInn) {
        SkbConturUL skbConturUL = skbConturService.getULByINN(ulInn);
        model.addAttribute("ulInfo", skbConturUL);
        return "ulInfoByINN";
    }
}
