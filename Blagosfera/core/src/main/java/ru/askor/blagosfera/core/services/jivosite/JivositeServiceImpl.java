package ru.askor.blagosfera.core.services.jivosite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.domain.jivosite.JivositeUserInfo;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.FieldConstants;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vtarasenko on 25.07.2016.
 */
@Service
@Transactional
public class JivositeServiceImpl implements JivositeService {
    @Autowired
    private UserService userService;
    @Autowired
    private FieldValueDao fieldValueDao;
    @Autowired
    private UserRepository userRepository;
    @Override
    public JivositeUserInfo getUserInfo(Long userId){
        User user = userService.getUserById(userId);
        JivositeUserInfo jivositeUserInfo = new JivositeUserInfo();
        jivositeUserInfo.setEmail(user.getEmail());
        List<FieldValueEntity> additionalFieldValues = fieldValueDao.getByFieldList(userRepository.findOne(userId), Arrays.asList(
                FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE,
                FieldConstants.SHARER_MOB_TEL,
                FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE,
                FieldConstants.SHARER_HOME_TEL
        ));
        String registratorOfficePhone = null;
        String registratorPhone = null;
        String registratorMobilePhone = null;
        for (FieldValueEntity fieldValue : additionalFieldValues) {
            if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE)) {
                registratorOfficePhone = FieldsService.getFieldStringValue(fieldValue);
            } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE)) {
                registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
            }
            else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_MOB_TEL)) {
                registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
            }
            else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_HOME_TEL)) {
                registratorPhone = FieldsService.getFieldStringValue(fieldValue);
            }

        }
        if ((registratorOfficePhone == null)){
            registratorOfficePhone = registratorPhone;
        }
        if ((registratorOfficePhone == null)){
            registratorOfficePhone = registratorMobilePhone;
        }
        jivositeUserInfo.setPhone(registratorOfficePhone);
        jivositeUserInfo.setName(user.getFullName());
        return jivositeUserInfo;
    }
    @Override
    public String getUserToken(Long userId){
        User user = userService.getUserById(userId);
       return  user.getIkp();
    }
}
