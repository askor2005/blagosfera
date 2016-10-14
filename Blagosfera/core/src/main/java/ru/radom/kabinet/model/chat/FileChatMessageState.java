package ru.radom.kabinet.model.chat;

/**
 *
 * Created by vgusev on 13.11.2015.
 */
public enum FileChatMessageState {

    UPLOAD_IN_PROCESS, // Загрузка файла на сервер в процессе
    UPLOAD_CANCEL, // Загрузка отменена
    UPLOADED // Файл загружен
}
