package ru.radom.kabinet.document.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Data
@AllArgsConstructor
public class CreateDocumentContentResponseDto {

    private String name;

    private String shortName;

    private String content;
}
