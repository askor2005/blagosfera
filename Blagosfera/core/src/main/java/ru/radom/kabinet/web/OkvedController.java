package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.utils.CommonConstants;

import java.util.List;

/**
 *
 * Created by vgusev on 08.12.2015.
 */
@Controller
public class OkvedController {

    @Autowired
    private OkvedDao okvedDao;

    /**
     * Поиск кодов деятельности организаций
     * @param query
     * @param ids
     * @return
     */
    @RequestMapping(value = "/okved/search.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<OkvedEntity> searchOkveds(@RequestParam(value = "query", required = true) String query,
                                    @RequestParam(value = "id[]", required = true, defaultValue = "") List<Long> ids) {
        return okvedDao.search(query, 0, 200, ids);
    }
}
