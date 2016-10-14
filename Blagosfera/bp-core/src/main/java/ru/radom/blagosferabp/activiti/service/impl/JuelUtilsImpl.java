package ru.radom.blagosferabp.activiti.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.radom.blagosferabp.activiti.dto.StoredKeyValueDTO;
import ru.radom.blagosferabp.activiti.service.JuelUtils;
import ru.radom.blagosferabp.activiti.service.StoredKeyValueService;
import ru.radom.blagosferabp.activiti.utils.DateWrapper;
import ru.radom.blagosferabp.activiti.utils.PadegWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Otts Alexey on 25.11.2015.<br/>
 * Стандартная реализация для {@link JuelUtils}
 */
@Service
public class JuelUtilsImpl implements JuelUtils {

    @Autowired
    private StoredKeyValueService storedKeyValueService;

    /**
     * Набор функций для работы с падежами
     */
    public final PadegWrapper padeg = new PadegWrapper();

    @Override
    public List<?> list(Object... objects) {
        List<Object> result = new ArrayList<>();
        for (Object o : objects) {
            if(o instanceof Collection) {
                result.addAll((Collection<?>) o);
            } else {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public List<?> asSet(Object... objects) {
        List<Object> result = new ArrayList<>();
        for (Object o : objects) {
            if(o instanceof Collection) {
                result.addAll((Collection<?>) o);
            } else {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> map(Object... pairs) {
        int length = pairs.length;
        if(length % 2 != 0) {
            throw new IllegalArgumentException("Pairs length must be even!");
        }
        Map<String, Object> result = new HashMap<>(length / 2);
        for (int i = 0; i < length; i+=2) {
            Object key = pairs[i];
            if(!(key instanceof String)) {
                throw new IllegalArgumentException(Integer.toString(i + 1) + " argument must be String!");
            }
            result.put(((String) key), pairs[i + 1]);
        }
        return result;
    }

    @Override
    public Object readFromStorage(String key) {
        List<StoredKeyValueDTO> list = storedKeyValueService.getValues(Collections.singletonList(key));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public DateWrapper toDate(Object object) {
        if(object instanceof Date) {
            return DateWrapper.wrap((Date) object);
        }
        if(object instanceof Calendar) {
            return DateWrapper.wrap((Calendar) object);
        }
        if(object instanceof Number) {
            return DateWrapper.wrap(((Number) object).longValue());
        }
        if(object instanceof String) {
            try {
                return DateWrapper.wrap(new SimpleDateFormat("dd.MM.yyyy").parse((String) object));
            } catch (ParseException e) {
                try {
                    return DateWrapper.wrap(Long.parseLong((String) object));
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public DateWrapper nowDate() {
        return new DateWrapper(Calendar.getInstance());
    }


}
