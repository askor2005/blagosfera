package ru.askor.blagosfera.core.services.user;

import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
public interface UserService {

    User getUserById(Long userId);

    User getUserByIkp(String ikp);

    List<Field> getUserFields(Long userId);

    User createFakeUser(String email, String firstName, String secondName, String lastName);

    void saveBasicInfo(Long id, Map<String, String> basicInformation);

    void saveRegAddress(Long id, Map<String, String> regAddress);

    void saveRegistratorData(Long id, Map<String, String> registratorData);

    void saveRegistratorOfficeAddress(Long id, Map<String, String> registratorOfficeAddress);

    void saveFactAddress(Long id, Map<String, String> factAddress);

    String saveUserSignature(String signature, Long userId);
    User saveUserAvatar(String base64avatar, String base64CroppedAvatar,Long userId);

    void saveUserProfile(Map<String, String> basicInformation, Map<String, String> regAddress, Map<String, String> factAddress,
                         Map<String, String> registratorOfficeAddress, Map<String, String> registratorData);

    Map<String,Boolean> getBasicInfoVisibilityMap(User user);

    Map<String,Boolean> getFactAddressVisibilityMap(User user);

    Map<String,Boolean> getRegAddressVisibilityMap(User user);

    Map<String,Boolean> getRegistratorOfficeAddressVisibilityMap(User user);

    Map<String,Boolean> getRegistratorDataAddressVisibilityMap(User user);

    void setFieldVisibilityBasicInformation(String name, boolean value);

    void setFieldVisibilityRegistratrationAddress(String name, boolean value);

    void setFieldVisibilityFactAddress(String name, boolean value);

    void setFieldVisibilityRegistratorOfficeAddress(String name, boolean value);

    void setFieldVisibilityRegistratorInfo(String name, boolean value);
}

