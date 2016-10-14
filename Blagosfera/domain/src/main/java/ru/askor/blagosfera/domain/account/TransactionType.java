package ru.askor.blagosfera.domain.account;

/**
 * Created by Maxim Nikitin on 29.03.2016.
 */
public enum TransactionType {

    PAYMENT_SYSTEM, // пополнение счета / вывод средств через платежные системы
    IAP,            // покупки внутри системы
    USER,           // перевод между пользователями
    USER_COMMUNITY, // перевод между пользователями и объединениями
    COMMUNITY,      // перевод между объединениями
    SHAREBOOK       // перевод между пользователем и паевой книжкой
}
