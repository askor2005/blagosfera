package ru.askor.blagosfera.domain.community;

public enum CommunityMemberStatus {
	REQUEST, // 0
	INVITE,  // 1
	MEMBER,  // 2
	CONDITION_NOT_DONE_REQUEST, // 3 Участник выполнил запрос на вступление, но есть условие для завершения
	CONDITION_DONE_REQUEST, // 4 Условие для завершения выполнено
	JOIN_IN_PROCESS, // 5 Принятие нового члена сообщества в процессе
	REQUEST_TO_LEAVE, // 6 Выполнен запрос на выход из объединения
	LEAVE_IN_PROCESS, // 7 Выход из объединения в рассмотрении
	//NEED_SIGN_DOCUMENTS // 8 Необходимо подписать документы для вступления
}