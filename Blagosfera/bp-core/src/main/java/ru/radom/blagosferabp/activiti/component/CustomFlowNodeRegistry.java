package ru.radom.blagosferabp.activiti.component;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.model.FlowNode;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.radom.blagosferabp.activiti.component.reflection.CustomIntermediateCatchEvent;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 09.10.2015.<br/>
 * Реестр не стандартных реализаций {@link org.activiti.bpmn.model.FlowNode}
 * помеченных аннотациями:
 * <ul>
 *   <li>{@link ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask}</li>
 *   <li>{@link ru.radom.blagosferabp.activiti.component.reflection.CustomIntermediateCatchEvent}</li>
 * </ul>
 */
@Log4j2
@Component
public class CustomFlowNodeRegistry {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Соответсвие класса к экземпляру {@link ModelBundle}
     */
    @Getter
    private Map<Class<? extends Annotation>, Map<Class<? extends FlowNode>, ModelBundle>> bundlesByClass = new HashMap<>();

    /**
     * Соответсвие класса к названию бина для этого класса
     */
    @Getter
    private Map<Class<? extends Annotation>, Map<Class<? extends FlowNode>, String>> beanNamesByClass = new HashMap<>();

    @PostConstruct
    private void postConstruct() throws IllegalAccessException, InstantiationException {
        registerCustomFlowNodesOfType(CustomServiceTask.class);
        registerCustomFlowNodesOfType(CustomIntermediateCatchEvent.class);
    }

    private void registerCustomFlowNodesOfType(Class<? extends Annotation> annotationClass) throws IllegalAccessException, InstantiationException {
        Method bundle = ReflectionUtils.findMethod(annotationClass, "bundle");
        Map<Class<? extends FlowNode>, ModelBundle> annotationBundles = new HashMap<>();
        Map<Class<? extends FlowNode>, String> annotationBeanNames = new HashMap<>();
        bundlesByClass.put(annotationClass, annotationBundles);
        beanNamesByClass.put(annotationClass, annotationBeanNames);
        Map<String, Object> nodes = applicationContext.getBeansWithAnnotation(annotationClass);
        for (Map.Entry<String, Object> entry : nodes.entrySet()) {
            FlowNode task = (FlowNode) entry.getValue();
            Class<? extends FlowNode> taskClass = task.getClass();
            Annotation annotation = taskClass.getAnnotation(annotationClass);
            Class<? extends ModelBundle> bundleClass = (Class<? extends ModelBundle>) ReflectionUtils.invokeMethod(bundle, annotation);
            ModelBundle modelBundle;

            try {
                modelBundle = applicationContext.getBean(bundleClass);
            } catch (NoSuchBeanDefinitionException e) {
                log.warn("No singleton bean definition for " + bundleClass);
                try {
                    modelBundle = applicationContext.getBean(bundleClass, new Object[] {});
                } catch (NoSuchBeanDefinitionException innerException) {
                    //если не нашли бина, то пытаемся создать экземпляр через конструктор
                    modelBundle = bundleClass.newInstance();
                }
            }
            annotationBundles.put(taskClass, modelBundle);
            annotationBeanNames.put(taskClass, entry.getKey());
        }
    }
}
