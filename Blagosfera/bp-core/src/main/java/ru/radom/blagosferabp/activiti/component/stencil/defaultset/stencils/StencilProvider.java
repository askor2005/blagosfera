package ru.radom.blagosferabp.activiti.component.stencil.defaultset.stencils;

import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Провайдер компонентов
 */
public interface StencilProvider {

    List<Stencil> getAll();
}
