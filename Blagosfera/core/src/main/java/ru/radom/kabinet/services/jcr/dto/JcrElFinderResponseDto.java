package ru.radom.kabinet.services.jcr.dto;

import lombok.Data;
import ru.radom.kabinet.services.jcr.JcrFilesService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 26.02.2016.
 */
@Data
public class JcrElFinderResponseDto {

    /**
     * Данные для ответа при открытии каталога
     */
    private List<ElFinderFile> files = new ArrayList<>();

    /**
     * Данные для ответа при открытии каталога
     */
    private List<ElFinderFile> tree = new ArrayList<>();

    /**
     * Данные для ответа при добавлении файлов
     */
    private List<ElFinderFile> added = new ArrayList<>();
    /**
     * Данные для ответа при изменении файлов
     */
    private List<ElFinderFile> changed = new ArrayList<>();
    /**
     * Данные для отображения в левой панели эксплорера
     */
    private ElFinderFileOptions options;

    /**
     * Список названий файлов каталога
     */
    private List<String> list = new ArrayList<>();

    /**
     * Список ИД файлов, которые были удалены
     */
    private List<String> removed = new ArrayList<>();

    /**
     * Контейнер с файлом
     */
    private JcrFilesService.FileContentWrapper fileContentWrapper;

    /**
     * Контент текстового файла
     */
    private String content;

    private ElFinderFile cwd;

    private String uplMaxSize;
}
