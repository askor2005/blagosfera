package ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules;

import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Провайдер правил
 */
public interface StencilRulesProvider<T extends StencilRule> {

    List<T> getAll();
}
