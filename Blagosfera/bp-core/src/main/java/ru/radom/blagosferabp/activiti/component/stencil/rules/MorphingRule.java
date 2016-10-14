package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Правила преобразования компонентов
 */
@Value
@Builder
public class MorphingRule implements StencilRule {

    private String role;

    @Singular("base") private List<String> baseMorphs;

    private Boolean preserveBounds;


    @Override
    public String group() {
        return "morphingRules";
    }
}
