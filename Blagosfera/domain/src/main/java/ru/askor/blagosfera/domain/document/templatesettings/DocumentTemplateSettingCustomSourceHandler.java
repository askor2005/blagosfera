package ru.askor.blagosfera.domain.document.templatesettings;

/**
 * Интерфейс обработки кастомных источников
 * Created by vgusev on 15.07.2016.
 */
public interface DocumentTemplateSettingCustomSourceHandler {

    Long handleCustomSource(String sourceName);
}
