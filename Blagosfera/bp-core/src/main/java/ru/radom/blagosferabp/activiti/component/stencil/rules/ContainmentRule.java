package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.Value;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Правила описывающие, что может находится внутри составных компонентов
 */
@Value
public class ContainmentRule implements StencilRule {

    /**
     * Id или роль компонента с типом {@link Stencil#NODE}
     */
    private String role;

    /**
     * Список ролей или компонентов который могут быть использованы внутри сосвтаного компанента
     */
    private List<String> contains;

    @Override
    public String group() {
        return "containmentRules";
    }
}
