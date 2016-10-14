package ru.radom.blagosferabp.activiti.component.stencil.rules;

/**
 * Created by alex on 30.09.2015.<br/>
 * Правило
 */
public interface StencilRule {

    /**
     * Группа правил, в которую оно попадет при трансформации в json
     */
    String group();
}
