package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 10.04.2016.
 */
@Data
public class DocumentClassFilterDto {

    private List<FieldDto> filters;

    private List<Long> associationForms;

    private String group;
}
