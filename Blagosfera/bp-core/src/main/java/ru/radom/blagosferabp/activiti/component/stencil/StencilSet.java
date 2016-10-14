package ru.radom.blagosferabp.activiti.component.stencil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 30.09.2015.<br/>
 * Сет элементов, которые будут использованы в редакторе модели
 */
@Data
@Builder(toBuilder = true)
@JsonIgnoreProperties("additionalStencilRules")
public class StencilSet {

    /**
     * Название сета
     */
    private String title;

    /**
     * Уникальный идентификатор сета, должен быть записан в формате URI.
     * Не знаю зачем такое правило, так написано в доке
     */
    private String namespace;

    /**
     * Описание сета
     */
    private String description;

    /**
     * Список элементов, которые могут быть использованы
     */
    @Singular private List<Stencil> stencils = new ArrayList<Stencil>();

    /**
     * Правила, которые описывают взаимодействия между элементами сета
     */
    @Singular private List<StencilRule> stencilRules = new ArrayList<StencilRule>();

    public Map<String, List<StencilRule>> getRules() {
        Map<String, List<StencilRule>> groupedRules = new HashMap<>();

        for (StencilRule stencilRule : stencilRules) {
            String group = stencilRule.group();
            List<StencilRule> rules = groupedRules.get(group);
            if(rules == null) {
                rules = new ArrayList<>();
                groupedRules.put(group, rules);
            }

            rules.add(stencilRule);
        }

        return groupedRules;
    }
}
