package ru.radom.kabinet.services.jcr.dto;

import lombok.Data;
import org.apache.commons.fileupload.FileItem;
import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка с параметрами запроса к действиям менеджера файлов ElFinder
 * Created by vgusev on 26.02.2016.
 */
@Data
public class JcrElFinderRequestDto {

    /**
     * Команда запроса к файлам
     */
    private ElFinderCommand command;
    /**
     * Название файла при команде созания
     */
    private String name;
    /**
     * ИД файла с которым нужно выполнить команду
     */
    private String target;
    /**
     *
     */
    private String current;
    /**
     * Признак запроса - получение параметров для менеджера файлов
     */
    private boolean isInit;
    /**
     * Признак запроса - получение иерархии
     */
    private boolean isTree;
    /**
     * Признак запроса - "Вырезать" файл
     */
    private boolean isCut;
    /**
     *
     */
    private List<String> targets = new ArrayList<>();
    /**
     *
     */
    private String destination;
    /**
     *
     */
    private String source;
    /**
     *
     */
    private String content;
    /**
     * Тип архива
     */
    private String type;

    /**
     * Список загружаемых файлов
     */
    private List<FileItem> fileItems;

    /**
     * Флаг - необходимо скачать документ
     */
    private boolean isDownload;

    /**
     * Текущий пользователь
     */
    private User currentUser;
    /**
     * Тип сущности к которой относятся файлы
     */
    private String entityType;
    /**
     * ИД сущности
     */
    private Long entityId;
}
