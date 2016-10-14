package ru.radom.kabinet.services.payment;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.Response;
import ru.radom.kabinet.dao.payment.PaymentDao;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;
import ru.radom.kabinet.services.SystemSettingsService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.AutopostParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller("yandexMoneyPaymentSystemBean")
public class YandexMoneyPaymentSystemBean implements PaymentSystemBean {

	private static final Logger logger = LoggerFactory.getLogger(YandexMoneyPaymentSystemBean.class);

	@Autowired
	private PaymentDao incomingPaymentDao;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private SystemSettingsService systemSettingsService;

	private JSONObject executePost(String url, Map<String, String> parameters, String token) {
		try {
            Map<String, String> headers = new HashMap<>();

            if (StringUtils.hasLength(token)) {
                headers.put("Authorization", "Bearer " + token);
            }

            Response response = new HTTP().doPost(url, parameters, headers);
			return response.getStatus() == 200 ? new JSONObject(response.getDataAsString()) : null;
		} catch (Exception e) {
			return null;
		}
	}

	private String getToken(String code, Payment payment) {
        Map<String, String> parameters = new HashMap<>();
		parameters.put("code", code);
		parameters.put("client_id", settingsManager.getSystemSetting("payment.yandex-money.client-id"));
		parameters.put("grant_type", "authorization_code");
		parameters.put("redirect_uri", systemSettingsService.getApplicationUrl() + "/yandex_money/success_auth?incoming_payment_id=" + payment.getId());
		JSONObject responseJson = executePost("https://sp-money.yandex.ru/oauth/token", parameters, null);
		if (responseJson.keySet().contains("access_token")) {
			return responseJson.getString("access_token");
		} else if (responseJson.keySet().contains("error")) {
			throw new YandexMoneyException(responseJson.getString("error") + (responseJson.keySet().contains("error_description") ? " " + responseJson.getString("error_description") : ""));
		} else {
			throw new YandexMoneyException("");
		}
	}

	private String requestPayment(String token, Payment payment, String receiver) {
        Map<String, String> parameters = new HashMap<>();
		parameters.put("pattern_id", "p2p");
		parameters.put("to", receiver);
		parameters.put("amount_due", StringUtils.formatMoney(payment.getRurAmount()));
		parameters.put("comment", StringUtils.toIso(payment.getComment()));
		parameters.put("message", StringUtils.toIso(payment.getComment()));
		parameters.put("test_payment", "true");
		parameters.put("test_result", "success");

		JSONObject responseJson = executePost("https://money.yandex.ru/api/request-payment", parameters, token);

		if (responseJson.keySet().contains("request_id")) {
			return responseJson.getString("request_id");
		} else if (responseJson.keySet().contains("error")) {
			throw new YandexMoneyException(responseJson.getString("error") + (responseJson.keySet().contains("error_description") ? " " + responseJson.getString("error_description") : ""));
		} else {
			throw new YandexMoneyException("");
		}
	}

	private YandexProcessPaymentResult processPayment(String requestId, String token) {
        Map<String, String> parameters = new HashMap<>();
		parameters.put("request_id", requestId);
		parameters.put("test_payment", "true");
		parameters.put("test_result", "success");
		JSONObject responseJson = executePost("https://money.yandex.ru/api/process-payment", parameters, token);

		if (responseJson == null) {
			return null;
		}

		YandexProcessPaymentResult result = new YandexProcessPaymentResult();
		result.setStatus(JsonUtils.getString(responseJson, "status", null));
		result.setError(JsonUtils.getString(responseJson, "error", null));
		result.setErrorDescription(JsonUtils.getString(responseJson, "error_description", null));
		result.setPayer(JsonUtils.getString(responseJson, "payer", null));
		result.setPayee(JsonUtils.getString(responseJson, "payee", null));
		return result;
	}

	private void revokeToken(String token) {
        executePost("https://money.yandex.ru/api/revoke", null, token);
	}

