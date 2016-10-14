package ru.radom.kabinet.services.bpmhandlers.document.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 * Created by vgusev on 24.02.2016.
 */
@Data
public class BPMDocumentsZipDto {

    /**
     * Мапа с ИД документов. (List не работает)
     */
    private Map<String, Long> documentMap;
}
