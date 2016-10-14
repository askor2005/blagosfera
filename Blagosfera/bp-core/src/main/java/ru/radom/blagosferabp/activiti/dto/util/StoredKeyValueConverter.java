package ru.radom.blagosferabp.activiti.dto.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastInput;
import org.springframework.core.convert.converter.Converter;
import ru.radom.blagosferabp.activiti.dto.StoredKeyValueDTO;
import ru.radom.blagosferabp.activiti.model.StoredKeyValue;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * Преобразрвание {@link StoredKeyValue} -> {@link StoredKeyValueDTO}</br>
 * Нельзя использовать один и тот же инстанс в несколько тредов.
 */
public class StoredKeyValueConverter implements Converter<StoredKeyValue, StoredKeyValueDTO> {

    /**
     * Десеарилизатор значения
     */
    private Kryo kryo = new Kryo();

    @Override
    public StoredKeyValueDTO convert(StoredKeyValue source) {
        StoredKeyValueDTO dto = new StoredKeyValueDTO();
        dto.setKey(source.getKey());
        FastInput fastInput = new FastInput(source.getValue());
        dto.setValue(kryo.readClassAndObject(fastInput));
        return dto;
    }
}
