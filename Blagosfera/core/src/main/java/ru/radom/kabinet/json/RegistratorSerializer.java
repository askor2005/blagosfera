package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dto.Timetable;

import java.text.DecimalFormat;

@Component
public class RegistratorSerializer extends AbstractSerializer<RegistratorDomain>{

    private final static DecimalFormat DISTANCE_FORMATTER = new DecimalFormat("#.##");

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private SharerDao sharerDao;

    @Override
	public JSONObject serializeInternal(RegistratorDomain registrator) {
		final JSONObject json = new JSONObject();
        json.put("user", serializationManager.serialize(registrator.getUser()));
        json.put("level", serializationManager.serialize(registrator.getLevel()));
        //json.put("registrationOfficeAddress", serializationManager.serialize(sharerDao.getRegistratorOfficeAddress(registrator.getUser())));
       /* FieldValueEntity tiimeTableFieldValue = fieldValueDao.get(registrator.getUser(), FieldConstants.SHARER_REGISTRATOR_OFFICE_TIMETABLE);
        if (tiimeTableFieldValue != null && !StringUtils.isBlank(tiimeTableFieldValue.getStringValue())) {
            Timetable timetable = new Timetable(FieldsService.getFieldStringValue(tiimeTableFieldValue));
            json.put("timetable", serializationManager.serialize(timetable));
        }*/
        if(registrator.getDistance() != null) {
            json.put("distance", DISTANCE_FORMATTER.format(registrator.getDistance() / 1000));
        }
        //new Timetable();

        // Доп поля пользователя
        /*List<FieldValueEntity> fieldValues = fieldValueDao.getByFieldList(registrator.getUser(), Arrays.asList(
                FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE,
                FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE,
                FieldConstants.SHARER_SKYPE
        ));*/
        String registratorMobilePhone = null;
        String registratorOfficePhone = null;
        String skype = null;
        /*for (FieldValueEntity fieldValue : fieldValues) {
            if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE)) {
                registratorOfficePhone = FieldsService.getFieldStringValue(fieldValue);
            } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE)) {
                registratorOfficePhone = FieldsService.getFieldStringValue(fieldValue);
            } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_SKYPE)) {
                skype = FieldsService.getFieldStringValue(fieldValue);
            }
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("registratorMobilePhone", registratorMobilePhone);
        jsonObject.put("registratorOfficePhone", registratorOfficePhone);
        jsonObject.put("skype", skype);

        json.put("sharerFields", jsonObject);
		return json;
	}

}
