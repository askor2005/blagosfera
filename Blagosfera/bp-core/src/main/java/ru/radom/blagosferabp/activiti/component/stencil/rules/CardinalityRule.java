package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Правило описывающее, какие связи и могут входить или выходить из компонента и в каком количестве.<br/>
 * Также оно описывает количество компонентов, которое может присутсвовать на диаграмме
 */
@Value
@Builder
public class CardinalityRule implements StencilRule {

    /**
     * Роль для который должно выполняться это правило
     */
    private String role;

    /**
     * Минимальное количество элементов на диаграмме
     */
    private Integer minimumOccurrence;

    /**
     * Максимально количество элементов на диаграмме
     */
    private Integer maximumOccurrence;

    /**
     * Описание какие "стрелочки" могут выходить из компонента и сколько
     */
    @Singular private List<EdgeOccurrence> outgoingEdges;

    /**
     * Описание какие "стрелочки" могут входить в компонент и сколько
     */
    @Singular private List<EdgeOccurrence> incomingEdges;

    @Override
    public String group() {
        return "cardinalityRules";
    }
}
