package ru.radom.blagosferabp.activiti.stencil.exchange;

import lombok.*;

import java.util.List;

/**
 * Created by alex on 30.09.2015.<br/>
 * Компонент диаграммы
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Stencil {

    public static final String NODE = "node";
    public static final String EDGE = "edge";

    /**
     * Уникальный идентификатор компонента
     */
    private String id;

    /**
     * Название компонента
     */
    private String title;

    /**
     * Стрелка или узел. Доступные значения:
     * <code>
     *     <ul>
     *         <li>{@value #EDGE}</li>
     *         <li>{@value #NODE}</li>
     *     </ul>
     * </code>
     */
    private String type;

    /**
     * SVG представление элемента.
     * <code>
     *     <ul>
     *         <li>URL до SVG на сервере</li>
     *         <li>SVG файл в виде строки</li>
     *     </ul>
     * </code>
     */
    private String view;

    /**
     * Короткое описание компонента
     */
    private String description;

    /**
     * Иконка для отображения
     */
    private String icon;

    /**
     * Может ли элемент быть корневым
     */
    private Boolean mayBeRoot;

    /**
     * Нужно ли скрыть элемент из панели
     */
    private Boolean hide;

    /**
     * Группы компонентов, в которых текущий компонент должен быть отображен
     */
    @Singular private List<String> groups;

    /**
     * Роли к которым принадлежит компонент
     */
    @Singular private List<String> roles;

    /**
     * Свойства компонента
     */
    @Singular private List<Property> properties;

    /**
     * Еще пока не понял зачем это
     */
    @Singular("layout") private List<Layout> layout;

}
