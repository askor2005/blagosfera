package ru.askor.blagosfera.core.services.vcard;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.concurrency.OrderingExecutor;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.services.EmailService;
import ru.radom.kabinet.services.SystemSettingsService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.utils.FieldConstants;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vtarasenko on 14.06.2016.
 */
@Service
@Transactional
public class VcardServiceImpl implements VcardService {
    @Autowired
    private FieldValueDao fieldValueDao;
    @Autowired
    private VcardGenerator vcardGenerator;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SharerDao sharerDao;
    @Autowired
    private OrderingExecutor orderingExecutor;
    @Autowired
    private UserSettingsService userSettingsService;
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemSettingsService systemSettingService;
    private void makeVcard(Long currentUserId, Long userId, VcardCreatedCallback vcardCreatedCallback, boolean async) {
        final User currentUser = userService.getUserById(currentUserId);
        final User user = userService.getUserById(userId);
        ru.askor.blagosfera.domain.Address officeAddress = sharerDao.getOfficeAddress(user.getId());
        ru.askor.blagosfera.domain.Address actualAddress = sharerDao.getActualAddress(user.getId());
        if ((actualAddress != null) && (!sharerDao.isActualAddressNotHidden(user.getId()))){
            actualAddress = null;
        }
        else if (actualAddress != null) {
            if (sharerDao.isActualBuildingHidden(userId)) {
             actualAddress.setBuilding(null);
            }
            else if (sharerDao.isActualStreetHidden(userId)) {
                actualAddress.setStreet(null);
            }
            else if (sharerDao.isActualCountryHidden(userId)) {
                actualAddress.setCountry(null);
            }
            else if (sharerDao.isActualCityHidden(userId)) {
                actualAddress.setCity(null);
            }
            actualAddress.setRoom(null);
            actualAddress.setRoomLabel(null);
        }
        List<FieldValueEntity> additionalFieldValues = fieldValueDao.getByFieldList(user.getId(), Discriminators.SHARER, Arrays.asList(
                FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE,
                FieldConstants.SHARER_MOB_TEL,
                FieldConstants.SHARER_HOME_TEL,
                FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE
        ));
        String registratorOfficePhone = null;
        String registratorPhone = null;
        String registratorMobilePhone = null;
        for (FieldValueEntity fieldValue : additionalFieldValues) {
            if ((fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE))) {
                registratorOfficePhone = FieldsService.getFieldStringValue(fieldValue);
            } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_MOB_TEL) && (!fieldValue.isHidden())) {
                registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
            }
            else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE) && (!fieldValue.isHidden())) {
                registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
            }
            else if ((fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_HOME_TEL)) && (!fieldValue.isHidden())) {
                registratorPhone = FieldsService.getFieldStringValue(fieldValue);
            }

        }
        final String timezone = userSettingsService.get(user, "profile.timezone");
        final Address actualAddressFinal = actualAddress;
        final String registratorOfficePhoneFinal = registratorOfficePhone;
        final String registratorMobilePhoneFinal = registratorMobilePhone;
        final String registratorPhoneFinal = registratorPhone;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] avatar = null;
                    if (user.getAvatar() != null) {
                        avatar = imagesService.getBytesFromUrl(user.getAvatar());
                    }
                    String vcard = vcardGenerator.generate(user, avatar, officeAddress, actualAddressFinal, timezone,
                            registratorOfficePhoneFinal, registratorMobilePhoneFinal, registratorPhoneFinal, systemSettingService.getApplicationUrl());
                    if (vcardCreatedCallback != null) {
                        vcardCreatedCallback.onVcardCreated(vcard, user, currentUser);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        if (async) {
            orderingExecutor.execute(runnable, currentUser.getId());
        }
        else {
         runnable.run();
        }
    }

    @Override
    public void sendToHttpResponse(Long currentUserId, Long userId, HttpServletResponse response, String charset) {
        response.setStatus(HttpServletResponse.SC_OK);
        final User currentUser = userService.getUserById(currentUserId);
        final User user = userService.getUserById(userId);
        makeVcard(currentUserId, userId, (vcard, user1, currentUser1) -> {
            response.setHeader("Content-Type", "text/vcard; charset=" + charset);
            response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(user.getFullName().replace(" ","_"),"utf-8") +"("+user.getIkp()+")" +".vcf");
            IOUtils.write(vcard, response.getOutputStream(), Charset.forName(charset));
        }, false);
    }

    @Override
    public void sendToEmail(Long currentUserId, Long userId) {
        final User currentUser = userService.getUserById(currentUserId);
        final User user = userService.getUserById(userId);
        makeVcard(currentUserId, userId, (vcard, user1, currentUser1) -> {
            emailService.sendVcard(user, currentUser, "Система благосфера", "", vcard);
        }, true);
    }
    private interface VcardCreatedCallback {
        public void onVcardCreated(String vcard,User user,User currentUser) throws IOException;
    }
}
