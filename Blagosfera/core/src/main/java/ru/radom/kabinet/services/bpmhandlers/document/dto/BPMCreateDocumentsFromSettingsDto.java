package ru.radom.kabinet.services.bpmhandlers.document.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 09.08.2016.
 */
@Getter
public class BPMCreateDocumentsFromSettingsDto {

    private Long creatorId;

    private List<Long> settingsIds;

    private String customSourceHandler;

    private Map<Integer, Long> customSourceParameters;

    private Map<String, String> documentParameters;
}
