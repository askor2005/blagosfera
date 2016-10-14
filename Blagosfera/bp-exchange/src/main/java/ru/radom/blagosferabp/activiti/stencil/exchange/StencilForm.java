package ru.radom.blagosferabp.activiti.stencil.exchange;

import lombok.Data;

/**
 * Created by Otts Alexey on 04.11.2015.<br/>
 * Форма для создания компонента
 */
@Data
public class StencilForm {

    /**
     * Вызвать и не ждать ответа
     */
    public static final String FIRE_AND_FORGET = "faf";

    /**
     * Вызвать и ждать ответа сразу
     */
    public static final String IMMEDIATE_WAIT = "iw";

    /**
     * Ждать ответа о выполненной работе
     */
    public static final String WAIT_ANSWER_MESSAGE = "wam";

    /**
     * Id компонента
     */
    private String id;

    /**
     * Тип ответа:
     * <li>
     *     <ul>{@link #FIRE_AND_FORGET}</ul>
     *     <ul>{@link #IMMEDIATE_WAIT}</ul>
     *     <ul>{@link #WAIT_ANSWER_MESSAGE}</ul>
     * </li>
     */
    private String answerType;

    /**
     * Очередь, в которую нужно послать сообщение при начале работы таска
     */
    private String queueToSend;

    /**
     * Название задачи
     */
    private String title;

    /**
     * Описание задачи
     */
    private String description;

    /**
     * Иконка
     */
    private String icon;

    /**
     * Представление в формате svg
     */
    private String view;

    /**
     * Группы через запятую, в которых должен находится этот компонент
     */
    private String groups;

    /**
     * Роли компонента через запятую
     */
    private String roles;

    /**
     * Параметры компонента. JSONArray в строковом виде
     */
    private String properties;
}
