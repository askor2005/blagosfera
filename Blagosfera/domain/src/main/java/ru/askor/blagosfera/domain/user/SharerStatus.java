package ru.askor.blagosfera.domain.user;

/**
 *
 * @author dfilinberg
 */
public enum SharerStatus {

	FIRST, // 0
	PARTIAL, // 1
	WAIT, // 2 ждет активации
	CONFIRM, // 3 емейл подтвержден, пользователь активен
	NEED_CHANGE_PASSWORD, // 4 Приглашённый пользователь не поменял пароль, но активен
    WAITING_FOR_CERTIFICATION // 5 certification request does exist
}
