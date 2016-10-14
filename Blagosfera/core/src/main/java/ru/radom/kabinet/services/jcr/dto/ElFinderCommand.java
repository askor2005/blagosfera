package ru.radom.kabinet.services.jcr.dto;

/**
 *
 * Created by vgusev on 26.02.2016.
 */
public enum ElFinderCommand {
    open, // Откырть файл
    tree, // Получить иерархию каталогов
    rename, // Изменить название файла
    mkdir, // Создать каталог
    mkfile, // Создать файл
    upload, // Загрузить файл
    ls, // Получить список файлов каталога
    rm, // Удалить файл
    paste, // Вставить файл
    put, // Отредактировать тектовый файл
    get, // Получить текстовый контент файла
    file, // Открыть файл для чтения
    archive, // Архивация файлов
    duplicate, // Дублирование файла
    parents // TODO Необходимо реализовать поведение
}
