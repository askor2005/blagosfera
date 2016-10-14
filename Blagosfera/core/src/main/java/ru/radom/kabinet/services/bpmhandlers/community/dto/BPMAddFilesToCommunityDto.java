package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 * Created by vgusev on 25.02.2016.
 */
@Data
public class BPMAddFilesToCommunityDto {

    /**
     * ИД объединения
     */
    private Long communityId;

    /**
     * Мапа с файлами объединения
     * ключ - название файла
     * значение - путь до файла
     */
    private Map<String, String> communityFiles;
}
