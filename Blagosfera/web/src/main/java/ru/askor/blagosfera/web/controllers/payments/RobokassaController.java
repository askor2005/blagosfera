package ru.askor.blagosfera.web.controllers.payments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.services.robokassa.RobokassaClient;
import ru.askor.blagosfera.core.services.robokassa.RobokassaService;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 19.07.16.
 */
@Controller
@RequestMapping("/payments/robokassa")
public class RobokassaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RobokassaService robokassaService;

    @Autowired
    private RobokassaClient robokassaClient;

    public RobokassaController() {
    }

    @PreAuthorize("isAuthenticated()")
    @TokenProtected
    @ResponseBody
    @RequestMapping(value = "initpayment.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> initTransaction(@RequestParam("account_type_id") Long accountTypeId,
                                               @RequestParam("payment_system_id") Long paymentSystemId,
                                               @RequestParam("ra_amount") BigDecimal amountRa,
                                               @RequestParam("rur_amount") BigDecimal amount,
                                               @RequestParam("description") String description) throws UnsupportedEncodingException {
        Transaction payment = robokassaService.initPayment(SecurityUtils.getUser().getId(), accountTypeId, amountRa, description);

        Map<String, String> result = new HashMap<>();
        result.put("login", robokassaClient.getLogin());
        result.put("test", robokassaClient.getTest());
        result.put("inv", String.valueOf(payment.getId()));
        result.put("amount", String.valueOf(payment.getAmount()));
        result.put("crc", robokassaClient.calculateCrc(amount, payment.getId(), true, true));
        return result;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "success", method = RequestMethod.POST)
    public String success() {
        return "redirect:/ng/windowclose.html";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "fail", method = RequestMethod.POST)
    public String fail() {
        return "redirect:/ng/windowclose.html";
    }

    @PreAuthorize("permitAll()")
    @ResponseBody
    @RequestMapping(value = "result", method = RequestMethod.POST)
    public String result(@RequestParam("OutSum") BigDecimal amount,
                         @RequestParam("InvId") Long invoiceId,
                         @RequestParam("SignatureValue") String crc) throws UnsupportedEncodingException {
        Transaction payment = accountService.getTransaction(invoiceId);

        if (payment != null) {
            String crc2 = robokassaClient.calculateCrc(amount, invoiceId, false, false);

            if (crc2.equalsIgnoreCase(crc)) {
                if (robokassaClient.getTest().equals("1")){
                    accountService.postTransaction(payment.getId());
                } else {
                    robokassaService.checkPayment(payment.getId());
                }

                return "OK" + invoiceId;
            }
        }

        return "bad sign\n";
    }
}
