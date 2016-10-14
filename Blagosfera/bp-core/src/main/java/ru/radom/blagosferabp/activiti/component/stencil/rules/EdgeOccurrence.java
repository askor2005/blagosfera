package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by alex on 01.10.2015.<br/>
 * Описание ограничения количества связей.
 */
@Data
@RequiredArgsConstructor(staticName = "forRole")
@Accessors(chain = true)
public class EdgeOccurrence {

    /**
     * Название роли
     */
    private final String role;

    /**
     * Минимальное количество
     */
    private Integer minimum;

    /**
     * Максимальное количество
     */
    private Integer maximum;
}
