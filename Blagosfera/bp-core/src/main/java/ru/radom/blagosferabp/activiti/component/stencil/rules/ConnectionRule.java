package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.List;

/**
 * Created by alex on 30.09.2015.<br/>
 * Правило описывающее возможные связи между компонентами
 */
@Value
@Builder
public class ConnectionRule implements StencilRule {

    /**
     * Id компонента у которого тип {@link Stencil#EDGE}
     */
    private String role;

    /**
     * Описание связей
     */
    @Singular("connect") private List<Connect> connects;

    @Override
    public String group() {
        return "connectionRules";
    }


}
