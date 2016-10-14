package ru.radom.blagosferabp.activiti.component.reflection;

import java.lang.annotation.*;

/**
 * Created by alex on 03.10.2015.<br/>
 * Параметр блока бизнесс процесса<br/>
 * Нужно использовать вместе с {@link CustomServiceTask}
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FlowNodeParameter {

    /**
     * ID параметра внутри JSON компонента
     */
    String stencilParameter() default "";

    /**
     * Название XML атрибута, в котором лежит значение параметра
     */
    String xmlParameter() default "";

    //------------------валидация------------------
    /**
     * Обязательное ли значение
     */
    boolean required() default false;

    /**
     * Минимальное значение для наследников {@link Number}
     */
    double min() default -Double.MAX_VALUE;

    /**
     * Максимальное значение для наследников {@link Number}
     */
    double max() default Double.MAX_VALUE;

    /**
     * Минимальная длина строки
     */
    int minLength() default 0;

    /**
     * Максимальная длина строки
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * {@link java.util.regex.Pattern} для проверки строки
     */
    String pattern() default "";
}
