package ru.askor.blagosfera.core.services.robokassa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.settings.SystemSettingObserver;
import ru.askor.blagosfera.domain.xml.robokassa.ObjectFactory;
import ru.askor.blagosfera.domain.xml.robokassa.OpState;
import ru.askor.blagosfera.domain.xml.robokassa.OpStateResponse;
import ru.askor.blagosfera.domain.xml.robokassa.OperationStateResponse;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by max on 19.07.16.
 */
public class RobokassaClientImpl extends WebServiceGatewaySupport implements RobokassaClient, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    private String login;
    private String pass1;
    private String pass2;
    private String test;
    private ObjectFactory objectFactory;
    private MessageDigest messageDigest;

    public RobokassaClientImpl(WebServiceMessageFactory messageFactory) throws NoSuchAlgorithmException {
        super(messageFactory);
        objectFactory = new ObjectFactory();
        messageDigest = MessageDigest.getInstance("MD5");
    }

    @PostConstruct
    @Override
    public void init() {
        login = settingsManager.getSystemSetting("robokassa.login");
        pass1 = settingsManager.getSystemSetting("robokassa.pass1");
        pass2 = settingsManager.getSystemSetting("robokassa.pass2");
        test = settingsManager.getSystemSetting("robokassa.test");

        settingsManager.registerObserver(this);
    }

    @Override
    public OperationStateResponse getTransactionState(Long transId) throws UnsupportedEncodingException {
        StringBuilder crcBase = new StringBuilder();
        crcBase.append(login);
        crcBase.append(":");
        crcBase.append(String.valueOf(transId));
        crcBase.append(":");
        crcBase.append(pass2);

        OpState opState = objectFactory.createOpState();
        opState.setMerchantLogin(login);
        opState.setInvoiceID(transId.intValue());
        opState.setSignature(calculateCrc(crcBase.toString()));

        OpStateResponse response = (OpStateResponse) getWebServiceTemplate().marshalSendAndReceive(getDefaultUri(),
                opState, new SoapActionCallback("http://merchant.roboxchange.com/WebService/OpState"));

        return response.getOpStateResult();
    }

    @Override
    public String calculateCrc(BigDecimal amount, Long invoiceId, boolean usePass1, boolean useLogin) throws UnsupportedEncodingException {
        StringBuilder crcBase = new StringBuilder();

        if (useLogin) {
            crcBase.append(login);
            crcBase.append(":");
        }

        crcBase.append(String.valueOf(amount));
        crcBase.append(":");
        crcBase.append(String.valueOf(invoiceId));
        crcBase.append(":");
        crcBase.append(usePass1 ? pass1 : pass2);
        return calculateCrc(crcBase.toString());
    }

    private String calculateCrc(String base) throws UnsupportedEncodingException {
        messageDigest.update(base.getBytes("UTF-8"));
        byte[] digest = messageDigest.digest();
        return DatatypeConverter.printHexBinary(digest);
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getTest() {
        return test;
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        switch (key) {
            case "robokassa.login":
                login = val;
                break;
            case "robokassa.pass1":
                pass1 = val;
                break;
            case "robokassa.pass2":
                pass2 = val;
                break;
            case "robokassa.test":
                test = val;
        }
    }
}
