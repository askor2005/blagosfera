package ru.askor.blagosfera.domain.section;

public enum SectionAccessType {
	// null принимается как ALL
	ALL, // 0 - доступно для всех
	REGISTERED, // 1 - доступно только для зарегистрированных пользователей
	VERIFIED // 2 - доступно только для сертифицированных пользователей
}
