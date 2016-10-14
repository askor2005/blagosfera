package ru.radom.kabinet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentSystemEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.payment.PaymentException;
import ru.radom.kabinet.services.payment.PaymentService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;

import java.math.BigDecimal;

@Controller
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

	@Autowired
    private SharerDao sharerDao;

	@RequestMapping(value = "/payment/incoming/account/init")
	public String showInitAccountIncomingPaymentPage(@RequestParam("account_type_id") AccountTypeEntity accountType, @RequestParam("payment_system_id") PaymentSystemEntity paymentSystem, @RequestParam(value = "ra_amount", required = false) BigDecimal raAmount, @RequestParam(value = "rur_amount", required = false) BigDecimal rurAmount, Model model) {
		Payment payment = paymentService.createAccountIncomingPayment(raAmount, rurAmount, accountDataService.getUserAccountEntity(SecurityUtils.getUser().getId(), accountType.getId()), paymentSystem);
		AutopostParameters autopostParameters = paymentService.initIncomingPayment(payment);
		model.addAttribute("autopostParameters", autopostParameters);
		return "autopost";
	}

	@RequestMapping(value = "/payment/incoming/status")
	public String redirectPaymentStatusPage(@RequestParam("payment_id") String id) {
		return "redirect:/payment/incoming/status/" + id;
	}

	@RequestMapping(value = "/payment/incoming/status/{id}")
	public String showPaymentStatusPage(@PathVariable("id") Payment payment, Model model) {
		switch (payment.getStatus()) {
		case SUCCESS:
			model.addAttribute("message", "Платеж успешно завершен");
			break;
		case FAIL:
			model.addAttribute("message", "Произошла ошибка, платеж отменен");
			model.addAttribute("description", payment.getError());
			break;
		default:
			model.addAttribute("message", "Платеж обрабатывается");
			break;
		}
		model.addAttribute("payment", payment);
		return "incomingPaymentStatus";
	}

	@RequestMapping(value = "/payment/outgoing/init.json", method = RequestMethod.POST)
	public @ResponseBody String initOutgoingPage(@RequestParam(value = "payment_system_id") PaymentSystemEntity system, @RequestParam(value = "account_type_id") AccountTypeEntity accountType, @RequestParam(value = "ra_amount", required = false) BigDecimal raAmount, @RequestParam(value = "rur_amount", required = false) BigDecimal rurAmount, @RequestParam(value = "receiver") String receiver) {
		try {
			if (accountType == null) {
				throw new PaymentException("Не выбран счёт");
			}
			Payment payment = paymentService.createAccountOutgoingPayment(raAmount, rurAmount, accountDataService.getUserAccountEntity(SecurityUtils.getUser().getId(), accountType.getId()), system, receiver);
			payment = paymentService.initOutgoingPayment(payment);
			return JsonUtils.getSuccessJson().toString();
		} catch (PaymentException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "Ошибка инициализации вывода. Попробуйте позже или обратитесь в службу технической поддержки.").toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson("Ошибка инициализации вывода. Попробуйте позже или обратитесь в службу технической поддержки.").toString();
		}
	}

}
