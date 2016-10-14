package ru.askor.blagosfera.domain.community;

/**
 * Состояния запросов на должность
 * Created by vgusev on 28.08.2015.
 */
public enum CommunityPostRequestStatus {
    NEW, // 0 Новый созданный запрос
    IN_PROCESS; // 1 Запрос находится в обработке (пользователь согласился на должность)
}
