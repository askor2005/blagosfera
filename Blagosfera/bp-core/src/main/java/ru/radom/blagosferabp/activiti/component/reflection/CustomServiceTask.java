package ru.radom.blagosferabp.activiti.component.reflection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.ModelBundle;

import java.lang.annotation.*;

/**
 * Created by alex on 03.10.2015.<br/>
 * Помечает реализацию не стандандартного {@link org.activiti.bpmn.model.ServiceTask}
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Scope("prototype")
public @interface CustomServiceTask {

    /**
     * Тип таска, используется в XML для того, чтобы отличать реализации
     */
    String type() default "";

    /**
     * Пакет ресурсов необходимых для работы с конкретной реализацией
     */
    Class<? extends ModelBundle> bundle();
}