	@Override
	public AutopostParameters initIncomingPayment(Payment payment) {
		AutopostParameters autopostParameters = new AutopostParameters();
		Map<String, String> map = new HashMap<String, String>();
		map.put("client_id", settingsManager.getSystemSetting("payment.yandex-money.client-id"));
		map.put("response_type", "code");
		map.put("redirect_uri", systemSettingsService.getApplicationUrl() + "/yandex_money/success_auth?incoming_payment_id=" + payment.getId());
		map.put("scope", "payment.to-account(\"" + settingsManager.getSystemSetting("payment.yandex-money.account-id") + "\").limit(," + StringUtils.formatMoney(payment.getRurAmount()) + ")");
		autopostParameters.setMap(map);
		autopostParameters.setAction("https://sp-money.yandex.ru/oauth/authorize");
		return autopostParameters;
	}

	@Override
	public PaymentStatus checkIncomingPayment(Payment payment) {
		JSONObject additionalData = new JSONObject(payment.getAdditionalData());
		String requestId = additionalData.getString("requestId");
		String token = additionalData.getString("token");

		YandexProcessPaymentResult result = processPayment(requestId, token);

		if (result == null || StringUtils.isEmpty(result.getStatus())) {
			return PaymentStatus.PROCESSING;
		}

		switch (result.getStatus()) {
		case "success":
			payment.setSender(result.getPayer());
			return PaymentStatus.SUCCESS;
		case "refused":
			payment.setError(result.getError() + (StringUtils.hasLength(result.getErrorDescription()) ? " " + result.getErrorDescription() : ""));
			return PaymentStatus.FAIL;
		default:
			return PaymentStatus.PROCESSING;
		}
	}

	@Override
	public void onPaymentComplete(Payment payment) {
		try {
			JSONObject additionalData = new JSONObject(payment.getAdditionalData());
			String token = additionalData.getString("token");
			revokeToken(token);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		payment.setAdditionalData(null);
		incomingPaymentDao.update(payment);
	}

	@RequestMapping("/yandex_money/success_auth")
	public String processSuccessAuth(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error, @RequestParam("incoming_payment_id") Payment payment, HttpServletRequest request) {
		try {
			System.out.println(request);
			if (StringUtils.hasLength(code)) {
				String token = getToken(code, payment);
				String requestId = requestPayment(token, payment, settingsManager.getSystemSetting("payment.yandex-money.account-id"));
				JSONObject additionalData = new JSONObject();
				additionalData.put("token", token);
				additionalData.put("requestId", requestId);
				payment.setAdditionalData(additionalData.toString());
				paymentService.changeIncomingPaymentStatus(payment, PaymentStatus.PROCESSING);
				// payment = paymentService.checkIncomingPayment(payment);
			} else {
				throw new YandexMoneyException(StringUtils.hasLength(error) ? error : "");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			paymentService.failIncomingPayment(payment, e instanceof YandexMoneyException ? e.getMessage() : "");
		}
		return "redirect:/payment/incoming/status/" + payment.getId();
	}

	@Override
	public Payment initOutgoingPayment(Payment payment) {
		try {
			String requestId = requestPayment(settingsManager.getSystemSetting("payment.yandex-money.token"), payment, payment.getReceiver());
			payment.setAdditionalData(JsonUtils.getJson("requestId", requestId).toString());
			return payment;
		} catch (YandexMoneyException e) {
			throw new PaymentException("Ошибка инициализации платежа. Сообщение от платежной системы: " + e.getMessage());
		}
	}

	@Override
	public PaymentStatus checkOutgoingPayment(Payment payment) {
		JSONObject additionalData = new JSONObject(payment.getAdditionalData());
		String requestId = additionalData.getString("requestId");
		try {
			YandexProcessPaymentResult result = processPayment(requestId, settingsManager.getSystemSetting("payment.yandex-money.token"));
			
			if (result == null || StringUtils.isEmpty(result.getStatus())) {
				return PaymentStatus.PROCESSING;
			}
			
			switch (result.getStatus()) {
			case "success":
				return PaymentStatus.SUCCESS;
			case "refused":
				payment.setError(result.getError() + (StringUtils.hasLength(result.getErrorDescription()) ? " " + result.getErrorDescription() : ""));
				return PaymentStatus.FAIL;
			default:
				return PaymentStatus.PROCESSING;
			}
			
		} catch (YandexMoneyException e) {
			payment.setError(e.getMessage());
			return PaymentStatus.FAIL;
		}
	}

	@Override
	public String getIdentifier() {
		return settingsManager.getSystemSetting("payment.yandex-money.account-id");
	}

}
