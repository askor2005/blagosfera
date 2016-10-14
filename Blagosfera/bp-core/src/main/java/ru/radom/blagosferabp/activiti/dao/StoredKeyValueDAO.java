package ru.radom.blagosferabp.activiti.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.model.StoredKeyValue;

import java.util.Date;
import java.util.List;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * DAO для {@link StencilEntity}
 */
public interface StoredKeyValueDAO extends JpaRepository<StoredKeyValue, String> {

    @Query(value = "select * from bp_value_storage where key in (:keys) for update", nativeQuery = true)
    List<StoredKeyValue> getByKeysForUpdate(@Param("keys") Iterable<String> keys);

    @Query("select s FROM StoredKeyValue s where s.lastModified <= :date")
    List<StoredKeyValue> getOlderThan(@Param("date") Date date);

    @Query("select s FROM StoredKeyValue s where s.lastModified <= :date and s.key in (:keys)")
    List<StoredKeyValue> getAllOlderThan(@Param("keys") Iterable<String> keys, @Param("date") Date date);
}
