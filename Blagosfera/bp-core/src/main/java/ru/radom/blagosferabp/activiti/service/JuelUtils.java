package ru.radom.blagosferabp.activiti.service;

import ru.radom.blagosferabp.activiti.utils.DateWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Otts Alexey on 25.11.2015.<br/>
 * Утили, которые будут использоваться внутри JUEL выражений.
 */
public interface JuelUtils {

    /**
     * Приводит множественные элементы в {@link List}, если элемент имеет тип {@link java.util.Collection},
     * то добавляются все его значения, иначе просто добавляется значение
     */
    List<?> list(Object... objects);

    /**
     * Приводит множественные элементы в {@link java.util.Set}, если элемент имеет тип {@link java.util.Collection},
     * то добавляются все его значения, иначе просто добавляется значение
     */
    List<?> asSet(Object... objects);

    /**
     * Приводит множественные элементы в {@link Map}.
     * @param pairs значения должны идти парами, на первом месте не нулевое значение типа {@link String}
     */
    Map<String, Object> map(Object... pairs);

    /**
     * Получить значение по ключу из хранилища
     */
    Object readFromStorage(String key);

    /**
     * Преобразовать объект к дате. Если не получится, то возврщает null
     */
    DateWrapper toDate(Object object);

    /**
     * Получить текущую дату
     */
    DateWrapper nowDate();
}
