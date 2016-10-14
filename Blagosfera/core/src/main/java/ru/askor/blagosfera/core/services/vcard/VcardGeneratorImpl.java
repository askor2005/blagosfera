package ru.askor.blagosfera.core.services.vcard;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.ImageType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.*;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.TimeZone;

/**
 * Created by vtarasenko on 13.06.2016.
 */
@Service
public class VcardGeneratorImpl implements VcardGenerator {
    @Override
    public String generate(User user, byte[] avatar, ru.askor.blagosfera.domain.Address officeAddress,
                           ru.askor.blagosfera.domain.Address actualAddress, String timezone,
                           String registratorOfficePhone, String registratorMobilePhone, String registratorPhone, String appUrl) throws IOException {
        VCard vcard = new VCard();
        vcard.setKind(Kind.individual());
        vcard.setGender(user.isSex() ? Gender.male() : Gender.female());
        StructuredName n = new StructuredName();
        n.setFamily(user.getLastName());
        n.setGiven(user.getFirstName());
        n.getAdditionalNames().add(user.getSecondName());
        vcard.setStructuredName(n);
        vcard.setFormattedName(user.getFullName());
        Address adr = null;
        if (officeAddress != null) {
            adr = new Address();
            if (!StringUtils.isEmpty(officeAddress.getStreet())){
                adr.setStreetAddress(((officeAddress.getStreet() != null) ? officeAddress.getStreet() : "") + ((officeAddress.getBuilding() != null) ? " "+officeAddress.getBuilding() : ""));
            }
            if (!StringUtils.isEmpty(officeAddress.getCity())) {
                adr.setLocality(officeAddress.getCity());
            }
            if (!StringUtils.isEmpty(officeAddress.getRegion())) {
                adr.setRegion(officeAddress.getRegion());
            }
            if (!StringUtils.isEmpty(officeAddress.getPostalCode())) {
                adr.setPostalCode(officeAddress.getPostalCode());
            }
            if (!StringUtils.isEmpty(officeAddress.getCountry())) {
                adr.setCountry(officeAddress.getCountry());
            }
            if (!StringUtils.isEmpty(officeAddress.getFullAddress())) {
                adr.setLabel(officeAddress.getFullAddress());
            }
            adr.getTypes().add(AddressType.WORK);
            vcard.addAddress(adr);
        }
        if (actualAddress != null) {
            adr = new Address();
            if (!StringUtils.isEmpty(actualAddress.getStreet())) {
                adr.setStreetAddress(((actualAddress.getStreet() != null) ? actualAddress.getStreet() : "") +
                        ((actualAddress.getBuilding() != null) ? " "+actualAddress.getBuilding() : ""));
            }
            if (!StringUtils.isEmpty(actualAddress.getCity())) {
                adr.setLocality(actualAddress.getCity());
            }
            if (!StringUtils.isEmpty(actualAddress.getRegion())) {
                adr.setRegion(actualAddress.getRegion());
            }
            if (!StringUtils.isEmpty(actualAddress.getPostalCode())) {
                adr.setPostalCode(actualAddress.getPostalCode());
            }
            if (!StringUtils.isEmpty(actualAddress.getCountry())) {
                adr.setCountry(actualAddress.getCountry());
            }
            if (!StringUtils.isEmpty(actualAddress.getFullAddress())) {
                adr.setLabel(actualAddress.getFullAddress());
            }
            adr.getTypes().add(AddressType.HOME);
            vcard.addAddress(adr);
        }
        if (registratorOfficePhone != null) {
            vcard.addTelephoneNumber(registratorOfficePhone, TelephoneType.WORK);
        }
        if (registratorMobilePhone != null) {
            vcard.addTelephoneNumber(registratorMobilePhone, TelephoneType.CELL);
        }
        if (registratorPhone != null) {
            vcard.addTelephoneNumber(registratorPhone, TelephoneType.HOME);
        }
        vcard.addEmail(user.getEmail(), EmailType.WORK);
        vcard.getUrls().add(new Url(appUrl+user.getLink()));
        if ((officeAddress != null) && (officeAddress.getLongitude() != null) && (officeAddress.getLatitude() != null)) {
            vcard.setGeo(officeAddress.getLatitude(), officeAddress.getLongitude());
        } else if ((actualAddress != null) && (actualAddress.getLongitude() != null) && (actualAddress.getLatitude() != null)) {
            vcard.setGeo(actualAddress.getLatitude(), actualAddress.getLongitude());
        }
        if (timezone != null) {
            vcard.setTimezone(new Timezone(TimeZone.getTimeZone(timezone)));
        }
        if ((user.getAvatar() != null) && (avatar != null)) {
            if ((user.getAvatar().toUpperCase().endsWith(".JPG")) || ((user.getAvatar().toUpperCase().endsWith(".JPEG")))) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(avatar)).size(64,64).toOutputStream(result);
                Photo photo = new Photo(result.toByteArray(),ImageType.JPEG);
                vcard.addPhoto(photo);
            }
            else if ((user.getAvatar().toUpperCase().endsWith(".PNG"))) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(avatar)).size(64,64).toOutputStream(result);
                Photo photo = new Photo(result.toByteArray(),ImageType.PNG);
                vcard.addPhoto(photo);
            }
        }


        vcard.setUid(Uid.random().random());

        vcard.setRevision(Revision.now());
        StringWriter stringWriter = new StringWriter();
        VCardWriter vCardWriter = new VCardWriter(stringWriter,VCardVersion.V3_0);
        //vCardWriter.setOutlookCompatibility(false);
        vCardWriter.write(vcard);
        //String str = Ezvcard.write(vcard).version(VCardVersion.V3_0).go();
        return stringWriter.toString();
    }

}
