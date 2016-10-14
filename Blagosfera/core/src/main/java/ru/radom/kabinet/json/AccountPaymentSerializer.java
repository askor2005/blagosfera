package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.payment.AccountPayment;

@Component
public class AccountPaymentSerializer extends AbstractSerializer<AccountPayment> {

	@Autowired
	private PaymentSerializer paymentSerializer;
	
	@Override
	public JSONObject serializeInternal(AccountPayment object) {
		JSONObject json = paymentSerializer.serialize(object);
		return json;
	}

}
