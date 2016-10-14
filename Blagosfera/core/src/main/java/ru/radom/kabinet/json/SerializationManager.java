package ru.radom.kabinet.json;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Component("serializationManager")
public class SerializationManager implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationManager.class);

    private Map<Class, AbstractSerializer> serializersMap = new HashMap<>();
    private Map<Class, AbstractCollectionSerializer> collectionSerializersMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));

        for (BeanDefinition beanDefinition : scanner.findCandidateComponents("ru.radom.kabinet.json")) {
            ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
            String serializerClassName = scannedGenericBeanDefinition.getBeanClassName();
            Class serializerClass = Class.forName(serializerClassName);
            Object serializer = applicationContext.getBean(serializerClass);

            if (!(serializer instanceof AbstractSerializer) && !(serializer instanceof AbstractCollectionSerializer)) {
                continue;
            }

            Class clazz = (Class) (((ParameterizedType) serializerClass.getGenericSuperclass()).getActualTypeArguments()[0]);

            if (serializer instanceof AbstractSerializer) {
                if (serializersMap.get(clazz) == null) {
                    serializersMap.put(clazz, (AbstractSerializer) serializer);
                    LOGGER.info("serializer for single " + clazz.getSimpleName() + " found");
                } else {
                    throw new RuntimeException("more than ona serializer found for single " + clazz.getSimpleName());
                }
            } else if (serializer instanceof AbstractCollectionSerializer) {
                if (collectionSerializersMap.get(clazz) == null) {
                    collectionSerializersMap.put(clazz, (AbstractCollectionSerializer) serializer);
                    LOGGER.info("serializer for collection of " + clazz.getSimpleName() + " found");
                } else {
                    throw new RuntimeException("more than ona serializer found for collection of " + clazz.getSimpleName());
                }
            }
        }
    }

    public JSONObject serialize(Object object) {
        if (object != null) {
            Class clazz = object instanceof HibernateProxy ? Hibernate.getClass(object) : object.getClass();
            AbstractSerializer serializer = serializersMap.get(clazz);
            if (serializer == null) throw new UnsupportedOperationException(clazz.getCanonicalName());
            return serializer.serialize(object);
        } else {
            return null;
        }
    }

    public JSONArray serializeCollection(Collection collection) {
        if (collection == null) {
            return null;
        } else if (collection.size() == 0) {
            return new JSONArray();
        } else {
            Object first = collection.toArray()[0];
            AbstractCollectionSerializer collectionSerializer = collectionSerializersMap.get(first.getClass());

            if (collectionSerializer != null) {
                return collectionSerializer.serialize(collection);
            } else {
                //long start = System.currentTimeMillis();
                JSONArray array = new JSONArray();
                for (Object item : collection) {
                    array.put(serialize(item));
                }
                //long stop = System.currentTimeMillis();
                //long time = (stop - start);
                //logger.info("List of " + collection.size() + " " + collection.toArray()[0].getClass().getSimpleName() + " serialized in " + time + " milliseconds");
                return array;
            }
        }
    }
}
