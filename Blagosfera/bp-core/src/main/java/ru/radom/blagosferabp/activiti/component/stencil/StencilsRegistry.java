package ru.radom.blagosferabp.activiti.component.stencil;

import org.activiti.bpmn.model.FlowNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.CustomFlowNodeRegistry;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.rules.StencilRulesProvider;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils.StencilProvider;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.*;

/**
 * Created by alex on 01.10.2015.<br/>
 * Реестр всех графических компонентов для редактора
 */
@Component
public class StencilsRegistry {

    @Autowired
    private List<StencilProvider> stencilProviders;

    @Autowired
    private List<StencilRulesProvider> stencilRulesProviders;

    @Autowired
    private CustomFlowNodeRegistry customFlowNodeRegistry;

    public StencilSet.StencilSetBuilder createSet() {
        return StencilSet.builder()
                .stencils(getAllStencils())
                .stencilRules(getAllStencilRules());
    }

    private Collection<StencilRule> getAllStencilRules() {
        Set<StencilRule> rules = new LinkedHashSet<>();
        for (StencilRulesProvider stencilRulesProvider : stencilRulesProviders) {
            rules.addAll(stencilRulesProvider.getAll());
        }
        Collection<Map<Class<? extends FlowNode>, ModelBundle>> bundlesByClasses = customFlowNodeRegistry.getBundlesByClass().values();
        for (Map<Class<? extends FlowNode>, ModelBundle> bundlesByClass : bundlesByClasses) {
            for (ModelBundle modelBundle : bundlesByClass.values()) {
                if(modelBundle.getRequiredRules() != null) {
                    rules.addAll(modelBundle.getRequiredRules());
                }
            }
        }
        return rules;
    }

    private Collection<Stencil> getAllStencils() {
        Set<Stencil> stencils = new LinkedHashSet<>();
        for (StencilProvider stencilProvider : stencilProviders) {
            stencils.addAll(stencilProvider.getAll());
        }
        Collection<Map<Class<? extends FlowNode>, ModelBundle>> bundlesByClasses = customFlowNodeRegistry.getBundlesByClass().values();
        for (Map<Class<? extends FlowNode>, ModelBundle> bundlesByClass : bundlesByClasses) {
            for (ModelBundle modelBundle : bundlesByClass.values()) {
                Stencil stencil = modelBundle.getStencil();
                if (stencil != null) {
                    stencils.add(stencil);
                }
            }
        }

        return stencils;
    }
}
