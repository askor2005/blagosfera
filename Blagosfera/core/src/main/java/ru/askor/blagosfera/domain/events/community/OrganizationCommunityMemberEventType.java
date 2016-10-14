package ru.askor.blagosfera.domain.events.community;

/**
 * Created by vgusev on 23.10.2015.
 */
public enum OrganizationCommunityMemberEventType {

    REQUEST, // Событие запроса на вступление в объединение
    REJECT_REQUEST, // Событие отказа в запросе на вступление
    ACCEPT_TO_JOIN, // Принятие в объедиение
    EXCLUDE, // Руководство объедиения исключило организацию

    REQUEST_TO_PO, // Событие запроса на вступление в ПО
    REJECT_REQUEST_TO_PO, // Событие отказа в запросе на вступление в ПО
    ACCEPT_TO_JOIN_IN_PO, // Принятие в ПО
    REQUEST_TO_EXCLUDE_FROM_PO, // Создан запрос на выход из ПО
    ACCEPT_TO_EXCLUDE_FROM_PO, // Организацию исключили из ПО

    REQUEST_TO_KUCH_PO, // Событие запроса на вступление в КУч ПО
    REJECT_REQUEST_TO_KUCH_PO, // Событие отказа в запросе на вступление в КУч ПО
    ACCEPT_TO_JOIN_IN_KUCH_PO, // Принятие в КУч ПО
    REQUEST_TO_EXCLUDE_FROM_KUCH_PO, // Создан запрос на выход из КУч ПО
    ACCEPT_TO_EXCLUDE_FROM_KUCH_PO // Организацию исключили из КУч ПО
}
