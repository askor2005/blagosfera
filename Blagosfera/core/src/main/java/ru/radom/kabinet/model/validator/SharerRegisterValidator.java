package ru.radom.kabinet.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.utils.StringUtils;

/**
 *
 * @author dfilinberg
 */
@Component
public class SharerRegisterValidator implements Validator {

	
	@Autowired
	private SharerDao sharerDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserEntity.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserEntity userEntity = (UserEntity) target;
		if (StringUtils.isEmpty(userEntity.getEmail())) {
			errors.rejectValue("email", "validator.register.empty-email", "E-mail не введен");
		}
		if (StringUtils.isEmpty(userEntity.getPassword())) {
			errors.rejectValue("password", "validator.register.empty-password", "Пароль не введен");
		}
		if (!StringUtils.checkEmail(userEntity.getEmail())) {
			errors.rejectValue("email", "validator.register.invalid-email", "Введен некорректный e-mail");
		}
		if (sharerDao.existsEmail(userEntity.getEmail())) {
			errors.rejectValue("email", "validator.register.already-exists-email", "Пользователь с указанным Email уже зарегистрирован");
		}
	}

}
