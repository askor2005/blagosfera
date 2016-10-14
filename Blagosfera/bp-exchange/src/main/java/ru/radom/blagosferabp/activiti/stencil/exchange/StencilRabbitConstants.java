package ru.radom.blagosferabp.activiti.stencil.exchange;

/**
 * Created by Otts Alexey on 02.11.2015.<br/>
 * Очереди для работы с редактированием списка компонентов
 */
public class StencilRabbitConstants {

    /**
     * Создать компонент
     */
    public static final String CREATE_STENCIL = "bp.stencil.exchange.create.stencil";

    /**
     * Компонент создан
     */
    public static final String STENCIL_CREATED = "bp.stencil.exchange.stencil.created";

    /**
     * Обновить существующий компонент
     */
    public static final String UPDATE_STENCIL = "bp.stencil.exchange.update.stencil";

    /**
     * Существующий компонент обновлен
     */
    public static final String STENCIL_UPDATED = "bp.stencil.exchange.stencil.updated";

    /**
     * Удалить существующий компонент
     */
    public static final String REMOVE_STENCIL = "bp.stencil.exchange.remove.stencil";

    /**
     * Существующий компонент удалён
     */
    public static final String STENCIL_REMOVED = "bp.stencil.exchange.stencil.removed";

    /**
     * Получить один компонент
     */
    public static final String GET_STENCIL = "bp.stencil.exchange.get";

    /**
     * Получить все нестандартные компоненты
     */
    public static final String GET_CUSTOM_STENCILS = "bp.stencil.exchange.list.custom";

    /**
     * Получить один компонент
     */
    public static final String GET_STENCIL_SET = "bp.stencil.exchange.get.set";

    /**
     * Exchange в который нужно посылать запросы
     */
    public static final String STENCILS_EXCHANGE = "stencil-exchange";
}
