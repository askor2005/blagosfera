package ru.radom.kabinet.services.payment;

import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;
import ru.radom.kabinet.services.SystemSettingsService;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.AutopostParameters;

@Component("webmoneyPaymentSystemBean")
public class WebmoneyPaymentSystemBean implements PaymentSystemBean {

	private static final Logger logger = LoggerFactory.getLogger(WebmoneyPaymentSystemBean.class);

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private SystemSettingsService systemSettingsService;

	@Autowired
	private PaymentService paymentService;

	@Override
	public AutopostParameters initIncomingPayment(Payment payment) {
		AutopostParameters parameters = new AutopostParameters();
		parameters.setAction("https://merchant.webmoney.ru/lmi/payment.asp");
		parameters.put("LMI_PAYMENT_AMOUNT", StringUtils.formatMoney(payment.getRurAmount()));
		String converted = StringUtils.toBase64(payment.getComment());
		parameters.put("LMI_PAYMENT_DESC_BASE64", converted);
		parameters.put("LMI_PAYEE_PURSE", settingsManager.getSystemSetting("payment.webmoney.payee-purse"));
		parameters.put("LMI_PAYMENT_NO", payment.getId().toString());
		parameters.put("LMI_SUCCESS_URL ", systemSettingsService.getApplicationUrl() + "/payment/incoming/status/" + payment.getId());
		parameters.put("LMI_FAIL_URL ", systemSettingsService.getApplicationUrl() + "/payment/incoming/status/" + payment.getId());
		parameters.put("LMI_SIM_MODE", "0");
		parameters.put("payment_id", payment.getId().toString());
		paymentService.changeIncomingPaymentStatus(payment, PaymentStatus.PROCESSING);
		return parameters;
	}

	private WebmoneyX18Result sendX18(String paymentNo) {
		try {
			JSONObject json = new JSONObject();
			json.put("wmid", settingsManager.getSystemSetting("payment.webmoney.wmid"));
			json.put("lmi_payee_purse", settingsManager.getSystemSetting("payment.webmoney.payee-purse"));
			json.put("lmi_payment_no", paymentNo);
			json.put("secret_key", settingsManager.getSystemSetting("payment.webmoney.secret-key"));
			String body = XML.toString(json, "merchant.request");
            body = new HTTP().doPost("https://merchant.webmoney.ru/conf/xml/XMLTransGet.asp", body, ContentType.APPLICATION_XML, null).getDataAsString();
			String retVal;

			try {
				retVal = body.substring(body.indexOf("<retval>") + 8, body.indexOf("</retval>"));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				retVal = null;
			}

			String purseFrom;
			try {
				purseFrom = body.substring(body.indexOf("<pursefrom>") + 11, body.indexOf("</pursefrom>"));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				purseFrom = null;
			}

			WebmoneyX18Result result = new WebmoneyX18Result();
			result.setPurseFrom(purseFrom);
			result.setRetVal(retVal);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public PaymentStatus checkIncomingPayment(Payment payment) {
		// раскомментировать когда начнем принимать по-настоящему
//		WebmoneyX18Result result = sendX18(payment.getId().toString());
//		if (result == null || StringUtils.isEmpty(result.getRetVal())) {
//			return PaymentStatus.PROCESSING;
//		}
//
//		switch (result.getRetVal()) {
//		case "0":
//			payment.setSender(result.getPurseFrom());
//			return PaymentStatus.SUCCESS;
//		case "7":
//		case "11":
//			return PaymentStatus.PROCESSING;
//		default:
//			return PaymentStatus.FAIL;
//		}

		return PaymentStatus.SUCCESS;
	}

	@Override
	public void onPaymentComplete(Payment payment) {
		// TODO Auto-generated method stub

	}

	@Override
	public Payment initOutgoingPayment(Payment payment) {
		return payment;
	}

	@Override
	public PaymentStatus checkOutgoingPayment(Payment payment) {
		return PaymentStatus.SUCCESS;
	}

	@Override
	public String getIdentifier() {
		return settingsManager.getSystemSetting("payment.webmoney.payee-purse");
	}

}
