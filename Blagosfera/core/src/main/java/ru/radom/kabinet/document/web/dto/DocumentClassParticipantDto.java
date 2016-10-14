package ru.radom.kabinet.document.web.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Data
public class DocumentClassParticipantDto {

    private Long id;

    private String name;

    private String type;

    private List<Object> qw;
}
