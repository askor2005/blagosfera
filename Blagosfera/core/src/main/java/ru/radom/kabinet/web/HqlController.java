package ru.radom.kabinet.web;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.CommonConstants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;

/**
 *
 * Created by vgusev on 27.11.2015.
 */
@Controller
@Transactional
public class HqlController {

    @PersistenceContext(unitName = "kabinetPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    public List<Object> createQuery(String hql) {
        Session session = em.unwrap(Session.class);
        return session.createQuery(hql).list();
    }

    private void checkRole() {
        if (!SecurityUtils.getUserDetails().hasRole("ROLE_DEVELOPER")){
            throw new RuntimeException("Нехер ползать где не надо! :D");
        }
    }

    @RequestMapping(value = "/hql/execute.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public List<Object> executeHql(@RequestParam("hql_string") String hqlString) {
        checkRole();
        return createQuery(hqlString);
    }

    @RequestMapping(value = "/hql", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String hqlPage() {
        checkRole();
        return "<html>" +
                "<head>" +
                "<script type='text/javascript' src='/js/jquery.js'></script>" +
                "<script type='text/javascript' src='/js/jjsonviewer.js'></script>" +
                "<script type='text/javascript' src='/js/hql.js'></script>" +
                "<link rel='stylesheet' href='/css/jjsonviewer.css'>" +
                "<title>HQL ридахтер</title>" +
                "</head>" +
                "<body>" +
                "<div><button id='execute_hql'>Сделать мир лучше</button></div>" +
                "<div id='text_block'><textarea id='hql_text'></textarea></div>" +
                "<div id='result_block'></div>" +
                "</body>" +
                "</html>";
    }
}
