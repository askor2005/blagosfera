package ru.askor.blagosfera.domain.document;

/**
 * Интерфейс создателя документа
 * Created by vgusev on 14.04.2016.
 */
public interface DocumentCreator {

    Long getId();

    String getName();

    String getAvatar();

    String getIkp();
}
