package ru.askor.blagosfera.data.jpa.repositories.field;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.fields.FieldValueEntity;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface FieldValueRepository extends JpaRepository<FieldValueEntity, Long>, JpaSpecificationExecutor<FieldValueEntity> {

    List<FieldValueEntity> findAllByObjectIdAndObjectType(Long objectId, String objectType);
}
