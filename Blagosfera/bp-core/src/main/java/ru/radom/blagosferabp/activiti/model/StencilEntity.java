package ru.radom.blagosferabp.activiti.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * Компонент диаграммы в БД
 */
@Getter
@Setter
@Entity
@Table(name = "bp_stencil_entity")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class StencilEntity {

    public static final String NODE = "node";
    public static final String EDGE = "edge";

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
     * Уникальный идентификатор компонента
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    private String id;

    /**
     * Тип ответа:
     * <li>
     *     <ul>{@link #FIRE_AND_FORGET}</ul>
     *     <ul>{@link #IMMEDIATE_WAIT}</ul>
     *     <ul>{@link #WAIT_ANSWER_MESSAGE}</ul>
     * </li>
     */
    @Column(name = "answer_type", nullable = false, length = 3)
    private String answerType;

    /**
     * Очередь, в которую отправляются данные, когда задача начинает выполняться
     */
    @Column(name = "queue_name", nullable = false, length = 255)
    private String queueToSend;

    /**
     * Название компонента
     */
    @Column
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
    @Column(name = "stencil_type")
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
    @Column(name = "stencil_view")
    private String view;

    /**
     * Короткое описание компонента
     */
    @Column
    private String description;

    /**
     * Иконка для отображения
     */
    @Column
    private String icon;

    /**
     * Группы компонентов, в которых текущий компонент должен быть отображен
     */
    @Column
    private String groups;

    /**
     * Роли к которым принадлежит компонент
     */
    @Column
    private String roles;

    /**
     * Свойства компонента
     */
    @Column
    private String properties;

}
