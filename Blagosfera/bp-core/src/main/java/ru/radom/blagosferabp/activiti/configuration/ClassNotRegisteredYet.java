package ru.radom.blagosferabp.activiti.configuration;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;

/**
 * Created by Otts Alexey on 13.10.2015.<br/>
 * Проверка на то, что класс еще не зарегестрирован в контексте
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class ClassNotRegisteredYet implements ConfigurationCondition {
    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            context.getBeanFactory().getBean(ClassUtils.forName(getClassName(metadata), context.getClassLoader()));
            return false;
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getClassName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof MethodMetadata) {
            return ((MethodMetadata) metadata).getReturnTypeName();
        }
        throw new IllegalArgumentException("Unsupported type of metadata: " + metadata.getClass());
    }
}
