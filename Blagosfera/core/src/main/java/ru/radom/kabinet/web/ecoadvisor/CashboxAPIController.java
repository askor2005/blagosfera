package ru.radom.kabinet.web.ecoadvisor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by vgusev on 17.08.2016.
 */
@RestController
@RequestMapping(value = "/cashbox", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
public class CashboxAPIController extends CashboxAPIControllerBase {

    public static final String CASHBOX_API_XSD = "/xsd/cashboxapi.xsd";

    @RequestMapping(value = "cashboxapi", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public Resource xsd() {
        return new ClassPathResource(CASHBOX_API_XSD);
    }

}
