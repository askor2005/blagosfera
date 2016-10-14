package ru.radom.kabinet.web.jcr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.jcr.Node;

/**
 *
 * Created by vgusev on 26.02.2016.
 */
@Data
@AllArgsConstructor
public class NodeEntityDto {

    private Node node;

    private LongIdentifiable longIdentifiable;


}
