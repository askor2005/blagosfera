package ru.radom.kabinet.services.bpmhandlers.user.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
@Data
public class BPMUsersMessageDto {

    private String content;

    private Map<String, Object> parameters;

    private List<Long> userIds;
}
