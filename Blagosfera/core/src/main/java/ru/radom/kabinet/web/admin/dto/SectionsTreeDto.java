package ru.radom.kabinet.web.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 29.03.2016.
 */
@Data
@AllArgsConstructor
public class SectionsTreeDto {

    private List<SectionTreeNodeDto> nodes;
}
