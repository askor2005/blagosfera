package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.utils.StringUtils;

@Component
public class PaymentSerializer extends AbstractSerializer<Payment> {

	@Override
	public JSONObject serializeInternal(Payment object) {
		JSONObject json = new JSONObject();
        if (object == null) return json;

		json.put("id", object.getId());
		json.put("comment", object.getComment());
		json.put("system", object.getSystem().getName());
		json.put("raAmount", StringUtils.formatMoney(object.getRaAmount()));
		json.put("rurAmount", StringUtils.formatMoney(object.getRurAmount()));
		json.put("rameraComissionAmount", StringUtils.formatMoney(object.getRameraComissionAmount()));
		json.put("rameraComission", object.getRameraComission());
		json.put("sender", object.getSender());
		json.put("receiver", object.getReceiver());
		return json;
	}

}
