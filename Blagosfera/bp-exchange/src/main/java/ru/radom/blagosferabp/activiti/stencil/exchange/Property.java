package ru.radom.blagosferabp.activiti.stencil.exchange;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by alex on 30.09.2015.<br/>
 * Описание одного свойства компонента
 */
@Data
@Builder(toBuilder = true)
public class Property {

    public final static String STRING = "string";
    public final static String BOOLEAN = "boolean";
    public final static String INTEGER = "integer";
    public final static String FLOAT = "float";
    public final static String COLOR = "color";
    public final static String DATE = "date";
    public final static String CHOICE = "choice";
    public final static String COMPLEX = "complex";
    public final static String MULTIPLECOMPLEX = "multiplecomplex";
    public final static String TEXT = "text";
    public final static String KISBPM_MULTIINSTANCE = "kisbpm-multiinstance";
    public final static String SCRIPT = "scriptcomplex";
    public final static String DOCUMENT_TEMPLATE = "document_template_complex";
    public final static String KEY_VALUE = "key_value_complex";
    public final static String VOTINGS_TEMPLATE = "votings_template_complex";

    /**
     * Идентификатор свойства, должен быть уникален в пределах компонента
     */
    private String id;

    /**
     * Тип свойства, доступные:
     * <ul>
     * 	<li>{@value #STRING}</li>
     * 	<li>{@value #BOOLEAN}</li>
     * 	<li>{@value #INTEGER}</li>
     * 	<li>{@value #FLOAT}</li>
     * 	<li>{@value #COLOR}</li>
     * 	<li>{@value #DATE}</li>
     * 	<li>{@value #CHOICE}</li>
     * 	<li>{@value #COMPLEX}</li>
     * 	<li>{@value #MULTIPLECOMPLEX}</li>
     * 	<li>{@value #TEXT}</li>
     * 	<li>{@value #KISBPM_MULTIINSTANCE}</li>
     * 	<li>{@value #SCRIPT}</li>
     * 	<li>{@value #DOCUMENT_TEMPLATE}</li>
     * 	<li>{@value #KEY_VALUE}</li>
     * <ul>
     */
    private String type = STRING;

    /**
     * Название свойства
     */
    private String title;

    /**
     * Стартовое значение свойства
     */
    private Object value;

    /**
     * Короткое описание свойства
     */
    private String description;

    /**
     * Можно ли изменять значение свойства
     */
    private Boolean readonly;

    /**
     * Формат даты  если {@link #type} равен <b>{@value #DATE}</b>
     */
    private String dateFormat;

    /**
     * Обязательное ли значение
     */
    private Boolean optional;

    /**
     * Описывает список, того что можно выбрать, если {@link #type} равен <b>{@value #CHOICE}</b>
     */
    private List<Object> items;

    /**
     * Максимальная длина строки, если {@link #type} равен <b>{@value #STRING}</b>
     */
    private Integer length;

    /**
     * Максимальная длина строки, если {@link #type} равен <b>{@value #INTEGER}</b> или <b>{@value #FLOAT}</b>
     */
    private Integer min;

    /**
     * Максимальная длина строки, если {@link #type} равен <b>{@value #INTEGER}</b> или <b>{@value #FLOAT}</b>
     */
    private Integer max;

    /**
     * id элементов внутри SVG представления компонента
     */
    private List<String> refToView;

    /**
     * Нужно ли переносить строку, если {@link #type} равен <b>{@value #STRING}</b>
     */
    private Boolean wrapValue;

    /**
     * Ссылка на модель редактора, для обработки на клиенте
     */
    private String customEditor;

    private Boolean popular;
}
