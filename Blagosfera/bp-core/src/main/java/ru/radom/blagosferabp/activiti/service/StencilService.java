package ru.radom.blagosferabp.activiti.service;

import ru.radom.blagosferabp.activiti.component.stencil.StencilSet;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilForm;

import java.util.List;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * Сервис для работы с компонентами
 */
public interface StencilService {

    StencilForm createStencil(StencilForm stencil);

    StencilForm updateStencil(StencilForm stencil);

    void deleteStencil(String stencilId);

    List<StencilForm> getCustomStencils();

    /**
     * Создает компонент из DTO
     * @param stencil
     */
    StencilEntity create(StencilForm stencil);

    /**
     * Обновить компонент из DTO
     * @param stencil
     */
    StencilEntity update(StencilForm stencil);

    /**
     * Удалить компонент
     */
    void delete(String stencilId);

    /**
     * Получить текущий set компонентов
     */
    StencilSet getStencilSet();
}
