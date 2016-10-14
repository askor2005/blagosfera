package ru.radom.kabinet.web.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;
import ru.radom.kabinet.services.EmailService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class ErrorsController {


    private static final Logger log = LoggerFactory.getLogger(ErrorsController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    Environment env;

    @RequestMapping(value = "/403")
    public String show403Page() {
        return "error403";
    }

    @RequestMapping(value = "/404")
    public String show404Page() {
        return "error404";
    }

    @RequestMapping(value = "/500")
    public String show500Page(final HttpServletRequest req) throws Throwable {

        Throwable exception = (Throwable) req.getAttribute("javax.servlet.error.exception");
        
        if(exception != null){
            if(exception instanceof NestedServletException) exception = exception.getCause();
            final String[] profiles = env.getActiveProfiles();
            final String profile = ((profiles != null) && (profiles.length > 0)) ? profiles[0] : "";
            switch (profile) {
                case "prod":
                case "dev":
                    emailService.sendError(req, exception, profile);
                    break;
                case "local":
                default:
                    break;
            }
            log.error("Request: " + req.getRequestURL() + " , error " + exception.getLocalizedMessage(), exception);
        }

        return "error500";
    }

}
